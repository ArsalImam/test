package sa.ai.keeptruckin.data.sources

import sa.ai.keeptruckin.data.CityService
import sa.ai.keeptruckin.data.beans.response.CitySearchResponse
import sa.ai.keeptruckin.utils.ApiConstants
import javax.inject.Inject

/**
 * [author] by `Arsal Imam`
 * [created] on 5/17/2020
 *
 * network datasource for feeds
 */
class CityRemoteSource {

    @Inject
    lateinit var cityService: CityService

    /**
     * this method is responsible to perform a network call to get latest feeds from NYT's server
     * [page] can be used to manage pagination
     * [callback] will be used to return data set on receive from server
     */
    suspend fun searchByName(
        query: String,
        rows: Int = ApiConstants.Request.DEFAULT_MAX_PER_PAGE
    ): CitySearchResponse = cityService.searchByName(query, rows)
}