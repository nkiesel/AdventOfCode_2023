import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import javax.security.auth.callback.CallbackHandler

class Day14 {
    private val sample = """
        O....#....
        O.OO#....#
        .....##...
        OO.#O....O
        .O.....O#.
        O.#..O.#.#
        ..O..#O..O
        .......O..
        #....###..
        #OO..#....
    """.trimIndent().lines()

    private fun one(input: List<String>): Int {
        val area = CharArea(input)
        for (x in area.xRange) {
            var nextY = ArrayDeque<Int>()
            for (y in area.yRange) {
                when (area.get(x, y)) {
                    '.' -> nextY.addLast(y)
                    '#' -> nextY.clear()
                    'O' -> if (nextY.isNotEmpty()) {
                        area.set(x, nextY.removeFirst(), 'O')
                        area.set(x, y, '.')
                        nextY.addLast(y)
                    }
                }
            }
        }
        val my = area.yRange.last + 1
        return area.tiles().sumOf { (x, y) -> if (area.get(x,y) == 'O') my - y else 0 }
    }

    private fun two(input: List<String>): Int {
        return 0
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 136
        one(input) shouldBe 0
    }

    @Test
    fun testTwo(input: List<String>) {
//        two(sample) shouldBe 0
//        two(input) shouldBe 0
    }
}
