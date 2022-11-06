package me.whirledsol.jsoncrypt.util


import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File

class FileUtil(var _context: Context) {

    @SuppressLint("Range")
    fun resolveFilenameFromUri(uri: Uri, includeExt: Boolean = true): String? {

        val uriString = uri.toString();
        val myFile = File(uriString);
        val path = myFile.getAbsolutePath();
        var displayName : String? = null;

        if (uriString.startsWith("content://")) {
            var cursor: Cursor? = null;
            try {
                cursor = _context.getContentResolver().query(uri, null, null, null, null)!!
                if (cursor != null && cursor.moveToFirst()) {
                    displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor?.close();
            }
        } else if (uriString.startsWith("file://")) {
            displayName = myFile.getName();
        }
        else {
            displayName = uri.lastPathSegment
        }

        if(!includeExt) {
            displayName = if(displayName?.contains(".") == true) displayName.substring(0, displayName.lastIndexOf('.')) else displayName
        }
        return displayName;
    }
}
