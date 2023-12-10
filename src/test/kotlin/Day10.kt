import Day10.Direction.*
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day10 {
    private val sample = """
        ..F7.
        .FJ|.
        SJ.L7
        |F--J
        LJ...
    """.trimIndent().lines()

    private val sample2 = """
        ...........
        .S-------7.
        .|F-----7|.
        .||.....||.
        .||.....||.
        .|L-7.F-J|.
        .|..|.|..|.
        .L--J.L--J.
        ...........
    """.trimIndent().lines()

    private val sample3 = """
        .F----7F7F7F7F-7....
        .|F--7||||||||FJ....
        .||.FJ||||||||L7....
        FJL7L7LJLJ||LJ.L-7..
        L--J.L7...LJS7F-7L7.
        ....F-J..F7FJ|L7L7L7
        ....L7.F7||L7|.L7L7|
        .....|FJLJ|FJ|F7|.LJ
        ....FJL-7.||.||||...
        ....L---J.LJ.LJLJ...
    """.trimIndent().lines()

    private val sample4 = """
        FF7FSF7F7F7F7F7F---7
        L|LJ||||||||||||F--J
        FL-7LJLJ||||||LJL-77
        F--JF--7||LJLJ7F7FJ-
        L---JF-JLJ.||-FJLJJ7
        |F|F-JF---7F7-L7L|7|
        |FFJF7L7F-JF7|JL---7
        7-L-JL7||F7|L7F-7F7|
        L.L7LFJ|||||FJL7||LJ
        L7JLJL-JLJLJL--JLJ.L
    """.trimIndent().lines()


    enum class Direction(val x: Int, val y: Int) {
        N(0, -1), S(0, 1), E(1, 0), W(-1, 0);

        fun possible(sx: Int, sy: Int, area: CharArea) = area.valid(sx + x, sy + y)
    }

    private fun one(input: List<String>): Int {
        val area = CharArea(input)
        val (sx, sy) = area.first('S')
        var d = Direction.entries.filter { area.valid(sx + it.x, sy + it.y) }.first { d ->
            val p = area.get(sx + d.x, sy + d.y)
            when (d) {
                N -> p in "|F7"
                S -> p in "|JL"
                E -> p in "-J7"
                W -> p in "-FL"
            }
        }
        var steps = 0
        var x = sx
        var y = sy
        do {
            steps++
            x += d.x
            y += d.y
            d = when (area.get(x, y)) {
                'L' -> if (d == S) E else N
                'J' -> if (d == S) W else N
                '7' -> if (d == N) W else S
                'F' -> if (d == N) E else S
                else -> d
            }
        } while (x != sx || y != sy)
        return steps / 2
    }

    private fun two(input: List<String>): Int {
        val area = CharArea(input[0].length * 3, input.size * 3, '.')
        for (y in input.indices) {
            for (x in input[0].indices) {
                area.set(x * 3, y * 3, input[y][x])
            }
        }

        val (sx, sy) = area.first('S')
        var d = Direction.entries.filter { it.possible(sx, sy, area) }.first { d ->
            val p = area.get(sx + d.x * 3, sy + d.y * 3)
            when (d) {
                N -> p in "|F7"
                S -> p in "|JL"
                E -> p in "-J7"
                W -> p in "-FL"
            }
        }

        var steps = 0
        var x = sx
        var y = sy
        do {
            steps++
            buildList {
                add(Pair(x, y))
                when (area.get(x, y)) {
                    'S' -> when (d) {
                        N -> add(Pair(x, y - 1))
                        S -> add(Pair(x, y + 1))
                        E -> add(Pair(x + 1, y))
                        W -> add(Pair(x - 1, y))
                    }

                    '-' -> addAll(listOf(Pair(x - 1, y), Pair(x + 1, y)))
                    '|' -> addAll(listOf(Pair(x, y - 1), Pair(x, y + 1)))
                    'J' -> addAll(listOf(Pair(x - 1, y), Pair(x, y - 1)))
                    '7' -> addAll(listOf(Pair(x - 1, y), Pair(x, y + 1)))
                    'F' -> addAll(listOf(Pair(x + 1, y), Pair(x, y + 1)))
                    'L' -> addAll(listOf(Pair(x + 1, y), Pair(x, y - 1)))
                }
            }.forEach { area.set(it, '#') }
            x += d.x * 3
            y += d.y * 3
            d = when (area.get(x, y)) {
                'L' -> if (d == S) E else N
                'J' -> if (d == S) W else N
                '7' -> if (d == N) W else S
                'F' -> if (d == N) E else S
                else -> d
            }
        } while (x != sx || y != sy)

        when (d) {
            N -> area.set(x, y + 1, '#')
            S -> area.set(x, y - 1, '#')
            E -> area.set(x - 1, y, '#')
            W -> area.set(x + 1, y, '#')
        }

        area.tiles().filter { area.get(it) != '#' }.forEach { area.set(it, '.') }

        area.corners()
            .filter { area.get(it) == '.' }
            .forEach { e ->
                area.set(e, 'O')
                bfs(e) { t -> area.neighbors4(t).filter { area.get(it) == '.' } }.forEach { area.set(it.value, 'O') }
            }

        return area.tiles().count { (x, y) -> x % 3 == 0 && y % 3 == 0 && area.get(x, y) == '.' }
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 8
        one(input) shouldBe 7107
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample2) shouldBe 4
        two(sample3) shouldBe 8
        two(sample4) shouldBe 10
        two(input) shouldBe 281
    }
}

/*
As expected, Day 10 was a bit more complicated than Day 9, but very much in the spirit of AoC.  The trick for part 2
was to expand the area by converting every tile to a 3x3 area so that we can handle the "squeeze between pipes" aspect.
My part 2 then uses the following approach (1) connect the pipes of the loop and convert all of them to '#', (2) set all
tiles not part of the loop to '.' (which replaces unused pipes), (3) starting from the edges, replace all connected '.'
tiles with 'O', and (4) count the remaining '.' that were part of the initial area.

This was first pretty open-coded using a 2-dimensional char array, but then I extracted all that area processing into
a `CharArea` helper class, which then simplified the Day 10 solution.

Update: after looking at the Slack channel, I realized that there are (again!) much faster solutions (using stuff I
never heard of like "shoelace formula", "Pick's theorem", "Stokes' theorem"). The only hint I took was to only look at
the 4 corner tiles instead of all the edge tiles when "flooding" the area.
*/
