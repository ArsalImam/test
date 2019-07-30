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
interface Callback<T> : Callback<T> {

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful) {
            response.body()?.let {
                onSuccess(it)
            }
        } else {
            onFail(response.code(), response.message())
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) = onFail(t.hashCode(), t.message!!)

    fun onSuccess(response: T)
    fun onFail(code: Int, message: String)

}
