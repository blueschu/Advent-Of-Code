package adventofcode.blueschu.y2017.day09

import java.io.*
import kotlin.test.assertEquals

fun input() = File("resources/y2017/day09.txt")
    .bufferedReader()
    .use { it.readText() }
    .asIterable()

fun main(args: Array<String>) {
    assertEquals(1, part1("{}".asIterable()))
    assertEquals(6, part1("{{{}}}".asIterable()))
    assertEquals(5, part1("{{},{}}".asIterable()))
    assertEquals(16,part1(" {{{},{},{{}}}}".asIterable()))
    assertEquals(1, part1("{<a>,<a>,<a>,<a>}".asIterable()))
    assertEquals(9, part1("{{<ab>},{<ab>},{<ab>},{<ab>}}".asIterable()))
    assertEquals(9, part1("{{<!!>},{<!!>},{<!!>},{<!!>}}".asIterable()))
    assertEquals(3, part1("{{<a!>},{<a!>},{<a!>},{<ab>}}".asIterable()))
    println("Part 1: ${part1(input())}")

    assertEquals(17, part2("<random characters>".asIterable()))
    assertEquals(10, part2("<{o\"i!a,<{i<a>".asIterable()))
    assertEquals(0, part2("<!!!>>".asIterable()))
    println("Part 2: ${part2(input())}")
}

fun part1(input: Iterable<Char>): Int {
    val stream = input.iterator()
    var score = 0
    var depth = 0
    var inGarbage = false
    while (stream.hasNext()) {
        val character = stream.next()
        if (character == '!') {
            stream.next() // skip next character
            continue // continue to next stream item
        }

        if (!inGarbage) {
            when (character) {
                '{' -> depth++
                '}' -> score += depth--
                '<' -> inGarbage = true
            }
        } else if (character == '>') inGarbage = false
    }
    return score
}

fun part2(input: Iterable<Char>): Int {
    val stream = input.iterator()
    var garbageCount = 0
    var inGarbage = false
    while (stream.hasNext()) {
        val character = stream.next()
        if (character == '!') {
            stream.next() // skip next character
            continue // continue to next stream item
        }

        if (inGarbage) {
            if (character == '>') {
                inGarbage = false
            }
            else {
                garbageCount++
            }
        } else if (character == '<') inGarbage = true
    }
    return garbageCount
}
