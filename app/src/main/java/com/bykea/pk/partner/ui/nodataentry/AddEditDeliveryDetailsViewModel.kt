package com.bykea.pk.partner.ui.nodataentry

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bykea.pk.partner.DriverApp
import com.bykea.pk.partner.dal.source.JobsDataSource
import com.bykea.pk.partner.dal.source.JobsRepository
import com.bykea.pk.partner.dal.source.pref.AppPref
import com.bykea.pk.partner.dal.source.remote.request.nodataentry.DeliveryDetailAddEditRequest
import com.bykea.pk.partner.dal.source.remote.request.nodataentry.DeliveryDetails
import com.bykea.pk.partner.dal.source.remote.response.DeliveryDetailAddEditResponse
import com.bykea.pk.partner.dal.util.Injection
import com.bykea.pk.partner.models.response.NormalCallData
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.utils.Dialogs


/**
 * Created by Sibtain Raza on 4/14/2020.
 */
class AddEditDeliveryDetailsViewModel : ViewModel() {

    private val jobRespository: JobsRepository = Injection.provideJobsRepository(DriverApp.getContext())

    private var _deliveryDetails = MutableLiveData<DeliveryDetails>()
    val deliveryDetails: MutableLiveData<DeliveryDetails>
        get() = _deliveryDetails

    private var _isAddedOrUpdatedSuccessful = MutableLiveData<Boolean>()
    val isAddedOrUpdatedSuccessful: MutableLiveData<Boolean>
        get() = _isAddedOrUpdatedSuccessful

    private var _callData = MutableLiveData<NormalCallData>()
    val callData: MutableLiveData<NormalCallData>
        get() = _callData

    /**
     * Get active trip from shared preferences
     */
    fun getActiveTrip() {
        _callData.value = AppPreferences.getCallData()
    }

    /**
     * Request add delivery detail item in list
     */
    fun requestAddDeliveryDetails() {
        _deliveryDetails.value?.let {
            jobRespository.addDeliveryDetail(callData.value?.tripId.toString(), it,
                    object : JobsDataSource.LoadDataCallback<DeliveryDetailAddEditResponse> {
                        override fun onDataLoaded(response: DeliveryDetailAddEditResponse) {
                            _deliveryDetails.value = response.data
                            _isAddedOrUpdatedSuccessful.value = true
                            Dialogs.INSTANCE.dismissDialog()
                        }

                        override fun onDataNotAvailable(errorCode: Int, reasonMsg: String) {
                            Dialogs.INSTANCE.showToast(reasonMsg)
                            Dialogs.INSTANCE.dismissDialog()
                        }
                    })
        }
    }

    /**
     * Request update delivery detail item in list
     */
    fun requestEditDeliveryDetail() {
        _deliveryDetails.value?.let {
            jobRespository.updateDeliveryDetail(callData.value?.tripId.toString(),
                    _deliveryDetails.value?.details?.trip_id.toString(), it,
                    object : JobsDataSource.LoadDataCallback<DeliveryDetailAddEditResponse> {
                        override fun onDataLoaded(response: DeliveryDetailAddEditResponse) {
                            _isAddedOrUpdatedSuccessful.value = true
                            Dialogs.INSTANCE.dismissDialog()
                        }

                        override fun onDataNotAvailable(errorCode: Int, reasonMsg: String) {
                            Dialogs.INSTANCE.dismissDialog()
                        }
                    })
        }
    }
}