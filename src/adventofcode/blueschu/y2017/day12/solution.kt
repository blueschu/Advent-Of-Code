package adventofcode.blueschu.y2017.day12

import java.io.File
import kotlin.test.assertEquals

val input: List<String> by lazy {
    File("resources/y2017/day12.txt")
        .bufferedReader()
        .use { it.readLines() }
}

fun main(args: Array<String>) {
    val exampleNetwork = listOf(
        "0 <-> 2",
        "1 <-> 1",
        "2 <-> 0, 3, 4",
        "3 <-> 2, 4",
        "4 <-> 2, 3, 6",
        "5 <-> 6",
        "6 <-> 4, 5"
    )

    assertEquals(6, part1(exampleNetwork))
    println("Part 1: ${part1(input)}")

    assertEquals(2, part2(exampleNetwork))
    println("Part 2: ${part2(input)}")
}

data class NetworkNode(val id: Int) {
    val connections = mutableSetOf<NetworkNode>()

    fun connectWith(other: NetworkNode) {
        if (other === this) return
        other.connections.add(this)
        connections.add(other)
    }
}

fun NetworkNode.findAllNodes(): List<NetworkNode> {
    val indexedNodes = mutableListOf<NetworkNode>(this)

    fun findNewNodes(node: NetworkNode) {
        if (node in indexedNodes) return
        val connectedNodes = node.connections.filterNot { it in indexedNodes }
        indexedNodes.add(node)
        if (connectedNodes.isNotEmpty()) {
            connectedNodes.forEach { findNewNodes(it) }
        }
    }

    connections.forEach {
        findNewNodes(it)
    }

    return indexedNodes
}

fun parseNetwork(description: List<String>): Array<NetworkNode> {
    val pattern = "^(?:\\d{1,4}) <-> ((?:(?:, )?\\d{1,4})+)$".toRegex()

    // node descriptions assumed to start at node 0 and increment
    val parsedDescriptions: List<List<Int>> = description.map {
        val connections = pattern.matchEntire(it)?.groups?.get(1)?.value
            ?: throw IllegalArgumentException("Node description could not be parsed: $it")

        connections.split(", ").map(String::toInt)
    }

    // create a node for each id
    val nodes = Array(parsedDescriptions.size, { NetworkNode(it) })

    // connect the nodes
    for ((id, connections) in parsedDescriptions.withIndex()) {
        connections.forEach { otherId ->
            nodes[id].connectWith(nodes[otherId])
        }
    }

    return nodes
}

fun findDistinctNetworks(nodes: Array<NetworkNode>): List<List<NetworkNode>> {
    val nodePool = nodes.associateBy { it.id }.toMutableMap()

    val networks = mutableListOf<List<NetworkNode>>()

    while (nodePool.isNotEmpty()) {
        val networkNodes = nodePool.values.first().findAllNodes()

        networks.add(networkNodes)

        networkNodes.map(NetworkNode::id).forEach { nodePool.remove(it) }
    }
    return networks
}

fun part1(networkDescription: List<String>): Int =
    parseNetwork(networkDescription).first().findAllNodes().size

fun part2(networkDescription: List<String>): Int =
    findDistinctNetworks(parseNetwork(networkDescription)).size
