package sa.ai.keeptruckin.data

import kotlinx.coroutines.flow.Flow
import sa.ai.keeptruckin.data.beans.City

/**
 * [author] by `Arsal Imam`
 * [created] on 5/17/2020
 *
 * interface for feeds repository implementation
 */
interface ICityRepository {

    /**
     * this method is responsible to perform a network call to get latest feeds from NYT's server
     * [page] can be used to manage pagination
     * [callback] will be used to return data set on receive from server
     */
    fun searchByName(query: String, rows: Int): Flow<List<City>>
}