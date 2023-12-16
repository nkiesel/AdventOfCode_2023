import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day12 {
    private val sample = """
        ???.### 1,1,3
        .??..??...?##. 1,1,3
        ?#?#?#?#?#?#?#? 1,3,1,6
        ????.#...#... 4,1,1
        ????.######..#####. 1,6,5
        ?###???????? 3,2,1
    """.trimIndent().lines()

    private fun parse(input: List<String>): List<Pair<String, List<Int>>> {
        return input.map { line -> line.split(" ").let { it[0] to it[1].ints() } }
    }

    private val cache: MutableMap<Pair<String, List<Int>>, Long> = mutableMapOf()

    private fun count(springs: String, damage: List<Int>): Long = cache.getOrPut(springs to damage) {
        if (springs.isEmpty()) return@getOrPut if (damage.isEmpty()) 1L else 0L

        val thisSpring = springs.first()
        val remainingSprings = springs.drop(1)

        return@getOrPut when (thisSpring) {
            '.' -> count(remainingSprings.dropWhile { it == '.' }, damage)

            '?' -> {
                count(remainingSprings, damage) + count("#$remainingSprings", damage)
            }

            '#' -> when {
                damage.isEmpty() -> 0L
                else -> {
                    val thisDamage = damage.first()
                    val remainingDamage = damage.drop(1)
                    if (thisDamage <= springs.length && springs.take(thisDamage).none { it == '.' }) {
                        when {
                            thisDamage == springs.length -> if (remainingDamage.isEmpty()) 1L else 0L
                            springs[thisDamage] == '#' -> 0
                            else -> count(remainingSprings.drop(thisDamage), remainingDamage)
                        }
                    } else 0L
                }
            }

            else -> error("Invalid springs: $springs")
        }
    }

    private fun three(input: List<String>, repetitions: Int = 1): Long {
        cache.clear()
        return parse(input).map { (s, d) ->
            Pair(
                List(repetitions) { s }.joinToString("?"),
                List(repetitions) { d }.flatten(),
            )
        }.sumOf { count(it.first, it.second) }
    }

    @Test
    fun testOne(input: List<String>) {
        three(sample) shouldBe 21L
        three(input) shouldBe 7084L
    }

    @Test
    fun testTwo(input: List<String>) {
        three(sample, 5) shouldBe 525152L
        three(input, 5) shouldBe 8414003326821L
    }
}

/*
Puuuh, this was the biggest challenge so far. I only solved part 2 after some days, and only by cheating and looking
at other solutions.  I knew I had to cache, but could not solve it.  One big issue was that the cache was not filled
correctly because I used "return" instead of "return@getOrPut", which rus side-stepped the "or put" part of "getOrPut".

I then abandoned my original part 1 solution which constructed a regex and mapped all possible combinations of '.' or
'#' for '?'.  This required "2^{ count of '?' }" tests, which worked ok for part 1 with at most 19 '?', but 2^99 is
way too large.
*/
