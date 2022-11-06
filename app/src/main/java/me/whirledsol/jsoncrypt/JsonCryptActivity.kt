package me.whirledsol.jsoncrypt

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout


abstract class JsonCryptActivity : AppCompatActivity(){

    private var TIMEOUT_DURATION : Long = 10*60*1000        //10 min
    private var TIMEOUT_WARNING_DURATION: Long = 1*60*1000  //1 min

    private lateinit var _timer : CountDownTimer
    private lateinit var _text_title: TextView
    private lateinit var _text_version : TextView

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

        //prevent screenshot
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
    }

    fun onCreateActionBar(showTabs: Boolean = false){
        //setup elements in action bar
        setSupportActionBar(findViewById(R.id.toolbar))

        //title
        _text_title =  findViewById<TextView>(R.id.toolbar_text_title)
        _text_title.setOnClickListener { view ->
            onClickTitle()
        }

        //version
        _text_version = findViewById<TextView>(R.id.toolbar_text_version)
        _text_version.text = BuildConfig.VERSION_NAME

        val tabLayout = findViewById<TabLayout>(R.id.tabs)
        if(showTabs){
            tabLayout.visibility = View.VISIBLE
        }
    }


    fun openUrl(url: String){
        //open the supplied link the browser
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }


    private fun onClickTitle(){
        //title is clicked, so go back no matter where you are
        var i = Intent(applicationContext,MainActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
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
            R.id.action_about -> {onAbout(); return true}
            R.id.action_close -> {onClose(); return true}
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onAbout(){
        var i = Intent(applicationContext,AboutActivity::class.java)
        startActivity(i)
    }

    fun onClose() {
        ExitActivity.exitApplication(this@JsonCryptActivity);
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