package adventofcode.blueschu.y2017.day05

import java.io.File
import kotlin.test.assertEquals

fun input(): List<Int> = File("resources/y2017/day05.txt")
    .readLines()
    .map { it.toInt() }

fun main(args: Array<String>) {
    assertEquals(5, part1(listOf(0, 3, 0, 1, -3)))
    println("Part 1: ${part1(input())}")

    assertEquals(10, part2(listOf(0, 3, 0, 1, -3)))
    println("Part 2: ${part2(input())}")
}

fun part1(input: List<Int>): Int {
    val jumpSequence = input.toTypedArray()
    var jumpCount = 0
    var pos = 0
    while (pos < jumpSequence.size) {
        pos += jumpSequence[pos]++
        ++jumpCount
    }
    return jumpCount
}

fun part2(input: List<Int>): Int {
    val jumpSequence = input.toTypedArray()
    var jumpCount = 0
    var pos = 0
    while (pos < jumpSequence.size) {
        val offset = jumpSequence[pos]
        jumpSequence[pos] += if (offset >= 3) -1 else 1
        pos += offset
        ++jumpCount
    }
    return jumpCount
}
