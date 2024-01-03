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

    data class State(val p: Point, val from: Direction, val c: Int)

    data class Step(val state: State, val loss: Int) : Comparable<Step> {
        override fun compareTo(other: Step) = loss - other.loss
    }

    private fun one(input: List<String>): Int {
        val area = CharArea(input)
        val start = Point(0, 0)
        val target = Point(area.xRange.last, area.yRange.last)
        val seen = mutableSetOf<State>()
        val queue = PriorityQueue<Step>()
        queue.add(Step(State(start, W, 0), 0))
        queue.add(Step(State(start, N, 0), 0))
        while (queue.isNotEmpty()) {
            val s = queue.remove()
            if (s.state.p == target) {
                return s.loss
            }

            if (!seen.add(s.state)) continue

            val possibleMove = when (s.state.from) {
                N -> listOf(S, E, W)
                S -> listOf(N, E, W)
                E -> listOf(W, N, S)
                W -> listOf(E, N, S)
            }

            for (move in possibleMove.withIndex()) {
                val c = if (move.index == 0) s.state.c + 1 else 1
                if (c <= 3) {
                    val point = s.state.p.move(move.value)
                    if (point in area) {
                        val loss = s.loss + area.get(point).digitToInt()
                        val from = when (move.value) {
                            N -> S
                            S -> N
                            E -> W
                            W -> E
                        }
                        queue.add(Step(State(point, from, c), loss))
                    }
                }
            }
        }
        error("")
    }

    private fun two(input: List<String>): Int {
        return three(input, 4, 10)
    }

    private fun three(input: List<String>, minSameDirection: Int, maxSameDirection: Int): Int {
        val area = CharArea(input)
        val start = Point(0, 0)
        val target = Point(area.xRange.last, area.yRange.last)
        val seen = mutableSetOf<State>()
        val queue = PriorityQueue(listOf(W, N).map { Step(State(start, it, 0), 0) })
        while (queue.isNotEmpty()) {
            val (state, loss) = queue.remove()
            if (state.p == target && state.c >= minSameDirection) {
                return loss
            }
            if (!seen.add(state)) continue

            val possibleMove = when (state.from) {
                // This must have the "straight" move as the first item for the `move.index == 0` tests below to work
                N -> listOf(S, E, W)
                S -> listOf(N, E, W)
                E -> listOf(W, N, S)
                W -> listOf(E, N, S)
            }

            for (move in possibleMove.withIndex()) {
                val c = if (move.index == 0) state.c + 1 else 1
                if (c <= maxSameDirection && (move.index == 0 || state.c >= minSameDirection)) {
                    val point = state.p.move(move.value)
                    if (point in area) {
                        val from = when (move.value) {
                            N -> S
                            S -> N
                            E -> W
                            W -> E
                        }
                        queue.add(Step(State(point, from, c), loss + area.get(point).digitToInt()))
                    }
                }
            }
        }
        error("no path")
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 102
        one(input) shouldBe 1065
        three(input, 0, 3) shouldBe 1065
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 94
        two(sample2) shouldBe 71
        two(input) shouldBe 1249
    }
}

/*
Oh man, this took a long time for part 2.  The code worked with the sample input, but not the real input.  I finally
cheated and looked ats some other solutions, and then realized that I only started from the West and not from West or
North!  Just adding the 2nd state as starting step then worked.  I think some earlier map traversals said to only
enter from the West, so that somehow stuck in my head.

The 2 parts could easily be implemented using the same method, so added `three` after I finally got the 2nd gold star.
The other change - after taking a peek at other solutions - was to break the overall state into 2 classes. This avoids
overriding the `equals` and `hashCode` method to not consider the accumulated loss.

One a bit awkward aspect of my solution is that I have the "from" as direction in the state, then have to change
that into the reverse for my `move` call, and then reverse again for the new state.  But will keep it for now.
 */
