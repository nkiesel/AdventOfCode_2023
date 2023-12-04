import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day04 {
    private val sample = """
        Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
        Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
        Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
        Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
        Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
        Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11
    """.trimIndent().lines()

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 13
        one(input) shouldBe 21959
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 30
        two(input) shouldBe 5132675
    }

    class Card(val wins: Int, var count: Int = 1)

    private fun parse(input: List<String>): List<Card> {
        fun String.ints() = Regex("""\d+""").findAll(this).map { it.value.toInt() }.toSet()
        return input.map { line ->
            val (winning, having) = Regex("""Card +\d+: (.+) \| (.+)""").matchEntire(line)!!.destructured
            Card((winning.ints() intersect having.ints()).size)
        }
    }

    private fun one(input: List<String>): Int {
        return parse(input).sumOf { 1.shl(it.wins - 1) }
    }

    private fun two(input: List<String>): Int {
        val cards = parse(input)
        cards.forEachIndexed { index, card ->
            (1..card.wins).forEach { cards[index + it].count += card.count }
        }
        return cards.sumOf { it.count }
    }
}

/*
Again, pretty simple; I got it right in the 2nd attempt (initially wrongly incremented the counts by wins instead of
counts). Only question was whether we would get into "Int overruns Int.MAX_VALUE" range.
 */
