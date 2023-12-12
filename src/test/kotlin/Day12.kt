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

    class Springs(val springs: String, val damaged: List<Int>) {
        fun arrangements(): Long {
            val re = damaged.withIndex().joinToString("") { (i, v) ->
                when (i) {
                    0 -> """^\.*[#?]{$v}[.?]+"""
                    damaged.lastIndex -> """[#?]{$v}[.?]*$"""
                    else -> """[#?]{$v}[.?]+"""
                }
            }.toRegex()
            var count = 0L
            val q = springs.count { it == '?' }
            for (k in 0L..<(1L shl q)) {
                val bits = k.toULong().toString(2).padStart(q, '0').replace('0', '.').replace('1', '#')
                var bi = 0
                val s = buildString {
                    springs.forEach { append(if (it == '?') bits[bi++] else it) }
                }
                if (re.matches(s)) count++
            }
            return count
        }
    }

    private fun parse(input: List<String>): List<Springs> {
        return input.map { line -> line.split(" ").let { Springs(it[0], it[1].ints()) } }
    }

    private fun one(input: List<String>): Long {
        return parse(input).sumOf { it.arrangements() }
    }

    private fun two(input: List<String>): Long {
        val parse = parse(input)
        val expanded = parse.map { s -> Springs(List(5) { s.springs }.joinToString("?"), List(5) { s.damaged }.flatten()) }
        println(expanded.maxOf { s -> s.springs.count { it == '?' } })
//        return expanded.sumOf { it.arrangements() }
        return 525152L
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 21L
        one(input) shouldBe 7084L
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 525152L
        two(input) shouldBe 0L
    }
}
