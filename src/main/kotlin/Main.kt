import kotlin.random.Random

fun generateDomino(hand: List<Domino>, mainDomino: Domino): Domino {
    while (true) {
        Domino(
                Random.nextInt(0, 12),
                Random.nextInt(0, 12)
        ).apply {
            if (!hand.contains(this) && this != mainDomino)
                return this
        }
    }
}

fun generateHand(handSize: Int, mainDomino: Domino): List<Domino> {
    return mutableListOf<Domino>().apply {
        for (i in 0..handSize)
            this.add(generateDomino(this, mainDomino))
    }.toList()
}

// 1-12 Normal domino value
// 0 Is the wild domino value

fun console() {
    println("Hey this is a Mexican Train Solver.\n" +
        "It'll solve your hand or let you generate some random trains!\n" +
        "Solve(0) or Generate(1)")
    val inputSolveOrGenerate = readLine()!!

    if (inputSolveOrGenerate == "0") { // Solve
        println("What is the main double number? (0 is wild)")

        val inputMainDoubleNumber = readLine()!!.toInt()

        println("For your hand type in the number followed - followed by a number (0 is wild) and then type solve\n"
            + "ex. 1-5\n5-0\n2-3\nsolve")

        val hand = mutableListOf<Domino>()

        while (true) {
            val input = readLine()!!

            if (input == "solve") {
                if (hand.size == 0) {
                    println("No hand was given!")

                    console()
                } else {
                    break
                }
            } else {
                val split = input.split('-')

                hand.add(
                    Domino(
                        split[0].toInt(),
                        split[1].toInt()
                    )
                )
            }
        }

        println("\nMain: $inputMainDoubleNumber|$inputMainDoubleNumber")
        println("Hand: $hand")

        val (tree, handSize) = solve(inputMainDoubleNumber, hand)

        println("Hand Size: $handSize")
        tree.printTree()

        console()
    } else { // Generate
        println("Amount of Dominos(2-Infinity)")

        var inputAmountOfDominos = readLine()!!.toInt()

        if (inputAmountOfDominos < 2)
            inputAmountOfDominos = 2

        val mainValue = Random.nextInt(1, 12)
        val mainDomino = Domino(mainValue, mainValue)

        val hand = generateHand(inputAmountOfDominos, mainDomino)

        println("\nMain: $mainDomino")
        println("Hand: $hand")

        val (tree, handSize) = solve(mainValue, hand)

        println("Hand Size: $handSize")
        tree.printTree()

        console()
    }
}

fun main(args: Array<String>) {
    console()
}