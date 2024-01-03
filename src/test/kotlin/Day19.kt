import io.kotest.matchers.shouldBe
import jdk.internal.org.jline.utils.Colors.s
import org.junit.jupiter.api.Test
import kotlin.math.max

class Day19 {
    private val sample = """
        px{a<2006:qkq,m>2090:A,rfg}
        pv{a>1716:R,A}
        lnx{m>1548:A,A}
        rfg{s<537:gd,x>2440:R,A}
        qs{s>3448:A,lnx}
        qkq{x<1416:A,crn}
        crn{x>2662:A,R}
        in{s<1351:px,qqz}
        qqz{s>2770:qs,m<1801:hdj,R}
        gd{a>3333:R,R}
        hdj{m>838:A,pv}

        {x=787,m=2655,a=1222,s=2876}
        {x=1679,m=44,a=2067,s=496}
        {x=2036,m=264,a=79,s=2244}
        {x=2461,m=1339,a=466,s=291}
        {x=2127,m=1623,a=2188,s=1013}
    """.trimIndent().lines()

    class Rule(val condition: String) {
        val v: Char
        val op: Char
        val t: Int
        val next: String
        val map: Pair<Char, Int>

        init {
            val re = Regex("""(.)([<>])(\d+):(.+)""")
            val r = re.matchEntire(condition)
            if (r != null) {
                v = r.groupValues[1][0]
                op = r.groupValues[2][0]
                t = r.groupValues[3].toInt()
                next = r.groupValues[4]
            } else {
                v = 'v'
                op = '!'
                t = 0
                next = condition
            }
            map = v to t
        }

        fun apply(part: Part): String? {
            return when (op) {
                '<' -> if (part.map[v]!! < t) next else null
                '>' -> if (part.map[v]!! > t) next else null
                else -> next
            }
        }
    }

    class Workflow(val name: String, val rules: List<Rule>) {
        companion object {
            fun of(line: String) = Regex("""(.+)\{(.+)}""").matchEntire(line)!!.groupValues.let { g ->
                Workflow(
                    g[1],
                    g[2].split(",").map { Rule(it) })
            }
        }
    }

    data class Part(val x: Int, val m: Int, val a: Int, val s: Int) {
        fun calc(xi: Int, mi: Int, ai: Int, si: Int, limits: Map<Char, List<Limit>>): Long {
            return listOf(
                if (xi == 0) x else x - limits['x']!![xi - 1].t,
                if (mi == 0) m else m - limits['m']!![mi - 1].t,
                if (ai == 0) a else a - limits['a']!![ai - 1].t,
                if (si == 0) s else s - limits['s']!![si - 1].t,
            ).fold(1L) { acc, i -> acc * i }
        }

        val rating = x + m + a + s
        val map = mapOf('x' to x, 'm' to m, 'a' to a, 's' to s)

        companion object {
            fun of(line: String) = line.ints().let {
                Part(it[0], it[1], it[2], it[3])
            }
        }
    }

    private fun parse(input: List<String>): Pair<Map<String, Workflow>, List<Part>> {
        val (workflows, parts) = input.chunkedBy { it.isEmpty() }
        return workflows.map { Workflow.of(it) }.associateBy { it.name } to parts.map { Part.of(it) }
    }

    private fun one(input: List<String>): Int {
        val (workflows, parts) = parse(input)
        val none = Part(0, 0, 0, 0)
        fun process(name: String, part: Part): Part {
            val workflow = workflows[name]!!
            for (rule in workflow.rules) {
                return when (val outcome = rule.apply(part)) {
                    "A" -> part
                    "R" -> none
                    null -> continue
                    else -> process(outcome, part)
                }
            }
            error("did not terminate")
        }

        return parts.map { process("in", it) }.sumOf { it.rating }
    }

    data class Limit(val op: Char, val t: Int) {
        val l = if (op == '<') t - 1 else t
        val u = if (op == '<') t else t + 1
    }

