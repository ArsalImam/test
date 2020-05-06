package com.bykea.pk.partner.ui.nodataentry

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bykea.pk.partner.DriverApp
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.source.JobsDataSource
import com.bykea.pk.partner.dal.source.JobsRepository
import com.bykea.pk.partner.dal.source.remote.request.nodataentry.DeliveryDetailInfo
import com.bykea.pk.partner.dal.source.remote.request.nodataentry.DeliveryDetails
import com.bykea.pk.partner.dal.source.remote.request.nodataentry.DeliveryDetailsLocationInfoData
import com.bykea.pk.partner.dal.source.remote.response.DeliveryDetailRemoveResponse
import com.bykea.pk.partner.dal.util.Injection
import com.bykea.pk.partner.utils.Dialogs


/**
 * Created by Sibtain Raza on 4/13/2020.
 */
class ListDeliveryDetailsViewModel : ViewModel() {

    val jobRespository: JobsRepository = Injection.provideJobsRepository(DriverApp.getContext())

    private var _items = ArrayList<DeliveryDetails>()
    val items: List<DeliveryDetails>
        get() = _items

    private var _itemRemoved = MutableLiveData<Boolean>()
    val itemRemoved: MutableLiveData<Boolean>
        get() = _itemRemoved

    fun requestDeliveryDetails() {
        val deliveryDetailsList: ArrayList<DeliveryDetails> = ArrayList()
        for (i in 65..67) {
            val deliveryDetails = DeliveryDetails()
            deliveryDetails.details = DeliveryDetailInfo()
            deliveryDetails.details?.batch_id = ((i % 65 + 1)).toString()
            deliveryDetails.details?.trip_no = "KHIJKL" + ((i % 65 + 1)).toString()
            deliveryDetails.dropoff = DeliveryDetailsLocationInfoData()
            deliveryDetails.dropoff?.address = DriverApp.getContext().getString(R.string.sawari)
            deliveryDetailsList.add(deliveryDetails)
        }
        _items = deliveryDetailsList
    }

    fun removeDeliveryDetail(deliveryDetails: DeliveryDetails) {
        jobRespository.removeDeliveryDetail(deliveryDetails.details?.batch_id.toString(), deliveryDetails.details?.trip_id.toString(), object : JobsDataSource.LoadDataCallback<DeliveryDetailRemoveResponse> {
            override fun onDataLoaded(response: DeliveryDetailRemoveResponse) {
                _itemRemoved.value = true
                Dialogs.INSTANCE.dismissDialog()
            }

            override fun onDataNotAvailable(errorCode: Int, reasonMsg: String) {
                Dialogs.INSTANCE.dismissDialog()
            }
        })
    }
}