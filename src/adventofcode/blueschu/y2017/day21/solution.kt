package adventofcode.blueschu.y2017.day21

import java.io.File
import java.util.*

val initialImage: Array<BitSet> = arrayOf(
    BitSet(3).apply { set(1) },
    BitSet(3).apply { set(0) },
    BitSet(3).apply { set(0, 3) }
)

val input: List<String> by lazy {
    File("resources/y2017/day21.txt")
        .bufferedReader()
        .use { it.readLines() }
}

fun main(args: Array<String>) {
    println("Part 1: ${renderImage(input, 5)}")

    println("Part 1: ${renderImage(input, 18)}")
}

// Two-Dim Arrays of Booleans are less memory efficient than
// an array of BitSets but prove easier implement with the transformation
// logic required to match pixel chunks against the enhancement rules.
typealias BoolGrid = Array<Array<Boolean>>

// For ease of debugging
fun BoolGrid.render(lineSeparator: String = "/") =
    joinToString(lineSeparator) { it.joinToString("") {if (it) "#" else "."} }

fun BoolGrid.transposed() = BoolGrid(size, { row ->
    Array(size, { col -> this[col][row] })
})

class BitImage(_image: Array<BitSet> = initialImage) {

    var image = _image
        private set

    val litPixels get() = image.sumBy(BitSet::cardinality)

    // Rather esoteric, but functional
    fun transform(rules: List<EnhancementRule>) {
        val chunkDim = if (image.size % 2 == 0) 2 else 3
        val newDim = image.size + (image.size / chunkDim)
        val buffer = Array(newDim) { BitSet(newDim) }

        for ((writeOffsetRow, row) in (0 until image.size step chunkDim).withIndex()) {
            for ((writeOffsetCol, col) in (0 until image.size step chunkDim).withIndex()) {
                val chunk = BoolGrid(chunkDim, { rowOffset ->
                    Array(chunkDim, { colOffset ->
                        image[row + rowOffset][col + colOffset]
                    })
                })

                val rule = rules.find { it matches chunk }
                    ?: throw IllegalStateException("Chunk about [row=$row,col=$col] matches no rule")

                val newChunk = rule.output

                // write new chunk to the buffer
                for (chunkRow in 0 until newChunk.size) {
                    for (chunkCol in 0  until newChunk.size) {
                        buffer[row + chunkRow + writeOffsetRow]
                            .set(col + chunkCol + writeOffsetCol, newChunk[chunkRow][chunkCol])
                    }
                }
            }
        }

        // assign buffer as bitimage
        image = buffer
    }

}

class EnhancementRule(val pattern: BoolGrid, val output: BoolGrid) {

    infix fun matches(chunk: BoolGrid): Boolean {
        fun check(grid: BoolGrid): Boolean {
            // vertical flip
            val mirror = grid.reversedArray()
            return chunk contentDeepEquals grid
                || chunk contentDeepEquals mirror
                || chunk contentDeepEquals grid.map { it.reversedArray() }.toTypedArray()
                || chunk contentDeepEquals mirror.map { it.reversedArray() }.toTypedArray()
        }

        return check(pattern) || check(pattern.transposed())
    }

    // For ease of debugging
    override fun toString(): String {
        return pattern.render() + " => " + output.render()
    }

}

fun parseEnhancementRule(token: String): EnhancementRule {

    fun String.toBoolGrid(onToken: Char = '#'): BoolGrid = split('/')
        .map { str -> Array(str.length) { pos -> str[pos] == onToken } }
        .toTypedArray()

    val (patten, output) = token.split(" => ")

    return EnhancementRule(patten.toBoolGrid(), output.toBoolGrid())
}

fun renderImage(ruleDesc: List<String>, transformCount: Int): Int {
    val rules = ruleDesc.map { parseEnhancementRule(it) }
    val image =  BitImage()
    repeat(times = transformCount) {
        image.transform(rules)
    }
    return image.litPixels
}
