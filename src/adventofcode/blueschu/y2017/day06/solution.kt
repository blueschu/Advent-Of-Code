package adventofcode.blueschu.y2017.day06

import java.io.File
import kotlin.test.assertEquals

fun input(): List<Byte> {
    return File("resources/y2017/day06.txt")
        .bufferedReader()
        .use { it.readText() }
        .trim()
        .split(Regex("\\s+"))
        .map { it.toByte() } // only 119 blocks in input, might as well as memory
}

fun main(args: Array<String>) {
    // [findAllocationLoop] extended to produce the solution to both parts of the puzzle
    assertEquals(AllocationLoop(2, 1), findAllocationLoop(listOf(0)))
    assertEquals(AllocationLoop(5, 4), findAllocationLoop(listOf(0, 2, 7, 0)))
    println("Solution: ${findAllocationLoop(input())}")
}

// for part 1
class MemoryBanks(private val memory: ByteArray) {

    constructor(memoryBanks: List<Byte>) : this(memoryBanks.toByteArray())

    val image get() = memory.toList()

    fun reallocate() {
        val largestBankIndex = mostPopulatedBankIndex()
        var blockCount = memory[largestBankIndex] // blocks in largest bank
        var seekingIndex = (largestBankIndex + 1) % memory.size
        memory[largestBankIndex] = 0 // empty the bank

        while (blockCount > 0) {
            memory[seekingIndex]++
            seekingIndex = (seekingIndex + 1) % memory.size
            blockCount--
        }
    }

    private fun mostPopulatedBankIndex(): Int {
        var maxVal= Byte.MIN_VALUE
        var maxIndex = 0
        for ((index, value) in memory.withIndex()) {
            if (value > maxVal) {
                maxVal = value
                maxIndex = index
            }
        }
        return maxIndex
    }
}

fun findAllocationLoop(initialMemoryBanks: List<Byte>): AllocationLoop {
    val memory = MemoryBanks(initialMemoryBanks)
    val previousStates = mutableSetOf<List<Byte>>()
    var cycleCount = 0

    while (true) {
        cycleCount++
        val currentImage = memory.run {
            reallocate()
            image
        }
        if (currentImage in previousStates) {
            return AllocationLoop(cycleCount, determineLoopLength(currentImage))
        }
        previousStates.add(currentImage)
    }
}

// added for part 2
data class AllocationLoop(val startsAfterCycles: Int, val lastsCycles: Int)

fun determineLoopLength(memoryAtLoopStart: List<Byte>): Int {
    val memory = MemoryBanks(memoryAtLoopStart)
    var cycleCount = 0

    while (true) {
        cycleCount++
        val currentImage = memory.run {
            reallocate()
            image
        }
        if (currentImage == memoryAtLoopStart) {
            return cycleCount
        }
    }
}
