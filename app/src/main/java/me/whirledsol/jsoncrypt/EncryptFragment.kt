package me.whirledsol.jsoncrypt

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts


class EncryptFragment: MainFragment() {
    private lateinit var _fileChoser : ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        super.onCreateView(inflater, container, savedInstanceState)

        _fileChoser = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK && result.data?.data is Uri) {
                onFileSelected(result.data?.data as Uri)
            }
        }
        return inflater.inflate(R.layout.fragment_encrypt, container, false)
    }

    override fun validate(): Boolean{
        return super.validate()
    }

    /**
     * onFileSelected
     */
    override fun onFileSelected(uri: Uri) {
        _filePath = uri
        _textFile.text = _fileUtil.resolveFilenameFromUri(_filePath)
        _inputPassword.requestFocus()
    }

    /**
     * onSelectFile
     */
    override fun onSelectFile(){
        val intent = Intent().setType("*/*").setAction(Intent.ACTION_OPEN_DOCUMENT)
        _fileChoser.launch(Intent.createChooser(intent, "Select a file"))
    }



    /**
     * onExecute
     */
    override fun onExecute(){

        _textMsg.text = ""

        if(!validate()) {
            _textMsg.text = resources.getText(R.string.message_arguments_error)
            return
        }

        val password = _inputPassword.text.toString()

        try {
            //val file = _service.encryptFile(_filePath, password)
            val file = _cryptUtil.encryptFile(_filePath,password)
            _textMsg.text = "File encrypted to ${file.name}. Please delete the original file."
        }
        catch(ex: Exception){
            _textMsg.text = resources.getText(R.string.message_encrypt_error)
            return
        }

    }


}