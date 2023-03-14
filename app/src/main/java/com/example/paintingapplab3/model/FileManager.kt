package com.example.paintingapplab3.model

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import com.example.paintingapplab3.R
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class FileManager {
    companion object {
        fun saveFile(context : Context, name : String, btm : Bitmap) : Boolean
        {
            val filename = name +"_"+ UUID.randomUUID() + ".png"
            val picturesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES + File.separator + context.getString(R.string.app_name))
            if (picturesDir!!.exists())
            {
                picturesDir.mkdirs();
            }
            val imageFile = File(picturesDir, filename)
            try {
                val outputStream = FileOutputStream(imageFile)
                btm.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()
                val mimeType = "image/png" // Укажите соответствующий MIME-тип вашего файла
                MediaScannerConnection.scanFile(context, arrayOf(imageFile.absolutePath), arrayOf(mimeType), null)
            }
            catch(e : FileNotFoundException)
            {
                e.printStackTrace()
                return false
            }

                return true
        }

    }

}