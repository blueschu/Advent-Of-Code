package adventofcode.blueschu.y2017.day10

import java.io.File
import kotlin.test.assertEquals

val input by lazy {
    File("resources/y2017/day10.txt")
        .bufferedReader()
        .use { it.readText().trim() }
        .split(',')
        .map(String::toInt)
}

fun main(args: Array<String>) {
    val testKnot = Knot(5)
    assertEquals(12, part1(listOf(3, 4, 1, 5), testKnot))
    println("Part 1: ${part1(input)}")
}

fun part1(lengths: List<Int>, knot: Knot = Knot()): Int {
    lengths.forEach {
        knot.twistLength(it)
    }
    return knot.hash
}

class Knot(private val stringSize: Int = 256) {
    // array of [0,1,2,3,4...stringSize - 1]
    private val _circleMarks = Array<Int>(stringSize, { it })

    // circle marks rotated to their true position
    val circleMarks get() = _circleMarks.copyOf().apply { rotate(position) }

    val hash: Int
        get() {
            val circle = circleMarks
            return circle[0] * circle[1]
        }

    private var skipSize = 0

    private var position = 0

    fun twistLength(lengthFromPos: Int) {
        _circleMarks.reverseRange(0, lengthFromPos - 1)
        advancePos(lengthFromPos)
    }

    // keep "position" always at the first index of the array
    private fun advancePos(length: Int) {
        _circleMarks.rotate(-(length + skipSize))
        position = (position + length + skipSize) % stringSize
        skipSize++
    }
}

fun <T> Array<T>.reverseRange(start: Int, end: Int) {
    var left = start
    var right = end
    while (left < right) {
        val temp = this[left]
        this[left] = this[right]
        this[right] = temp
        left++
        right--
    }
}

fun <T> Array<T>.rotate(shift: Int) {
    val wrappedShift = shift % size
    if (wrappedShift == 0) return
    if (wrappedShift > 0) {
        val offset = size - wrappedShift
        reverseRange(offset, size - 1)
        reverseRange(0, offset - 1)
        reverse()
    } else { // shift < 0
        reverseRange(0, -wrappedShift - 1)
        reverseRange(-wrappedShift, size - 1)
        reverse()
    }
}
