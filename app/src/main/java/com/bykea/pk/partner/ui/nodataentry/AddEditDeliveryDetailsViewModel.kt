package com.bykea.pk.partner.ui.nodataentry

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bykea.pk.partner.DriverApp
import com.bykea.pk.partner.dal.source.JobsDataSource
import com.bykea.pk.partner.dal.source.JobsRepository
import com.bykea.pk.partner.dal.source.remote.request.nodataentry.DeliveryDetailAddEditRequest
import com.bykea.pk.partner.dal.source.remote.request.nodataentry.DeliveryDetails
import com.bykea.pk.partner.dal.source.remote.response.DeliveryDetailAddEditResponse
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
        val deliveryDetailAddRequest = DeliveryDetailAddEditRequest()
        jobRespository.addDeliveryDetail(deliveryDetails.value?.details?.batch_id.toString(), deliveryDetailAddRequest,
                object : JobsDataSource.LoadDataCallback<DeliveryDetailAddEditResponse> {
                    override fun onDataLoaded(response: DeliveryDetailAddEditResponse) {
                        Dialogs.INSTANCE.dismissDialog()
                    }

                    override fun onDataNotAvailable(errorCode: Int, reasonMsg: String) {
                        Dialogs.INSTANCE.dismissDialog()
                    }
                })
    }

    fun requestEditDeliveryDetail() {
        val deliveryDetailAddRequest = DeliveryDetailAddEditRequest()
        jobRespository.updateDeliveryDetail(deliveryDetails.value?.details?.batch_id.toString(),
                deliveryDetails.value?.details?.trip_id.toString(), deliveryDetailAddRequest,
                object : JobsDataSource.LoadDataCallback<DeliveryDetailAddEditResponse> {
                    override fun onDataLoaded(response: DeliveryDetailAddEditResponse) {
                        Dialogs.INSTANCE.dismissDialog()
                    }

                    override fun onDataNotAvailable(errorCode: Int, reasonMsg: String) {
                        Dialogs.INSTANCE.dismissDialog()
                    }
                })
    }
}