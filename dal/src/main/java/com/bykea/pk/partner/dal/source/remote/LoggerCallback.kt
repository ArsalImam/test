package com.bykea.pk.partner.dal.source.remote

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Overriding callback of retrofit to handle generic responses
 * @param T Response model
 *
 * @author Yousuf Sohail
 */
interface LoggerCallback<T> : Callback<T> {

    override fun onResponse(call: Call<T>, response: Response<T>) {

        if (response.isSuccessful) {
            response.body()?.let {
                onSuccess(it, call.request().toString(), response.toString())
            }
        } else {
            onFail(response.code(), response.message())
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) = onFail(t.hashCode(), t.message)

    /**
     * Positive response callback; will be called on success with response
     * @param response Server response
     */
    fun onSuccess(response: T, request: String, resp: String)

    /**
     * Negative response callback; will be called on fail with server code and message
     * @param code Server code
     * @param message Server message
     */
    fun onFail(code: Int, message: String?)

}
