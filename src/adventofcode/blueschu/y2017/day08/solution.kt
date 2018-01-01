package adventofcode.blueschu.y2017.day08

import java.io.File
import kotlin.math.max
import kotlin.test.assertEquals

fun input() = File("resources/y2017/day08.txt").useLines { it.toList() }

fun main(args: Array<String>) {
    val exampleInstructions = listOf(
        "b inc 5 if a > 1",
        "a inc 1 if b < 5",
        "c dec -10 if a >= 1",
        "c inc -20 if c == 10"
    )

    assertEquals(1, part1(exampleInstructions))
    println("Part 1: ${part1(input())}")

    assertEquals(10, part2(exampleInstructions))
    println("Part 2: ${part2(input())}")
}

data class Instruction(val action: Action, val condition: Condition)

class Condition(
    private val registerKey: String,
    private val operator: ComparisonOperator,
    private val literal: Int
) {
    constructor(registerKey: String, operator: String, literal: String) :
        this(
            registerKey,
            ComparisonOperator.parseToken(operator),
            literal.toInt()
        )

    enum class ComparisonOperator {
        EQUAL, LESS_THAN, GREATER_THAN, GREATER_OR_EQUAL, LESS_OR_EQUAL, NOT_EQUAL;

        companion object {
            fun parseToken(token: String): ComparisonOperator = when (token) {
                "==" -> EQUAL
                ">" -> GREATER_THAN
                "<" -> LESS_THAN
                ">=" -> GREATER_OR_EQUAL
                "<=" -> LESS_OR_EQUAL
                "!=" -> NOT_EQUAL
                else -> throw IllegalArgumentException("token cannot be mapped to a comparison operator: $token")
            }
        }
    }

    fun satisfiedBy(registerTable: RegisterRepository): Boolean = when (operator) {
        ComparisonOperator.EQUAL -> registerTable[registerKey] == literal
        ComparisonOperator.LESS_THAN -> registerTable[registerKey] < literal
        ComparisonOperator.GREATER_THAN -> registerTable[registerKey] > literal
        ComparisonOperator.GREATER_OR_EQUAL -> registerTable[registerKey] >= literal
        ComparisonOperator.LESS_OR_EQUAL -> registerTable[registerKey] <= literal
        ComparisonOperator.NOT_EQUAL -> registerTable[registerKey] != literal
    }
}

// ensuring expandability as part two may introduce new actions
// ...it didn't
abstract class Action(private val registerKey: String) {
    abstract fun apply(previousValue: Int): Int
    fun applyTo(registerTable: RegisterRepository) {
        registerTable[registerKey] = apply(registerTable[registerKey])
    }
}

class ActionIncrement(registerKey: String, private val magnitude: Int) :
    Action(registerKey) {

    override fun apply(previousValue: Int): Int = previousValue + magnitude
}

class RegisterRepository {
    private val registerTable = mutableMapOf<String, Int>()

    operator fun get(registerKey: String): Int {
        if (registerKey !in registerTable) {
            registerTable += registerKey to 0
        }
        return registerTable[registerKey] ?:
            throw IllegalStateException("Register '$registerKey' missing")
    }

    operator fun set(registerKey: String, value: Int) {
        if (registerKey !in registerTable) {
            registerTable += registerKey to value
        } else {
            registerTable[registerKey] = value
        }
    }

    fun maxRegisterValue() = registerTable.map { it.value }.max() ?: 0
}

class Interpreter(val registerTable: RegisterRepository) {
    constructor() : this(RegisterRepository())

    fun execute(instr: Instruction) {
        if (instr.condition.satisfiedBy(registerTable)) {
            instr.action.applyTo(registerTable)
        }
    }

    fun executeAll(instr: Collection<Instruction>) = instr.forEach { execute(it) }
}

fun parseAction(
    registerKey: String,
    operation: String,
    literal: String
): Action {
    val literalMagnitude = when (operation) {
        "inc" -> literal.toInt()
        "dec" -> -literal.toInt()
        else -> throw IllegalArgumentException("action operator could not be parsed: $operation")
    }
    return ActionIncrement(registerKey, literalMagnitude)
}

fun parseInstruction(instr: String): Instruction {
    val pattern = "^(?<ActReg>\\w+) (?<ActOp>\\w{3}) (?<ActLit>[-\\d]+) if (?<CondReg>\\w+) (?<CondOp>[<=>!]{1,2}) (?<CondLit>[-\\d]+)\$".toRegex()
    val parsedFields = pattern.matchEntire(instr)?.groups
        ?: throw IllegalArgumentException("Malformed instruction: $instr")
    val action = parseAction(
        parsedFields["ActReg"]!!.value,
        parsedFields["ActOp"]!!.value,
        parsedFields["ActLit"]!!.value
    )
    val condition = Condition(
        parsedFields["CondReg"]!!.value,
        parsedFields["CondOp"]!!.value,
        parsedFields["CondLit"]!!.value
    )
    return Instruction(action, condition)
}

fun part1(input: List<String>): Int {
    val interpreter = Interpreter()
    interpreter.executeAll(input.map { parseInstruction(it) })
    return interpreter.registerTable.maxRegisterValue()
}

fun part2(input: List<String>): Int {
    val interpreter = Interpreter()
    val instructions = input.map { parseInstruction(it) }
    var maxRegisterValue = Int.MIN_VALUE
    instructions.forEach {
        interpreter.execute(it)
        maxRegisterValue = max(
            maxRegisterValue,
            interpreter.registerTable.maxRegisterValue()
        )
    }
    return maxRegisterValue
}
