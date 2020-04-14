package com.bykea.pk.partner.ui.nodataentry

import androidx.lifecycle.ViewModel
import com.bykea.pk.partner.DriverApp
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.source.remote.response.DeliveryDetails
import org.apache.commons.lang3.StringUtils


/**
 * Created by Sibtain Raza on 4/13/2020.
 * smsibtainrn@gmail.com
 */
class ListDeliveryDetailsViewModel : ViewModel() {

    private var _items = ArrayList<DeliveryDetails>()
    val items: List<DeliveryDetails>
        get() = _items

    fun requestDeliveryDetails() {
        val deliveryDetailsList: ArrayList<DeliveryDetails> = ArrayList()
        for (i in 65..90) {
            deliveryDetailsList.add(DeliveryDetails(i.toChar().toString(), ((i % 65 + 1)).toString() + StringUtils.SPACE + DriverApp.getContext().getString(R.string.sawari), "KHIJKL" + ((i % 65 + 1)).toString()))
        }
        _items = deliveryDetailsList
    }
}