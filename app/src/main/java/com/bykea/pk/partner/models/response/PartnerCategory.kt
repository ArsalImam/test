package com.bykea.pk.partner.models.response

import com.google.gson.annotations.SerializedName

data class PartnerCategory(var label: String?, @SerializedName("crown_url") var crownUrl: String?)
