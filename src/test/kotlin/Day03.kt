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

    class MutableInt(var value: Int)

    private fun parse(input: List<String>): Array<Array<MutableInt>> {
        val parts = Array(input.size) { Array(input[0].length) { MutableInt(0) } }
        input.forEachIndexed { y, row ->
            Regex("""\d+""").findAll(row).forEach {m ->
                val num = MutableInt(m.value.toInt())
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
                val nums = parts.neighbors8(x, y).map { (px, py) -> parts[py][px] }.filter { it.value != 0 }.toSet()
                total += nums.sumOf { it.value }
                nums.forEach { it.value = 0 }
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
                val nums = parts.neighbors8(x, y).map { (px, py) -> parts[py][px] }.filter { it.value != 0 }.toSet()
                if (nums.size == 2) {
                    total += nums.map { it.value }.reduce(Int::times)
                    nums.forEach { it.value = 0 }
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
*/
