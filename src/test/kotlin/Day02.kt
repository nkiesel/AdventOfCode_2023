import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day02 {
    private val sample = """
        Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
        Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
        Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
        Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
        Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green
    """.trimIndent().lines()

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 8
        one(input) shouldBe 2377
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 2286
        two(input) shouldBe 71220
    }

    class Reveal(val red: Int, val green: Int, val blue: Int)

    class Game(val id: Int, val reveals: List<Reveal>)

    private fun parse(input: List<String>): List<Game> {
        val red = Regex("""(\d+) red""")
        val green = Regex("""(\d+) green""")
        val blue = Regex("""(\d+) blue""")
        fun cubes(line: String, re: Regex) = re.find(line)?.groupValues?.get(1)?.toInt() ?: 0

        return input.map { line ->
            val record = line.split(";", ":").toMutableList()
            val id = record.removeFirst().substringAfter(" ").toInt()
            val reveals = record.map {
                Reveal(red = cubes(it, red), green = cubes(it, green), blue = cubes(it, blue))
            }
            Game(id, reveals)
        }
    }

    private fun one(input: List<String>): Int {
        return parse(input).filter { it.reveals.all { it.red <= 12 && it.green <= 13 && it.blue <= 14 } }.sumOf { it.id }
    }

    private fun two(input: List<String>): Int {
        return parse(input).sumOf { with(it.reveals) { maxOf { it.red } * maxOf { it.green } * maxOf { it.blue } } }
    }
}

/*
This was pretty simple; I got it right on the first attempt.  The only a bit complicated part was the parsing;
there is very likely a more efficient way to do that. But finished coding in 30 minutes and tests run fast, so
no need to optimize.
*/
