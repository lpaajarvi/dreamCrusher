package fi.lpaajarvi.dreamcrusher

import android.annotation.SuppressLint
import android.content.Intent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.graphics.Color
import android.widget.TextView
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class MainActivity : AppCompatActivity() {

    var numberHandler = NumberHandler()
    var buttonHandler = ButtonHandler(this)
    var buttonArray = arrayOfNulls<Button>(39)


    var singleRowReceiver = RowListener()
    var lotteryReceiver = ResultListener()

    lateinit var startButton: Button
    lateinit var summaryText: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton = findViewById(R.id.StartButton)
        buttonHandler.disableButton(startButton)
        summaryText = findViewById(R.id.ResultTextView)
        // Just a fast way to do this, not pretty
        summaryText.text="This app generates Lottery.\n " + "\n" +
                "Just pick your lucky numbers\n" +
                "and it will tell you how long\n"+
                "it would take for you to win, if\n"+
                "if you played every week with\n"+
                "those same numbers."

        // Initializing buttonArray,
        // getting references of number Buttons that are initialized in XML
        for (i in 0..38) {
            val bnumber = "b"+ (i+1)
            val resIDbutton = resources.getIdentifier(bnumber, "id", packageName)
            buttonArray[i] = (findViewById(resIDbutton))
            buttonArray[i]!!.setOnClickListener {
                clickButton(i)
            }
            // making sure everything has the same starting colors
            buttonHandler.unpressButton(buttonArray[i]!!)
        }
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(singleRowReceiver, IntentFilter("single row"))

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(lotteryReceiver, IntentFilter("Lottery Results"))

    }
    fun clickButton(number : Int) {

        var enabled : Boolean = numberHandler.handleNumber(number+1)

        if (enabled) {
            buttonHandler.pressButton(buttonArray[number]!!)
        } else {
            buttonHandler.unpressButton(buttonArray[number]!!)
        }

        if (numberHandler.isFullSet()) {
            buttonHandler.enableButton(startButton)
        } else {
            buttonHandler.disableButton(startButton)
        }
        Log.d("CHOSEN", numberHandler.playerNumbers.toString())
    }

    fun startLottery(v: View) {

        buttonHandler.transformIntoLoadingButton(startButton)

        var intent = Intent(this, LottoService::class.java)
        intent.putExtra("Player Numbers", numberHandler.playerNumbers.toIntArray())
        startService(intent)

        // disabling all buttons since the lottery started
        buttonHandler.disableButtons(buttonArray)
    }

    fun stopLottery(v: View) {
        stopService(Intent(this, LottoService::class.java))
    }

    /**
     * Array of IntArrays may seem confusing at first, the logic behind it is described in
     * LottoService and LottoCalculator classes
     *
     * index 0 is LottoCalculator equivalent of "resultTable" variable while
     * indexes 1-7 are LottoCalculator equivalent of "winningLine" variables indexes 0-6
     */
    inner class ResultListener : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
           val results : Array<IntArray> = intent?.extras?.get("Result Array") as Array<IntArray>

           val stringHandler = StringHandler()

           val resultString =  stringHandler.stringifyFinalResults(results)

           runOnUiThread {
               summaryText.text=resultString
               summaryText.setTextColor(Color.DKGRAY)
               summaryText.setPadding(20,20,20,20)
           }

           // When the Lottery is finished the index 6 is filled, in index 0 is filled (see more
           // about this logic in LottoService and LottoCalculator classes comments

           if (results[0][6] != 0) {
               Toast.makeText(context, "DreamCrusher has finished generating the Lottery.", Toast.LENGTH_LONG).show();
               buttonHandler.transformIntoFinishedButton(startButton)
           }
        }
    }

    /**
     * Recieves single row but in the current version it actually could be just part of ResultListener
     * another intent.extra since in the end they never get sent on different times
     */
    inner class RowListener : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val singleRow : IntArray = intent?.extras?.get("row") as IntArray

            val buttons = mutableListOf<Button>()

            for (i in 1..7) {
                buttons.add(buttonArray[singleRow[i-1]-1]!!)
            }
            buttonHandler.hlHandler.handleHighlights(buttons)
        }

    }
}