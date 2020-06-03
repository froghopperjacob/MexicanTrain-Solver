private fun findDeepestNodes(nodeParent: Node): List<Node> {
    val nodes = mutableListOf<Node>()

    if (nodeParent.candidates.size == 0) {
        nodes.add(nodeParent)
    } else {
        for (candidate in nodeParent.candidates) {
            if (candidate.branch2 == null)
                nodes.addAll(findDeepestNodes(candidate.branch1))
            else
                nodes.addAll(findDeepestNodes(candidate.branch2))
        }
    }

    return nodes
}

private fun findBestNode(nodeParent: Node): Node {
    var bestNode: Node? = null
    val deepestNodes = findDeepestNodes(nodeParent)
    var deepestSize = deepestNodes[0].hand.size

    for (node in deepestNodes) {
        if (node.hand.size < deepestSize) {
            deepestSize = node.hand.size
        }
    }

    for (node in deepestNodes) {
        if (node.hand.size == deepestSize)
            bestNode = node
    }

    return bestNode!!
}

class Node(val mainDomino: Domino, val hand: List<Domino>, val parent: Node? = null) {
    constructor(mainValue: Int, hand: List<Domino>): this(Domino(mainValue, mainValue), hand)

    val candidates: MutableList<Candidate> = mutableListOf()

    fun addCandidate(leftDomino: Domino, rightDomino: Domino?, hand: List<Domino>) {
        candidates.add(
                Candidate(
                        Node(leftDomino, hand, this),
                        if (rightDomino != null) {
                            Node(rightDomino, hand, this)
                        } else {
                            null
                        },
                        this
                )
        )
    }

    fun fullCopy(): Node {
        return Node(mainDomino, hand, parent).also {
            for (candidate in candidates) {
                it.candidates.add(candidate.fullCopy())
            }
        }
    }

    fun addCandidate(leftNode: Node, rightDomino: Domino?, hand: List<Domino>) {
        candidates.add(
                Candidate(
                        leftNode,
                        if (rightDomino != null) {
                            Node(rightDomino, hand, this)
                        } else {
                            null
                        },
                        this
                )
        )
    }

    fun findCandidate(node: Node): Candidate {
        return candidates.first {
            candidate ->  candidate.branch1 == node || candidate.branch2 == node
        }
    }

    fun findCandidateBest(node: Node): Candidate? {
        val finds = mutableListOf<Candidate>()

        for (candidate in candidates) {
            if (candidate.branch1 == node || candidate.branch2 == node) {
                finds.add(candidate)
            }
        }

        var bestHandSize = finds[0].hand.size

        for (candidate in finds) {
            if (candidate.hand.size < bestHandSize) {
                bestHandSize = candidate.hand.size
            }
        }

        for (candidate in finds) {
            if (candidate.hand.size == bestHandSize)
                return candidate
        }

        return null
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Node) {
            other.mainDomino == this.mainDomino && other.parent == this.parent
        } else {
            super.equals(other)
        }
    }

    fun printTree(isBranch2: Boolean = false) {
        if (parent == null)
            println(mainDomino)

        if (candidates.size > 0) {
            if (!isBranch2)
                println("|")
            else
                println("*")

            println("(${candidates[0].branch1.mainDomino}) | (${candidates[0].branch2?.mainDomino})")

            candidates[0].branch1.printTree()
            candidates[0].branch2?.printTree(true)
        }
    }

    fun copy(): Node {
        return Node(mainDomino, hand, parent)
    }
}