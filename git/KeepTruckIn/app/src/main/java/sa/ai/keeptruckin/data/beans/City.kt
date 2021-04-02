package sa.ai.keeptruckin.data.beans


import com.google.gson.annotations.SerializedName

data class City(
    @SerializedName("adminCode1")
    val adminCode1: String?,
    @SerializedName("adminName1")
    val adminName1: String?,
    @SerializedName("countryCode")
    val countryCode: String?,
    @SerializedName("countryId")
    val countryId: String?,
    @SerializedName("countryName")
    val countryName: String?,
    @SerializedName("fcl")
    val fcl: String?,
    @SerializedName("fclName")
    val fclName: String?,
    @SerializedName("fcode")
    val fcode: String?,
    @SerializedName("fcodeName")
    val fcodeName: String?,
    @SerializedName("geonameId")
    val geonameId: Int?,
    @SerializedName("lat")
    val lat: String?,
    @SerializedName("lng")
    val lng: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("population")
    val population: Int?,
    @SerializedName("toponymName")
    val toponymName: String?
)