package me.whirledsol.jsoncrypt

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.security.crypto.EncryptedFile
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
        var files = externalDir!!.listFiles();
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

        val encryptedFile = EncryptedFile.Builder(
            file,
            _context,
            password,
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

        var originalFile = File(path.path)
        val encFileName : String = originalFile.nameWithoutExtension + ".enc.json"
        val externalDir = _context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val encodedFile = File(externalDir,encFileName)

        //delete if exists
        if (encodedFile.exists()) { encodedFile.delete(); }

        val encryptedFile = EncryptedFile.Builder(
            encodedFile,
            _context,
            password,
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
        //_context.contentResolver.delete (path,null ,null ); //TODO requires grantUriPermission
        //originalFile.delete() //no work

        return encodedFile;
    }

    /**
     * readFile
     */
    private fun readFile(path: Uri): String{
        var stream = _context.contentResolver.openInputStream(path)
        return stream!!.bufferedReader().use { it.readText() }
    }



}

