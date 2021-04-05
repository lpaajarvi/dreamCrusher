package fi.lpaajarvi.dreamcrusher

class NumberHandler {

    var playerNumbers = mutableSetOf<Int>()

    fun handleNumber(number : Int) : Boolean {
        return when {
            playerNumbers.contains(number) -> {
                playerNumbers.remove(number)
                false
            }
            playerNumbers.size < 7 -> {
                playerNumbers.add(number)
                true
            }
            else -> {
                false
            }
        }
    }

    fun isFullSet() : Boolean {
        return playerNumbers.size == 7
    }
}