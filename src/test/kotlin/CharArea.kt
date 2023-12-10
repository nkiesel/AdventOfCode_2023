class CharArea(private val area: Array<CharArray>) {
    constructor(mx: Int, my: Int, def: Char) : this(Array(my) { CharArray(mx) { def } })
    constructor(lines: List<String>) : this(lines.map{ it.toCharArray() }.toTypedArray())

    private val xRange = area[0].indices
    private val yRange = area.indices

    fun get(x: Int, y: Int) = area[y][x]

    fun getOrNull(x: Int, y: Int) = if (valid(x, y)) area[y][x] else null

    fun get(p: Pair<Int, Int>) = get(p.first, p.second)

    fun valid(x: Int, y: Int) = x in xRange && y in yRange

    fun valid(p: Pair<Int, Int>) = valid(p.first, p.second)

    fun set(x: Int, y: Int, c: Char) {
        if (valid(x, y)) area[y][x] = c
    }

    fun set(x: Int, y: Int, c: (Char) -> Char) {
        if (valid(x, y)) area[y][x] = c(area[y][x])
    }

    fun set(p: Pair<Int, Int>, c: Char) {
        set(p.first, p.second, c)
    }

    fun tiles(): Sequence<Pair<Int, Int>> = sequence {
        for (x in xRange) {
            for (y in yRange) {
                yield(Pair(x, y))
            }
        }
    }

    fun edges(): Sequence<Pair<Int, Int>> = tiles()
        .filter { (x, y) -> x == xRange.first || x == xRange.last || y == yRange.first || y == yRange.last }

    fun first(c: Char): Pair<Int, Int> {
        val y = area.indexOfFirst { c in it }
        val x = area[y].indexOfFirst { it == c }
        return Pair(x, y)
    }

    fun filter(condition: (Pair<Int, Int>) -> Boolean) = sequence {
        for (x in xRange) {
            for (y in yRange) {
                val p = Pair(x, y)
                if (condition(p))
                yield(p)
            }
        }
    }

    fun neighbors4(x: Int, y: Int): List<Pair<Int, Int>> =
        listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)
            .map { (dx, dy) -> x + dx to y + dy }
            .filter { valid(it) }

    fun neighbors4(p: Pair<Int, Int>): List<Pair<Int, Int>> = neighbors4(p.first, p.second)

    fun show() {
        area.forEach { println(it) }
    }
}