    private fun List<Limit>.expand(): List<Int> {
        return buildList {
            add(1)
            add(4000)
            this@expand.forEach {
                add(it.t)
                if (it.op == '<') add(it.t - 1) else add(it.t + 1)
            }
        }.sorted()
    }

    private fun two(input: List<String>): Long {
        val (workflows, _) = parse(input)
        val none = Part(0, 0, 0, 0)
        val limits = workflows.asSequence().flatMap { it.value.rules }
            .filterNot { it.v == 'v' }.map { it.v to Limit(it.op, it.t) }.groupBy({ it.first }, { it.second })
            .mapValues { e -> e.value.expand() }
        var count = 0L

        fun process(name: String, part: Part): Part {
            val workflow = workflows[name]!!
            for (rule in workflow.rules) {
                return when (val outcome = rule.apply(part)) {
                    "A" -> part
                    "R" -> none
                    null -> continue
                    else -> process(outcome, part)
                }
            }
            error("did not terminate")
        }

        val part = Part(1, 1800, 4000, 1)
        println(part)
        println(process("in", part))

        println(limits['x'])
        println(limits['m'])
        println(limits['a'])
        println(limits['s'])

        var bruteForce = 0L
        for (x in 1..4000) {
            for (m in 1..4000) {
                for (a in 1..4000) {
                    bruteForce += (1..4000).count { process("in", Part(x, m, a, it)) != none }.toLong()
                }
            }
        }
        return bruteForce

//        var ms = 0L
//        for (x in limits['x']!!) {
//            for (m in limits['m']!!) {
//                for (a in limits['a']!!) {
//                    val s = (1..4000).count { process("in", Part(x, m, a, it)) != none }.toLong()
////                    val s = limits['s']!!.map { it to process("in", Part(x, m, a, it)) }.zipWithNext().fold(0L) { acc, i -> if (i.second.second != none) acc + (i.second.first - i.first.first) else acc }
//                    if (x == 1 && m == 1800 && a == 4000) {
//                        println("s is $s")
//                    }
//                    ms = max(s, ms)
//                }
//            }
//        }
//        println("s: $ms")
//        count = ms

//        var mx =0L
//        for (m in limits['m']!!) {
//            for (a in limits['a']!!) {
//                for (s in limits['s']!!) {
//                    val x = (1..4000).count { process("in", Part(it, m, a, s)) != none }.toLong()
////                    val x = limits['x']!!.map { it to process("in", Part(it, m, a, s)) }.zipWithNext().fold(0L) { acc, i -> if (i.second.second != none) acc + (i.second.first - i.first.first) else acc }
//                    mx = max(x, mx)
//                }
//            }
//        }
//        println("x: $mx")
//        count *= mx
//
//        var mm = 0L
//        for (a in limits['a']!!) {
//            for (s in limits['s']!!) {
//                for (x in limits['x']!!) {
//                    val m = (1..4000).count { process("in", Part(x, it, a, s)) != none }.toLong()
////                    val m = limits['m']!!.map { it to process("in", Part(x, it, a, s)) }.zipWithNext().fold(0L) { acc, i -> if (i.second.second != none) acc + (i.second.first - i.first.first) else acc }
//                    mm = max(m, mm)
//                }
//            }
//        }
//        println("m: $mm")
//        count *= mm
//
//        var ma = 0L
//        for (s in limits['s']!!) {
//            for (x in limits['x']!!) {
//                for (m in limits['m']!!) {
//                    val a = (1..4000).count { process("in", Part(x, m, it, s)) != none }.toLong()
////                    val a = limits['a']!!.map { it to process("in", Part(x, m, it, s)) }.zipWithNext().fold(0L) { acc, i -> if (i.second.second != none) acc + (i.second.first - i.first.first) else acc }
//                    ma = max(a, ma)
//                }
//            }
//        }
//        println("a: $ma")
//        count *= ma

//        return count
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 19114
        one(input) shouldBe 383682
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 167409079868000L
//        two(input) shouldBe 0
    }
}
