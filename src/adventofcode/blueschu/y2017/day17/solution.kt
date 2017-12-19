package adventofcode.blueschu.y2017.day17

import kotlin.test.assertEquals

// puzzle input
const val PUZZLE_INPUT = 344

fun main(args: Array<String>) {
    assertEquals(638, part1(3))
    println("Part 1: ${part1(PUZZLE_INPUT)}")
    println("Part 2: ${part2(PUZZLE_INPUT)}")
}

// curiously expanding circular buffer
class HurricaneBuffer(private val step: Int) {
    // buffer begins with a single value: 0
    val buffer = mutableListOf(0)

    var pos = 0
        private set

    var nextValue = 1
        private set

    val nextBufferItem get() = buffer[(pos + 1) % buffer.size]

    fun cycle() {
        pos = (pos + step) % buffer.size + 1
        buffer.add(pos, nextValue++)
    }
}

fun part1(step: Int): Int {
    val hurricaneBuffer = HurricaneBuffer(step)
    (1..2017).forEach { _ ->
        hurricaneBuffer.cycle()
    }
    return hurricaneBuffer.nextBufferItem
}

fun part2(step: Int): Int {
    var bufferSize = 1
    var result = 0
    var pos = 0
    for (i in 1..50_000_000) {
        pos = (pos + step) % bufferSize++ + 1
        if (pos == 1) {
            result = i
        }
    }
    return result
}
