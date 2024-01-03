import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.util.*

class Day21 {
    private val sample = """
        ...........
        .....###.#.
        .###.##..#.
        ..#.#...#..
        ....#.#....
        .##..S####.
        .##..#...#.
        .......##..
        .##.#.####.
        .##..##.##.
        ...........
    """.trimIndent().lines()

    private fun one(input: List<String>, steps: Int): Int {
        val area = CharArea(input)
        val start = area.first('S')
        return bfs(start) { t -> area.neighbors4(t).filter { area.get(it) != '#' } }.takeWhile { it.index <= steps }
            .count { it.index % 2 == 0 }
//        bfs(start) { t -> area.neighbors4(t).filter { area.get(it) != '#'} }.takeWhile { it.index <= 6 }.forEach { if (it.index %2 == 0) area.set(it.value, 'O') }
    }

//    data class ExtendedPoint(val point: Point, val steps: Int, val area: Point)

    private fun two(input: List<String>, steps: Int): Int {
//        val area = CharArea(input)
//        var remaining = steps
//        val starts = PriorityQueue(compareBy<ExtendedPoint> { it.steps })
//        starts.add(ExtendedPoint(area.first('S'), 0, Point(0, 0)))
        var total = 0
//        val visitedAreas = mutableSetOf(Point(0, 0))
//        while (remaining != 0) {
//            val ep = starts.remove()
//            val s = when {
//                ep.point.x < 0 -> ExtendedPoint(Point(area.xRange.last, ep.point.y), ep.steps, ep.area.move(Direction.W))
//                ep.point.y < 0 -> ExtendedPoint(Point(ep.point.x, area.yRange.last), ep.steps, ep.area.move(Direction.N))
//                ep.point.x > area.xRange.last -> ExtendedPoint(Point(0, ep.point.y), ep.steps, ep.area.move(Direction.E))
//                ep.point.y > area.xRange.last -> ExtendedPoint(Point(ep.point.x, 0), ep.steps, ep.area.move(Direction.S))
//                else -> ep
//            }
//            if (visitedAreas.add(s.area) && area.get(s.point) != '#') {
//                val c = bfs(s) { t: ExtendedPoint ->
//                    val (i, o) = t.point.neighbors4().partition { area.valid(it) }
//                    if (t.steps < steps) {
//                        o.forEach { starts.add(ExtendedPoint(it, t.steps + 1, t.area)) }
//                        i.filter { area.get(it) != '#' }.map { ExtendedPoint(it, t.steps + 1, t.area) }
//                    } else {
//                        emptyList()
//                    }
//                }.toList()
//                total += c.count { it.index % 2 == s.steps % 2 }
//                remaining -= c.size
//            }
//        }
        return total
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample, 6) shouldBe 16
        one(input, 64) shouldBe 3820
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample, 6) shouldBe 16
//        two(input) shouldBe 0
    }
}
