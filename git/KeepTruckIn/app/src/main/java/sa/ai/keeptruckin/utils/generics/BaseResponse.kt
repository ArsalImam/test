package sa.ai.keeptruckin.utils.generics

import sa.ai.keeptruckin.utils.ApiConstants.Request.STATUS_SUCCESS
import org.apache.commons.lang.StringUtils

/**
 * [author] by `Arsal Imam`
 * [created] on 5/17/2020
 *
 * base response class for NYT server response
 */
open class BaseResponse<T> {
    /**
     * found error on server side
     */
    val error: String? = StringUtils.EMPTY

    /**
     * status of the processing,
     * will be "OK" in case of success
     */
    private val status: String? = StringUtils.EMPTY

    /**
     * data received on successful processing,
     */
    val results: T? = null


    /**
     * will be true if [status] is equals to OK,
     */
    val isSuccess
        get() = true/*StringUtils.equals(status, STATUS_SUCCESS)*/
}