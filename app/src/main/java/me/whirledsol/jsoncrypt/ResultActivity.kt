package me.whirledsol.jsoncrypt

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class ResultActivity : AppCompatActivity() {

    private lateinit var _json: JSONObject;
    private lateinit var _jsonviewer: TextView
    private lateinit var _input_searchvalues : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_result)

        val json: String? = intent.extras?.getString("json");
        try {
            _json = JSONObject(json)

        }
        catch(ex: Exception){
            navigate()
            return
        }

        _jsonviewer = findViewById<TextView>(R.id.jsonviewer)
        _input_searchvalues = findViewById<EditText>(R.id.input_searchvalues)
        _input_searchvalues.setImeActionLabel("Search", KeyEvent.KEYCODE_ENTER);

        _input_searchvalues.setOnKeyListener(object : View.OnKeyListener{
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {

                if(!shouldFilter(_input_searchvalues.text)){
                    setViewerJson(_json); return true
                }
                // If the event is a key-down event on the "enter" button
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER)
                {
                    onSearch()
                }
                return false
            }
        })

        setViewerJson(_json)

    }


    fun setViewerJson(json: JSONObject?){
        _jsonviewer.text = json?.toString(4) ?: "{}"
    }


    fun shouldFilter(searchValue: Editable): Boolean{
        return ((searchValue ?: "").trim().length > 1 );
    }

    fun onSearch(){
        var searchValue = _input_searchvalues.text;
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
        finishAffinity();
    }
}