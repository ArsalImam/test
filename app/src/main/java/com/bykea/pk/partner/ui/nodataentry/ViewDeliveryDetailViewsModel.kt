package com.bykea.pk.partner.ui.nodataentry

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bykea.pk.partner.dal.source.remote.request.nodataentry.DeliveryDetails
import com.bykea.pk.partner.models.response.NormalCallData
import com.bykea.pk.partner.ui.helpers.AppPreferences


/**
 * Created by Sibtain Raza on 5/11/2020.
 * smsibtainrn@gmail.com
 */
class ViewDeliveryDetailViewsModel : ViewModel() {

    private var _deliveryDetails = MutableLiveData<DeliveryDetails>()
    val deliveryDetails: MutableLiveData<DeliveryDetails>
        get() = _deliveryDetails

    private var _callData = MutableLiveData<NormalCallData>()
    val callData: MutableLiveData<NormalCallData>
        get() = _callData

    /**
     * Get active trip from shared preferences
     */
    fun getActiveTrip() {
        _callData.value = AppPreferences.getCallData()
    }

}