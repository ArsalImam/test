package com.bykea.pk.partner.models.data


import com.google.gson.annotations.SerializedName

data class BykeaDistanceMatrix(
        @SerializedName("destination")
        var destination: String? = null,
        @SerializedName("distance")
        var distance: KeyValuePair? = null,
        @SerializedName("duration")
        var duration: KeyValuePair? = null,
        @SerializedName("origin")
        var origin: String? = null
)