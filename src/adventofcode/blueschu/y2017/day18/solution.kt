package adventofcode.blueschu.y2017.day18

import java.io.File
import java.util.*
import kotlin.test.assertEquals

val input: List<String> by lazy {
    File("resources/y2017/day18.txt")
        .bufferedReader()
        .use { r -> r.readLines() }
}

fun main(args: Array<String>) {
    assertEquals(4, part1(listOf("set a 1",
        "add a 2",
        "mul a a",
        "mod a 5",
        "snd a",
        "set a 0",
        "rcv a",
        "jgz a -1",
        "set a 1",
        "jgz a -2")))
    println("Part 1: ${part1(input)}")

    assertEquals(3, part2(listOf("snd 1",
        "snd 2",
        "snd p",
        "rcv a",
        "rcv b",
        "rcv c",
        "rcv d")))
    println("Part 2: ${part2(input)}")
}

// Instruction value tokens
sealed class Value

data class RegisterKey(val key: Char) : Value()
data class Literal(val value: Long) : Value()

// Duet assembly instructions
sealed class DuetInstruction

data class Send(val frequency: Value) : DuetInstruction()
data class Set(val register: RegisterKey, val newValue: Value) : DuetInstruction()
data class Add(val register: RegisterKey, val additive: Value) : DuetInstruction()
data class Mult(val register: RegisterKey, val factor: Value) : DuetInstruction()
data class Mod(val register: RegisterKey, val base: Value) : DuetInstruction()
data class Receive(val register: RegisterKey) : DuetInstruction()
data class JumpGtrZero(val condition: Value, val offset: Value) : DuetInstruction()

// parse input line into assembly-like instruction
fun parseDuetInstruction(line: String): DuetInstruction {
    fun valueFor(token: String) = if (token.length == 1 && token.first() in 'a'..'z') {
        RegisterKey(token.first())
    } else Literal(token.toInt().toLong())

    val instr = line.slice(0..2)
    val tokens = line.substring(4).split(' ')
        .map { valueFor(it) }

    return when (instr) {
        "snd" -> Send(tokens[0])
        "set" -> Set(tokens[0] as RegisterKey, tokens[1])
        "add" -> Add(tokens[0] as RegisterKey, tokens[1])
        "mul" -> Mult(tokens[0] as RegisterKey, tokens[1])
        "mod" -> Mod(tokens[0] as RegisterKey, tokens[1])
        "rcv" -> Receive(tokens[0] as RegisterKey)
        "jgz" -> JumpGtrZero(tokens[0], tokens[1])
        else -> throw IllegalArgumentException("Malformed line could not be parsed: $line")
    }
}

// Maps register key to their respective values while defaulting to 0 for uninitiated registers
class RegisterRepository<K, V : Number> {
    val registerTable = mutableMapOf<K, V>()

    operator fun get(registerKey: K): V {
        if (registerKey !in registerTable) {
            // 0L can be safely converted to any Number subtype
            @Suppress("UNCHECKED_CAST")
            registerTable += registerKey to 0L as V
        }
        return registerTable[registerKey] ?:
            throw IllegalStateException("Register '$registerKey' missing")
    }

    operator fun set(registerKey: K, value: V) {
        if (registerKey !in registerTable) {
            registerTable += registerKey to value
        } else {
            registerTable[registerKey] = value
        }
    }
}

// Functionalities common to both Part One and Part Two
sealed class Interpreter {
    // Key must be boxed, otherwise a JVM internal error occurs during compilation
    protected open val registerTable = RegisterRepository<Char?, Long>()

    protected val toneQueue: Queue<Long> = ArrayDeque()

    val lastTone: Long? get() = if (toneQueue.isEmpty()) null else toneQueue.last()

    protected var pos = 0

    protected fun executeInstruction(instr: DuetInstruction) {
        var nextJump = 1
        when (instr) {
            is Send -> processSend(instr)

            is Set -> registerTable[instr.register.key] = instr.newValue.inferredValue

            is Add -> registerTable[instr.register.key] += instr.additive.inferredValue

            is Mult -> registerTable[instr.register.key] *= instr.factor.inferredValue

            is Mod -> registerTable[instr.register.key] %= instr.base.inferredValue

            is Receive -> processReceive(instr)

            is JumpGtrZero -> if (instr.condition.inferredValue > 0) {
                nextJump = instr.offset.inferredValue.toInt()
            }
        }
        pos += nextJump
    }

    abstract protected fun processSend(instr: Send)

    abstract protected fun processReceive(instr: Receive)

    protected val Value.inferredValue
        get() = when (this) {
            is RegisterKey -> registerTable[this.key]
            is Literal -> this.value
        }

    protected open fun reset() {
        pos = 0
    }
}

// Implementation for Part One
class SoloInterpreter : Interpreter() {
    private var recoverCalled = false

    fun executeUntilRecovery(prog: List<DuetInstruction>): Int {
        while (pos < prog.size && !recoverCalled) {
            executeInstruction(prog[pos])
        }
        val tone = lastTone
        reset()
        return tone?.toInt() ?: throw IllegalStateException("No tones have been played")
    }

    override fun processSend(instr: Send) {
        toneQueue.add(instr.frequency.inferredValue)
    }

    override fun processReceive(instr: Receive) {
        if (instr.register.inferredValue != 0L) {
            recoverCalled = true
        }
    }

    override fun reset() {
        super.reset()
        recoverCalled = false
        toneQueue.clear()
    }

}

// Implementation for Part Two
class DuetInterpreter(val id: Long, private val prog: List<DuetInstruction>) : Interpreter() {
    // Initialize 'p' register with instance ID
    override val registerTable = RegisterRepository<Char?, Long>().apply { set('p', id) }

    val nextInstr get() = prog[pos]

    val locked get() = nextInstr is Receive && toneQueue.isEmpty()

    var buddy: DuetInterpreter? = null
        private set

    var sentCount = 0
        private set

    infix fun pairWith(other: DuetInterpreter) {
        if (other.buddy !== this) other.buddy = this
        this.buddy = other
    }

    fun processNext() = executeInstruction(nextInstr)

    override fun processSend(instr: Send) {
        buddy?.toneQueue?.add(instr.frequency.inferredValue)
            ?: throw IllegalStateException("Interpreter has no buddy")
        sentCount++
    }

    override fun processReceive(instr: Receive) {
        // will throw [NoSuchElementException] if the queue is empty
        registerTable[instr.register.key] = toneQueue.remove()
    }

}

fun part1(lines: List<String>): Int {
    val instructions = lines.map { parseDuetInstruction(it) }
    val interpreter = SoloInterpreter()
    return interpreter.executeUntilRecovery(instructions)
}

// non-multithreaded for ease of implementation
fun part2(lines: List<String>): Int {
    val instructions = lines.map { parseDuetInstruction(it) }

    // load the interpreters
    val interpreters = Pair(
        DuetInterpreter(id = 0, prog = instructions),
        DuetInterpreter(id = 1, prog = instructions)
    ).apply { first pairWith second }

    with (interpreters) {
        while (!(first.locked && second.locked)) {
            if (!first.locked) {
                first.processNext()
            }
            if (!second.locked) {
                second.processNext()
            }
        }
        // deadlock has occurred
        return second.sentCount
    }
}

