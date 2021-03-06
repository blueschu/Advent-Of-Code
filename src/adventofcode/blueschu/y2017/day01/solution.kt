package adventofcode.blueschu.y2017.day01

import java.io.File
import kotlin.test.assertEquals

fun main(args: Array<String>) {
    assertEquals(3, part1("1122"))
    assertEquals(9, part1("923459"))
    assertEquals(4, part1("1111"))
    println("Part 1: ${part1(input())}")

    assertEquals(6, part2("1212"))
    println("Part 2: ${part2(input())}")
}

fun input(): String = File("resources/y2017/day01.txt")
    .bufferedReader()
    .use { it.readText() }.dropLast(1)

fun part1(input: String): Int {
    val digitSequence = input.map { it.toString().toInt() }
    val len = digitSequence.size
    return digitSequence.filterIndexed { index, next -> next == digitSequence[(index + 1) % len] }.sum()
}

fun part2(input: String): Int {
    val digitSequence = input.map { it.toString().toInt() }
    val length = digitSequence.size
    val offset: Int = length / 2
    return digitSequence.filterIndexed { index, next ->
        digitSequence.elementAt((index + offset) % length) == next
    }.sum()
}

