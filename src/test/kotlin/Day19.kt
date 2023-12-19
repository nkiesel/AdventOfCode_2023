import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

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

    class Part(val x: Int, val m: Int, val a: Int, val s: Int) {
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

    private fun two(input: List<String>): Int {
        return 0
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 19114
        one(input) shouldBe 383682
    }

    @Test
    fun testTwo(input: List<String>) {
//        two(sample) shouldBe 0
//        two(input) shouldBe 0
    }
}
