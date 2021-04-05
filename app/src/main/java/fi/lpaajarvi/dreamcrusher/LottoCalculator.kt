package fi.lpaajarvi.dreamcrusher

import android.util.Log

class LottoCalculator(var playerNumbersArray : IntArray) {

    var MIN_NUMBER = 1
    var MAX_NUMBER = 39
    var NUMBER_AMOUNT = 7

    // currentLotto = how many lotteries have been played
    var currentLotto=0
    // currentBest = best result (how many right out of NUMBER AMOUNT) so far
    var currentBest=0

    // holds info on how many lotteries it took to first time reach the number
    // of correct ones in a lottery.
    //
    // For example: First lottery: 0 right
    //              Second Lottery: 1 right
    //              ... third and fourth 0-1 right ...
    //              Fifth Lottery: 3 right
    //
    // would result in an array of [2, 5, 5, 0, 0, 0, 0]
    // where indexes marked with 0 will be replaced later after those targets
    // are reached
    val resultTable = IntArray(NUMBER_AMOUNT)


    // holds all the generated numbers in single lotto results, where currentBest was beaten
    //
    // index 0: [3, 4, 12, 25, 33, 35, 39] if player only had 4 from this line
    // index 1: same as index 0 except this time 2 was right etc
    //
    // Some indexes might be left empty; If there was never a line
    // that had only 1 right, but instead there was 2 right right away so currentBest was added
    // by 2 numbers same lottery, then the index 0 will be left empty

    // val winningLines = Array<IntArray>(NUMBER_AMOUNT)

    var winningLines = Array(NUMBER_AMOUNT) { IntArray(NUMBER_AMOUNT) }

    var playerNumbers = playerNumbersArray.toSet()

    fun doSingleLottery() : Set<Int> {
        currentLotto++
        val row = generateRow()
        val corrects = howManyRight(row, playerNumbers)

        var resultString= ""

        if (corrects > currentBest) {

            resultTable[corrects-1] = currentLotto

            // sorting and saving the result row in winningLines
            var sortedRow = row.sorted()
            winningLines[corrects-1] = sortedRow.toIntArray()

            currentBest = corrects

        }
        return row
    }

    // generating 7 unique numbers between 1-40
    fun generateRow() : Set<Int> {

        var row = mutableSetOf<Int>()
        while (row.size < NUMBER_AMOUNT) {
            val item = (MIN_NUMBER..MAX_NUMBER).random()
            row.add(item)
        }
        return row
    }
    fun howManyRight(a: Set<Int>, b: Set<Int>) : Int {
        var corrects : Int = 0
        for (i in 0..a.size-1) {
            if (a.elementAt(i) in b) {
                corrects++
            }
        }
        return corrects
    }

    fun generateLotteryResults() : Array<IntArray> {
        while (currentBest < NUMBER_AMOUNT) {
            doSingleLottery()
        }

        return buildFinalResultArray()

    }
    fun buildFinalResultArray() : Array<IntArray> {

        //creating a new array of intArrays, where index 0 holds the resultTable and
        //index 1-7 hold the each winningLine in the same order

        val finalResultArray = Array(NUMBER_AMOUNT+1) { IntArray(NUMBER_AMOUNT) }

        finalResultArray[0] = resultTable
        for (i in 1..NUMBER_AMOUNT) {
            finalResultArray[i] = winningLines[i-1]
        }

        return finalResultArray

    }
}