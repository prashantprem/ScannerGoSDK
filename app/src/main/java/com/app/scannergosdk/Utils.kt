package com.app.scannergosdk

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns

import android.webkit.MimeTypeMap
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


object Utils {

    fun getFileName(context: Context, uri: Uri?): String? {
        var fileName = getFileNameFromCursor(context, uri)
        if (fileName == null) {
            val fileExtension = getFileExtension(context, uri)
            fileName = "temp_file" + if (fileExtension != null) ".$fileExtension" else ""
        } else if (!fileName.contains(".")) {
            val fileExtension = getFileExtension(context, uri)
            fileName = "$fileName.$fileExtension"
        }
        return fileName
    }


    private fun getFileExtension(context: Context, uri: Uri?): String? {
        val fileType: String? = uri?.let { context.contentResolver.getType(it) }
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType)
    }

    private fun getFileNameFromCursor(context: Context, uri: Uri?): String? {
        val fileCursor: Cursor? = uri?.let {
            context.contentResolver
                .query(it, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
        }
        var fileName: String? = null
        if (fileCursor != null && fileCursor.moveToFirst()) {
            val cIndex: Int = fileCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cIndex != -1) {
                fileName = fileCursor.getString(cIndex)
            }
        }
        return fileName
    }

     @Throws(IOException::class)
     fun copy(source: InputStream, target: OutputStream) {
         val buf = ByteArray(8192)
         while (true) {
             val length: Int = source.read(buf)
             if (length <= 0) {
                 return
             }
             target.write(buf, 0, length)
         }
     }
}