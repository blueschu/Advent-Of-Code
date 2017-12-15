package adventofcode.blueschu.y2017.day14

import adventofcode.blueschu.y2017.day10.knotHash
import java.util.*

import kotlin.test.assertEquals

fun input(): String = "ljoxqyyw"

fun main(args: Array<String>) {
    assertEquals("33efeb34ea91902bb2f59c9920caa6cd", knotHash("AoC 2017"))
    assertEquals(8108, part1(generateDisk("flqrgnkx")))
    println("Part 1: ${part1(generateDisk(input()))}")

    assertEquals(1242, part2(generateDisk("flqrgnkx")))
    println("Part 2: ${part2(generateDisk(input()))}")
}


// convert hexadecimal string into a bit set
// original method with byte arrays proved overtly verbose
fun String.hexBits(): BitSet {

    val bitString = this.chunked(4).joinToString(separator = "") {
        it.toInt(16).toString(2).padStart(16, '0')
    }

    val bitSet = BitSet(128)

    // indexes have been ordered to read l-r rather than r-l for ease of debugging
    (0 until bitString.length)
        .filter { bitString[it] == '1' }
        .forEach { bitSet.set(it) }

    return bitSet
}

fun generateDisk(inputString: String) = Array<BitSet>(128) {
    knotHash(inputString + "-" + it).hexBits()
}


fun part1(disk: Array<BitSet>): Int {
    return disk.sumBy { rowBits ->
        (0 until rowBits.size())
            .filter { rowBits[it] } // true if underlying bit == 1
            .size
    }
}

// Equivalent implementation without inlined lambdas
//fun part1(disk: Array<BitSet>): Int {
//    var usedMemorySquares = 0
//    for (rowBits in disk) {
//        for (col in 0 until rowBits.size()) {
//            if (rowBits[col]) usedMemorySquares++
//        }
//    }
//    return usedMemorySquares
//}

fun part2(disk: Array<BitSet>): Int {
    data class Region(val row: Int, val col: Int)

    val groups = mutableListOf<MutableList<Region>>()

    fun addToGroup(groupIndex: Int, square: Region) {
        if (groupIndex == groups.size) {
            groups.add(mutableListOf(square))
        } else {
            groups[groupIndex].add(square)
        }
    }

    fun groupOf(square: Region): Int {
        groups.forEachIndexed() { index, members ->
            if (square in members) return index
        }
        throw IllegalArgumentException("square does not belong to a group: $square")
    }

    fun mergeGroups(destination: Int, others: Int) {
        if (destination == others) return
        groups[destination].addAll(groups[others])
        groups.removeAt(others)
    }

    for ((row: Int, rowBits: BitSet) in disk.withIndex()) {

        for (col in 0 until rowBits.size()) {
            if (rowBits[col]) { // if the grid square is "used"

                val cellAbove = row != 0 && disk[row - 1][col]
                val cellLeft = col != 0 && disk[row][col - 1]

                if (cellAbove && cellLeft) {
                    val groupAbove = groupOf(Region(row - 1, col))
                    val groupLeft = groupOf(Region(row, col - 1))
                    addToGroup(groupAbove, Region(row, col))
                    mergeGroups(groupAbove, groupLeft)

                } else {
                    when {
                        cellAbove -> // square above is also used
                            // get the group of the square above
                            groupOf(Region(row - 1, col)).let {
                                // add the current square to the same group
                                addToGroup(it, Region(row, col))
                            }
                        cellLeft -> // square to the left is also used
                            // get the group of the square to the left
                            groupOf(Region(row, col - 1)).let {
                                // add the current square to the same group
                                addToGroup(it, Region(row, col))
                            }
                        else -> // neither the square above nor the square to the left is used
                            // add the current square to a new group
                            addToGroup(groups.size, Region(row, col))
                    }
                }
            }
        }
    }

    return groups.size
}
