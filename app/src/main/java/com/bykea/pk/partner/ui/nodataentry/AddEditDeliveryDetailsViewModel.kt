package com.bykea.pk.partner.ui.nodataentry

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bykea.pk.partner.DriverApp
import com.bykea.pk.partner.dal.source.JobsDataSource
import com.bykea.pk.partner.dal.source.JobsRepository
import com.bykea.pk.partner.dal.source.remote.request.DeliveryDetailAddRequest
import com.bykea.pk.partner.dal.source.remote.request.DeliveryDetailUpdateRequest
import com.bykea.pk.partner.dal.source.remote.request.nodataentry.DeliveryDetails
import com.bykea.pk.partner.dal.source.remote.response.DeliveryDetailAddResponse
import com.bykea.pk.partner.dal.source.remote.response.DeliveryDetailUpdateResponse
import com.bykea.pk.partner.dal.util.Injection
import com.bykea.pk.partner.utils.Dialogs


/**
 * Created by Sibtain Raza on 4/14/2020.
 */
class AddEditDeliveryDetailsViewModel : ViewModel() {

    val jobRespository: JobsRepository = Injection.provideJobsRepository(DriverApp.getContext())

    private var _deliveryDetails = MutableLiveData<DeliveryDetails>()
    val deliveryDetails: MutableLiveData<DeliveryDetails>
        get() = _deliveryDetails

    fun requestAddDeliveryDetails() {
        val deliveryDetailAddRequest = DeliveryDetailAddRequest()
        jobRespository.addDeliveryDetail("PENDING", deliveryDetailAddRequest, object : JobsDataSource.LoadDataCallback<DeliveryDetailAddResponse> {
            override fun onDataLoaded(response: DeliveryDetailAddResponse) {
                Dialogs.INSTANCE.dismissDialog()
            }

            override fun onDataNotAvailable(errorCode: Int, reasonMsg: String) {
                Dialogs.INSTANCE.dismissDialog()
            }
        })
    }

    fun requestEditDeliveryDetail() {
        val deliveryDetailUpdateRequest = DeliveryDetailUpdateRequest()
        jobRespository.updateDeliveryDetail("PENDING", "PENDING", deliveryDetailUpdateRequest, object : JobsDataSource.LoadDataCallback<DeliveryDetailUpdateResponse> {
            override fun onDataLoaded(response: DeliveryDetailUpdateResponse) {
                Dialogs.INSTANCE.dismissDialog()
            }

            override fun onDataNotAvailable(errorCode: Int, reasonMsg: String) {
                Dialogs.INSTANCE.dismissDialog()
            }
        })
    }
}