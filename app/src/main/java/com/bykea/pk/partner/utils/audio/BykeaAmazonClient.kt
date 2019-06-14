package com.bykea.pk.custom

import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.services.s3.model.PutObjectResult
import com.bykea.pk.partner.DriverApp
import com.bykea.pk.partner.utils.Constants
import com.bykea.pk.partner.utils.audio.Callback
import io.fabric.sdk.android.services.concurrency.AsyncTask
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.Exception

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
    fun uploadFile(fileName: String, file: File, callback: Callback<String>) {
        UploadFileToAmazon(fileName, callback).execute(file)
    }


    /**
     * get file from amazon
     * @param fileName fileName is to be used to fetch file from amazon bucket
     * @param callback callback for success/fail fetch
     */
    fun getFileObject(fileName: String, callback: Callback<File>) {
        RetrieveFileFromAmazon(fileName, callback).execute()
    }

    /**
     * AsyncTask to upload file to Amazon
     */
    private class UploadFileToAmazon(var fileName: String, var callback: Callback<String>) : AsyncTask<File, Unit, PutObjectResult>() {
        override fun doInBackground(vararg files: File): PutObjectResult? {
            return try {
                val p = PutObjectRequest(Constants.Amazon.BUCKET_NAME, fileName, files[0])
                val client = AmazonS3Client(amazonCredential()) //creating Amazon client instance
                client.putObject(p) //upload file to amazon now
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        override fun onPostExecute(result: PutObjectResult?) {
            super.onPostExecute(result)
            if (result != null) {
//                Utils.redLog(TAG, "Done > ${result?.versionId} > ${result?.contentMd5} > ${result?.eTag}")
                callback.success(fileName)
            } else {
                callback.fail(-1, "fail")
            }
        }
    }

    /**
     * AsyncTask to fetch file from Amazon
     */
    private class RetrieveFileFromAmazon(var fileName: String, var callback: Callback<File>) : AsyncTask<Unit, InputStream, File?>() {
        override fun doInBackground(vararg units: Unit): File? {
            return try {
                if (android.os.Debug.isDebuggerConnected())
                    android.os.Debug.waitForDebugger()
                val s3Client = AmazonS3Client(amazonCredential())
                val getRequest = GetObjectRequest(Constants.Amazon.BUCKET_NAME, fileName)
                val getResponse = s3Client.getObject(getRequest)
                val myObjectBytes = getResponse.objectContent
                val f = takeInputStream(myObjectBytes)
                return f
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
    private fun amazonCredential(): CognitoCachingCredentialsProvider {
        //Amazon credential
        return CognitoCachingCredentialsProvider(
                DriverApp.getContext(), //context
                Constants.Amazon.IDENTITY_POOL_ID, // pool id in amazon
                Regions.EU_WEST_1) // region
    }

}