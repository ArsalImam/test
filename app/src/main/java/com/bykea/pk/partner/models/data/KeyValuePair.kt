package com.bykea.pk.partner.models.data

import com.google.gson.annotations.SerializedName
import com.zendesk.util.StringUtils
import org.apache.commons.lang3.math.NumberUtils

data class KeyValuePair(
        @SerializedName("text")
        var text: String? = StringUtils.EMPTY_STRING,
        @SerializedName("value")
        var value: Int? = NumberUtils.INTEGER_ZERO
)