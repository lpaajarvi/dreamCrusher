package fi.lpaajarvi.dreamcrusher

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlin.concurrent.thread


class LottoService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        var playerNumbers : IntArray = intent!!.extras!!.get("Player Numbers") as IntArray

        /*
        This version would have sent the broadcast only once, after whole lottery is done.

        thread() {
            val lottoCalculator = LottoCalculator(playerNumbers)

            // this will take long time and later rows will be completed after this is done
            val results : Array<IntArray> = lottoCalculator.generateLotteryResults()

            var resultIntent = Intent("Lottery Results")
            resultIntent.putExtra("Result Array", results)
            sendBroadcast(resultIntent)
        }
        */

        thread() {
            val lottoCalculator = LottoCalculator(playerNumbers)
            val manager = LocalBroadcastManager.getInstance(this)

            // building the same results as LottoCalculator would return with its
            // generateTheLottoResults() method.
            //
            // that means we are building an array of int arrays where
            // index 0 holds the resultTable (check comments in LottoCalculator for variable
            // resultTable) and
            //        indexes 1-7 hold the each winning row in the same order

            var currentBestWatcher = 0

            while (lottoCalculator.currentBest < 7) {

                var generatedRow = lottoCalculator.doSingleLottery()
                //var i = Intent("single row")
                //i.putExtra("row", generatedRow.toIntArray())
                //manager.sendBroadcast(i)

                if (currentBestWatcher != lottoCalculator.currentBest) {

                    /* It would be possible to send every generated to Receiver (commented out
                    * in outer loop)
                    *
                    * But it got too clumsy changing the visuals of buttons on every row and
                    * whole lottery would have taken ages.
                    *
                    * */
                    var i = Intent("single row")
                    i.putExtra("row", generatedRow.toIntArray())
                    manager.sendBroadcast(i)

                    // Thread.sleep(500)
                    var resultArray : Array<IntArray> = lottoCalculator.buildFinalResultArray()

                    var resultIntent = Intent("Lottery Results")
                    resultIntent.putExtra("Result Array", resultArray)
                    manager.sendBroadcast(resultIntent)

                    // waiting here so it will be more clear to player with which row they have
                    // won with while it happens
                    Thread.sleep(2000)

                    currentBestWatcher = lottoCalculator.currentBest
                }
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}