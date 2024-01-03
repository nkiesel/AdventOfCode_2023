import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import sun.awt.www.content.image.png
import kotlin.io.path.absolutePathString
import kotlin.io.path.createTempFile

class Day25 {
    private val sample = """
        jqt: rhn xhk nvd
        rsh: frs pzl lsr
        xhk: hfx
        cmg: qnr nvd lhk bvb
        rhn: xhk bvb hfx
        bvb: xhk hfx
        pzl: lsr hfx nvd
        qnr: nvd
        ntq: jqt hfx bvb xhk
        nvd: lhk
        lsr: lhk
        rzs: qnr cmg lsr rsh
        frs: qnr lhk lsr
    """.trimIndent().lines()

    private fun one(input: List<String>, delete: List<Pair<String, String>>, render: Boolean = false): Int {
        val edges = input.map { it.split(": ") }.flatMap { (l, r) -> r.split(" ").map { l to it } }

        val graph = buildMap<String, MutableSet<String>> {
            edges.forEach { (a, b) ->
                getOrPut(a) { mutableSetOf() } += b
                getOrPut(b) { mutableSetOf() } += a
            }
        }

        if (render) {
            val dot = createTempFile(suffix = ".dot")
            val name = dot.absolutePathString()
            dot.toFile().printWriter().use { out ->
                out.println("graph G {")
                edges.forEach { out.println("  ${it.first} -- ${it.second};") }
                out.println("}")
            }
            ProcessBuilder("neato", "-T", "png", "-O", name).start().waitFor()
            ProcessBuilder("eog", "$name.png").start().waitFor()
        }

        delete.forEach { (a, b) ->
            graph[a]!! -= b
            graph[b]!! -= a
        }

        val a = bfs(delete.first().first) { graph[it]!! }.count()
        return a * (graph.keys.size - a)
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample, listOf("hfx" to "pzl", "bvb" to "cmg", "nvd" to "jqt"), true) shouldBe 54
        one(input, listOf("mnf" to "hrs", "rkh" to "sph", "kpc" to "nnl")) shouldBe 614655
    }
}
