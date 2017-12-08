package adventofcode.blueschu.y2017.day07

import java.io.File
import java.lang.RuntimeException
import kotlin.test.assertEquals

fun input(): List<String> =
    File("resources/y2017/day07.txt").useLines { it.toList() }

fun runDemoAssertions() {
    val demoTower = parseTower(listOf(
        "pbga (66)",
        "xhth (57)",
        "ebii (61)",
        "havc (66)",
        "ktlj (57)",
        "fwft (72) -> ktlj, cntj, xhth",
        "qoyq (66)",
        "padx (45) -> pbga, havc, qoyq",
        "tknk (41) -> ugml, padx, fwft",
        "jptl (61)",
        "ugml (68) -> gyxo, ebii, jptl",
        "gyxo (61)",
        "cntj (57)"))
    assertEquals("tknk", demoTower.name)
}

fun main(args: Array<String>) {
    runDemoAssertions()
    val towerRoot = parseTower(input())
    println("Part 1: ${towerRoot.name}")
}

data class TowerNode(val name: String, val weight: Int) {

    var parent: TowerNode? = null
        private set

    val children = mutableListOf<TowerNode>()

    inline val isRoot get() = parent == null

    inline val isLeaf get() = children.isEmpty()

    fun addChild(node: TowerNode) {
        if (node.parent != null) {
            throw IllegalArgumentException("Node is already a member of a tree")
        }
        node.parent = this
        children.add(node)
    }

    fun branchWeight(): Int =
        weight + if (isLeaf) 0 else children.map { it.branchWeight() }.sum()

    fun findRoot(): TowerNode = if (isRoot) this else parent!!.findRoot()
}

fun parseTower(structureList: List<String>): TowerNode {
    val pattern = "^(\\w+)\\s\\((\\d+)\\)(?:\\s->\\s([\\w,\\s]+))?$".toRegex()
    val parsedStructure = structureList.map {
        pattern.matchEntire(it) ?: throw IllegalStateException("Tower structure contains malformed data")
    }

    // all nodes with name and weight
    val nodes = parsedStructure.map {
        val (name, weight) = it.destructured
        name to TowerNode(name, weight.toInt())
    }.toMap() // map faster than repetitive List#find { it.name == name }

    // map node name to list of child names
    val nonLeafNodes = parsedStructure
        .filter { it.groups[3] != null }
        .associateBy({ it.groups[1]!!.value },
            { it.groups[3]!!.value.split(",\\s+".toRegex()) })

    nonLeafNodes.forEach {
        val (name, children) = it
        children.forEach {
            nodes[name]!!.addChild(nodes[it]!!)
        }
    }

    // return the root node
    return nodes.values.first().findRoot()
}
