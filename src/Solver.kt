import java.lang.RuntimeException

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

private fun mapDoublePossibilities(findValue: Int, hand: List<Domino>, parentNode: Node) {
    mapPossibilities(findValue, hand, parentNode) // Map left branch of double

    // This doesn't actually work for making the right branch the best, but i can't figure it out :(
    findBestNode(parentNode).apply {
        var top = this

        while (top.parent!! != parentNode) {
            top = top.parent!!
        }

        fun newCandidate(domino: Domino, flip: Boolean) {
            var dominoCopy = domino.copy()

            if (flip)
                dominoCopy = dominoCopy.flip()

            parentNode.addCandidate(
                    top.fullCopy(),
                    dominoCopy,
                    this.hand.toMutableList().also {
                        it.remove(domino)
                    }
            )
        }

        fun addBothOptions(domino: Domino) {
            for (i in 0..1) {
                newCandidate(domino, i == 1)
            }
        }

        if (this.hand.isNotEmpty()) {
            for (handDomino in this.hand) {
                if (findValue == 0) {
                    addBothOptions(handDomino)
                } else {
                    if (handDomino.canUseValue(findValue)) {
                        if (handDomino.hasWild) {
                            if (handDomino.canUseValueWithoutWild(findValue)) {
                                addBothOptions(handDomino)
                            } else { // Has to use wild
                                newCandidate(handDomino, handDomino.leftValue != 0)
                            }
                        } else {
                            newCandidate(handDomino, handDomino.leftValue != findValue)
                        }
                    }
                }
            }

            for (candidate in parentNode.candidates) {
                if (candidate.branch2 != null) {
                    mapPossibilities(
                            candidate.branch2.mainDomino.rightValue,
                            candidate.branch2.hand,
                            candidate.branch2,
                            candidate.branch2.mainDomino.isDouble
                    )
                }
            }
        }
    }
}

private fun mapPossibilities(findValue: Int, hand: List<Domino>, parentNode: Node, isDouble: Boolean = false) {
    if (isDouble)
        return mapDoublePossibilities(findValue, hand, parentNode)

    fun newNode(domino: Domino, flip: Boolean) {
        domino.copy().apply {
            var useDomino = this

            if (flip)
                useDomino = this.flip()

            parentNode.addCandidate(
                    useDomino,
                    null,
                    hand.toMutableList().also {
                        it.remove(domino)
                    }
            )
        }
    }

    fun addBothOptions(domino: Domino) {
        for (i in 0..1) {
            newNode(domino, i == 1)
        }
    }

    for (handDomino in hand) {
        if (findValue == 0) {
            addBothOptions(handDomino)
        } else {
            if (handDomino.canUseValue(findValue)) {
                if (handDomino.hasWild) {
                    if (handDomino.canUseValueWithoutWild(findValue)) {
                        addBothOptions(handDomino)
                    } else { // Has to use wild
                        newNode(handDomino, handDomino.leftValue != 0)
                    }
                } else {
                    newNode(handDomino, handDomino.leftValue != findValue)
                }
            }
        }
    }

    for (candidate in parentNode.candidates) {
        mapPossibilities(
                candidate.branch1.mainDomino.rightValue,
                candidate.branch1.hand,
                candidate.branch1,
                candidate.branch1.mainDomino.isDouble
        )
    }
}

fun findBestUsage(root: Node): Pair<Node, Int> {
    val newRoot = Node(root.mainDomino.leftValue, root.hand)
    var bestHandSize: Int? = null

    fun createTree(addToTop: Node, addFrom: Node, isRoot: Boolean = false) {
        val bestNode = findBestNode(addFrom)

        if (isRoot)
            bestHandSize = bestNode.hand.size

        if (bestNode.parent == null)
            throw RuntimeException("The best node is root")

        var useNode = bestNode

        val candidates = mutableListOf<Candidate>()
        val doubleNodes = mutableListOf<Node?>()

        while (useNode.parent != null && useNode.parent != addFrom.parent) {
            val candidate = useNode.parent!!.findCandidateBest(useNode)

            candidates.add(candidate!!.copy())

            if(candidate.branch2 != null) {
                if (candidate.branch1.candidates.size > 0)
                    doubleNodes.add(candidate.branch1)
                else
                    doubleNodes.add(null)
            }

            useNode = useNode.parent!!
        }

        var addTo = addToTop
        var doubleNodeIndex = doubleNodes.size - 1

        for (candidateIndex in candidates.size - 1 downTo 0) {
            val candidate = candidates[candidateIndex]

            addTo.candidates.add(candidate)

            addTo = candidate.branch1

            if (candidate.branch2 != null) {
                val doubleNode = doubleNodes[doubleNodeIndex]

                if (doubleNode != null) {
                    createTree(candidate.branch1, doubleNode)
                }

                doubleNodeIndex--

                addTo = candidate.branch2
            }
        }
    }

    createTree(newRoot, root, isRoot = true)

    return Pair(newRoot, bestHandSize!!)
}

fun solve(mainValue: Int, hand: List<Domino>): Pair<Node, Int> {
    return Node(mainValue, hand).run {
        mapPossibilities(mainValue, hand, this)

        findBestUsage(this)
    }
}