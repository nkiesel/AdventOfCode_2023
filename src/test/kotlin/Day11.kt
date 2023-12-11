import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.lang.Integer.max
import kotlin.math.min

class Day11 {
    private val sample = """
        ...#......
        .......#..
        #.........
        ..........
        ......#...
        .#........
        .........#
        ..........
        .......#..
        #...#.....
    """.trimIndent().lines()

    private fun one(input: List<String>): Int {
        val base = CharArea(input)
        val emptyRows = base.rows().withIndex().filter { l -> l.value.all { it == '.' } }.map { it.index }.toList()
        val emptyCols = base.columns().withIndex().filter { l -> l.value.all { it == '.' } }.map { it.index }.toList()
        val area = CharArea(base.xRange.last + emptyCols.size + 1, base.yRange.last + emptyRows.size + 1, '.')
        base.tiles().forEach { (x, y) ->
            area.set(x + emptyCols.count { it < x }, y + emptyRows.count { it < y }, base.get(x, y))
        }
        val galaxies = area.filter { area.get(it) == '#' }
        var sum = 0
        galaxies.forEachIndexed { index, g1 ->
            galaxies.drop(index + 1).forEach { g2 ->
                val d = manhattanDistance(g1.first, g1.second, g2.first, g2.second)
                sum += d
            }
        }
        return sum
    }

    private fun two(input: List<String>, factor: Long): Long {
        val area = CharArea(input)
        val emptyRows = area.rows().withIndex().filter { l -> l.value.all { it == '.' } }.map { it.index }.toList()
        val emptyCols = area.columns().withIndex().filter { l -> l.value.all { it == '.' } }.map { it.index }.toList()
        val galaxies = area.filter { area.get(it) == '#' }.toList()

        return galaxies
            .flatMapIndexed { index, g -> galaxies.drop(index).map { g to it } }
            .sumOf { (g1, g2) ->
                val rx = min(g1.first, g2.first)..max(g1.first, g2.first)
                val ry = min(g1.second, g2.second)..max(g1.second, g2.second)
                val dx = (factor - 1L) * emptyCols.count { it in rx } + rx.last - rx.first
                val dy = (factor - 1L) * emptyRows.count { it in ry } + ry.last - ry.first
                dx + dy
            }
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 374
        one(input) shouldBe 9734203
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample, 2) shouldBe 374L
        two(sample, 10) shouldBe 1030L
        two(sample, 100) shouldBe 8410L
        two(input, 2) shouldBe 9734203L
        two(input, 1000000) shouldBe 568914596391L
    }
}

/*
This was fun and not as complicated as yesterday.  As usual, part 2 can be used to also solve part 1. However, this
time my part 2 solution (which handles the expansion using a formula) was actually more efficient for part 1 than
the original part 1. Oh, and I initially used Int instead of Long for part 2.  I would love for Kotlin to throw an
exception for Int or Long overflows.
*/
