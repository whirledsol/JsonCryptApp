package me.whirledsol.jsoncrypt

import android.os.Bundle
import android.os.CountDownTimer
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import me.whirledsol.jsoncrypt.util.CryptUtil

abstract class JsonCryptActivity : AppCompatActivity(){

    private var TIMEOUT_DURATION : Long = 10*60*1000        //10 min
    private var TIMEOUT_WARNING_DURATION: Long = 1*60*1000  //1 min

    private lateinit var _timer : CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setup timeout
        _timer = object : CountDownTimer(TIMEOUT_DURATION, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if(millisUntilFinished == TIMEOUT_DURATION - TIMEOUT_WARNING_DURATION){
                    Toast.makeText(this@JsonCryptActivity,"You will be logged out in ${TIMEOUT_WARNING_DURATION/1000} seconds.",
                        Toast.LENGTH_SHORT)
                }
            }
            override fun onFinish() {
                onClose()
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_erase -> {onErase(); return true}
            R.id.action_close -> {onClose(); return true}
            else -> super.onOptionsItemSelected(item)
        }
    }


    fun onClose() {
        ExitActivity.exitApplication(this@JsonCryptActivity);
    }

    fun onErase(){
        val builder = AlertDialog.Builder(this@JsonCryptActivity)
        builder.setMessage("Are you sure you want to erase all encrypted files?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                CryptUtil(applicationContext).eraseAll()
            }
            .setNegativeButton("No") { dialog, _ ->
                // Dismiss the dialog
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }


    override fun onPause() {
        super.onPause()
        _timer.start()
    }

    override fun onStop() {
        super.onStop()
        _timer.start()
    }

    override fun onResume() {
        super.onResume()
        _timer.cancel()
    }
}