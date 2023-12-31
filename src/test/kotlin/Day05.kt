import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

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
        private val destRange = sourceRange.first - offset..sourceRange.last - offset

        constructor(destination: Long, source: Long, length: Long) : this(
            source..<source + length,
            source - destination
        )

        fun transform(value: Long): Long? {
            return if (value in sourceRange) value - offset else null
        }

        fun reverse(value: Long): Long? {
            return if (value in destRange) value + offset else null
        }
    }

    class Mapping(private val transformations: List<Transformation>) {
        fun transform(value: Long): Long {
            return transformations.firstNotNullOfOrNull { it.transform(value) } ?: value
        }

        fun reverse(value: Long): Long {
            return transformations.firstNotNullOfOrNull { it.reverse(value) } ?: value
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
        two(sample) shouldBe 46L
        two(input) shouldBe 20191102L
    }

    private fun one(input: List<String>): Long {
        val seeds = input[0].longs()
        val mappings = parse(input)
        return seeds.minOf { seed -> mappings.fold(seed) { acc, mapping -> mapping.transform(acc) } }
    }

    private fun two(input: List<String>): Long {
        val seeds = input[0].longs().chunked(2).map { it[0]..<it[0] + it[1] }
        val mappings = parse(input).reversed()
        for (v in 0L..Long.MAX_VALUE) {
            val s = mappings.fold(v) { acc, mapping -> mapping.reverse(acc) }
            if (seeds.any { s in it }) {
                return v
            }
        }
        error("no solution")
    }
}

/*
We are now getting into typical AoC territory: Int overflows, and brute force no longer works.  My solution for part 2
works, but runs for a looong time.  I'm sure there are optimizations I'm missing, but cannot think of anything right now.

Update: reversed the transformations for part 2, and it now finishes in a few seconds.  Pretty sure there must still be
a much better approach than brute-forcing, but will let it rest for now.
*/
