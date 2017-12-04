package adventofcode.blueschu.y2017.day03

import kotlin.test.assertEquals

fun input(): Int = 289326

fun main(args: Array<String>) {
    assertEquals(0, part1(1))
    assertEquals(3, part1(12))
    assertEquals(2, part1(23))
    assertEquals(31, part1(1024))
    println("Part 1: ${part1(input())}")
}

fun part1(input: Int): Int {
    if (input == 1) return 0 // 1 maps to 0
    if (input < 1) return -1 // ignore invalid cell numbers

    // Distance along perpendicular from origin; may be vertical or horizontal.
    // Also equal to the distance from a ring corner to a ring center.
    val perpendicularDistance =
        (Math.ceil(Math.sqrt(input.toDouble())) / 2).toInt()

    // "index" of the ring containing the memory cell.
    // The square of this value is the cell number in the bottom right corner
    // of the ring.
    val ringIndex = (perpendicularDistance * 2) + 1

    // Distance "walked" around ring clockwise from ring index to input.
    // e.g. 49 -> 48 -> 47 ... -> 26
    val distanceAroundRing = (ringIndex * ringIndex) - input

    // Distance from the center of the ring (at the end of a perpendicular
    // distance) to the memory cell.
    val offsetDistance =
        Math.abs(distanceAroundRing % (ringIndex - 1) - perpendicularDistance)

    return perpendicularDistance + offsetDistance
}
