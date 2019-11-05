package com.bykea.pk.partner.utils.audio

/**
 * Generic type callback that is used for request or response from server or local
 * @param T generic type that will be converted into give data type at runtime
 */
interface Callback<T> {
    fun success(obj: T)
    fun fail(errorCode: Int, errorMsg: String)
}