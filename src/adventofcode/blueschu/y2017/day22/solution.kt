package adventofcode.blueschu.y2017.day22

import java.io.File
import kotlin.test.assertEquals

val input: List<String> by lazy {
    File("resources/y2017/day22.txt")
        .bufferedReader()
        .use { it.readLines() }
}

fun main(args: Array<String>) {
    assertEquals(5587, part1(listOf(
        "..#",
        "#..",
        "...")))

    println("Part 1: ${part1(input)}")

    assertEquals(2511944, part2(listOf(
        "..#",
        "#..",
        "...")))

    println("Part 2: ${part2(input)}")
}

data class Point(var x: Int, var y: Int)

enum class Direction {
    NORTH, EAST, SOUTH, WEST;

    fun turnedLeft(): Direction = when (this) {
        NORTH -> WEST
        WEST -> SOUTH
        SOUTH -> EAST
        EAST -> NORTH
    }

    fun turnedRight(): Direction = when (this) {
        NORTH -> EAST
        EAST -> SOUTH
        SOUTH -> WEST
        WEST -> NORTH
    }
}

class Position(val loc: Point = Point(0, 0), facing: Direction = Direction.NORTH) {
    var facing = facing
        private set

    fun advance() = when (facing) {
        Direction.NORTH -> loc.y += 1
        Direction.EAST -> loc.x += 1
        Direction.SOUTH -> loc.y -= 1
        Direction.WEST -> loc.x -= 1
    }

    fun turnLeft() {
        facing = facing.turnedLeft()
    }

    fun turnRight() {
        facing = facing.turnedRight()
    }
}

abstract class AbstractCarrier(val pos: Position) {
    var infectionCount = 0
        protected set

    abstract fun burst()
}

// Carrier for part one
class CarrierOne(val infectedNodes: MutableList<Point>,
                 pos: Position = Position()) : AbstractCarrier(pos) {

    override fun burst() {
        val position = pos.loc
        if (position in infectedNodes) {
            pos.turnRight()
            infectedNodes.remove(position)
        } else {
            pos.turnLeft()
            infectedNodes.add(position.copy())
            infectionCount++
        }
        pos.advance()
    }
}

// For part two
enum class NodeStatus { INFECTED, CLEAN, FLAGGED, WEAKENED }

// Carrier for part two
class CarrierTwo(val affectedNodes: MutableMap<Point, NodeStatus>,
                 pos: Position = Position()) : AbstractCarrier(pos) {

    override fun burst() {
        val position = pos.loc
        when (affectedNodes.getOrDefault(position, NodeStatus.CLEAN)) {
            NodeStatus.INFECTED -> {
                affectedNodes.put(position.copy(), NodeStatus.FLAGGED)
                pos.turnRight()
            }
            NodeStatus.CLEAN -> {
                affectedNodes.put(position.copy(), NodeStatus.WEAKENED)
                pos.turnLeft()
            }
            NodeStatus.FLAGGED -> {
                affectedNodes.remove(position)
                // reverse direction
                pos.turnRight()
                pos.turnRight()
            }
            NodeStatus.WEAKENED -> {
                affectedNodes.put(position.copy(), NodeStatus.INFECTED)
                // no change in direction
                infectionCount++
            }
        }
        pos.advance()
    }
}

fun parseInfectedNodes(desc: List<String>): MutableList<Point> {
    // grid assumed to be a square with odd dimensions
    val midOffset = (desc.size - 1) / 2
    val infectedNodes = mutableListOf<Point>()

    for ((row, rowVals) in desc.withIndex()) {
        for ((col, elem) in rowVals.withIndex()) {
            if (elem == '#') {
                infectedNodes.add(Point(col - midOffset, midOffset - row))
            }
        }
    }
    return infectedNodes
}

fun part1(infectionDesc: List<String>): Int {
    val infectedNodes = parseInfectedNodes(infectionDesc)
    val carrier = CarrierOne(infectedNodes)

    repeat(times = 10_000) {
        carrier.burst()
    }

    return carrier.infectionCount
}

fun part2(infectionDesc: List<String>): Int {
    val affectedNodes = parseInfectedNodes(infectionDesc)
        .associateBy ({ it }, {NodeStatus.INFECTED})

    val carrier = CarrierTwo(affectedNodes.toMutableMap())

    repeat(times = 10_000_000) {
        carrier.burst()
    }

    return carrier.infectionCount
}
