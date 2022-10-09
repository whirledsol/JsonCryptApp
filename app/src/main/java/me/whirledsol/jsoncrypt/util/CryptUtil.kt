package me.whirledsol.jsoncrypt.util

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec


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
     * encryptStream: Guarenteed to work with any URI
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun encryptStream(uri: Uri, password: String): File{
        val contentResolver = _context.contentResolver;
        val externalDir = _context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val encFileName : String = uri.fragment + ".enc.json"
        val encryptedFile = File(externalDir,encFileName)

        //key
        val decodedKey: ByteArray = Base64.getDecoder().decode(password)
        val secretKey: SecretKey = SecretKeySpec(decodedKey, 0, decodedKey.size, "AES")

        //cipher
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        //stream
        val inputStream = contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(encryptedFile)
        val cipherOuputStream = CipherOutputStream(outputStream, cipher)
        inputStream?.copyTo(cipherOuputStream)
        cipherOuputStream.flush()
        cipherOuputStream.close()

        return encryptedFile
    }
    /**
     * encryptFile
     */
    fun encryptFile(path: Uri, password: String): File{
        //TODO: replace with CipherInputStream
        var originalFile = File(path.path)
        val encFileName : String = originalFile.nameWithoutExtension + ".enc.json"
        val externalDir = _context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val encodedFile = File(externalDir,encFileName)
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

