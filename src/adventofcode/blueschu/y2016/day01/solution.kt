package adventofcode.blueschu.y2016.day01

import java.lang.IllegalArgumentException

data class Point(var x: Int, var y: Int) {
    constructor() : this(0, 0)

    fun distanceFrom(other: Point): Int =
            Math.abs(x - other.x) + Math.abs(y - other.y)
}

enum class Direction {
    NORTH, EAST, SOUTH, WEST;

    fun turnedLeft(): Direction =
            if (this == NORTH) WEST else values()[ordinal - 1]
            //values()[if (ordinal == 0) values().size - 1 else ordinal - 1]

    fun turnedRight(): Direction = values()[(ordinal + 1) % values().size]
}

class Walker(val start: Point, var facing: Direction) {
    val location = start.copy()

    val distanceTravelled get() = start.distanceFrom(location)

    fun turn(token: String) = when (token) {
        "R" -> facing = facing.turnedRight()
        "L" -> facing = facing.turnedLeft()
        else -> throw IllegalArgumentException("Bad turn token")
    }

    fun walk(mag: Int) = when (facing) {
        Direction.NORTH -> location.y += mag
        Direction.EAST -> location.x += mag
        Direction.WEST -> location.x -= mag
        Direction.SOUTH -> location.y -= mag
    }
}

fun main(args: Array<String>) {
    val data = input().split(", ")
    val walker = Walker(Point(), Direction.NORTH)
    data.forEach {
        walker.turn(it.take(1))
        walker.walk(it.substring(1).toInt())
    }
    println(walker.distanceTravelled)

}

fun input(): String = "L4, L3, R1, L4, R2, R2, L1, L2, R1, R1, L3, R5, L2, R5, L4, L3, R2, R2, L5, L1, R4, L1, R3, L3, R5, R2, L5, R2, R1, R1, L5, R1, L3, L2, L5, R4, R4, L2, L1, L1, R1, R1, L185, R4, L1, L1, R5, R1, L1, L3, L2, L1, R2, R2, R2, L1, L1, R4, R5, R53, L1, R1, R78, R3, R4, L1, R5, L1, L4, R3, R3, L3, L3, R191, R4, R1, L4, L1, R3, L1, L2, R3, R2, R4, R5, R5, L3, L5, R2, R3, L1, L1, L3, R1, R4, R1, R3, R4, R4, R4, R5, R2, L5, R1, R2, R5, L3, L4, R1, L5, R1, L4, L3, R5, R5, L3, L4, L4, R2, R2, L5, R3, R1, R2, R5, L5, L3, R4, L5, R5, L3, R1, L1, R4, R4, L3, R2, R5, R1, R2, L1, R4, R1, L3, L3, L5, R2, R5, L1, L4, R3, R3, L3, R2, L5, R1, R3, L3, R2, L1, R4, R3, L4, R5, L2, L2, R5, R1, R2, L4, L4, L5, R3, L4"
