class CharArea(private val area: Array<CharArray>) {
    constructor(mx: Int, my: Int, def: Char) : this(Array(my) { CharArray(mx) { def } })
    constructor(lines: List<String>) : this(lines.map { it.toCharArray() }.toTypedArray())

    val xRange = area[0].indices
    val yRange = area.indices

    fun get(x: Int, y: Int) = area[y][x]

    fun getOrNull(x: Int, y: Int) = if (valid(x, y)) area[y][x] else null

    fun get(p: IntPair) = get(p.first, p.second)

    fun valid(x: Int, y: Int) = x in xRange && y in yRange

    fun valid(p: IntPair) = valid(p.first, p.second)

    fun set(x: Int, y: Int, c: Char) {
        if (valid(x, y)) area[y][x] = c
    }

    fun set(x: Int, y: Int, c: (Char) -> Char) {
        if (valid(x, y)) area[y][x] = c(area[y][x])
    }

    fun set(p: IntPair, c: Char) {
        set(p.first, p.second, c)
    }

    fun tiles(): Sequence<IntPair> = sequence {
        for (x in xRange) {
            for (y in yRange) {
                yield(Pair(x, y))
            }
        }
    }

    fun edges(): Sequence<IntPair> = tiles()
        .filter { (x, y) -> x == xRange.first || x == xRange.last || y == yRange.first || y == yRange.last }

    fun corners() = listOf(
        Pair(xRange.first, yRange.first),
        Pair(xRange.first, yRange.last),
        Pair(xRange.last, yRange.first),
        Pair(xRange.last, yRange.last),
    )

    fun first(c: Char): IntPair {
        val y = area.indexOfFirst { c in it }
        val x = area[y].indexOfFirst { it == c }
        return Pair(x, y)
    }

    fun filter(condition: (IntPair) -> Boolean) = sequence {
        for (x in xRange) {
            for (y in yRange) {
                val p = Pair(x, y)
                if (condition(p))
                    yield(p)
            }
        }
    }

    fun neighbors4(x: Int, y: Int): List<IntPair> =
        listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)
            .map { (dx, dy) -> x + dx to y + dy }
            .filter { valid(it) }

    fun neighbors4(p: IntPair): List<IntPair> = neighbors4(p.first, p.second)

    fun show() {
        area.forEach { println(it) }
    }

    fun rows() = sequence { yRange.forEach { y -> yield(area[y]) } }

    fun columns() = sequence { xRange.forEach { x -> yield(yRange.map { y -> get(x, y) }) } }

    fun row(i: Int) = area[i]

    fun column(i: Int) = yRange.map { get(i, it) }
}
