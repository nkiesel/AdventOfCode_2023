import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day03 {
    private val sample = """
        467..114..
        ...*......
        ..35..633.
        ......#...
        617*......
        .....+.58.
        ..592.....
        ......755.
        ...${'$'}.*....
        .664.598..
    """.trimIndent().lines()

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 4361
        one(input) shouldBe 546563
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 467835
        two(input) shouldBe 91031374
    }

    class PartNumber(var value: Int, var active: Boolean = true)

    private fun parse(input: List<String>): Array<Array<PartNumber>> {
        val parts = Array(input.size) { Array(input[0].length) { PartNumber(0, false) } }
        input.forEachIndexed { y, row ->
            Regex("""\d+""").findAll(row).forEach {m ->
                val num = PartNumber(m.value.toInt(), true)
                m.range.forEach { parts[y][it] = num }
            }
        }
        return parts
    }

    private fun one(input: List<String>): Int {
        val parts = parse(input)
        var total = 0
        input.forEachIndexed { y, row ->
            Regex("""[^\d.]""").findAll(row).forEach { m ->
                val x = m.range.first
                val nums = parts.neighbors8(x, y).map { (px, py) -> parts[py][px] }.filter { it.active }.toSet()
                total += nums.sumOf { it.value }
                nums.forEach { it.active = false }
            }
        }
        return total
    }

    private fun two(input: List<String>): Int {
        val parts = parse(input)
        var total = 0
        input.forEachIndexed { y, row ->
            Regex("""\*""").findAll(row).forEach { m ->
                val x = m.range.first
                val nums = parts.neighbors8(x, y).map { (px, py) -> parts[py][px] }.filter { it.active }.toSet()
                if (nums.size == 2) {
                    total += nums.map { it.value }.reduce(Int::times)
                    // do not deactivate part numbers, because `...12*34*56...` looks like a valid input where
                    // `34` is used for 2 gears.
                }
            }
        }
        return total
    }
}

/*
Another typical AoC puzzle: parse and process a map.  I converted all the digit sequences into numbers and stored the
number in every cell covered by a digits. The only issue was that this could then end up using the same number multiple
times.  I therefore re-used a trick from last year: instead of storing Ints, store objects containing an Int. That way,
updating the value contained in the object automatically updates all the copies.

Another re-use was the "neighbors8" (from Util), which is a simple method that returns the coordinates of all 8 neighbors
of a cell in a 2-dimensional array, honoring the array dimensions.

After creating the solution in Typescript I realized that assuming part numbers to never be 0 was not guaranteed, so
added an "active" flag to the MutableInt class.
*/
