package me.whirledsol.jsoncrypt.util

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.charset.StandardCharsets


/**
 * CryptService
 */
class CryptUtil(var _context: Context) {

    /**
     * Show Encrypted Files
     */
    fun getEncryptedFiles(): List<File>{
        val externalDir = _context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        var files = externalDir!!.listFiles()
        return files.toList().sortedByDescending { it.lastModified() }
    }

    /**
     * getEncryptedFile
     */
    fun getEncryptedFile(filename: String): Uri {
        val externalDir = _context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        return Uri.parse("${externalDir}/${filename}")
    }
    /**
     * getEncryptedFile
     */
    fun eraseAll() {
        val externalDir = _context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        externalDir!!.listFiles().forEach{
            it.delete()
        }
    }

    /**
     * decryptFile
     */
    fun decryptFile(path: Uri, password: String): String{

        val treeFile = File(path.path)
        val externalDir = _context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = File(externalDir,treeFile.name)
        if(!file.exists()){throw Exception("File was not encoded by this app.")}
        val masterKey = MasterKey.Builder(_context,password)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val encryptedFile = EncryptedFile.Builder(
            _context,
            file,
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        val inputStream = encryptedFile.openFileInput()
        val byteArrayOutputStream = ByteArrayOutputStream()
        var nextByte: Int = inputStream.read()
        while (nextByte != -1) {
            byteArrayOutputStream.write(nextByte)
            nextByte = inputStream.read()
        }

        return String(byteArrayOutputStream.toByteArray())
    }

    /**
     * encryptFile
     */
    fun encryptFile(path: Uri, password: String): File{
        var fileUtil = FileUtil(_context)
        val encFileName : String = fileUtil.resolveFilenameFromUri(path, false) + ".enc.json"
        val externalDir = _context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val encodedFile = File(externalDir,encFileName)

        //master key
        val masterKey = MasterKey.Builder(_context,password)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        //delete if exists
        if (encodedFile.exists()) { encodedFile.delete(); }

        val encryptedFile = EncryptedFile.Builder(
            _context,
            encodedFile,
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        //get content
        val fileContent = readFile(path).toByteArray(StandardCharsets.UTF_8)

        //write
        encryptedFile.openFileOutput().apply {
            write(fileContent)
            flush()
            close()
        }

        //delete original
        //_context.contentResolver.delete (path,null ,null ); //requires grantUriPermission
        //originalFile.delete() //not that simple

        return encodedFile
    }

    /**
     * readFile
     */
    private fun readFile(path: Uri): String{
        var stream = _context.contentResolver.openInputStream(path)
        return stream!!.bufferedReader().use { it.readText() }
    }



}

