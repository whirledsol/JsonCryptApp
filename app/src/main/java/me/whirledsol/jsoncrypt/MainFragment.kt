package me.whirledsol.jsoncrypt

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import me.whirledsol.jsoncrypt.util.CryptUtil
import me.whirledsol.jsoncrypt.util.FileUtil


abstract class MainFragment : Fragment() {

    protected lateinit var _buttonFile: Button
    protected lateinit var _textFile: TextView
    protected lateinit var _inputPassword: EditText
    protected lateinit var _textMsg: TextView
    protected lateinit var _buttonPeek: ImageButton
    protected lateinit var _buttonExecute: Button


    protected lateinit var _filePath: Uri
    protected lateinit var _cryptUtil: CryptUtil
    protected lateinit var _fileUtil: FileUtil

    /**
     * onCreateView
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return null
    }

    /**
     * onViewCreated
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _cryptUtil = CryptUtil(view.context)
        _fileUtil = FileUtil(view.context)
        _buttonFile = view.findViewById<Button>(R.id.button_file)
        _textFile = view.findViewById<TextView>(R.id.text_file)
        _inputPassword = view.findViewById<EditText>(R.id.input_password)
        _textMsg = view.findViewById<TextView>(R.id.text_msg)
        _buttonPeek = view.findViewById<ImageButton>(R.id.button_peek)
        _buttonExecute = view.findViewById<Button>(R.id.button_execute)

        _buttonFile.setOnClickListener { view ->
            onSelectFile()
        }
        _buttonExecute.setOnClickListener { view ->
            onExecute()
        }

        _buttonPeek.setOnTouchListener { v, event ->
            onPeek(event)
        }
    }

    open fun onSelectFile() {
        //override me
    }

    open fun onExecute() {
        //override me
    }

    /**
     * onFileSelected
     */
    open fun onFileSelected(uri: Uri) {
       //override me
    }

    /**
     * Validate
     */
    open fun validate(): Boolean {
        return _filePath != null && !_inputPassword.text?.toString()?.isNullOrBlank()!!
    }

    /**
     * navigate
     */
    fun navigateResult(data: String) {
        val i = Intent(activity, ResultActivity::class.java)
        i.putExtra(getString(R.string.putExtra_json), data)
        i.putExtra(getString(R.string.putExtra_filePath),_filePath.toString())
        startActivity(i)
    }


    /**
     * onPeek
     */
    private fun onPeek(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            //PEEK
            _inputPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            return true
        }

        if (event.action == MotionEvent.ACTION_UP) {
            //HIDE
            _inputPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD //129
            return true
        }
        return false
    }

}

