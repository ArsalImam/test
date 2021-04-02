package sa.ai.keeptruckin.utils.generics

import sa.ai.keeptruckin.utils.ApiConstants
import sa.ai.keeptruckin.utils.Callback
import org.apache.commons.lang.StringUtils
import retrofit2.Call
import retrofit2.Response

/**
 * [author] by `Arsal Imam`
 * [created] on 5/17/2020

 * Generic Retrofit Call Back to receive Network Call response and send that to previous Layer
 */
class GenericNetworkCallback<T, A : BaseResponse<T>>(val callback: Callback<T>) :
    retrofit2.Callback<A> {
    /**
     * retrofit failure method, will trigger on api failure
     * [call] request context of the service call
     * [t] error caused of the failure
     */
    override fun onFailure(call: Call<A>, t: Throwable) {
        t.printStackTrace()
        callback.onError(t.localizedMessage ?: StringUtils.EMPTY)
    }

    /**
     * retrofit success method, will trigger on api successful
     *
     * [call] request context of the service call
     * [response] response on service call success
     */
    override fun onResponse(call: Call<A>, response: Response<A>) {
        //handle success
        try {
            if (response.isSuccessful) {
                response.body()?.let {
                    if (it.isSuccess) {
                        it.results?.let { it1 -> callback.onResult(it1) }
                    } else {
                        it.error?.let { it1 -> callback.onError(it1) }
                    }
                    return
                }
            }

            //handle failure
            val errorBody = response.errorBody()
            if (errorBody != null) {
                callback.onError(errorBody.string())
                println(errorBody.string())
            } else {
                callback.onError(ApiConstants.ErrorMessage.SOMETHING_WENT_WRONGE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            callback.onError(ApiConstants.ErrorMessage.SOMETHING_WENT_WRONGE)
        }
    }
}