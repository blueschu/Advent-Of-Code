package adventofcode.blueschu.y2017.day11

import java.io.File
import kotlin.math.max
import kotlin.test.assertEquals

val input: List<String> by lazy {
    File("resources/y2017/day11.txt")
        .bufferedReader()
        .use { r -> r.readText() }
        .trim()
        .split(",")
}

fun main(args: Array<String>) {
    assertEquals(3, part1(listOf("ne", "ne", "ne")))
    assertEquals(0, part1(listOf("ne", "ne", "sw", "sw")))
    assertEquals(2, part1(listOf("ne", "ne", "s", "s")))
    assertEquals(3, part1(listOf("se", "sw", "se", "sw", "sw")))
    println("Part 1: ${part1(input)}")

    println("Part 2: ${part2(input)}")
}

data class Point(var x: Int = 0, var y: Int = 0)

enum class HexDirection { NORTH, NORTH_EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, NORTH_WEST }

fun parseHexDirection(token: String): HexDirection = when (token) {
    "n" -> HexDirection.NORTH
    "ne" -> HexDirection.NORTH_EAST
    "se" -> HexDirection.SOUTH_EAST
    "s" -> HexDirection.SOUTH
    "sw" -> HexDirection.SOUTH_WEST
    "nw" -> HexDirection.NORTH_WEST
    else -> throw IllegalArgumentException("token can not be parsed as hex direction: $token")
}

// see http://keekerdc.com/2011/03/hexagon-grids-coordinate-systems-and-distance-calculations/
// for an explanation of the hexagonal grid system employed
class HexGridWalker(private val origin: Point = Point()) {
    private val position = origin.copy()

    val distanceFromStart get() = position.hexDistanceFrom(origin)

    fun walk(dir: HexDirection) = when (dir) {
        HexDirection.NORTH -> position.y += 1
        HexDirection.SOUTH -> position.y -= 1
        HexDirection.NORTH_EAST -> position.x += 1
        HexDirection.SOUTH_WEST -> position.x -= 1
        HexDirection.NORTH_WEST -> {
            position.x -= 1
            position.y += 1
        }
        HexDirection.SOUTH_EAST -> {
            position.x += 1
            position.y -= 1
        }
    }

    private fun Point.hexDistanceFrom(other: Point) =
        max(max(x - other.x, y - other.y), -((x - other.x) + (y - other.y)))
}

fun part1(instructions: List<String>): Int {
    val walker = HexGridWalker()
    instructions.map { parseHexDirection(it) }.forEach { walker.walk(it) }
    return walker.distanceFromStart
}

fun part2(instructions: List<String>): Int {
    val walker = HexGridWalker()
    var peakDistance = Int.MIN_VALUE
    instructions.map { parseHexDirection(it) }.forEach {
        walker.walk(it)
        peakDistance = max(peakDistance, walker.distanceFromStart)
    }
    return peakDistance
}
