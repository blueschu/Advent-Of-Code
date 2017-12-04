package adventofcode.blueschu.y2017.day03

import kotlin.test.assertEquals

fun input(): Int = 289326

fun main(args: Array<String>) {
    assertEquals(0, part1(1))
    assertEquals(3, part1(12))
    assertEquals(2, part1(23))
    assertEquals(31, part1(1024))
    println("Part 1: ${part1(input())}")

    assertEquals(23, part2(20))
    println("Part 2: ${part2(input())}")
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


enum class Direction {
    NORTH, EAST, SOUTH, WEST;

    fun turnedLeft(): Direction =
        if (this == NORTH) WEST else values()[ordinal - 1]

    fun turnedRight(): Direction = values()[(ordinal + 1) % values().size]
}

// for part 2, very messy but functional
class CellGrid(radius: Int) {

    private inner class Walker(var xPos: Int, var yPos: Int) {
        var facing: Direction = Direction.EAST

        fun advance(): Int {
            // advance one cell
            when (facing) {
                Direction.NORTH -> yPos++
                Direction.EAST -> xPos++
                Direction.SOUTH -> yPos--
                Direction.WEST -> xPos--

            }
            // check if cell to the left is open, if so turn
            if (0 == when (facing) {
                Direction.NORTH -> this@CellGrid[xPos - 1, yPos]
                Direction.EAST -> this@CellGrid[xPos, yPos + 1]
                Direction.SOUTH -> this@CellGrid[xPos + 1, yPos]
                Direction.WEST -> this@CellGrid[xPos, yPos - 1]
            }) facing = facing.turnedLeft()

            return loadCurrentCell()
        }

        private fun loadCurrentCell(): Int {
            val cellValue = this@CellGrid[xPos + 1, yPos] +
                this@CellGrid[xPos + 1, yPos + 1] +
                this@CellGrid[xPos, yPos + 1] +
                this@CellGrid[xPos - 1, yPos + 1] +
                this@CellGrid[xPos - 1, yPos] +
                this@CellGrid[xPos - 1, yPos - 1] +
                this@CellGrid[xPos, yPos - 1] +
                this@CellGrid[xPos + 1, yPos - 1]
            this@CellGrid[xPos, yPos] = cellValue
            return cellValue
        }
    }

    // walker starts at the origin
    private val gridWalker: Walker = Walker(radius + 1, radius + 1)

    private val width: Int = (radius * 2) + 1

    private val grid = Array<Int>(width * width, { _ -> 0 })

    private operator fun get(i: Int, j: Int) = try {
        grid[i * width + j]
    } catch (e: IndexOutOfBoundsException) {
        0
    }

    private operator fun set(i: Int, j: Int, value: Int) {
        // println("Adding $value @ [$i, $j]")
        grid[i * width + j] = value

    }

    fun nextCell(): Int = gridWalker.advance()

    init {
        this[radius + 1, radius + 1] = 1
    }
}

fun cellGridFor(cellNumber: Int) =
    CellGrid((Math.ceil(Math.sqrt(cellNumber.toDouble())) / 2).toInt())

fun part2(input: Int): Int {
    val cellGrid = cellGridFor(input)
    while (true) {
        val cell = cellGrid.nextCell()
        if (cell > input) {
            return cell
        }
    }
}
