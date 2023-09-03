// from https://github.com/alexhwoods/alexhwoods.com/blob/master/kotlin-algorithms/src/main/kotlin/com/alexhwoods/graphs/datastructures/Graph.kt

fun <T> List<Pair<T, T>>.getUniqueValuesFromPairs(predicate: (T) -> Boolean): Set<T> = this
    .flatMap { (a, b) -> listOf(a, b) }
    .filter(predicate)
    .toSet()

class Graph<T>(
    val vertices: Set<T>,
    val edges: Map<T, Set<T>>,
    val weights: Map<Pair<T, T>, Int>
) {
    constructor(weights: Map<Pair<T, T>, Int>) : this(
        vertices = weights.keys.toList().getUniqueValuesFromPairs { true },
        edges = weights.keys
            .groupBy { it.first }
            .mapValues { it.value.getUniqueValuesFromPairs { x -> x != it.key } }
            .withDefault { emptySet() },
        weights = weights
    )
}

fun <T> dijkstra(graph: Graph<T>, start: T): Map<T, T?> {
    val s = mutableSetOf<T>() // a subset of vertices, for which we know the true distance

    val delta = graph.vertices.associateWith { Int.MAX_VALUE }.toMutableMap()
    delta[start] = 0

    val previous: MutableMap<T, T?> = graph.vertices.associateWith { null }.toMutableMap()

    while (s != graph.vertices) {
        val v: T = delta
            .filter { !s.contains(it.key) }
            .minBy { it.value }
            .key

        graph.edges.getValue(v).minus(s).forEach { neighbor ->
            val newPath = delta.getValue(v) + graph.weights.getValue(Pair(v, neighbor))

            if (newPath < delta.getValue(neighbor)) {
                delta[neighbor] = newPath
                previous[neighbor] = v
            }
        }

        s.add(v)
    }

    return previous
}

fun <T> toEdges(vararg list: Pair<T, T>) = list
    .fold(mutableMapOf<T, MutableSet<T>>()) { acc, i -> acc.getOrPut(i.first) { mutableSetOf() }.add(i.second); acc }
    .mapValues { it.value.toSet() }
    .toMap()

fun <T> shortestPath(start: T, end: T, vararg edges: Pair<T, T>): List<T> = shortestPath(start, end, toEdges(*edges))

fun <T> shortestPath(start: T, end: T, edges: Map<T, Set<T>>): List<T> {
    val parent = mutableMapOf<T, T>()
    val seen = mutableSetOf(start)
    val queue = ArrayDeque(listOf(start))
    queue@ while (queue.isNotEmpty()) {
        val a = queue.removeFirst()
        for (b in edges.getOrDefault(a, emptySet())) {
            parent[b] = a
            if (b == end) break@queue
            if (seen.add(b)) {
                queue.add(b)
            }
        }
    }
    return buildList {
        var t = end
        while (true) {
            add(t)
            if (t == start) break
            t = parent[t] ?: error("No path from $start to $end")
        }
    }.reversed()
}

enum class Walk { Breadth, Depth }

fun <T> dfs(start: T, next: (T) -> Iterable<T>): Sequence<IndexedValue<T>> = walk(start, next, Walk.Depth)
fun <T> bfs(start: T, next: (T) -> Iterable<T>): Sequence<IndexedValue<T>> = walk(start, next, Walk.Breadth)

// initially copied from https://github.com/ephemient/aoc2022/blob/main/kt/src/commonMain/kotlin/com/github/ephemient/aoc2022/Day12.kt
fun <T> walk(start: T, next: (T) -> Iterable<T>, first: Walk): Sequence<IndexedValue<T>> = sequence {
    val seen = mutableSetOf(start)
    val queue = ArrayDeque(listOf(IndexedValue(0, start)))
    val enqueue = if (first == Walk.Breadth) queue::add else queue::addFirst
    while (queue.isNotEmpty()) {
        val a = queue.removeFirst()
        yield(a)
        for (b in next(a.value)) {
            if (seen.add(b)) {
                enqueue(IndexedValue(a.index + 1, b))
            }
        }
    }
}
