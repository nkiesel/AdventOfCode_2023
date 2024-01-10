import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

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

    private fun northSouth(area: CharArea, north: Boolean) {
        val yRange = if (north) area.yRange else area.yRange.reversed()
        for (x in area.xRange) {
            val next = ArrayDeque<Int>()
            for (y in yRange) {
                when (area[x, y]) {
                    '.' -> next.addLast(y)
                    '#' -> next.clear()
                    'O' -> if (next.isNotEmpty()) {
                        area[x, next.removeFirst()] = 'O'
                        area[x, y] = '.'
                        next.addLast(y)
                    }
                }
            }
        }
    }

    private fun westEast(area: CharArea, west: Boolean) {
        val xRange = if (west) area.xRange else area.xRange.reversed()
        for (y in area.yRange) {
            val next = ArrayDeque<Int>()
            for (x in xRange) {
                when (area[x, y]) {
                    '.' -> next.addLast(x)
                    '#' -> next.clear()
                    'O' -> if (next.isNotEmpty()) {
                        area[next.removeFirst(), y] = 'O'
                        area[x, y] = '.'
                        next.addLast(x)
                    }
                }
            }
        }
    }

    private fun one(input: List<String>): Int {
        val area = CharArea(input)
        northSouth(area, true)
        val maxY = area.yRange.last + 1
        return area.tiles().sumOf { (x, y) -> if (area[x, y] == 'O') maxY - y else 0 }
    }

    private fun two(input: List<String>): Int {
        val area = CharArea(input)
        var cycles = 1000000000
        var cycle = 0
        var detect = true
        val cache = mutableMapOf<Int, Int>()
        while (cycles > 0) {
            northSouth(area, true)
            westEast(area, true)
            northSouth(area, false)
            westEast(area, false)
            cycles--
            cycle++
            if (detect) {
                val fingerprint = area.hashCode()
                val prev = cache[fingerprint]
                if (prev == null) {
                    cache[fingerprint] = cycle
                } else {
                    cycles -= cycles / (cycle - prev) * (cycle - prev)
                    detect = false
                }
            }
        }
        val maxY = area.yRange.last + 1
        return area.tiles().sumOf { (x, y) -> if (area[x, y] == 'O') maxY - y else 0 }
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 136
        one(input) shouldBe 107951
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 64
        two(input) shouldBe 95736
    }
}

/*
Nice one!  For part 1, I wrote what is now `northSouth` without the "south" option.  For part 2, I first thought to
rotate the area and then use the same "north" method, but then I thought that this would be too expensive, so instead
thought about what must be changed to tilt to south. Turned out to just be reversing one range! Then `eastWest` was
very simple. Given the amount of cycles, it was obvious that we must memorize.  I first thought of optimizing this by
converting the positions of the 'O' into a fingerprint, but as a start I simply used the whole area converted to a string.
The code is running fast enough, so I will not optimize further to keep it simple.

Update: changed the cache to use the hashCode of the CharArea as key. No big performance difference, but should save
some memory.
*/
