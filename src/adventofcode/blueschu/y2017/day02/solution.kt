package adventofcode.blueschu.y2017.day02

import java.io.File
import kotlin.test.assertEquals

fun input(): List<List<Int>> = File("resources/y2017/day02.txt")
    .readLines()
    .map { it.split('\t') }
    .map { it.map { it.toInt() } }

fun main(args: Array<String>) {
    assertEquals(18, part1(listOf(
        listOf(5, 1, 9, 5),
        listOf(7, 5, 3),
        listOf(2, 4, 5, 8))))
    println("Part 1: ${part1(input())}")
    assertEquals(9, part2(listOf(
        listOf(5, 9, 2, 8),
        listOf(9, 4, 7, 3),
        listOf(3, 8, 6, 5))))
    println("Part 2: ${part2(input())}")
}

fun part1(input: List<List<Int>>): Int {
    return input.fold (emptyList()) { sum: List<Int>, next ->
        sum + (next.max()!! - next.min()!!)
    }.sum()
}

fun part2(input: List<List<Int>>): Int {
    return input.fold (emptyList()) { sum: List<Int>, next ->
        var offset = 1
        var result = 0
        loop@for (i in 0 until next.count() - 1) {
            for (j in offset until next.count()) {
                val num = next.elementAt(j)
                val dem = next.elementAt(i)
                if (num % dem == 0) {
                    //println("FOUND $num/$dem")
                    result = num / dem
                } else if(dem % num == 0) {
                    //println("FOUND $dem/$num")
                    result = dem / num
                }
                if (result != 0) {
                    break@loop
                }
            }
            offset += 1
        }
        sum + result
    }.sum()
}
