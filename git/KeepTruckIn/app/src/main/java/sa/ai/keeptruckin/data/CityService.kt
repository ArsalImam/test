package sa.ai.keeptruckin.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import sa.ai.keeptruckin.BuildConfig
import sa.ai.keeptruckin.data.beans.response.CitySearchResponse
import sa.ai.keeptruckin.utils.ApiConstants

/**
 * [author] by `Arsal Imam`
 * [created] on 5/17/2020
 *
 * network client to handle feed's services
 */
interface CityService {

    /**
     * this method is responsible to perform a network call to get latest feeds from NYT's server
     * [page] can be used to manage pagination
     * [callback] will be used to return data set on receive from server
     */
    @GET(ApiConstants.SEARCH_CITIES)
    suspend fun searchByName(
        @Path(ApiConstants.Request.QUERY) query: String,
        @Query(ApiConstants.Request.MAX_ROWS) maxRows: Int,
        @Query(ApiConstants.Request.USERNAME) userName: String = BuildConfig.BASE_URL
    ): CitySearchResponse
}