import Direction.*
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.util.*

class Day17 {
    private val sample = """
        2413432311323
        3215453535623
        3255245654254
        3446585845452
        4546657867536
        1438598798454
        4457876987766
        3637877979653
        4654967986887
        4564679986453
        1224686865563
        2546548887735
        4322674655533
    """.trimIndent().lines()

    private val sample2 = """
        111111111111
        999999999991
        999999999991
        999999999991
        999999999991
    """.trimIndent().lines()

    data class Step(val p: Point, val from: Direction, val c: Int, val loss: Int) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Step) return false

            if (p != other.p) return false
            if (from != other.from) return false
            if (c != other.c) return false

            return true
        }

        override fun hashCode(): Int {
            var result = p.hashCode()
            result = 31 * result + from.hashCode()
            result = 31 * result + c
            return result
        }
    }

    private fun one(input: List<String>): Int {
        val area = CharArea(input)
        val start = Point(0, 0)
        val target = Point(area.xRange.last, area.yRange.last)
        val seen = mutableSetOf<Step>()
        val queue = PriorityQueue(compareBy<Step> { it.loss })
        queue.add(Step(start, W, 0, 0))
        val candidates = mutableListOf<Int>()
        while (queue.isNotEmpty()) {
            val s = queue.remove()
            if (s.p == target) {
                return s.loss
            }
            if (!seen.add(s)) continue

            val possibleMove = when (s.from) {
                N -> listOf(S, E, W)
                S -> listOf(N, E, W)
                E -> listOf(W, N, S)
                W -> listOf(E, N, S)
            }

            for (move in possibleMove.withIndex()) {
                val c = if (move.index == 0) s.c + 1 else 1
                if (c <= 3) {
                    val point = s.p.move(move.value)
                    if (point in area) {
                        val loss = s.loss + area.get(point).digitToInt()
                        val from = when (move.value) {
                            N -> S
                            S -> N
                            E -> W
                            W -> E
                        }
                        queue.add(Step(point, from, c, loss))
                    }
                }
            }
        }
        error("")
    }

    private fun two(input: List<String>): Int {
        val area = CharArea(input)
        val start = Point(0, 0)
        val target = Point(area.xRange.last, area.yRange.last)
        val seen = mutableSetOf<Step>()
        val queue = PriorityQueue(compareBy<Step> { it.loss })
        queue.add(Step(start, W, 0, 0))
        val candidates = mutableListOf<Int>()
        while (queue.isNotEmpty()) {
            val s = queue.remove()
            if (s.p == target && s.c >= 4) {
                return s.loss
            }
            if (!seen.add(s)) continue

            val possibleMove = when (s.from) {
                N -> listOf(S, E, W)
                S -> listOf(N, E, W)
                E -> listOf(W, N, S)
                W -> listOf(E, N, S)
            }

            for (move in possibleMove.withIndex()) {
                val c = if (move.index == 0) s.c + 1 else 1
                if (c <= 10 && (move.index == 0 || s.c >= 4)) {
                    val point = s.p.move(move.value)
                    if (point in area) {
                        val loss = s.loss + area.get(point).digitToInt()
                        val from = when (move.value) {
                            N -> S
                            S -> N
                            E -> W
                            W -> E
                        }
                        queue.add(Step(point, from, c, loss))
                    }
                }
            }
        }
        error("")
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 102
        one(input) shouldBe 1065
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 94
        two(sample2) shouldBe 71
        two(input) shouldBe 1256
    }
}
