import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day09 {
    private val sample = """
        0 3 6 9 12 15
        1 3 6 10 15 21
        10 13 16 21 30 45
    """.trimIndent().lines()

    private fun predictNextValue(start: List<Int>): Int {
        var row = start
        var nextValue = 0
        while (row.any { it != 0 }) {
            nextValue += row.last()
            row = row.windowed(2).map { (a, b) -> b - a }
        }
        return nextValue
    }

    private fun one(input: List<String>) = input.map { it.ints() }.sumOf { predictNextValue(it) }

    private fun two(input: List<String>) = input.map { it.ints().reversed() }.sumOf { predictNextValue(it) }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 114
        one(input) shouldBe 1939607039
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 2
        two(input) shouldBe 1041
    }
}

/*
Again, pretty simple.  My only issue was that my String.ints() method did not handle negative numbers.  I then
first implemented part 2 keeping all the initial values in a list and using foldRight on it. But then I realized
that part 2 is exactly the same as part 1 if I simply reverse the start line! I also first implemented it using Long
instead of Int because I assumed that part 2 would overflow Ints, but that was not the case.
*/
