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

class InfectionGrid(val infectedNodes: MutableList<Point>, carrierPos: Point) {

    inner class Carrier(val pos: Point) {

        var infectionCount = 0

        var facing = Direction.NORTH
            private set

        fun runBurst() {
            if (pos in infectedNodes) {
                facing = facing.turnedRight()
                infectedNodes.remove(pos)
            } else {
                facing = facing.turnedLeft()
                infectedNodes.add(pos.copy())
                infectionCount++
            }
            advance()
        }

        private fun advance() = when (facing) {
            Direction.NORTH -> pos.y += 1
            Direction.EAST -> pos.x += 1
            Direction.SOUTH -> pos.y -= 1
            Direction.WEST -> pos.x -= 1
        }
    }

    val carrier = Carrier(carrierPos)
}

fun parseInfectionGrid(desc: List<String>): InfectionGrid {
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
    return InfectionGrid(infectedNodes, Point(0,0))
}

fun part1(infectionDesc: List<String>): Int {
    val infectionGrid = parseInfectionGrid(infectionDesc)
    repeat(times = 10_000) {
        infectionGrid.carrier.runBurst()
    }

    return infectionGrid.carrier.infectionCount
}
