enum class Direction { N, S, E, W }

data class Point(val x: Int, val y: Int) {
    fun move(d: Direction, n: Int = 1) = when (d) {
        Direction.N -> Point(x, y - n)
        Direction.S -> Point(x, y + n)
        Direction.E -> Point(x + n, y)
        Direction.W -> Point(x - n, y)
    }

    fun move(dx: Int, dy: Int) = Point(x + dx, y + dy)

    fun neighbors4() = listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)
        .map { (dx, dy) -> Point(x + dx, y + dy) }
}

class CharArea(private val area: Array<CharArray>) {
    constructor(columns: Int, rows: Int, def: Char) : this(Array(rows) { CharArray(columns) { def } })
    constructor(lines: List<String>) : this(lines.map { it.toCharArray() }.toTypedArray())

    val xRange = area[0].indices
    val yRange = area.indices

    operator fun get(x: Int, y: Int) = area[y][x]

    fun getOrNull(x: Int, y: Int) = if (valid(x, y)) get(x, y) else null

    fun getOrNull(p: Point) = if (valid(p)) get(p) else null

    operator fun get(p: Point) = get(p.x, p.y)

    fun valid(x: Int, y: Int) = x in xRange && y in yRange

    fun valid(p: Point) = valid(p.x, p.y)

    operator fun set(x: Int, y: Int, c: Char) {
        if (valid(x, y)) area[y][x] = c
    }

    fun set(x: Int, y: Int, c: (Char) -> Char) {
        if (valid(x, y)) area[y][x] = c(area[y][x])
    }

    operator fun set(p: Point, c: Char) {
        set(p.x, p.y, c)
    }

    fun tiles(): Sequence<Point> = sequence {
        for (x in xRange) {
            for (y in yRange) {
                yield(Point(x, y))
            }
        }
    }

    fun edges(): Sequence<Point> = tiles()
        .filter { (x, y) -> x == xRange.first || x == xRange.last || y == yRange.first || y == yRange.last }

    fun corners() = listOf(
        Point(xRange.first, yRange.first),
        Point(xRange.first, yRange.last),
        Point(xRange.last, yRange.first),
        Point(xRange.last, yRange.last),
    )

    fun first(c: Char): Point {
        val y = area.indexOfFirst { c in it }
        val x = area[y].indexOfFirst { it == c }
        return Point(x, y)
    }

    fun filter(condition: (Point) -> Boolean) = sequence {
        for (x in xRange) {
            for (y in yRange) {
                val p = Point(x, y)
                if (condition(p))
                    yield(p)
            }
        }
    }

    fun neighbors4(x: Int, y: Int): List<Point> = Point(x, y).neighbors4().filter { valid(it) }

    fun neighbors4(p: Point): List<Point> = p.neighbors4().filter { valid(it) }

    fun show() {
        area.forEach { println(it) }
    }

    fun rows() = sequence { yRange.forEach { y -> yield(area[y]) } }

    fun columns() = sequence { xRange.forEach { x -> yield(yRange.map { y -> get(x, y) }) } }

    fun row(i: Int) = area[i]

    fun column(i: Int) = yRange.map { get(i, it) }

    fun substring(y: Int, startIndex: Int, endIndex: Int) = area[y].concatToString(startIndex, endIndex)

    fun rotated(): CharArea {
        val inverted = CharArea(yRange.last + 1, xRange.last + 1, ' ')
        tiles().forEach { (x, y) -> inverted[y, x] = get(x, y) }
        return inverted
    }

    override fun toString(): String {
        return area.joinToString("\n") { it.joinToString("") }
    }

    override fun hashCode(): Int {
        return toString().hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is CharArea && toString() == other.toString()
    }

    operator fun contains(p: Point) = p.x in xRange && p.y in yRange
}
