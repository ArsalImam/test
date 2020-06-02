package com.bykea.pk.partner.dal.source.remote

import com.bykea.pk.partner.dal.source.remote.response.BaseResponse
import com.bykea.pk.partner.dal.source.remote.response.BaseResponseError
import com.bykea.pk.partner.dal.util.ERROR_PLEASE_TRY_AGAIN
import com.bykea.pk.partner.dal.util.INTERNAL_SERVER_ERROR
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

/**
 * Overriding callback of retrofit to handle generic responses
 * @param T Response model
 *
 * @author Yousuf Sohail
 */
interface Callback<T : BaseResponse> : Callback<T> {

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.body() == null) {
            if (response.errorBody() != null) {
                try {
                    val res = Gson().fromJson(response.errorBody()?.string(), BaseResponse::class.java)

                    res.error?.let {
                        onFail(res.code, it, res.message)
                    } ?: run {
                        onFail(res.code, res.subcode, res.message)
                    }

                    onFail(res.code, res.message)
                } catch (e: Exception) {
                    e.printStackTrace()
                    onFail(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR, ERROR_PLEASE_TRY_AGAIN)
                    onFail(INTERNAL_SERVER_ERROR, ERROR_PLEASE_TRY_AGAIN)
                }
            } else {
                onFail(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR, ERROR_PLEASE_TRY_AGAIN)
                onFail(INTERNAL_SERVER_ERROR, ERROR_PLEASE_TRY_AGAIN)
            }
            return
        }

        if (response.body()?.isSuccess()!!) {
            onSuccess(response.body()!!)
        } else {
            onFail(response.body()?.code!!, response.body()?.subcode!!, response.body()?.message!!)
            onFail(response.body()?.code!!, response.body()?.message!!)
        }

        /*if (response.isSuccessful) {
            response.body()?.let {
                onSuccess(it)
            }
        } else {
            onFail(response.code(), response.message())
        }*/
    }

    override fun onFailure(call: Call<T>, t: Throwable) = onFail(t.hashCode(), t.message)

    /**
     * Positive response callback; will be called on success with response
     * @param response Server response
     */
    fun onSuccess(response: T)

    /**
     * Negative response callback; will be called on fail with server code and message
     * @param code Server code
     * @param message Server message
     */
    fun onFail(code: Int, message: String?) {}

    /**
     * Negative response callback; will be called on fail with server code and message
     * @param code Server code
     * @param subCode Server sub code
     * @param message Server message
     */
    fun onFail(code: Int, subCode: Int?, message: String?) {}

    /**
     * Negative response callback; will be called on fail with server code and message
     * @param code Server code
     * @param errorBody Error Body
     * @param message Server message
     */
    fun onFail(code: Int, errorBody: BaseResponseError?, message: String?) {}
}
