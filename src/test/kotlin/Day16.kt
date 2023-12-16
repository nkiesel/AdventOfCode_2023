import io.kotest.matchers.shouldBe
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

    data class Beam(val x: Int, val y: Int, val dx: Int, val dy: Int)

    private fun one(area: CharArea, start: Beam): Int {
        var beams = listOf(start)
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
        } while (beams.isNotEmpty())
        return energized.size
    }

    private fun two(input: List<String>): Int {
        val area = CharArea(input)
        val mx = area.xRange.last + 1
        val my = area.yRange.last + 1
        return listOf(
            area.yRange.maxOf { y -> one(area, Beam(-1, y, 1, 0)) },
            area.yRange.maxOf { y -> one(area, Beam(mx, y, -1, 0)) },
            area.xRange.maxOf { x -> one(area, Beam(x, -1, 0, 1)) },
            area.xRange.maxOf { x -> one(area, Beam(x, my, 0, -1)) },
        ).max()
    }

    @Test
    fun testOne(input: List<String>) {
        val start = Beam(-1, 0, 1, 0)
        one(CharArea(sample), start) shouldBe 46
        one(CharArea(input), start) shouldBe 8249
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 51
        two(input) shouldBe 8444
    }
}
