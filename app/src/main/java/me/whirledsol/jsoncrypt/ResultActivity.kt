package me.whirledsol.jsoncrypt

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import me.whirledsol.jsoncrypt.util.JsonUtil
import org.json.JSONObject




class ResultActivity : JsonCryptActivity() {

    private lateinit var _json: JSONObject
    private lateinit var _jsonviewer: TextView
    private lateinit var _input_searchvalues : EditText
    private lateinit var _button_search : ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        onCreateActionBar()

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

        //doesn't work on testing phone
        //_input_searchvalues.setImeActionLabel("Search", KeyEvent.KEYCODE_ENTER)

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

        //button to search
        _button_search.setOnClickListener { onSearch(); }

        setViewerJson(_json)

    }


    fun setViewerJson(json: JSONObject?){
        _jsonviewer.text = json?.toString(4) ?: "{}"
    }


    fun shouldFilter(searchValue: Editable): Boolean{
        return (searchValue.trim().length > 1 )
    }

    fun onSearch(){
        var searchValue = _input_searchvalues.text
        if(!shouldFilter(searchValue)){ setViewerJson(_json); return;}
        var json = JsonUtil().searchJsonNode(_json,searchValue.toString())
        _jsonviewer.text = json?.toString(4)?: "{}"
    }


    /**
     * navigate
     */
    fun navigate(){
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }

}