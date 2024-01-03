import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day22 {
    private val sample = """
        1,0,1~1,2,1
        0,0,2~2,0,2
        0,2,3~2,2,3
        0,0,4~0,2,4
        2,0,5~2,2,5
        0,1,6~2,1,6
        1,1,8~1,1,9
    """.trimIndent().lines()

    data class D3(val x: Int, val y: Int, val z: Int)

    data class Brick(val i: Int, val s: D3, val e: D3) {
        val dir = when {
            s.x != e.x -> 'x'
            s.y != e.y -> 'y'
            else -> 'z'
        }
    }

    private fun parse(input: List<String>): List<Brick> {
        return input.mapIndexed { index, line ->
            line.ints().let { l -> Brick(index + 1, D3(l[0], l[1], l[2]), D3(l[3], l[4], l[5])) }
        }
    }

    private fun fall(bricks: List<Brick>, skip: Int): Pair<Int, List<Int>> {
        val maxX = bricks.flatMap { listOf(it.s, it.e) }.maxOf { it.x } + 1
        val maxY = bricks.flatMap { listOf(it.s, it.e) }.maxOf { it.y } + 1
        val maxZ = bricks.flatMap { listOf(it.s, it.e) }.maxOf { it.z } + 1

        val stack = Array(maxZ) { Array(maxY) { IntArray(maxX) } }
        val top = Array(maxY) { IntArray(maxX) }

        val removable = bricks.map { it.i }.toMutableSet()
        val zList = mutableListOf<Int>()

        for (b in bricks) {
            if (b.i == skip) {
                zList += 0
                continue
            }
            when (b.dir) {
                'x' -> {
                    val rx = b.s.x..b.e.x
                    val y = b.s.y
                    val z = rx.maxOf { x -> top[y][x] } + 1
                    val t = mutableSetOf(0)
                    rx.forEach { x ->
                        stack[z][y][x] = b.i
                        t += stack[z - 1][y][x]
                        top[y][x] = z
                    }
                    zList += z
                    if (t.size == 2) removable -= t
                }

                'y' -> {
                    val x = b.s.x
                    val ry = b.s.y..b.e.y
                    val z = ry.maxOf { y -> top[y][x] } + 1
                    val t = mutableSetOf(0)
                    ry.forEach { y ->
                        stack[z][y][x] = b.i
                        t += stack[z - 1][y][x]
                        top[y][x] = z
                    }
                    zList += z
                    if (t.size == 2) removable -= t
                }

                'z' -> {
                    val x = b.s.x
                    val y = b.s.y
                    val cz = b.e.z - b.s.z
                    val tz = top[y][x] + 1
                    val rz = tz..tz + cz
                    rz.forEach { z -> stack[z][y][x] = b.i }
                    zList += tz
                    removable -= stack[tz - 1][y][x]
                    top[y][x] += cz + 1
                }
            }
        }

        return removable.size to zList
    }

    private fun one(input: List<String>): Int {
        val bricks = parse(input).sortedBy { it.s.z }
        return fall(bricks, 0).first
    }

    private fun two(input: List<String>): Int {
        val bricks = parse(input).sortedBy { it.s.z }
        val base = fall(bricks, 0).second
        return bricks.sumOf { b ->
            fall(bricks, b.i).second.zip(base).count { it.first != 0 && it.first != it.second }
        }
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 5
        one(input) shouldBe 426
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 7
        two(input) shouldBe 61920
    }
}

/*
Took a while, but at least did not require fancy caching.  The one mistake that cost me quite some time was to miss the
`.sortedBy { it.s.z }`.
*/
