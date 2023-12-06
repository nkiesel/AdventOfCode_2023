import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import kotlin.math.min

class Day05 {
    private val sample = """
        seeds: 79 14 55 13

        seed-to-soil map:
        50 98 2
        52 50 48

        soil-to-fertilizer map:
        0 15 37
        37 52 2
        39 0 15

        fertilizer-to-water map:
        49 53 8
        0 11 42
        42 0 7
        57 7 4

        water-to-light map:
        88 18 7
        18 25 70

        light-to-temperature map:
        45 77 23
        81 45 19
        68 64 13

        temperature-to-humidity map:
        0 69 1
        1 0 69

        humidity-to-location map:
        60 56 37
        56 93 4
    """.trimIndent().lines()

    class Transformation(private val sourceRange: LongRange, private val offset: Long) {
        constructor(destination: Long, source: Long, length: Long) : this(
            source..< source + length,
            source - destination
        )

        fun transform(value: Long): Long? {
            return if (value in sourceRange) value - offset else null
        }
    }

    class Mapping(private val transformations: List<Transformation>) {
        fun transform(value: Long): Long {
            return transformations.firstNotNullOfOrNull { it.transform(value) } ?: value
        }
    }

    private fun parse(input: List<String>): List<Mapping> {
        return input.drop(2).chunkedBy { it.isEmpty() }.map { mapping ->
            Mapping(mapping.drop(1).map { line -> line.longs().let { Transformation(it[0], it[1], it[2]) } })
        }
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 35L
        one(input) shouldBe 600279879L
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 46
        two(input) shouldBe 20191102L
    }

    private fun one(input: List<String>): Long {
        val seeds = input[0].longs()
        val mappings = parse(input)
        return seeds.minOf { seed -> mappings.fold(seed) { acc, mapping -> mapping.transform(acc) } }
    }

    private fun two(input: List<String>): Long {
        val seeds = input[0].longs().chunked(2).map { it[0]..<it[0] + it[1] }
        println("${seeds.size} seed ranges with ${seeds.sumOf { it.last - it.first + 1 }} values")
        val mappings = parse(input)
        var minValue = Long.MAX_VALUE
        seeds.forEachIndexed { index, seedRange ->
            for (v in seedRange) {
                if (index == 0 || seeds.take(index - 1).none { v in it }) {
                    minValue = min(minValue, mappings.fold(v) { acc, mapping -> mapping.transform(acc) })
                }
            }
        }
        return minValue
    }
}

/*
We are now getting into typical AoC territory: Int overflows, and brute force no longer works.  My solution for part 2
works, but runs for a looong time.  I'm sure there are optimizations I'm missing, but cannot think of anything right now.
 */
