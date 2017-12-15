package adventofcode.blueschu.y2017.day15

import kotlin.test.assertEquals

// puzzle input
const val GEN_START_A = 722L

const val GEN_START_B = 354L

// Generator factors
const val GEN_FACTOR_A = 16807

const val GEN_FACTOR_B = 48271

// Generator iterations considered by "the judge"
const val PART_ONE_ITERATIONS = 40_000_000

const val PART_TWO_ITERATIONS = 5_000_000

// Make a generator
fun makeGenerator(seed: Long, factor: Int) = generateSequence(seed) { (it * factor) % 2147483647 }

// The generators
val generatorA: Sequence<Long> = makeGenerator(GEN_START_A, GEN_FACTOR_A)

val generatorB: Sequence<Long> = makeGenerator(GEN_START_B, GEN_FACTOR_B)

fun main(args: Array<String>) {
    val exampleGenA = makeGenerator(65, GEN_FACTOR_A)
    val exampleGenB = makeGenerator(8921, GEN_FACTOR_B)
    assertEquals(588, countTailing16BitMatches(
        exampleGenA.take(PART_ONE_ITERATIONS).iterator(),
        exampleGenB.take(PART_ONE_ITERATIONS).iterator()))
    println("Part 1: ${part1()}")

    assertEquals(309, countTailing16BitMatches(
        exampleGenA.filter {it % 4 == 0L}.take(PART_TWO_ITERATIONS).iterator(),
        exampleGenB.filter {it % 8 == 0L}.take(PART_TWO_ITERATIONS).iterator()))
    println("Part 2: ${part2()}")
}

fun part1(): Int = countTailing16BitMatches(
        genA = generatorA.take(PART_ONE_ITERATIONS).iterator(),
        genB = generatorB.take(PART_ONE_ITERATIONS).iterator())

fun part2(): Int = countTailing16BitMatches(
    genA = generatorA.filter {it % 4 == 0L}.take(PART_TWO_ITERATIONS).iterator(),
    genB = generatorB.filter {it % 8 == 0L}.take(PART_TWO_ITERATIONS).iterator())

fun countTailing16BitMatches(genA: Iterator<Long>, genB: Iterator<Long>): Int {
    var matchCount = 0
    while (genA.hasNext() && genB.hasNext()) {
        val a = genA.next()
        val b = genB.next()
        // compare only the last 16 bits
        if (a and 0xFFFF == b and 0xFFFF) matchCount++
    }
    return matchCount
}
