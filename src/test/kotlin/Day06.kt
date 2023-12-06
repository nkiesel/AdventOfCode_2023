import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day06 {
    private val sample = """
        Time:      7  15   30
        Distance:  9  40  200
    """.trimIndent().lines()

    data class Race(val time: Long, val distance: Long) {
        fun better() = (1..<time).count { (time - it) * it > distance }
    }

    private fun parse1(input: List<String>): List<Race> {
        return input.map { it.longs() }.let { (t, d) -> t.zip(d) }.map { Race(it.first, it.second) }
    }

    private fun parse2(input: List<String>): Race {
        return input.map { it.ints().joinToString("").toLong() }.let { (t, d) -> Race(t, d) }
    }

    private fun one(input: List<String>): Int {
        return parse1(input).map { it.better() }.reduce(Int::times)
    }

    private fun two(input: List<String>): Int {
        return parse2(input).better()
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 288
        one(input) shouldBe 281600
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 71503
        two(input) shouldBe 33875953
    }
}

/*
So much simpler than Day 5!!! It had the usual Int/Long issue, but otherwise brute force did the trick.  I guess that
`better()` could be optimized by using binary search to find the beginning and the end of the time range where the
result is better.  And very likely, there is even a formula to compute the boundaries instead of searching for them.
However, this is running so fast that all of this does not matter.
*/
