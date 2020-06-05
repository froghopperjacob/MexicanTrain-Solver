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

    fun findCandidateExact(node: Node): Candidate {
        return candidates.first {
            candidate ->
                ((candidate.branch1 == node) && (candidate.branch1.candidates == node.candidates)) ||
                    ((candidate.branch2 == node) && (candidate.branch2.candidates == node.candidates))
        }
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

    override fun hashCode(): Int {
        var result = mainDomino.hashCode()

        result = 31 * result + (parent?.hashCode() ?: 0)

        return result
    }
}