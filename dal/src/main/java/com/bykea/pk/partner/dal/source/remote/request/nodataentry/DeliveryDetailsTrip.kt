package com.bykea.pk.partner.dal.source.remote.request.nodataentry

import android.os.Parcelable
import com.bykea.pk.partner.dal.util.EMPTY_STRING
import kotlinx.android.parcel.Parcelize

/**
 * Created by Sibtain Raza on 4/14/2020.
 * smsibtainrn@gmail.com
 */

@Parcelize
data class DeliveryDetailsTrip(var lat: String = EMPTY_STRING,
                               var lng: String = EMPTY_STRING) : Parcelable