package com.bykea.pk.partner.compression

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.AsyncTask
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by ArsalImam on 8/19/2017.
 */
open class AsynctaskOptimizeImages(
        private val path: String,
        private val onApiResult: OnResult) : AsyncTask<String?, Void?, String?>() {

    override fun doInBackground(vararg params: String?): String? {
        try {
            var optimizedBitmap = resize(BitmapFactory.decodeFile(this.path))
            optimizedBitmap = checkOrientationAndRotate(optimizedBitmap)
            var out: FileOutputStream? = null
            try {
                out = FileOutputStream(this.path)
                optimizedBitmap?.compress(Bitmap.CompressFormat.JPEG, 85, out)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    out?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun resize(bitmap: Bitmap): Bitmap? {
        var resizedBitmap: Bitmap?
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height
        var newWidth: Int
        var newHeight: Int
        var multFactor: Float
        if (originalHeight == originalWidth) {
            newHeight = SCALE_VALUE
            newWidth = SCALE_VALUE
        } else {
            newWidth = SCALE_VALUE
            multFactor = originalHeight.toFloat() / originalWidth.toFloat()
            newHeight = (newWidth * multFactor).toInt()
        }
        resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false)
        return resizedBitmap
    }

    private fun checkOrientationAndRotate(img: Bitmap?): Bitmap? {
        var image: Bitmap? = null
        img?.let {
            val height = img.height
            val width = img.width
            image = if (width > height) {
                val matrix = Matrix()
                matrix.postRotate(90f)
                Bitmap.createBitmap(img, 0, 0, width, height, matrix, false)
            } else img
        }

        return image
    }

    override fun onPostExecute(s: String?) {
        super.onPostExecute(s)
        this.onApiResult.onResult(this.path)
    }

    companion object {
        private const val SCALE_VALUE = 1024
    }

    interface OnResult {
        fun onResult(path: String)
    }
}