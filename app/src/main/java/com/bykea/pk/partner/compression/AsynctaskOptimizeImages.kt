package com.bykea.pk.partner.compression

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.AsyncTask
import org.apache.commons.lang3.math.NumberUtils
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by ArsalImam on 8/19/2017.
 *
 * this can be used to optimize image files
 *
 * [path] image file path
 * [onResult] callback to return to the main controller
 */
open class AsynctaskOptimizeImages(
        private val path: String,
        private val onResult: OnResult) : AsyncTask<String?, Void?, String?>() {

    /**
     * background execution of the thread
     * [params] input params of this thread
     */
    override fun doInBackground(vararg params: String?): String? {
        try {
            var optimizedBitmap = resize(BitmapFactory.decodeFile(this.path))
            optimizedBitmap = checkOrientationAndRotate(optimizedBitmap)
            var out: FileOutputStream? = null
            try {
                out = FileOutputStream(this.path)
                optimizedBitmap?.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, out)
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

    /**
     * will resize bitmap with scaling value mentioned in [SCALE_VALUE]
     * [img] bitmap to resize
     */
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

    /**
     * will update the orientation of the image if required
     * [img] bitmap to check
     */
    private fun checkOrientationAndRotate(img: Bitmap?): Bitmap? {
        var image: Bitmap? = null
        img?.let {
            val height = img.height
            val width = img.width
            image = if (width > height) {
                val matrix = Matrix()
                matrix.postRotate(ROTATE_ANGLE)
                Bitmap.createBitmap(img, NumberUtils.INTEGER_ZERO, NumberUtils.INTEGER_ZERO, width, height, matrix, false)
            } else img
        }

        return image
    }

    override fun onPostExecute(s: String?) {
        super.onPostExecute(s)
        this.onResult.onResult(this.path)
    }

    companion object {
        private const val SCALE_VALUE = 1024
        private const val IMAGE_QUALITY = 85
        private const val ROTATE_ANGLE = 90f
    }

    interface OnResult {
        /**
         * callback method for this asynctask
         * [path] of the output file
         */
        fun onResult(path: String)
    }
}