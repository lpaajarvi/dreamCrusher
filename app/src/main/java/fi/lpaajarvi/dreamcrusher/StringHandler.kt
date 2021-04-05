package fi.lpaajarvi.dreamcrusher

class StringHandler() {


    fun stringifyRow(row: Set<Int>): List<String> {
        var sortedRow = row.sorted()
        return sortedRow.map { it.toString().padStart(2, '0')}
    }

    fun stringifyFinalResults(fullResults: Array<IntArray>): String {

        var resultTable = fullResults[0]


        var finalResultString: String = ""
        for (i in 1..resultTable.size) {
            if (resultTable[i - 1] != 0) {

                finalResultString += stringifyRow(fullResults[i].toSet())
                finalResultString += "\n"
                val doubleYears: Double = ((resultTable[i - 1]).toDouble() / 52)
                finalResultString += "Getting " + i + " right took " + "%.2f".format(doubleYears) + " years.\n"
            }
        }

        return finalResultString
    }



}