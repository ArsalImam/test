package com.bykea.pk.partner.utils.audio

import android.os.AsyncTask
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.services.s3.model.PutObjectResult
import com.bykea.pk.partner.DriverApp
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.util.DIGIT_ZERO
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.utils.Constants
import com.bykea.pk.partner.utils.Util
import com.bykea.pk.partner.utils.Utils
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * Class to use upload and download file for amazon
 */
object BykeaAmazonClient {

    /**
     * upload file to amazon
     * @param fileName fileName to be uploaded
     * @param file     audio file
     * @param callback callback for success/fail upload
     */
    fun uploadFile(fileName: String, file: File, callback: Callback<String>, bucketName: String) {
        UploadFileToAmazon(fileName, callback, bucketName).execute(file)
    }


    /**
     * get file from amazon
     * @param fileName fileName is to be used to fetch file from amazon bucket
     * @param callback callback for success/fail fetch
     * @param bucketName Bucket Name
     */
    fun getFileObject(fileName: String, callback: Callback<File>, bucketName: String) {
        RetrieveFileFromAmazon(fileName, callback, bucketName).execute()
    }

    /**
     * AsyncTask to upload file to Amazon
     */
    private class UploadFileToAmazon(var fileName: String, var callback: Callback<String>, val bucketName: String) : AsyncTask<File, Unit, PutObjectResult>() {
        override fun doInBackground(vararg files: File): PutObjectResult? {
            return try {
                amazonCredential()?.let {
                    val p = PutObjectRequest(bucketName, fileName, files[DIGIT_ZERO])
                    val client = AmazonS3Client(it) //creating Amazon client instance
                    client.putObject(p) //upload file to amazon now
                } ?: run {
                    throw Exception()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        override fun onPostExecute(result: PutObjectResult?) {
            super.onPostExecute(result)
            if (result != null) {
                callback.success(fileName)
            } else {
                callback.fail(-1, "fail")
            }
        }
    }

    /**
     * AsyncTask to fetch file from Amazon
     */
    private class RetrieveFileFromAmazon(var fileName: String, var callback: Callback<File>, var bucketName: String) : AsyncTask<Unit, InputStream, File?>() {
        override fun doInBackground(vararg units: Unit): File? {
            return try {
                if (android.os.Debug.isDebuggerConnected())
                    android.os.Debug.waitForDebugger()
                amazonCredential()?.let {
                    val s3Client = AmazonS3Client(it)
                    val getRequest = GetObjectRequest(bucketName, fileName)
                    val getResponse = s3Client.getObject(getRequest)
                    val myObjectBytes = getResponse.objectContent
                    return takeInputStream(myObjectBytes)
                } ?: run {
                    throw Exception()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        override fun onPostExecute(result: File?) {
            super.onPostExecute(result)
            if (result != null) {
                callback.success(result)
            } else {
                callback.fail(-1, "fail")
            }
        }
    }

    /**
     * convert input stream to file
     * @param stream input stream of file to be converted to file
     */
    private fun takeInputStream(stream: InputStream): File? {
        var convertedFile: File? = null
        try {
            convertedFile = File.createTempFile(Constants.AUDIO_TEMP_FILE_NAME, Constants.FILE_EXT)
            val out = FileOutputStream(convertedFile)
            val buffer = ByteArray(16384)
            var length = 0
            while (length != -1) {
                out.write(buffer, 0, length)
                length = stream.read(buffer)
            }
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return convertedFile
    }

    /**
     * prepare amazon credentials for upload/download files on amazon
     */
    private fun amazonCredential(): CognitoCachingCredentialsProvider? {
        //Amazon credential
        var cognitoCachingCredentialsProvider: CognitoCachingCredentialsProvider? = null
        Util.safeLet(AppPreferences.getDriverSettings().data?.s3PoolId,
                AppPreferences.getDriverSettings().data?.s3BucketRegion) { s3PoolId, s3BucketRegion ->
            cognitoCachingCredentialsProvider = CognitoCachingCredentialsProvider(
                    DriverApp.getContext(), //context
                    AppPreferences.getDriverSettings().data?.s3PoolId, // pool id in amazon
                    Regions.fromName(AppPreferences.getDriverSettings().data?.s3BucketRegion)) // region
        } ?: run {
            Utils.appToast(DriverApp.getContext().getString(R.string.settings_are_not_updated))
        }
        return cognitoCachingCredentialsProvider
    }

}