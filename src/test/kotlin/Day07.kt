import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day07 {
    private val sample = """
        32T3K 765
        T55J5 684
        KK677 28
        KTJJT 220
        QQQJA 483
    """.trimIndent().lines()

    enum class Type {
        HIGH_CARD, ONE_PAIR, TWO_PAIRS, THREE_OF_A_KIND, FULL_HOUSE, FOUR_OF_A_KIND, FIVE_OF_A_KIND
    }

    class Hand(private val hand: String, val bid: Int, private val joker: Boolean) : Comparable<Hand> {
        private val grouped =
            (if (joker) hand.filterNot { it == 'J' } else hand).groupingBy { it }.eachCount().values.sorted().reversed()
                .joinToString("")
        private val jokers = if (joker) hand.count { it == 'J' } else 0
        private val type = when (jokers) {
            5, 4 -> Type.FIVE_OF_A_KIND
            3 -> when (grouped) {
                "2" -> Type.FIVE_OF_A_KIND
                else -> Type.FOUR_OF_A_KIND
            }

            2 -> when (grouped) {
                "3" -> Type.FIVE_OF_A_KIND
                "21" -> Type.FOUR_OF_A_KIND
                else -> Type.THREE_OF_A_KIND
            }

            1 -> when (grouped) {
                "4" -> Type.FIVE_OF_A_KIND
                "31" -> Type.FOUR_OF_A_KIND
                "22" -> Type.FULL_HOUSE
                "211" -> Type.THREE_OF_A_KIND
                else -> Type.ONE_PAIR
            }

            else -> when (grouped) {
                "5" -> Type.FIVE_OF_A_KIND
                "41" -> Type.FOUR_OF_A_KIND
                "32" -> Type.FULL_HOUSE
                "311" -> Type.THREE_OF_A_KIND
                "221" -> Type.TWO_PAIRS
                "2111" -> Type.ONE_PAIR
                else -> Type.HIGH_CARD
            }
        }

        override fun compareTo(other: Hand): Int {
            val c = type compareTo other.type
            if (c != 0) return c
            val order = if (joker) "AKQT98765432J" else "AKQJT98765432"
            return hand.withIndex().firstNotNullOfOrNull { (i, h) ->
                // comparing in reversed order because order constant is descending
                (order.indexOf(other.hand[i]) compareTo order.indexOf(h)).takeIf { it != 0 }
            } ?: 0
        }
    }

    private fun parse(input: List<String>, joker: Boolean): List<Hand> {
        return input.map { line -> line.split(" ").let { Hand(it[0], it[1].toInt(), joker) } }
    }

    private fun one(input: List<String>): Int {
        return parse(input, false).sorted().mapIndexed { index, hand -> (index + 1) * hand.bid }.sum()
    }

    private fun two(input: List<String>): Int {
        return parse(input, true).sorted().mapIndexed { index, hand -> (index + 1) * hand.bid }.sum()
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 6440
        one(input) shouldBe 249204891
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 5905
        two(input) shouldBe 249666369
    }
}

/*
This was fun!  I first thought of converting the hands in some way to a number, but then decided to directly implement
the rules for the types.  This initially ended up with the "joker == false" version (i.e. just the "else" of the outer
"when").  For the jokers, I started thinking how a number of jokers affect that rule, and given that there are only 5
cases, I just open-coded them.  The only issue I had when then re-implementing part 1 using part 2 with "joker == false"
was that I first forgot to adjust the order constant based on the joker flag.
*/
