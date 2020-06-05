class Domino(val leftValue: Int, val rightValue: Int) {
    val isDouble: Boolean = leftValue == rightValue
    val hasWild: Boolean = leftValue == 0 || rightValue == 0

    fun flip(): Domino {
        return Domino(rightValue, leftValue)
    }

    fun copy(): Domino {
        return Domino(leftValue, rightValue)
    }

    fun canUseValueWithoutWild(value: Int): Boolean {
        return leftValue == value || rightValue == value
    }

    fun canUseValue(value: Int): Boolean {
        return canUseValueWithoutWild(value) ||
                leftValue == 0 ||
                rightValue == 0
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Domino) {
            (this.leftValue == other.leftValue && this.rightValue == other.rightValue) ||
                    (this.leftValue == other.rightValue && this.rightValue == other.leftValue)
        } else {
            super.equals(other)
        }
    }

    override fun toString(): String {
        return "$leftValue | $rightValue"
    }

    override fun hashCode(): Int {
        var result = leftValue + leftValue

        result = 31 * result + rightValue + leftValue

        return result
    }
}