

package fi.lpaajarvi.dreamcrusher

import android.graphics.Color
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


/**
 * It might have more sense to have buttonArray here instead of MainActivity
 * but I don't feel it's worth the time to change everything (more about this
 * in inner class HighlightHandler description)
 */
class ButtonHandler(var host: AppCompatActivity) {

    // needed for the highlight thing
    var pressedButtons = mutableListOf<Button>()

    var hlHandler = HighlightHandler()

    var pressedBgColor = Color.MAGENTA
    var pressedTextColor = Color.BLACK

    var unpressedBgColor = Color.DKGRAY
    var unpressedTextColor = Color.WHITE


    fun pressButton(button: Button) {
        host.runOnUiThread {
            button.setBackgroundColor(pressedBgColor)
            button.setTextColor(pressedTextColor)
            pressedButtons.add(button)
        }
    }
    fun unpressButton(button: Button) {
        host.runOnUiThread {
            button.setBackgroundColor(unpressedBgColor)
            button.setTextColor(unpressedTextColor)

            pressedButtons.remove(button)
        }
    }
    fun enableButton(button: Button) {
        host.runOnUiThread {
            button.isEnabled = true
        }
    }

    fun disableButton(button: Button) {
        host.runOnUiThread {
            button.isEnabled = false
        }
    }

    fun disableButtons(buttons: Array<Button?>) {
        for (item in buttons) {
            if (item != null) {
                item.isEnabled = false
            }
        }
    }

    fun transformIntoLoadingButton(button :Button) {
        button.isEnabled = false
        button.text = "Calculating Lottery... It will take a long time. Feel free to use other apps"
    }

    fun transformIntoFinishedButton(button :Button) {
        button.text="YOU GOT 7 RIGHT. YOU'RE RICH! CONGRATULATIONS!"
    }


    /**
     *
     * This class is more complicated and slower than it had to be
     *  because there doesn't seem to be a way to GET a background color of a button for some
     *  odd reason. Setting it works of course but not getting.
     *
     *  Each button could have a state of its own but didn't feel it would be worth it to
     *  change it since I'm guessing it would still be too slow to have them all rows
     *  flashing every time while millions of them are usually generated before 7 wins.
     *
     */
    inner class HighlightHandler() {

        // Int actually presents a Color
        var highlightedButtons = mutableMapOf<Button, Int>()

        fun handleHighlights(buttons: List<Button>) {

            host.runOnUiThread {
                // first returning previously highlighted buttons to their original color
                removeHighlights()
                addHighlights(buttons)
            }
        }
        fun removeHighlights() {
            if (highlightedButtons.isNotEmpty()) {
                for ((k, v) in highlightedButtons) {
                    var color = -1
                    if (v == 0) {
                        color = unpressedBgColor
                    } else {
                        color = pressedBgColor
                    }
                    k.setBackgroundColor(color)
                }
            }
        }
        fun addHighlights(buttons: List<Button>) {

            for (button in buttons) {
                // need to check if the button is among pressed buttons so we can have a right
                // color value for it
                var color = -1
                if (button in pressedButtons) {
                    color = 1
                } else {
                    color = 0
                }
                highlightedButtons.put(button, color)

                var highlightColor = -1

                if (color == 1) {
                    highlightColor = Color.GREEN
                } else {
                    highlightColor = Color.RED
                }
                button.setBackgroundColor(highlightColor)
            }
        }
    }

}

