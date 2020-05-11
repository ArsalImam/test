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
import com.bykea.pk.partner.dal.source.remote.response.DeliveryDetailListResponse
import com.bykea.pk.partner.dal.source.remote.response.DeliveryDetailRemoveResponse
import com.bykea.pk.partner.dal.util.Injection
import com.bykea.pk.partner.models.response.NormalCallData
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.utils.Dialogs


/**
 * Created by Sibtain Raza on 4/13/2020.
 */
class ListDeliveryDetailsViewModel : ViewModel() {

    val jobRespository: JobsRepository = Injection.provideJobsRepository(DriverApp.getContext())

    private var _items = MutableLiveData<ArrayList<DeliveryDetails>>().apply { value = ArrayList() }
    val items: MutableLiveData<ArrayList<DeliveryDetails>>
        get() = _items

    private var _itemRemoved = MutableLiveData<Boolean>()
    val itemRemoved: MutableLiveData<Boolean>
        get() = _itemRemoved

    private var _callData = MutableLiveData<NormalCallData>()
    val callData: MutableLiveData<NormalCallData>
        get() = _callData

    fun getActiveTrip() {
        _callData.value = AppPreferences.getCallData()
    }

    fun getAllDeliveryDetails() {
        jobRespository.getAllDeliveryDetails(callData.value?.tripId.toString(),
                object : JobsDataSource.LoadDataCallback<DeliveryDetailListResponse> {
                    override fun onDataLoaded(response: DeliveryDetailListResponse) {
                        _items.value = response.bookings
                    }

                    override fun onDataNotAvailable(errorCode: Int, reasonMsg: String) {
                        Dialogs.INSTANCE.dismissDialog()
                    }
                })
    }

    fun removeDeliveryDetail(deliveryDetails: DeliveryDetails) {
        jobRespository.removeDeliveryDetail(callData.value?.tripId.toString(),
                deliveryDetails.details?.trip_id.toString(),
                object : JobsDataSource.LoadDataCallback<DeliveryDetailRemoveResponse> {
                    override fun onDataLoaded(response: DeliveryDetailRemoveResponse) {
                        _items.value?.remove(deliveryDetails)
                        _itemRemoved.value = true
                        Dialogs.INSTANCE.dismissDialog()
                    }

                    override fun onDataNotAvailable(errorCode: Int, reasonMsg: String) {
                        Dialogs.INSTANCE.dismissDialog()
                    }
                })
    }
}