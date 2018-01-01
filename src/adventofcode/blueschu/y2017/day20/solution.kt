package adventofcode.blueschu.y2017.day20

import java.io.File
import kotlin.math.abs
import kotlin.test.assertEquals

val input: List<String> by lazy {
    File("resources/y2017/day20.txt")
        .bufferedReader()
        .use { r -> r.readLines() }
}

fun main(args: Array<String>) {
    println("Part 1: ${part1(input)}")

    assertEquals(1, part2(listOf(
        "p=<-6,0,0>, v=<3,0,0>, a=<0,0,0>",
        "p=<-4,0,0>, v=<2,0,0>, a=<0,0,0>",
        "p=<-2,0,0>, v=<1,0,0>, a=<0,0,0>",
        "p=<3,0,0>, v=<-1,0,0>, a=<0,0,0>"))
    )
    println("Part 2: ${part2(input)}")
}

data class CartesianVector(var x: Int, var y: Int, var z: Int) {
    val manhattan get() = abs(x) + abs(y) + abs(z)

    fun offset(dx: Int, dy: Int, dz: Int) {
        x += dx
        y += dy
        z += dz
    }

    fun offset(delta: CartesianVector) =
        offset(delta.x, delta.y, delta.z)
}

data class Particle(
    val pos: CartesianVector,
    val vel: CartesianVector,
    val acc: CartesianVector
)

fun parseParticle(token: String): Particle {
    val pattern = ("p=<(-?\\d+),(-?\\d+),(-?\\d+)>, " +
        "v=<(-?\\d+),(-?\\d+),(-?\\d+)>, " +
        "a=<(-?\\d+),(-?\\d+),(-?\\d+)>").toRegex()

    val match = pattern.matchEntire(token)
        ?: throw IllegalArgumentException("Particle could not be parsed: $token")

    val result = match
        .groupValues
        .takeLast(9)
        .map(String::toInt)

    val (pos, vel, acc) = result.chunked(3)

    return Particle(
        pos = CartesianVector(pos[0], pos[1], pos[2]),
        vel = CartesianVector(vel[0], vel[1], vel[2]),
        acc = CartesianVector(acc[0], acc[1], acc[2])
    )
}

fun part1(particlesDesc: List<String>): Int {
    val particles = particlesDesc.map { parseParticle(it) }
    // Find the particles with the lowest acceleration
    val minAcc = particles.map { it.acc.manhattan }.min()!!
    val lowestAccParticles = particles.filter { it.acc.manhattan == minAcc }
    // Find the particle with the least initial velocity, assuming that
    // each particles has a distinct velocity
    val result = lowestAccParticles.minBy { it.vel.manhattan }!!
    return particles.indexOf(result)
}

fun part2(particlesDesc: List<String>): Int {
    var particles = particlesDesc.map { parseParticle(it) }

    // 39 ticks produced all possible collisions for the provided input.
    // The required tick count will vary for other puzzle inputs.
    repeat(times = 39) {
        particles.forEach {
            it.vel.offset(it.acc)
            it.pos.offset(it.vel)
        }

        // remove collocated particles
        particles = particles
            .groupBy { it.pos }
            .filter { it.value.size == 1 }
            .flatMap { it.value }
    }
    return particles.size
}
