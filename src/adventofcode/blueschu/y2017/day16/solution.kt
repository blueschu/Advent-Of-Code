package adventofcode.blueschu.y2017.day16

import adventofcode.blueschu.y2017.day10.rotate

import java.io.File
import kotlin.test.assertEquals

val input: List<String> by lazy {
    File("resources/y2017/day16.txt")
        .bufferedReader()
        .use { r -> r.readText().trim() }
        .split(',')
}

fun main(args: Array<String>) {
    assertEquals("baedc", part1(listOf("s1","x3/4","pe/b"), floorSize = 5))
    println("Part 1: ${part1(input)}")

    part2(listOf("s1","x3/4","pe/b"), floorSize = 5)
    println("Part 2: ${part2(input)}")
}

sealed class DanceMove
data class Spin(val mag: Int) : DanceMove()
data class Exchange(val posA: Int, val posB: Int) : DanceMove()
data class Partner(val dancerA: Char, val dancerB: Char) : DanceMove()

fun parseDanceMove(token: String): DanceMove {
    val move = token[0]
    val components = token.slice(1 until token.length).split('/')
    return when (move) {
        's' -> Spin(components[0].toInt())
        'x' -> Exchange(components[0].toInt(), components[1].toInt())
        'p' -> Partner(components[0].first(), components[1].first())
        else -> throw IllegalArgumentException("Dance move could not be parsed: $token")
    }
}

class DanceFloor(floorSize: Int, charGen: CharIterator) {
    constructor(floorSize: Int = 16, charRange: CharRange = 'a'..'p') :
        this(floorSize, charRange.iterator())

    val dancers = Array(floorSize) { charGen.nextChar() }

    fun runMove(danceMove: DanceMove) {
        when (danceMove) {
            is Spin -> dancers.rotate(danceMove.mag)
            is Exchange -> exchangeDancers(danceMove.posA, danceMove.posB)
            is Partner -> exchangeDancers(dancers.indexOf(danceMove.dancerA),
                dancers.indexOf(danceMove.dancerB))
        }
    }

    private fun exchangeDancers(posA: Int, posB: Int) {
        if (posA !in 0 until dancers.size || posB !in 0 until dancers.size) {
            throw IllegalArgumentException("Illegal position exchange: $posA <=> $posB")
        }
        val tmp = dancers[posA]
        dancers[posA] = dancers[posB]
        dancers[posB] = tmp
    }

    override fun toString() = dancers.joinToString(separator = "")
}

fun part1(moveStrings: List<String>, floorSize: Int = 16): String {
    val moves = moveStrings.map { parseDanceMove(it) }
    return DanceFloor(floorSize).also {
        floor -> moves.forEach { move -> floor.runMove(move) }
    }.toString()
}

fun part2(moveStrings: List<String>, floorSize: Int = 16): String {
    val moves = moveStrings.map { parseDanceMove(it) }
    val floor = DanceFloor(floorSize)
    val previousPositions = mutableListOf<String>()
    var totalDances = 0

    // add starting position
    previousPositions.add(floor.toString())

    // Find point of repetition
    while (true) {
        totalDances++
        val positions = floor.also {
            moves.forEach { move -> floor.runMove(move) }
        }.toString()

        if (positions in previousPositions) break

        previousPositions.add(positions)
    }

    // determine position after billionth dance
    val finalPosition = 1_000_000_000 % totalDances
    return previousPositions[finalPosition]
}
