import Direction.*
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

    data class Beam(val p: Point, val d: Direction)

    private fun energized(area: CharArea, start: Beam): Int {
        var beams = setOf(start)
        val energized = mutableSetOf<Point>()
        val seen = mutableSetOf<Beam>()
        do {
            beams = buildSet {
                beams.filter { seen.add(it) }.forEach { beam ->
                    val d = beam.d
                    val p = beam.p.move(d)
                    if (p in area) {
                        energized += p
                        when (area[p]) {
                            '\\' -> when (d) {
                                N -> listOf(W)
                                S -> listOf(E)
                                E -> listOf(S)
                                W -> listOf(N)
                            }

                            '/' -> when (d) {
                                N -> listOf(E)
                                S -> listOf(W)
                                E -> listOf(N)
                                W -> listOf(S)
                            }

                            '-' -> if (d == N || d == S) listOf(E, W) else listOf(d)
                            '|' -> if (d == E || d == W) listOf(N, S) else listOf(d)
                            else -> listOf(d)
                        }.forEach { add(Beam(p, it)) }
                    }
                }
            }
        } while (beams.isNotEmpty())
        return energized.size
    }

    private fun one(input: List<String>): Int {
        return energized(CharArea(input), Beam(Point(-1, 0), E))
    }

    private fun two(input: List<String>): Int {
        val area = CharArea(input)
        val mx = area.xRange.last + 1
        val my = area.yRange.last + 1
        return listOf(
            area.yRange.map { y -> Beam(Point(-1, y), E) },
            area.yRange.map { y -> Beam(Point(mx, y), W) },
            area.xRange.map { x -> Beam(Point(x, -1), S) },
            area.xRange.map { x -> Beam(Point(x, my), N) },
        ).flatten().maxOf { energized(area, it) }
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
Again pretty simple from an algorithmic point of view.  One question I had was: what happens to beams that hit a
boundary of the area? Then - apart from one +/- error - the only other issue was that I initially did not add
the "seen" cache, which made the code run forever.
*/
