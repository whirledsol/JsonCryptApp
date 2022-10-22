package me.whirledsol.jsoncrypt

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import android.R as androidR


class DecryptFragment: MainFragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        super.onCreateView(inflater, container, savedInstanceState)

        return inflater.inflate(R.layout.fragment_decrypt, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tryGetSingleFile()
    }

    /**
     * tryGetSingleFile: if only one file, just select it. saves time. ;)
     */
    private fun tryGetSingleFile(){
        val files = _cryptUtil.getEncryptedFiles()
        if(files.count() == 1){
            onFileSelected(_cryptUtil.getEncryptedFile(files.first().name))
        }
    }

    /**
     * onSelectFile
     */
    override fun onSelectFile(){
        val files = _cryptUtil.getEncryptedFiles()

        if(files.isEmpty()) {
            Toast.makeText(this.requireContext(),"No encrypted files.",Toast.LENGTH_SHORT)
            return
        }

        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this.requireContext())
        dialogBuilder.setTitle("Select Encrypted File")

        val arrayAdapter = ArrayAdapter<String>(this.requireContext(),androidR.layout.select_dialog_singlechoice)
        arrayAdapter.addAll(files.map{it.name})

        dialogBuilder.setNegativeButton("cancel") { dialog, _ -> dialog.dismiss() }

        dialogBuilder.setAdapter(arrayAdapter) { _, which ->
            var filename =  arrayAdapter.getItem(which)!!
            onFileSelected(_cryptUtil.getEncryptedFile(filename))
        }
        dialogBuilder.show()
    }

    /**
     * onFileSelected
     */
    override fun onFileSelected(uri: Uri) {
        _filePath = uri
        _textFile.text = _filePath.lastPathSegment.toString()
        _inputPassword.requestFocus()
    }

    /**
     * onDecrypt
     */
    override fun onExecute(){
        _textMsg.text = ""

        if(!validate()) {
            _textMsg.text = resources.getText(R.string.message_arguments_error)
            return
        }
        var password = _inputPassword.text.toString()

        try {
            var json = _cryptUtil.decryptFile(_filePath,password)
            _inputPassword.setText("") //clear ASAP
            navigate(json)
        }
        catch(ex: Exception){
            _textMsg.text = "${resources.getText(R.string.message_decrypt_error)}" //: ${ex.message}"
            return
        }


    }



}