import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import kotlin.math.min

class Day13 {
    private val sample = """
        #.##..##.
        ..#.##.#.
        ##......#
        ##......#
        ..#.##.#.
        ..##..##.
        #.#.##.#.

        #...##..#
        #....#..#
        ..##..###
        #####.##.
        #####.##.
        ..##..###
        #....#..#
    """.trimIndent().lines()

    private fun parse(input: List<String>): List<CharArea> {
        return input.chunkedBy { it.isEmpty() }.map { CharArea(it) }
    }

    private fun vertical(area: CharArea, exclude: Int = -1): Int {
        val mx = area.xRange.last + 1
        for (x in 1..<mx) {
            if (x == exclude) continue
            val w = min(x, mx - x)
            if (area.yRange.all { y -> area.substring(y, x - w, x).reversed() == area.substring(y, x, x + w) }) {
                return x
            }
        }
        return 0
    }

    private fun one(input: List<String>): Int {
        return parse(input).sumOf { area -> vertical(area) + vertical(area.rotated()) * 100 }
    }

    private fun smudged(area: CharArea): Int {
        val rotated = area.rotated()
        val v = vertical(area)
        val h = vertical(rotated)
        area.tiles().forEach { (x, y) ->
            val c = area.get(x, y)
            val o = if (c == '.') '#' else '.'
            area.set(x, y, o)
            rotated.set(y, x, o)
            val v1 = vertical(area, v)
            val h1 = vertical(rotated, h)
            if (v1 != 0 || h1 != 0) {
                return v1 + h1 * 100
            }
            area.set(x, y, c)
            rotated.set(y, x, c)
        }
        error("no smudge")
    }

    private fun two(input: List<String>): Int {
        return parse(input).sumOf { smudged(it) }
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 405
        one(input) shouldBe 34993
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 400
        two(input) shouldBe 29341
    }
}

/*
Phew, this was pretty straight-forward.  I initially tried to not use my CharArea because of the substring approach,
but then for part 2 I had to modify the area, which is much easier with CharArea.  I therefore added a "substring"
method to CharArea (and a "rotated" method).  Turns out that every area had exactly one horizontal or one vertical
mirror line.

I struggled with part 2 because I could not find smudges for some areas.  I then realized that my mistake was that
smudged areas might have more than one horizontal or vertical mirror line (the original one and the smudged one). I
had originally coded "vertical" to simply return the first mirror line, and then ignored the returned value if it
was identical to the original one.  The solution thus was to pass the excluded mirror index as an additional
parameter to "vertical", and then simply skip over that.
*/
