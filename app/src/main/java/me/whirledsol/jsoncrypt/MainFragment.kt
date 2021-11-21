package me.whirledsol.jsoncrypt

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment


open abstract class MainFragment : Fragment() {

    protected lateinit var _buttonFile: Button
    protected lateinit var _textFile: TextView
    protected lateinit var _inputPassword: EditText
    protected lateinit var _textMsg: TextView
    protected lateinit var _buttonExecute: Button

    protected lateinit var _filePath: Uri
    protected lateinit var _service: CryptUtil

    /**
     * onCreateView
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return null;
    }

    /**
     * onViewCreated
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _service = CryptUtil(view.context)
        _buttonFile = view.findViewById<Button>(R.id.button_file);
        _textFile = view.findViewById<TextView>(R.id.text_file);
        _inputPassword = view.findViewById<EditText>(R.id.input_password);
        _textMsg = view.findViewById<TextView>(R.id.text_msg);
        _buttonExecute = view.findViewById<Button>(R.id.button_execute);

        _buttonFile!!.setOnClickListener { view ->
            onSelectFile()
        }
        _buttonExecute.setOnClickListener { view ->
            onExecute()
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
    fun onFileSelected(uri: Uri) {
        _filePath = uri;
        _textFile.text = _filePath.lastPathSegment.toString();
        _inputPassword.requestFocus()
    }

    /**
     * Validate
     */
    fun validate(): Boolean {
        return _filePath != null && !_inputPassword.text?.toString()?.isNullOrBlank()!!;
    }

    /**
     * navigate
     */
    fun navigate(data: String) {
        val i = Intent(activity, ResultActivity::class.java)
        i.putExtra("json", data)
        startActivity(i)
    }

}


