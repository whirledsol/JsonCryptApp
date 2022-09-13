package me.whirledsol.jsoncrypt

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import me.whirledsol.jsoncrypt.util.JsonUtil
import org.json.JSONObject




class ResultActivity : AppCompatActivity() {

    private var TIMEOUT_DURATION : Long = 10*60*1000
    private var TIMEOUT_WARNING_DURATION: Long = 1*60*1000

    private lateinit var _json: JSONObject
    private lateinit var _jsonviewer: TextView
    private lateinit var _input_searchvalues : EditText
    private lateinit var _button_search : ImageButton
    private lateinit var _timer : CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_result)

        val json: String? = intent.extras?.getString("json")
        try {
            _json = JsonUtil().safeCastJsonObject(json)
        }
        catch(ex: Exception){
            navigate()
            return
        }


        _jsonviewer = findViewById<TextView>(R.id.jsonviewer)
        _input_searchvalues = findViewById<EditText>(R.id.input_searchvalues)
        _button_search = findViewById<ImageButton>(R.id.button_search)

        //doesn't work on OnePlus
        _input_searchvalues.setImeActionLabel("Search", KeyEvent.KEYCODE_ENTER)
        _input_searchvalues.setOnKeyListener(object : View.OnKeyListener{
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {

                if(!shouldFilter(_input_searchvalues.text)){
                    setViewerJson(_json)
                    return true
                }
                // If the event is a key-down event on the "enter" button
                if (keyCode == KeyEvent.KEYCODE_ENTER)
                {
                    onSearch()
                    return true
                }
                return false
            }
        })

        //second method to search
        _button_search.setOnClickListener { onSearch(); }

        setViewerJson(_json)

        //setup timeout
        _timer = object : CountDownTimer(TIMEOUT_DURATION, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if(millisUntilFinished == TIMEOUT_DURATION - TIMEOUT_WARNING_DURATION){
                    Toast.makeText(this@ResultActivity,"You will be logged out in ${TIMEOUT_WARNING_DURATION/1000} seconds.",Toast.LENGTH_SHORT)
                }
            }
            override fun onFinish() {
                ExitActivity.exitApplication(this@ResultActivity)
            }
        }

    }


    fun setViewerJson(json: JSONObject?){
        _jsonviewer.text = json?.toString(4) ?: "{}"
    }


    fun shouldFilter(searchValue: Editable): Boolean{
        return (searchValue.trim().length > 1 )
    }

    fun onSearch(){
        var searchValue = _input_searchvalues.text
        if(!shouldFilter(searchValue)){return;}
        var json = JsonUtil().searchJsonNode(_json,searchValue.toString())
        _jsonviewer.text = json?.toString(4)?: "{}"
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
            R.id.action_close -> {onClose(); return true}
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * navigate
     */
    fun navigate(){
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }

    fun onClose() {
        ExitActivity.exitApplication(this@ResultActivity)
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