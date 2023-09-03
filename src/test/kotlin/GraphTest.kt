import io.kotest.assertions.throwables.shouldThrowMessage
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class GraphTest {
    @Test
    fun `basic test for BFS`() {
        data class TNode(val name: String) {
            override fun toString() = name
        }
        data class TEdge(val a: TNode, val b: TNode)

        val n1 = TNode("one")
        val n2 = TNode("two")
        val n3 = TNode("three")
        val n4 = TNode("four")
        val edges = listOf(TEdge(n1, n2), TEdge(n2, n3), TEdge(n3, n4), TEdge(n2, n4), TEdge(n1, n4))
        bfs(n1) { n -> edges.filter { e -> e.a == n || e.b == n }.flatMap { listOf(it.a, it.b) }.filter { it != n } }.forEach { println(it) }
        println()
        bfs(n1) { n -> edges.mapNotNull { e -> if (e.a == n) e.b else if (e.b == n) e.a else null }.filter { it != n } }.forEach { println(it) }
    }

    @Test
    fun `another basic test for BFS`() {
        data class TNode(val name: String) {
            override fun toString() = name
        }

        val n1 = TNode("one")
        val n2 = TNode("two")
        val n3 = TNode("three")
        val n4 = TNode("four")
        val edges = listOf(setOf(n1, n2), setOf(n2, n3), setOf(n3, n4), setOf(n2, n4), setOf(n1, n4))
        bfs(n1) { n -> edges.filter { n in it }.map { e -> e.first { it != n } } }.forEach { println(it) }
    }

    @Test
    fun `basic test for BFS of directed graph`() {
        data class TNode(val name: String, val next: Set<TNode>) {
            override fun toString() = name
        }

        val n1 = TNode("one", emptySet())
        val n5 = TNode("five", emptySet())
        val n2 = TNode("two", setOf(n1, n5))
        val n3 = TNode("three", setOf(n2))
        val n4 = TNode("four", setOf(n1, n3))
        bfs(n4) { it.next }.forEach { println(it) }
    }

    @Test
    fun `basic test for DFS of directed graph`() {
        data class TNode(val name: String, val next: Set<TNode>) {
            override fun toString() = name
        }

        val n1 = TNode("one", emptySet())
        val n5 = TNode("five", emptySet())
        val n2 = TNode("two", setOf(n1, n5))
        val n3 = TNode("three", setOf(n2))
        val n4 = TNode("four", setOf(n1, n3))
        dfs(n4) { it.next }.forEach { println(it) }
    }

    @Test
    fun `shortest path`() {
        shortestPath(
            'a',
            'e',
            toEdges('a' to 'b', 'a' to 'd', 'b' to 'c', 'c' to 'd', 'd' to 'e')
        ) shouldBe listOf('a', 'd', 'e')

        shortestPath(
            'a',
            'e',
            'a' to 'b', 'a' to 'd', 'b' to 'c', 'c' to 'd', 'd' to 'e'
        ) shouldBe listOf('a', 'd', 'e')

        shouldThrowMessage("No path from a to z") { shortestPath('a', 'z', 'a' to 'b') }
    }
}
