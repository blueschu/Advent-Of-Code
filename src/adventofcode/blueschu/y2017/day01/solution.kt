package adventofcode.blueschu.y2017.day01

import java.io.File
import kotlin.test.assertEquals

fun main(args: Array<String>) {
    assertEquals(part1("1122"), 3)
    assertEquals(part1("923459"), 9)
    assertEquals(part1("1111"), 4)
    println("Part 1: ${part1(input())}")
    assertEquals(part2("1212"), 6)
    println("Part 2: ${part2(input())}")
    println("Input Last: \"${input().last()}\"")
}

fun input(): String = File("resources/y2017/day01.txt")
    .bufferedReader()
    .use { it.readText() }.dropLast(1)

fun part1(input: String): Any {
    val digitSequence = input.map { it.toString() }.map { it.toInt() }
    var prior: Int = digitSequence.last()
    return digitSequence.fold(0) { sum, next ->
        val result = if (prior == next) sum + next else sum
        prior = next
        result
    }
}

fun part2(input: String): Any {
    val digitSequence = input.map { it.toString() }.map { it.toInt() }
    val length = digitSequence.count()
    val offset: Int = length / 2
    return digitSequence.foldIndexed(0) { index, sum, next ->
        if (digitSequence.elementAt((index + offset) % length) == next) {
            sum + next
        } else sum
    }
}

