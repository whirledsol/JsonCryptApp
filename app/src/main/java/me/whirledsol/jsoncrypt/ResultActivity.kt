package me.whirledsol.jsoncrypt

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import me.whirledsol.jsoncrypt.util.CryptUtil
import me.whirledsol.jsoncrypt.util.FileUtil
import me.whirledsol.jsoncrypt.util.JsonUtil
import org.json.JSONObject




class ResultActivity : JsonCryptActivity() {

    private lateinit var _json: JSONObject
    private lateinit var _filePath: Uri
    private lateinit var _jsonviewer: TextView
    private lateinit var _input_searchvalues : EditText
    private lateinit var _button_search : ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        onCreateActionBar()

        _jsonviewer = findViewById<TextView>(R.id.jsonviewer)
        _input_searchvalues = findViewById<EditText>(R.id.input_searchvalues)
        _button_search = findViewById<ImageButton>(R.id.button_search)


        onSetup()


    }

    private fun onSetup(){

        //putExtras _fileName
        val filePath =  intent.extras?.getString(getString(R.string.putExtra_filePath)) ?: ""
        if(filePath == "") {
            throw Exception("Expected filePath")
        }
        _filePath = Uri.parse(filePath)

        //putExtra json
        val json: String? = intent.extras?.getString(getString(R.string.putExtra_json))
        try {
            _json = JsonUtil().safeCastJsonObject(json)
        }
        catch(ex: Exception){
            navigateHome()
            return
        }



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


    private fun setViewerJson(json: JSONObject?){
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_result, menu)
        return true
    }
    /**
     *
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_erase -> {onErase(); return true}
            else -> super.onOptionsItemSelected(item)
        }
    }


    /**
     * erases file
     */
    fun onErase(){
        val builder = AlertDialog.Builder(this@ResultActivity)
        var fileName = FileUtil(applicationContext).resolveFilenameFromUri(_filePath)
        builder.setMessage(getString(R.string.content_erase_confirm).format(fileName))
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                CryptUtil(applicationContext).eraseFile(_filePath)
                onClose()
            }
            .setNegativeButton("No") { dialog, _ ->
                // Dismiss the dialog
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }



    /**
     * navigate
     */
    private fun navigateHome(){
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }

}