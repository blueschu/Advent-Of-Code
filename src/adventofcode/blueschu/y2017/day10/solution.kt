package adventofcode.blueschu.y2017.day10

import java.io.File
import kotlin.test.assertEquals

val input: String by lazy {
    File("resources/y2017/day10.txt")
        .bufferedReader()
        .use { it.readText() }
        .trim()
}

fun main(args: Array<String>) {
    assertEquals(12, part1("3,4,1,5", Knot(circleSize = 5)))
    println("Part 1: ${part1(input)}")

    assertEquals("a2582a3a0e66e6e86e3812dcb672a272", knotHash(""))
    assertEquals("33efeb34ea91902bb2f59c9920caa6cd", knotHash("AoC 2017"))
    assertEquals("3efbe78a8d82f29979031a4aa0b16a9d", knotHash("1,2,3"))
    assertEquals("63960835bcdc130f0b66d7ff4f6a5a8e", knotHash("1,2,4"))
    println("Part 2: ${knotHash(input)}")

}

fun part1(lengthString: String, knot: Knot = Knot()): Int {
    val lengths = lengthString
        .split(',')
        .map(String::toInt)

    lengths.forEach {
        knot.twistLength(it)
    }
    return knot.hash
}

class Knot(private val circleSize: Int = 256) {
    // array of [0,1,2,3,4...circleSize - 1]
    private val _circleMarks = Array<Int>(circleSize, { it })

    // circle marks rotated to their true position
    val circleMarks get() = _circleMarks.copyOf().apply { rotate(position) }

    // product of first and second mark when the circle has been rotated to its true position
    val hash: Int
        get() = _circleMarks[circleSize - position] * _circleMarks[circleSize - position + 1]

    private var skipSize = 0

    private var position = 0

    fun twistLength(lengthFromPos: Int) {
        _circleMarks.reverseRange(0, lengthFromPos - 1)
        advancePos(lengthFromPos)
    }

    // keep "position" always at the first index of the array
    private fun advancePos(length: Int) {
        _circleMarks.rotate(-(length + skipSize))
        position = (position + length + skipSize) % circleSize
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

// part 2
fun knotHash(stringToBeHashed: String): String {
    fun Knot.twistLength(l: Byte) = twistLength(l.toInt())
    val hashInput = byteArrayOf(*stringToBeHashed.toByteArray(), 17, 31, 73, 47, 23)
    val knot = Knot()
    repeat(times = 64) {
        hashInput.forEach { knot.twistLength(it) }
    }
    val sparseHash = knot.circleMarks
    val denseHash = (0 until 16).map {
        sparseHash
            .copyOfRange(it * 16, it * 16 + 16)
            .reduce { acc, next -> acc xor next }
    }
    return denseHash.fold("") { hash, next: Int ->
        val hex = next.toString(radix = 0x10) // hexadecimal string
        hash + if (hex.length == 1) "0" + hex else hex // pad with leading 0
    }
}
