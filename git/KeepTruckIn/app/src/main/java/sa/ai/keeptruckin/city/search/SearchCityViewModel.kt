package sa.ai.keeptruckin.city.search

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import sa.ai.keeptruckin.data.CityRepository
import sa.ai.keeptruckin.data.beans.City
import sa.ai.keeptruckin.utils.ApiConstants

/**
 * [author] by `Arsal Imam`
 * [created] on 5/17/2020
 */
class SearchCityViewModel @ViewModelInject constructor(
) : ViewModel() {

    var cityRepository: CityRepository = CityRepository()

    /**
     * observable list of feeds which updates ui (on change)
     */
    var cityList: LiveData<List<City>> = MutableLiveData<List<City>>().apply { value = ArrayList() }

    /**
     * this method will request repository to fetch (local/remote) data to show on ui
     */
    fun searchCity(query: String) {
        cityList = cityRepository.searchByName(query, ApiConstants.Request.DEFAULT_MAX_PER_PAGE).asLiveData()
    }
}