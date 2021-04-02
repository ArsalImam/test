package sa.ai.keeptruckin.data

import sa.ai.keeptruckin.data.sources.CityRemoteSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import sa.ai.keeptruckin.data.beans.City

/**
 * [author] by `Arsal Imam`
 * [created] on 5/17/2020
 *
 * this repository is responsible to manage data transactions related feed
 * [remoteDataSource] network datasource for feeds
 */
class CityRepository : ICityRepository {
    private var cityRemoteDataSource: CityRemoteSource = CityRemoteSource()

    /**
     * this method is responsible to perform a network call to get latest feeds from NYT's server
     * [page] can be used to manage pagination
     */
    override fun searchByName(query: String, rows: Int): Flow<List<City>> = flow {
        cityRemoteDataSource.searchByName(query).also {
            it.geonames?.let { results -> emit(results) }
        }
    }

}