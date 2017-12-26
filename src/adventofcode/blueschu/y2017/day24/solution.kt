package adventofcode.blueschu.y2017.day24

import java.io.File
import kotlin.test.assertEquals

val input: List<String> by lazy {
    File("resources/y2017/day24.txt")
        .bufferedReader()
        .use { it.readLines() }
}

fun main(args: Array<String>) {

    val exampleSegments = listOf(
        "0/2",
        "2/2",
        "2/3",
        "3/4",
        "3/5",
        "0/1",
        "10/1",
        "9/10")

    assertEquals(31, part1(exampleSegments))
    println("Part 1: ${part1(input)}")

    assertEquals(19, part2(exampleSegments))
    println("Part 2: ${part2(input)}")
}

typealias BridgeSegment = Pair<Int, Int>

typealias Bridge = List<BridgeSegment>

val BridgeSegment.strength get() = first + second

fun BridgeSegment.hasEnd(pins: Int): Boolean = first == pins || second == pins

fun BridgeSegment.flipped() = BridgeSegment(second, first)

infix fun BridgeSegment.matches(other: BridgeSegment): Boolean {
    return first == other.first && second == other.second
        || first == other.second && second == other.first
}

fun Bridge.strength() = sumBy(BridgeSegment::strength)

fun parseBridgeSegment(token: String): BridgeSegment {
    val (first, second) = token.split('/').map(String::toInt)
    return BridgeSegment(first, second)
}

// RAM thirsty devil
fun findNextBridges(bridge: Bridge,
                    segmentPool: List<BridgeSegment>): List<Bridge> {
    val endPins = bridge.last().second
    val validSegments = segmentPool.filter { it.hasEnd(endPins) }
        .map { if (it.first == endPins) it else it.flipped() }

    return if (validSegments.isEmpty()) listOf(bridge) else {
        listOf(bridge) + validSegments.flatMap { next ->
            findNextBridges(bridge.plus(next), segmentPool.filterNot { it matches next }) }
    }
}

fun part1(segmentDesc: List<String>): Int {
    val segments = segmentDesc.map { parseBridgeSegment(it) }

    return findNextBridges(listOf(BridgeSegment(0,0)), segments).map(Bridge::strength).max()
        ?: throw IllegalArgumentException("No possible bridges for the specified segments")
}

fun part2(segmentDesc: List<String>): Int {
    val segments = segmentDesc.map { parseBridgeSegment(it) }

    val bridges: List<Bridge> = findNextBridges(listOf(BridgeSegment(0,0)), segments)

    val maxLength = bridges.map(Bridge::size).max()
        ?: throw IllegalArgumentException("No possible bridges for the specified segments")

    return bridges.filter { it.size == maxLength }.map(Bridge::strength).max()!!
}
