package adventofcode.blueschu.y2017.day19

import java.io.File
import kotlin.test.assertEquals

val input: List<String> by lazy {
    File("resources/y2017/day19.txt")
        .bufferedReader()
        .use { it.readLines() }
}

fun main(args: Array<String>) {
    assertEquals("ABCDEF" to 38, walkMap(listOf(
        "     |          ",
        "     |  +--+    ",
        "     A  |  C    ",
        " F---|----E|--+ ",
        "     |  |  |  D ",
        "     +B-+  +--+ "))
    )
    val (chars, steps) = walkMap(input)
    println("Part 1: $chars")
    println("Part 2: $steps")

}

data class Point(var x: Int, var y: Int)

enum class Direction {
    NORTH, EAST, SOUTH, WEST;

    fun turnedLeft(): Direction = if (this === NORTH) WEST else values()[ordinal - 1]

    fun turnedRight(): Direction = values()[(ordinal + 1) % values().size]
}

class MapWalker(val tubeMap: List<String>) {

    var stepCount = 0
        private set

    var reachedEnd = false
        private set

    var facing = Direction.SOUTH
        private set

    val passedChars = mutableListOf<Char>()

    val position = Point(tubeMap[0].indexOf('|'), 0)

    fun walk() {
        val nextChar = adjacentChar()
        advance()
        when (nextChar) {
            '+' -> {
                val left = facing.turnedLeft()
                val right = facing.turnedRight()
                facing = when {
                    adjacentChar(left) != ' ' -> left
                    adjacentChar(right) != ' ' -> right
                    else -> throw IllegalStateException(
                        "Map contains no viable path at $position")
                }
            }
            ' ' -> reachedEnd = true
            in 'A'..'Z' -> {
                passedChars.add(nextChar)
            }
        }
    }

    private fun adjacentChar(dir: Direction = facing) = when (dir) {
        Direction.NORTH -> tubeMap[position.y - 1][position.x]
        Direction.EAST -> tubeMap[position.y][position.x + 1]
        Direction.SOUTH -> tubeMap[position.y + 1][position.x]
        Direction.WEST -> tubeMap[position.y][position.x - 1]
    }

    private fun advance() {
        stepCount++
        when (facing) {
            Direction.NORTH -> position.y--
            Direction.EAST -> position.x++
            Direction.SOUTH -> position.y++
            Direction.WEST -> position.x--
        }
    }
}

fun walkMap(tubes: List<String>): Pair<String, Int> {
    val walker = MapWalker(tubes.map { it.padEnd(201, ' ') })
    while (!walker.reachedEnd) {
        walker.walk()
    }
    return walker.passedChars.joinToString(separator = "") to walker.stepCount
}

