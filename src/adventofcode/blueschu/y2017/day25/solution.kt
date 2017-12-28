package adventofcode.blueschu.y2017.day25

import java.io.File
import java.util.*
import kotlin.test.assertEquals

val input: List<String> by lazy {
    File("resources/y2017/day25.txt")
        .bufferedReader()
        .use { r -> r.readLines() }
}

fun main(args: Array<String>) {
    val exampleMachine = listOf("Begin in state A.",
        "Perform a diagnostic checksum after 6 steps.",
        "",
        "In state A:",
        "  If the current value is 0:",
        "    - Write the value 1.",
        "    - Move one slot to the right.",
        "    - Continue with state B.",
        "  If the current value is 1:",
        "    - Write the value 0.",
        "    - Move one slot to the left.",
        "    - Continue with state B.",
        "",
        "In state B:",
        "  If the current value is 0:",
        "    - Write the value 1.",
        "    - Move one slot to the left.",
        "    - Continue with state A.",
        "  If the current value is 1:",
        "    - Write the value 1.",
        "    - Move one slot to the right.",
        "    - Continue with state A.")

    assertEquals(3, part1(exampleMachine))
    println("Part 1: ${part1(input)}")
}

/**
 * Simulates an infinite tape by mapping positive indexes to even indexes and
 * negative indexes to odd indexes.
 *
 * E.g.
 *   1 => 2;  2 => 4
 *  -1 => 1; -2 => 3
 *
 */
class Tape {

    private val store = BitSet()

    val checksum get() = store.cardinality()

    operator fun get(index: Int) = store[tapeIndex(index)]

    operator fun set(index: Int, newValue: Boolean) {
        store[tapeIndex(index)] = newValue
    }
    private fun tapeIndex(token: Int) = if (token >= 0) token * 2 else -(token * 2) + 1

}

data class StateRule(val onAction: StateAction, val offAction: StateAction)

data class StateAction(val newValue: Boolean,
                       val cursorOffset: Int,
                       val nextState: Char)

class TuringMachineEmulator(private val stateRules: Map<Char, StateRule>,
                            val checksumPeriod: Int,
                            initialState: Char = 'A') {

    var cycleCount = 0

    val checksum get() = tape.checksum

    private var cursorPos = 0

    private val tape = Tape()

    private var stateKey: Char = initialState

    private val currentStateRule: StateRule? get() = stateRules[stateKey]

    fun cycle() {
        val rule = currentStateRule
            ?: throw IllegalStateException("No rule for state key: $stateKey")

        val action = if (tape[cursorPos]) rule.onAction else rule.offAction

        with (action) {
            tape[cursorPos] = newValue
            cursorPos += cursorOffset
            stateKey = nextState
        }
        cycleCount++
    }
}

// Parsing methods employ excessive copying for ease of expression

// Assumes that the rule descriptions are properly formatted
fun parseStateRule(desc: List<String>): Pair<Char, StateRule> {
    if (desc.size !in 9..10) { // accepts optional trailing empty line
        throw IllegalArgumentException("Malformed state rule - bad description size: ${desc.size}")
    }
    fun parseStateAction(actionDesc: List<String>): StateAction {
        val newValue = when (actionDesc[1][22]) {
            '1' -> true
            '0' -> false
            else -> throw IllegalArgumentException("Malformed value")
        }
        val offset = when (actionDesc[2].substring(27)) {
            "right." -> 1
            "left." -> -1
            else -> throw IllegalArgumentException("Malformed cursor move")
        }
        val nextKey = actionDesc[3][26]
        return StateAction(newValue, offset, nextKey)
    }

    val stateKey = desc.first()[9]

    val actions = desc.drop(1)
        .chunked(4)
        .take(2)
        .map { parseStateAction(it) }

    return stateKey to StateRule(actions[1], actions[0])
}

// Assumes that the machine description is properly formatted
fun parseTuringMachine(desc: List<String>): TuringMachineEmulator {
    val initialKey = desc.first()[15]

    val checksumDesc = desc[1]
    val checksumPeriod = checksumDesc.slice(36..checksumDesc.length - 8).toInt()

    val rules = desc.drop(3)
        .chunked(10)
        .map { parseStateRule(it) }
        .toMap()

    return TuringMachineEmulator(rules, checksumPeriod, initialKey)
}

fun part1(machineDesc: List<String>): Int {
    val machine = parseTuringMachine(machineDesc)

    machine.run {
        while (cycleCount < checksumPeriod) {
            cycle()
        }
    }

    return machine.checksum
}
