import Direction.*
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

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

    data class Instruction(val direction: Direction, val meters: Int, val color: String)

    private fun parse(input: List<String>): List<Instruction> {
        val re = Regex("""(.) (\d+) \((.)+\)""")
        val dirs = mapOf("U" to N, "D" to S, "R" to E, "L" to W)
        return input.map { line -> re.matchEntire(line)!!.groupValues.let { Instruction(dirs[it[1]]!!, it[2].toInt(), it[3]) } }
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

    private fun two(input: List<String>): Int {
        return 0
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 62
        one(input) shouldBe 48652
    }

    @Test
    fun testTwo(input: List<String>) {
//        two(sample) shouldBe 0
//        two(input) shouldBe 0
    }
}
