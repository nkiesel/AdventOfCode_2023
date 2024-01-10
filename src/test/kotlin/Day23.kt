import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import kotlin.math.max

class Day23 {
    private val sample = """
        #.#####################
        #.......#########...###
        #######.#########.#.###
        ###.....#.>.>.###.#.###
        ###v#####.#v#.###.#.###
        ###.>...#.#.#.....#...#
        ###v###.#.#.#########.#
        ###...#.#.#.......#...#
        #####.#.#.#######.#.###
        #.....#.#.#.......#...#
        #.#####.#.#.#########v#
        #.#...#...#...###...>.#
        #.#.#v#######v###.###v#
        #...#.>.#...>.>.#.###.#
        #####v#.#.###v#.#.###.#
        #.....#...#...#.#.#...#
        #.#########.###.#.#.###
        #...###...#...#...#.###
        ###.###.#.###v#####v###
        #...#...#.#.>.>.#.>.###
        #.###.###.#.###.#.#v###
        #.....###...###...#...#
        #####################.#
    """.trimIndent().lines()

    private fun one(input: List<String>, slip: Boolean = true): Int {
        val area = CharArea(input)
        val start = Point(area.xRange.first { area[it, 0] == '.' }, 0)
        val finish = Point(area.xRange.first { area[it, area.yRange.last] == '.' }, area.yRange.last)
        val seen = mutableSetOf(start)
        val queue = ArrayDeque(listOf(start to seen))
        var len = 0
        while (queue.isNotEmpty()) {
            val a = queue.removeFirst()
            val p = a.first
            val s = a.second
            if (p == finish) {
                len = max(len, s.size)
            } else {
                val next = (if (slip) {
                    when (area[p]) {
                        '>' -> listOf(p.move(Direction.E))
                        '<' -> listOf(p.move(Direction.W))
                        '^' -> listOf(p.move(Direction.N))
                        'v' -> listOf(p.move(Direction.S))
                        else -> area.neighbors4(p)
                    }
                } else {
                    area.neighbors4(p)
                }).filter { area[it] != '#' && it !in s }
                for (b in next) {
                    val ss = if (next.size == 1) s else s.toMutableSet()
                    ss.add(b)
                    queue.addFirst(b to ss)
                }
            }
        }
        return len - 1
    }

    private fun directNeighbors(p: Point, area: CharArea, neighbors: Set<Point>): Map<Point, Int> {
        val candidates = java.util.ArrayDeque(listOf(IndexedValue(0, p)))
        val seen = mutableSetOf(p)
        return buildMap {
            while (candidates.isNotEmpty()) {
                val n = candidates.removeFirst()
                for (c in area.neighbors4(n.value).filter { area[it] != '#' }) {
                    if (c in neighbors && c != p) {
                        put(c, max(get(c) ?: 0, n.index + 1))
                    } else if (seen.add(c)) {
                        candidates.addLast(IndexedValue(n.index + 1, c))
                    }
                }
            }
        }
    }

    private fun two(input: List<String>): Int {
        val area = CharArea(input)
        val start = Point(area.xRange.first { area[it, 0] == '.' }, 0)
        val finish = Point(area.xRange.first { area[it, area.yRange.last] == '.' }, area.yRange.last)
        val junctions =
            area.tiles().filter { p -> area[p] != '#' && area.neighbors4(p).count { area[it] != '#' } > 2 }
                .toSet() + start + finish
        val graph = junctions.associateWith { directNeighbors(it, area, junctions) }

        val visited = mutableSetOf<Point>()
        var longest = 0

        fun longestPath(from: Point, steps: Int = 0) {
            if (from == finish) {
                longest = max(longest, steps)
            } else {
                visited += from
                graph.getValue(from).filter { it.key !in visited }.forEach { longestPath(it.key, it.value + steps) }
                visited -= from
            }
        }

        longestPath(start)
        return longest
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 94
        one(input) shouldBe 2402
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 154
        two(input) shouldBe 6450
    }
}
