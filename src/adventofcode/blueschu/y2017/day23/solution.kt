package adventofcode.blueschu.y2017.day23

import adventofcode.blueschu.y2017.day18.RegisterRepository
import java.io.File

val input: List<String> by lazy {
    File("resources/y2017/day23.txt")
        .bufferedReader()
        .use { r -> r.readLines() }
}

fun main(args: Array<String>) {
    println("Part 1: ${part1(input)}")

    println("Part 2: ${part2(input)}")
}

// Instruction value tokens
sealed class Value

data class RegisterKey(val key: Char) : Value()
data class Literal(val value: Long) : Value()

// Assembly instructions
sealed class Instruction

data class Set(val register: RegisterKey, val newValue: Value) : Instruction()
data class Sub(val register: RegisterKey, val delta: Value) : Instruction()
data class Mult(val register: RegisterKey, val factor: Value) : Instruction()
data class JumpNotZero(val condition: Value, val offset: Value) : Instruction()

// parse input line into assembly-like instruction
fun parseInstruction(line: String): Instruction {
    fun valueFor(token: String) = if (token.length == 1 && token.first() in 'a'..'h') {
        RegisterKey(token.first())
    } else Literal(token.toInt().toLong())

    val instr = line.slice(0..2)
    val tokens = line.substring(4).split(' ')
        .map { valueFor(it) }

    return when (instr) {
        "set" -> Set(tokens[0] as RegisterKey, tokens[1])
        "sub" -> Sub(tokens[0] as RegisterKey, tokens[1])
        "mul" -> Mult(tokens[0] as RegisterKey, tokens[1])
        "jnz" -> JumpNotZero(tokens[0], tokens[1])
        else -> throw IllegalArgumentException("Malformed line could not be parsed: $line")
    }
}

class Interpreter(registerInitializer: Long) {
    // Key must be boxed, otherwise a JVM internal error occurs during compilation
    val registerTable = RegisterRepository<Char?, Long>().apply {
        set('a', registerInitializer)
    }

    // For part one
    var multInstrCount = 0

    private var pos = 0

    fun execute(prog: List<Instruction>) {
        while (pos < prog.size) {
            val instr = prog[pos]
            if (instr is Mult) {
                multInstrCount++
            }
            executeInstruction(instr)
        }
    }

    private fun executeInstruction(instr: Instruction) {
        var nextJump = 1
        when (instr) {
            is Set -> registerTable[instr.register.key] = instr.newValue.inferredValue

            is Sub -> registerTable[instr.register.key] -= instr.delta.inferredValue

            is Mult -> registerTable[instr.register.key] *= instr.factor.inferredValue

            is JumpNotZero -> if (instr.condition.inferredValue != 0L) {
                nextJump = instr.offset.inferredValue.toInt()
            }
        }
        pos += nextJump
    }

    private val Value.inferredValue
        get() = when (this) {
            is RegisterKey -> registerTable[this.key]
            is Literal -> this.value
        }
}

fun part1(lines: List<String>): Int {
    val instructions = lines.map { parseInstruction(it) }
    val interpreter = Interpreter(0)
    interpreter.execute(instructions)
    return interpreter.multInstrCount
}

// Brute force
// todo Implement optimization
fun part2(lines: List<String>): Long {
    val instructions = lines.map { parseInstruction(it) }
    val interpreter = Interpreter(1)
    interpreter.execute(instructions)
    return interpreter.registerTable['h']
}
