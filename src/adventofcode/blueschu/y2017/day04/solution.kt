package adventofcode.blueschu.y2017.day04

import java.io.File
import kotlin.test.assertFalse
import kotlin.test.assertTrue

fun input(): List<String> = File("resources/y2017/day04.txt")
    .readLines()

fun main(args: Array<String>) {
    assertTrue { wordsDistinct("aa bb cc dd ee") }
    assertFalse { wordsDistinct("aa bb cc dd aa") }
    assertTrue { wordsDistinct("aa bb cc dd aaa") }
    println("Part 1: ${part1(input())}")

    assertFalse { anagramsDistinct("abcde xyz ecdab")}
    assertTrue { anagramsDistinct("a ab abc abd abf abj") }
    assertTrue { anagramsDistinct("iiii oiii ooii oooi oooo") }
    println("Part 2: ${part2(input())}")
}

fun wordsDistinct(passphrase: String): Boolean {
    val parts = passphrase.split(' ')
    val distinctCount = parts.distinct().size
    return parts.size == distinctCount
}

fun part1(input: List<String>): Int = input.filter { wordsDistinct(it) }.count()

fun anagramsDistinct(passphrase: String): Boolean {
    val parts = passphrase.split(' ')
        .map {it.toCharArray().sorted().toString()}
    val distinctCount = parts.distinct().size
    return parts.size == distinctCount
}

fun part2(input: List<String>): Int = input.filter { anagramsDistinct(it) }.count()
