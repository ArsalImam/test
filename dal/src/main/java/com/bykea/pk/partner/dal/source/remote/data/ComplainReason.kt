package com.bykea.pk.partner.dal.source.remote.data

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/***
 * this is a response for complain reasons
 */
@Parcelize
data class ComplainReason(
        @SerializedName("_id") @Expose var _id: String?,
        @SerializedName("message") @Expose var message: String?,
        @SerializedName("code") @Expose var code: String?
) : Parcelable