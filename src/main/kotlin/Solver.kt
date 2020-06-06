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

        while (top.parent != null && top.parent != parentNode) {
            top = top.parent!!
        }

        mapPossibilities(findValue, this.hand, parentNode, fromDouble = true, topNode = top)
    }
}

private fun mapPossibilities(findValue: Int, hand: List<Domino>, parentNode: Node, fromDouble: Boolean = false, topNode: Node? = null) {
    fun newNode(domino: Domino, flip: Boolean) {
        domino.copy().apply {
            var useDomino = this

            if (flip)
                useDomino = this.flip()

            if (!fromDouble)
                parentNode.addCandidate(
                        useDomino,
                        null,
                        hand.toMutableList().also {
                            it.remove(domino)
                        }
                )
            else
                parentNode.addCandidate(
                        topNode!!.fullCopy(),
                        useDomino,
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
        var useBranch: Node? = candidate.branch1

        if (fromDouble)
            useBranch = candidate.branch2

        if (!fromDouble || candidate.branch2 != null)
            if (!candidate.branch1.mainDomino.isDouble)
                mapPossibilities(
                        useBranch!!.mainDomino.rightValue,
                        useBranch.hand,
                        useBranch
                )
            else
                mapDoublePossibilities(
                        useBranch!!.mainDomino.rightValue,
                        useBranch.hand,
                        useBranch
                )
    }
}

private fun cleanNodeToBest(root: Node): Pair<Node, Int> {
    val bestNode = findBestNode(root)
    var useNode = bestNode

    while (useNode.parent != null) {
        val useNodeParent = useNode.parent!!
        val findCandidate = useNodeParent.findCandidateExact(useNode)
        val removeIndexes = arrayOfNulls<Candidate>(useNodeParent.candidates.size)
        var removeIndexIndex = 0

        for (candidateCompare in useNodeParent.candidates) {
            if (candidateCompare.branch1 == findCandidate.branch1) {
                if (candidateCompare.branch2 == null && findCandidate.branch2 == null) {
                    continue
                } else if (candidateCompare.branch2 != null && findCandidate.branch2 != null) {
                    if (candidateCompare.branch2 == findCandidate.branch2) {
                        continue
                    }
                }
            }

            removeIndexes[removeIndexIndex] = candidateCompare
            removeIndexIndex++
        }

        for (removeCandidate in removeIndexes) {
            if (removeCandidate != null) {
                useNodeParent.candidates.remove(removeCandidate)
            }
        }

        useNode = useNodeParent
    }

    return Pair(root, bestNode.hand.size)
}

fun solve(mainValue: Int, hand: List<Domino>): Pair<Node, Int> {
    return Node(mainValue, hand).run {
        mapPossibilities(mainValue, hand, this)

        cleanNodeToBest(this)
    }
}