package sa.ai.keeptruckin.utils

/**
 * generic callback implementation for network/local
 *
 * [author] ArsalImam
 * [createdBy] 17-05-2020
 */
interface Callback<T> {
    /**
     * result method, will trigger on api success
     * [data] received from host
     */
    fun onResult(data: T)
    /**
     * error method, will trigger on api failure
     * [error] message, receive from host
     */
    fun onError(error: String)
}