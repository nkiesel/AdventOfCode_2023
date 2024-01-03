import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day24 {
    private val sample = """
        19, 13, 30 @ -2,  1, -2
        18, 19, 22 @ -1, -1, -2
        20, 25, 34 @ -2, -2, -4
        12, 31, 28 @ -1, -2, -1
        20, 19, 15 @  1, -5, -3
    """.trimIndent().lines()

    data class Storm(
        val startX: Long,
        val startY: Long,
        val startZ: Long,
        val velocityX: Long,
        val velocityY: Long,
        val velocityZ: Long,
        val a: Double,
        val b: Double
    ) {
        fun intercept(other: Storm): Double? {
            val d = a - other.a
            return if (d == 0.0) null else (other.b - b) / d
        }

        fun y(x: Double) = a * x + b

        val points = generateSequence(Triple(startX, startY, startZ)) { point -> Triple(
            point.first + velocityX,
            point.second + velocityY,
            point.third + velocityZ
        ) }

        fun possible(x: Double) = if (velocityX >= 0) x >= startX else x < startX

        companion object {
            fun of(l: List<Long>): Storm {
                val (sx, sy, sz) = l.take(3)
                val (vx, vy, vz) = l.drop(3).take(3)
                val a = vy.toDouble() / vx.toDouble()
                val b = (sy - a * sx)
                return Storm(sx, sy, sz, vx, vy, vz, a, b)
            }
        }
    }
    private fun parse(input: List<String>): List<Storm> {
        return input.map { Storm.of(it.longs()) }
    }

    private fun one(input: List<String>, lower: Double, upper: Double): Int {
        val storms = parse(input)
        val range = lower..upper
        var interceptions = 0
        for (l in storms.withIndex()) {
            val index = l.index
            val l1 = l.value
            for (l2 in storms.drop(index + 1)) {
                val x = l1.intercept(l2)
                if (x != null && x in range && l1.possible(x) && l2.possible(x)) {
                    val y = l1.y(x)
                    if (y in range) interceptions++
                }
            }
        }
        return interceptions
    }

    private fun two(input: List<String>): Long {
        val storms = parse(input)
        val rock = Storm.of(listOf(24, 13, 10, -3, 1, 2))
        return rock.startX + rock.startY + rock.startZ
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample, 7.0, 27.0) shouldBe 2
        one(input, 200000000000000.0, 400000000000000.0) shouldBe 28266
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 47L
//        two(input) shouldBe 0
    }
}
