import Direction.*
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import kotlin.math.absoluteValue

class Day18 {
    private val sample = """
        R 6 (#70c710)
        D 5 (#0dc571)
        L 2 (#5713f0)
        D 2 (#d2c081)
        R 2 (#59c680)
        D 2 (#411b91)
        L 5 (#8ceee2)
        U 2 (#caa173)
        L 1 (#1b58a2)
        U 2 (#caa171)
        R 2 (#7807d2)
        U 3 (#a77fa3)
        L 2 (#015232)
        U 2 (#7a21e3)
    """.trimIndent().lines()

    private val sample3 = """
        R 2 (#70c710)
        D 2 (#0dc571)
        R 2 (#5713f0)
        D 2 (#d2c081)
        L 2 (#59c680)
        D 2 (#411b91)
        R 3 (#8ceee2)
        D 2 (#caa173)
        L 5 (#1b58a2)
        U 8 (#a77fa3)
    """.trimIndent().lines()

    data class Instruction(val direction: Direction, val meters: Int)

    private fun parse(input: List<String>): List<Instruction> {
        val re = Regex("""(.) (\d+) \(.+\)""")
        val dirs = mapOf("U" to N, "D" to S, "R" to E, "L" to W)
        return input.map { line ->
            re.matchEntire(line)!!.groupValues.let {
                Instruction(
                    dirs[it[1]]!!,
                    it[2].toInt()
                )
            }
        }
    }

    private fun one(input: List<String>): Int {
        val instructions = parse(input)
        var t = Point(0, 0)
        val trench = instructions.flatMap { i -> (1..i.meters).map { t = t.move(i.direction); t } }.toSet()
        val xRange = trench.minOf { it.x }..trench.maxOf { it.x }
        val yRange = trench.minOf { it.y }..trench.maxOf { it.y }

        val outer = mutableSetOf<Point>()
        for (x in xRange) {
            for (y in yRange) {
                if (x == xRange.first || x == xRange.last || y == yRange.first || y == yRange.last) {
                    val p = Point(x, y)
                    if (p !in trench && outer.add(p)) {
                        bfs(p) { t ->
                            Direction.entries
                                .map { d -> t.move(d) }
                                .filter { it !in trench && it !in outer && it.x in xRange && it.y in yRange }
                        }.forEach { outer += it.value }
                    }
                }
            }
        }

        return (xRange.last - xRange.first + 1) * (yRange.last - yRange.first + 1) - outer.size
    }

    private fun parse2(input: List<String>): List<Instruction> {
        val re = Regex(""".+\(#(.+)(.)+\)""")
        val dirs = mapOf("3" to N, "1" to S, "0" to E, "2" to W)
        return input.map { line ->
            re.matchEntire(line)!!.groupValues.let {
                Instruction(dirs[it[2]]!!, it[1].toInt(16))
            }
        }
    }

    private fun two(input: List<String>, part2: Boolean): Long {
        val instructions = if (part2) parse2(input) else parse(input)
        val area = instructions
            .runningFold(Point(0, 0)) { point, i -> point.move(i.direction, i.meters) }
            .zipWithNext { p1, p2 -> (p2.x - p1.x) * p1.y.toLong() }
            .sum().absoluteValue
        val trenchLength = instructions.sumOf { it.meters }
        return area + trenchLength / 2 + 1
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 62
        one(input) shouldBe 48652
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample, false) shouldBe 62L
        two(sample3, false) shouldBe 42L
        two(input, false) shouldBe 48652L
        two(sample, true) shouldBe 952408144115L
        two(input, true) shouldBe 45757884535661L
    }
}

/*
Could not solve part 2 without cheating. I spent quite some time trying a "ray tracing" approach (where I for every
X coordinate use the crossed horizontal lines to decide if the next points are inside or outside), but could never
handle all the corner cases correctly. Finally, I took a peek at the Slack channel, which suggested to use "Pick's theorem"
to compute the number of "inside" points based on the trench length and the trenched area, and to compute the area itself
using the "shoelace formula". Once I understood these, the actual code is very short: compute the list of points using
"runningFold" and compute the area from the list.
 */
