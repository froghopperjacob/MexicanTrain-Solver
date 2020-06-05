class Candidate(val branch1: Node, val branch2: Node? = null, val parent: Node) {
    val hand = branch2?.hand ?: branch1.hand

    fun copy(): Candidate {
        return Candidate(branch1.copy(), branch2?.copy(), parent)
    }

    fun fullCopy(): Candidate {
        return Candidate(branch1.fullCopy(), branch2?.fullCopy(), parent)
    }
}