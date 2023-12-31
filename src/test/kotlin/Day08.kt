import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day08 {
    private val sample1 = """
        RL

        AAA = (BBB, CCC)
        BBB = (DDD, EEE)
        CCC = (ZZZ, GGG)
        DDD = (DDD, DDD)
        EEE = (EEE, EEE)
        GGG = (GGG, GGG)
        ZZZ = (ZZZ, ZZZ)
    """.trimIndent().lines()

    private val sample2 = """
        LLR

        AAA = (BBB, BBB)
        BBB = (AAA, ZZZ)
        ZZZ = (ZZZ, ZZZ)
    """.trimIndent().lines()

    private val sample3 = """
        LR

        11A = (11B, XXX)
        11B = (XXX, 11Z)
        11Z = (11B, XXX)
        22A = (22B, XXX)
        22B = (22C, 22C)
        22C = (22Z, 22Z)
        22Z = (22B, 22B)
        XXX = (XXX, XXX)
    """.trimIndent().lines()

    class LR(private val left: String, private val right: String) {
        fun get(direction: Char) = if (direction == 'L') left else right
    }

    class Network(private val directions: String, private val steps: Map<String, LR>) {
        fun nodes(condition: (String) -> Boolean) = steps.keys.filter(condition)
        fun step(node: String, direction: Char) = steps[node]!!.get(direction)
        fun directions() = generateSequence { directions.toList() }.flatten()
    }

    private fun parse(input: List<String>): Network {
        val regex = Regex("""(\w+) = \((\w+), (\w+)\)""")
        return Network(input[0], buildMap {
            input.drop(2).forEach { line ->
                val (n, l, r) = regex.matchEntire(line)!!.destructured
                put(n, LR(l, r))
            }
        })
    }

    private fun one(input: List<String>): Int {
        val network = parse(input)
        var node = "AAA"
        var count = 0
        network.directions().forEach { d ->
            node = network.step(node, d)
            count++
            if (node == "ZZZ") return count
        }
        error("no ZZZ")
    }

    private fun two(input: List<String>): Long {
        val network = parse(input)
        val startNodes = network.nodes { it.endsWith("A") }

        fun steps(start: String): Long {
            var count = 0L
            var node = start
            network.directions().forEach { d ->
                node = network.step(node, d)
                count++
                if (node.endsWith("Z")) return count
            }
            error("no Z")
        }

        return startNodes.map { steps(it) }.reduce { acc, c -> lcm(acc, c) }
    }

    private fun three(input: List<String>, startCondition: (String) -> Boolean, endCondition: (String) -> Boolean): Long {
        val network = parse(input)

        fun steps(start: String): Long {
            var count = 0L
            var node = start
            var firstEnd: String? = null
            var firstCount = 0L
            network.directions().forEach { d ->
                count++
                node = network.step(node, d)
                if (endCondition(node)) {
                    if (firstEnd == null) {
                        firstEnd = node
                        firstCount = count
                    } else {
                        check(firstEnd == node) { "Not at same node" }
                        check(firstCount.rem(count - firstCount) == 0L) { "Not a valid cycle length" }
                        return firstCount
                    }
                }
            }
            error("no end")
        }

        return network.nodes(startCondition).map { steps(it) }.reduce { acc, c -> lcm(acc, c) }
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample1) shouldBe 2
        one(sample2) shouldBe 6
        one(input) shouldBe 13771
        three(sample2, { it == "AAA" }, { it == "ZZZ" }) shouldBe 6L
        three(input, { it == "AAA" }, { it == "ZZZ" }) shouldBe 13771L
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample3) shouldBe 6L
        two(input) shouldBe 13129439557681L
        three(input, { it.endsWith("A") }, { it.endsWith("Z") }) shouldBe 13129439557681L
    }
}

/*
Part 1 was simple, but this brute-force version was not working for part 2.  I then realized that I would have
to find cycles for each start node and align these, instead of simply stepping forward.  Finding the shortest
point for cycles to align is using the "least common multiplier" of all the cycle lengths.  But to find cycles
in a generic way would have to make sure that we are at the same point of the direction steps and the same node.
This started to become quite complex, and I wondered if something so complicated would be required for such an
early puzzle.  So I paused that and took a quick shot at assuming that all the shortest possible cycles by magic
align.  Turns out that worked!  Still, I don't believe that will work for all possible inputs. But calling it
a done day.

Update: I changed the `three` function to ensure that the cycles really align by running them until I hit the end
node a second time, and then checking that we rally entered a proper loop.  This will now fail for "unfriendly"
input, but works for the provided input.
*/
