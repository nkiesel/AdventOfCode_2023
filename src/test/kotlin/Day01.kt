import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day01 {
    private val sample1 = """
        1abc2
        pqr3stu8vwx
        a1b2c3d4e5f
        treb7uchet
    """.trimIndent().lines()

    private val sample2 = """
        two1nine
        eightwothree
        abcone2threexyz
        xtwone3four
        4nineeightseven2
        zoneight234
        7pqrstsixteen
    """.trimIndent().lines()

    @Test
    fun testOne(input: List<String>) {
        one(sample1) shouldBe 142
        one(input) shouldBe 53080
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample2) shouldBe 281
        two(input) shouldBe 53268
    }

    @Test
    fun testThree(input: List<String>) {
        three(sample1, false) shouldBe 142
        three(sample2, true) shouldBe 281
        three(input, false) shouldBe 53080
        three(input, true) shouldBe 53268
    }

    private fun one(input: List<String>): Int {
        return input.sumOf { line -> line.mapNotNull { it.digitToIntOrNull() }.let { it.first() * 10 + it.last() } }
    }

    private fun two(input: List<String>): Int {
        return input.sumOf { line -> toNumList(line).let { it.first() * 10 + it.last() } }
    }

    private fun toNumList(line: String): List<Int> {
        val map = mapOf(
            "one" to 1,
            "two" to 2,
            "three" to 3,
            "four" to 4,
            "five" to 5,
            "six" to 6,
            "seven" to 7,
            "eight" to 8,
            "nine" to 9
        )
        val pattern = map.keys.joinToString("|", prefix = "^(", postfix = ")").toRegex()

        return buildList {
            for (i in line.indices) {
                val d = line[i].digitToIntOrNull()
                if (d != null) {
                    add(d)
                } else {
                    pattern.find(line.substring(i))?.let { add(map[it.value]!!) }
                }
            }
        }
    }

    private fun three(input: List<String>, withLetters: Boolean): Int {
        val map = (1..9).associateBy { it.toString() }.toMutableMap()
        if (withLetters) map += mapOf(
            "one" to 1, "two" to 2, "three" to 3, "four" to 4, "five" to 5,
            "six" to 6, "seven" to 7, "eight" to 8, "nine" to 9
        )
        fun calibration(line: String): Int {
            val first = map[line.findAnyOf(map.keys)!!.second]!!
            val last = map[line.findLastAnyOf(map.keys)!!.second]!!
            return first * 10 + last
        }
        return input.sumOf { calibration(it) }
    }
}

/**
 * This was a bit more complicated than I expected for day 1.  The one a bit weird point about part 2 is that a line
 * "3oneight" should be converted to [3, 1, 8] although the 'e' is then used by 2 spelled digits.  I initially moved the
 * index beyond the mapped input part (which resulted in [3, 1]), but that created the wrong answer.
 *
 * After thinking a bit more about that, I now realize that my "convert to number list" was the cause of this issue: the
 * puzzle explicitly asked for the last digit, and thus the 'e' does belong to the "eight" and it does not matter that
 * it could also belong to the one.  I guess the only issue would be an input of "oneeight": should that be a [1, 8]?
 *
 * Another surprising point was that `Regex.find(input, index)` does not work with the pattern starting with an '^'. Thus,
 * I had to switch to `Regex.find(input.substring(index))`.
 *
 * I did realize that I could have optimized the solution a bit more by not computing the whole digit list, but I
 * thought we might need that for part 2, and then did not want to change it.
 */
