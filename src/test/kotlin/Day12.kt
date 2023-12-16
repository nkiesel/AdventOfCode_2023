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

    private fun count(springs: String, damages: List<Int>): Long = cache.getOrPut(springs to damages) {
        val spring = springs.firstOrNull()
        val damage = damages.firstOrNull()
        if (spring == null) {
            if (damage == null) 1L else 0L
        } else {
            val remainingSprings = springs.drop(1)
            when {
                spring == '.' -> count(remainingSprings.dropWhile { it == '.' }, damages)
                spring == '?' -> count(remainingSprings, damages) + count("#$remainingSprings", damages)
                damage == null -> 0L
                else -> {
                    val remainingDamage = damages.drop(1)
                    when {
                        damage > springs.length || springs.take(damage).any { it == '.' } -> 0L
                        damage == springs.length -> if (remainingDamage.isEmpty()) 1L else 0L
                        springs[damage] == '#' -> 0L
                        else -> count(remainingSprings.drop(damage), remainingDamage)
                    }
                }
            }
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

Update: avoided all "return" in the "orPut" part, which simplified the code a bit further.
*/
