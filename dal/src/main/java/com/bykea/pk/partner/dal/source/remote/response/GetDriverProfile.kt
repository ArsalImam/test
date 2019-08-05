package com.bykea.pk.partner.dal.source.remote.response

import com.bykea.pk.partner.dal.source.remote.data.PersonalInfoData
import com.google.gson.annotations.SerializedName

data class GetDriverProfile(
        @SerializedName("data") var data: PersonalInfoData?
) : BaseResponse() {
}
