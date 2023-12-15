import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day15 {
    private val sample = """
        rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7
    """.trimIndent().lines()

    private fun hash(s: String): Int {
        return s.fold(0) { acc, c -> (acc + c.code) * 17 % 256 }
    }

    private fun one(input: List<String>): Int {
        return input[0].split(",").sumOf { hash(it) }
    }

    class Lens(val label: String, val op: Char, var focal: Int) {
        companion object {
            private val re = Regex("""(.+)([-=])(\d+)?""")

            fun of(s: String): Lens {
                val l = re.matchEntire(s)!!.groupValues
                return Lens(l[1], l[2][0], l[3].toIntOrNull() ?: 0)
            }
        }
    }

    private fun two(input: List<String>): Int {
        val boxes = Array(256) { mutableListOf<Lens>() }
        input[0].split(",").map { Lens.of(it) }.forEach { lens ->
            val box = boxes[hash(lens.label)]
            when (lens.op) {
                '-' -> box.removeIf { it.label == lens.label }
                '=' -> {
                    val e = box.find { it.label == lens.label }
                    if (e == null) box.add(lens) else e.focal = lens.focal
                }
            }
        }
        return boxes.withIndex()
            .sumOf { b -> b.value.withIndex().sumOf { i -> (b.index + 1) * (i.index + 1) * i.value.focal } }
    }

    @Test
    fun testOne(input: List<String>) {
        hash("HASH") shouldBe 52
        hash("rn=1") shouldBe 30
        one(sample) shouldBe 1320
        one(input) shouldBe 511215
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 145
        two(input) shouldBe 236057
    }
}

/*
This was pretty simple again. The only mistake I made was to not see that lenses with "-" op do not have a focal
value.  Otherwise, there was nothing special.
*/
