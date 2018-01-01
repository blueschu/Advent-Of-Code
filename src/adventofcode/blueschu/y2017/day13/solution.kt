package adventofcode.blueschu.y2017.day13

import java.io.File
import kotlin.test.assertEquals

val input: List<String> by lazy {
    File("resources/y2017/day13.txt")
        .bufferedReader()
        .use { r -> r.readLines() }
}

fun main(args: Array<String>) {
    val exampleFirewall = listOf(
        "0: 3",
        "1: 2",
        "4: 4",
        "6: 4"
    )
    assertEquals(24, part1(parseFirewall(exampleFirewall)))
    println("Part 1: ${part1(parseFirewall(input))}")

    assertEquals(10, part2(parseFirewall(exampleFirewall)))
    println("Part 2: ${part2(parseFirewall(input))}")

}

data class FirewallLayer(val depth: Int, val range: Int)

fun parseFirewall(description: List<String>): Array<FirewallLayer> {
    return Array<FirewallLayer>(description.size) {
        val (depth, range) = description[it].split(": ")
        FirewallLayer(depth = depth.toInt(), range = range.toInt())
    }
}

fun part1(firewall: Array<FirewallLayer>): Int {

    fun FirewallLayer.severity() = depth * range

    return firewall.filter {
        it.depth % ((it.range * 2) - 2) == 0
    }.map(FirewallLayer::severity).sum()
}

fun part2(firewall: Array<FirewallLayer>): Int {
    var totalDelay = 0
    nextDelay@ while (true) {
        totalDelay++

        for (layer in firewall) {
            if ((totalDelay + layer.depth) % ((layer.range * 2) - 2) == 0) {
                continue@nextDelay
            }
        }
        return totalDelay
    }
}
