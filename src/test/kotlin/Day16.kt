import io.kotest.matchers.shouldBe
import jdk.internal.org.jline.utils.Colors.s
import org.junit.jupiter.api.Test

class Day16 {
    private val sample = """
        .|...\....
        |.-.\.....
        .....|-...
        ........|.
        ..........
        .........\
        ..../.\\..
        .-.-/..|..
        .|....-|.\
        ..//.|....
    """.trimIndent().lines()

    data class Beam(var x: Int, var y: Int, var dx: Int, var dy: Int) {
        fun isAlive(area: CharArea) = x in area.xRange && y in area.yRange

        fun move() {
            x += dx
            y += dy
        }

    }

    private fun one(input: List<String>): Int {
        val area = CharArea(input)
        var beams = listOf(Beam(-1, 0, 1, 0))
        val energized = mutableSetOf<Pair<Int, Int>>()
        val seen = mutableSetOf<Beam>()
        do {
            beams = buildList {
                for (beam in beams.filter { seen.add(it) }) {
                    val dx = beam.dx
                    val dy = beam.dy
                    val x = beam.x + dx
                    val y = beam.y + dy
                    if (x in area.xRange && y in area.yRange) {
                        energized += Pair(x, y)
                        when (area.get(x, y)) {
                            '-' -> if (dx == 0) {
                                add(Beam(x, y, -1, 0))
                                add(Beam(x, y, 1, 0))
                            } else {
                                add(Beam(x, y, dx, dy))
                            }

                            '|' -> if (dy == 0) {
                                add(Beam(x, y, 0, -1))
                                add(Beam(x, y, 0, 1))
                            } else {
                                add(Beam(x, y, dx, dy))
                            }

                            '\\' -> when {
                                dx == 1 -> add(Beam(x, y, 0, 1))
                                dx == -1 -> add(Beam(x, y, 0, -1))
                                dy == 1 -> add(Beam(x, y, 1, 0))
                                dy == -1 -> add(Beam(x, y, -1, 0))
                            }

                            '/' -> when {
                                dx == 1 -> add(Beam(x, y, 0, -1))
                                dx == -1 -> add(Beam(x, y, 0, 1))
                                dy == 1 -> add(Beam(x, y, -1, 0))
                                dy == -1 -> add(Beam(x, y, 1, 0))
                            }

                            '.' -> add(Beam(x, y, dx, dy))
                        }
                    }
                }
            }
//            val s = CharArea(area.xRange.last + 1, area.yRange.last + 1, '.')
//            energized.forEach { s.set(it, '#') }
//            println("-----------------------")
//            s.show()
        } while (beams.any { it.isAlive(area) })
        return energized.size
    }

    private fun two(input: List<String>): Int {
        return 0
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 46
        one(input) shouldBe 8249
    }

    @Test
    fun testTwo(input: List<String>) {
//        two(sample) shouldBe 51
//        two(input) shouldBe 0
    }
}
