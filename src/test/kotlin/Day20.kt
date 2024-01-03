import Day20.Module.Companion.wave
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day20 {
    private val sample1 = """
        broadcaster -> a, b, c
        %a -> b
        %b -> c
        %c -> inv
        &inv -> a
    """.trimIndent().lines()

    private val sample2 = """
        broadcaster -> a
        %a -> inv, con
        &inv -> b
        %b -> con
        &con -> output
    """.trimIndent().lines()

    enum class Level(val i: Int) { low(0), high(1) }

    class Pulse(val source: Module, val target: Module, val level: Level) {
        fun execute() {
            target.receive(level, source)
        }
    }

    sealed class Module(val name: String) {
        val next = mutableListOf<Module>()
        var lowCount = 0L
        var highCount = 0L

        abstract fun receive(level: Level, source: Module)

        abstract fun state(): Int

        fun send(level: Level) {
            if (level == Level.low) lowCount++ else highCount++
            next.forEach { wave.addLast(Pulse(this, it, level)) }
        }

        companion object {
            var wave = ArrayDeque<Pulse>()
        }
    }

    class Button() : Module("button") {
        fun push() {
            send(Level.low)
        }

        override fun receive(level: Level, source: Module) {
        }

        override fun state(): Int {
            return 0
        }
    }

    class Broadcaster() : Module("broadcaster") {
        private var level = Level.low
        override fun receive(level: Level, source: Module) {
            this.level = level
            send(level)
        }

        override fun state(): Int {
            return level.i
        }
    }

    class FlipFlop(name: String) : Module(name) {
        private var on: Boolean = false
        override fun receive(level: Level, source: Module) {
            if (level == Level.low) {
                send(if (on) Level.high else Level.low)
                on = !on
            }
        }

        override fun state(): Int {
            return if (on) 0 else 1
        }
    }

    class Conjunction(name: String) : Module(name) {
        var prev = mutableMapOf<Module, Level>().withDefault { Level.low }
        override fun receive(level: Level, source: Module) {
            prev[source] = level
            send(if (prev.values.all { it == Level.high }) Level.low else Level.high)
        }

        override fun state(): Int {
            return prev.values.sortedBy { it.name }.fold(0) { acc, level -> acc * 2 + level.i }
        }

    }

    private fun parse(input: List<String>): List<Module> {
        val re = Regex("""([%&])?(\w+) -> (.+)""")
        val modules = mutableListOf<Module>()
        val cables = mutableMapOf<String, List<String>>()
        input.map { line ->
            val (type, name, next) = re.matchEntire(line)!!.destructured
            cables[name] = next.split(", ")
            modules += when (type) {
                "%" -> FlipFlop(name)
                "&" -> Conjunction(name)
                else -> Broadcaster()
            }
        }
        val map = modules.associateBy { it.name }
        for ((name, next) in cables) {
            map[name]!!.next += next.map { map[it]!! }
        }
        return map.values.toList()
    }

    private fun one(input: List<String>): Long {
        val modules = parse(input)
        val button = Button().apply { next += modules.first { it.name == "broadcaster" } }
        val states = mutableSetOf<List<Int>>()
        for (i in 1..1000) {
            button.push()
            do {
                wave.removeFirst().execute()
            } while (wave.isNotEmpty())
            val state = modules.map { it.state() }
            if (!states.add(state)) {
                val rep = states.size.toLong()
                check(1000 / i * i == 1000)
                return (1000L / rep) * (1000L / rep) * modules.sumOf { it.lowCount } * modules.sumOf { it.highCount }
            }
        }
        return 0L
    }

    private fun two(input: List<String>): Int {
        return 0
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample1) shouldBe 32000000L
//        one(input) shouldBe 0
    }

    @Test
    fun testTwo(input: List<String>) {
//        two(sample) shouldBe 0
//        two(input) shouldBe 0
    }
}
