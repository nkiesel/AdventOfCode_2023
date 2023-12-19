import Direction.*
import io.kotest.matchers.shouldBe
import jdk.internal.org.jline.utils.Colors.h
import org.junit.jupiter.api.Test
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

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

    private fun displayTrench(
        minX: Int,
        maxX: Int,
        minY: Int,
        maxY: Int,
        horizontals: MutableList<Line>,
        verticals: MutableList<Line>
    ) {
        val area = CharArea(maxX - minX + 1, maxY - minY + 1, '.')
        horizontals.forEach { h ->
            var p = Point(h.start.x - minX, h.start.y - minY)
            val end = Point(h.end.x - minX, h.end.y - minY)
            area.set(p, '#')
            do {
                p = p.move(E)
                area.set(p, '#')
            } while (p != end)
        }
        verticals.forEach { h ->
            var p = Point(h.start.x - minX, h.start.y - minY)
            val end = Point(h.end.x - minX, h.end.y - minY)
            do {
                area.set(p, '#')
                p = p.move(S)
            } while (p != end)
        }
        area.show()
    }

    private fun displayTrenchImage(
        name: String,
        minX: Int,
        maxX: Int,
        minY: Int,
        maxY: Int,
        horizontals: MutableList<Line>,
        verticals: MutableList<Line>
    ) {
        val area = BufferedImage(maxX - minX + 1, maxY - minY + 1, BufferedImage.TYPE_INT_RGB)
        val red = Color.RED.rgb
        horizontals.forEach { h ->
            var p = Point(h.start.x - minX, h.start.y - minY)
            val end = Point(h.end.x - minX, h.end.y - minY)
            area.setRGB(p.x, p.y, red)
            do {
                p = p.move(E)
                area.setRGB(p.x, p.y, red)
            } while (p != end)
        }
        verticals.forEach { h ->
            var p = Point(h.start.x - minX, h.start.y - minY)
            val end = Point(h.end.x - minX, h.end.y - minY)
            area.setRGB(p.x, p.y, red)
            do {
                p = p.move(S)
                area.setRGB(p.x, p.y, red)
            } while (p != end)
        }
        ImageIO.write(area, "BMP", File("$name.bmp"))
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

    data class Line(val start: Point, val end: Point)

    private fun two(input: List<String>, part2: Boolean, name: String): Long {
        val instructions = if (part2) parse2(input) else parse(input)
        if (part2) println(instructions.joinToString("\n"))
        val verticals = mutableListOf<Line>()
        val horizontals = mutableListOf<Line>()
        var t = Point(0, 0)
        for (i in instructions) {
            val n = t.move(i.direction, i.meters)
            when (i.direction) {
                N -> verticals.add(Line(n, t))
                S -> verticals.add(Line(t, n))
                E -> horizontals.add(Line(t, n))
                W -> horizontals.add(Line(n, t))
            }
            t = n
        }

        horizontals.sortBy { it.start.y }
        verticals.sortBy { it.start.x }

        val minX = horizontals.minOf { it.start.x }
        val maxX = horizontals.maxOf { it.end.x }
        val minY = verticals.minOf { it.start.y }
        val maxY = verticals.maxOf { it.end.y }
        val xRange = minX..maxX
        val width = maxX - minX + 1
        val height = maxY - minY + 1

        displayTrenchImage(name, minX, maxX, minY, maxY, horizontals, verticals)

        val vr = verticals.groupBy { it.start.x }

        var count = 0L
        println("size: $width * $height = ${width.toLong() * height}")

        for (x in xRange) {
            val h = horizontals.filter { x in it.start.x..it.end.x }
            var cc = h.first().start.y - minY
            var nextIsInside = cc > 0
            h.zipWithNext().forEach { (l1, l2) ->
                val v = vr[x]?.find { v -> v.start == l1.start || v.start == l1.end && v.end == l2.start || v.end == l2.end }
                if (v == null) {
                    if (!nextIsInside) {
                        cc += l2.start.y - l1.start.y - 1
                    }
                    nextIsInside = !nextIsInside
                }
            }
            cc += maxY - h.last().start.y
            if (!part2) println("count for x $x: $cc")
            count += cc
        }
        return (maxX - minX + 1).toLong() * (maxY - minY + 1) - count
    }


    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 62
        one(input) shouldBe 48652
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample, false, "sample") shouldBe 62L
        two(sample3, false, "sample3") shouldBe 41L
        two(input, false, "input") shouldBe 48652L
//        two(sample) shouldBe 952408144115L
//        two(input) shouldBe 0
    }
}
