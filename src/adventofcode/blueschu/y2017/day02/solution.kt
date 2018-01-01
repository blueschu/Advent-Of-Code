package adventofcode.blueschu.y2017.day02

import java.io.File
import kotlin.test.assertEquals

fun input(): List<List<Int>> = File("resources/y2017/day02.txt")
    .readLines()
    .map { it.split('\t').map(String::toInt) }

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

fun part1(table: List<List<Int>>): Int {
    return table.map { (it.max()!! - it.min()!!) }.sum()
}

fun part2(table: List<List<Int>>): Int {
    return table.map { row ->
        var result = 0
        loop@ for (i in 0 until row.count() - 1) {
            for (j in i + 1 until row.count()) {
                val num = row[j]
                val dem = row[i]
                result = when {
                    num % dem == 0 -> num / dem
                    dem % num == 0 -> dem / num
                    else -> result
                }
                if (result != 0) {
                    break@loop
                }
            }
        }
        result
    }.sum()
}
