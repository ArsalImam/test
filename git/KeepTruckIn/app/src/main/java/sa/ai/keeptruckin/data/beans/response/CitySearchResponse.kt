package sa.ai.keeptruckin.data.beans.response


import com.google.gson.annotations.SerializedName
import sa.ai.keeptruckin.data.beans.City

data class CitySearchResponse(
    @SerializedName("geonames")
    val geonames: List<City>?,
    @SerializedName("totalResultsCount")
    val totalResultsCount: Int?
)