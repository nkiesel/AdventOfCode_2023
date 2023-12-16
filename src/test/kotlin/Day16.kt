import Day16.Direction.*
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

    enum class Direction { N, S, E, W }

    data class Beam(val x: Int, val y: Int, val d: Direction)

    private fun energized(area: CharArea, start: Beam): Int {
        var beams = setOf(start)
        val energized = mutableSetOf<Pair<Int, Int>>()
        val seen = mutableSetOf<Beam>()
        do {
            beams = buildSet {
                beams.filter { seen.add(it) }.forEach { beam ->
                    val d = beam.d
                    val x = beam.x + when (d) {
                        E -> 1
                        W -> -1
                        else -> 0
                    }
                    val y = beam.y + when (d) {
                        N -> -1
                        S -> 1
                        else -> 0
                    }
                    if (x in area.xRange && y in area.yRange) {
                        energized += Pair(x, y)
                        when (area.get(x, y)) {
                            '-' -> if (d == N || d == S) {
                                add(Beam(x, y, E))
                                add(Beam(x, y, W))
                            } else {
                                add(Beam(x, y, d))
                            }

                            '|' -> if (d == E || d == W) {
                                add(Beam(x, y, N))
                                add(Beam(x, y, S))
                            } else {
                                add(Beam(x, y, d))
                            }

                            '\\' -> when (d) {
                                E -> add(Beam(x, y, S))
                                W -> add(Beam(x, y, N))
                                S -> add(Beam(x, y, E))
                                N -> add(Beam(x, y, W))
                            }

                            '/' -> when (d) {
                                E -> add(Beam(x, y, N))
                                W -> add(Beam(x, y, S))
                                S -> add(Beam(x, y, W))
                                N -> add(Beam(x, y, E))
                            }

                            '.' -> add(Beam(x, y, d))
                        }
                    }
                }
            }
        } while (beams.isNotEmpty())
        return energized.size
    }

    private fun one(input: List<String>): Int {
        return energized(CharArea(input), Beam(-1, 0, E))
    }

    private fun two(input: List<String>): Int {
        val area = CharArea(input)
        val mx = area.xRange.last + 1
        val my = area.yRange.last + 1
        return listOf(
            area.yRange.maxOf { y -> energized(area, Beam(-1, y, E)) },
            area.yRange.maxOf { y -> energized(area, Beam(mx, y, W)) },
            area.xRange.maxOf { x -> energized(area, Beam(x, -1, S)) },
            area.xRange.maxOf { x -> energized(area, Beam(x, my, N)) },
        ).max()
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 46
        one(input) shouldBe 8249
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 51
        two(input) shouldBe 8444
    }
}

/*
Again pretty simple from an algorithmic point of view.  One question I had was what happens to beams that hit a
boundary of the area. Then - apart from one +/- error - the only other issue was that I initially did not add
the "seen" cache, which made the code run forever.
*/
