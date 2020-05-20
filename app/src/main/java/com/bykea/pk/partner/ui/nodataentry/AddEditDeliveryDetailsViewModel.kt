package com.bykea.pk.partner.ui.nodataentry

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bykea.pk.partner.DriverApp
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.source.JobsDataSource
import com.bykea.pk.partner.dal.source.JobsRepository
import com.bykea.pk.partner.dal.source.remote.request.nodataentry.DeliveryDetails
import com.bykea.pk.partner.dal.source.remote.response.DeliveryDetailAddEditResponse
import com.bykea.pk.partner.dal.util.Injection
import com.bykea.pk.partner.models.response.NormalCallData
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.utils.Constants.ApiError.BUSINESS_LOGIC_ERROR
import com.bykea.pk.partner.utils.Constants.ApiError.WALLET_EXCEED_THRESHOLD
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

    private var _isCashLimitLow = MutableLiveData<Boolean>().apply { value = false }
    val isCashLimitLow: MutableLiveData<Boolean>
        get() = _isCashLimitLow

    /**
     * Get active trip from shared preferences
     */
    fun getActiveTrip() {
        _callData.value = AppPreferences.getCallData()
    }

    /**
     * Request add delivery detail item in list
     */
    fun requestAddDeliveryDetails(deliveryDetails: DeliveryDetails) {
        jobRespository.addDeliveryDetail(callData.value?.tripId.toString(), deliveryDetails,
                object : JobsDataSource.LoadDataCallback<DeliveryDetailAddEditResponse> {
                    override fun onDataLoaded(response: DeliveryDetailAddEditResponse) {
                        _deliveryDetails.value = response.data
                        _isAddedOrUpdatedSuccessful.value = true
                        Dialogs.INSTANCE.dismissDialog()
                    }

                    override fun onDataNotAvailable(errorCode: Int, subCode: Int?, reasonMsg: String) {
                        Dialogs.INSTANCE.dismissDialog()
                        deliveryAddEditCodeSubCodeHandling(errorCode, subCode, reasonMsg)
                    }
                })
    }

    /**
     * Request update delivery detail item in list
     */
    fun requestEditDeliveryDetail(deliveryDetails: DeliveryDetails) {
        jobRespository.updateDeliveryDetail(callData.value?.tripId.toString(),
                _deliveryDetails.value?.details?.trip_id.toString(), deliveryDetails,
                object : JobsDataSource.LoadDataCallback<DeliveryDetailAddEditResponse> {
                    override fun onDataLoaded(response: DeliveryDetailAddEditResponse) {
                        _deliveryDetails.value = response.data
                        _isAddedOrUpdatedSuccessful.value = true
                        Dialogs.INSTANCE.dismissDialog()
                    }

                    override fun onDataNotAvailable(errorCode: Int, subCode: Int?, reasonMsg: String) {
                        Dialogs.INSTANCE.dismissDialog()
                        deliveryAddEditCodeSubCodeHandling(errorCode, subCode, reasonMsg)
                    }
                })
    }

    private fun deliveryAddEditCodeSubCodeHandling(errorCode: Int, subCode: Int?, reasonMsg: String) {
        if (errorCode == BUSINESS_LOGIC_ERROR && subCode != null) {
            when (subCode) {
                WALLET_EXCEED_THRESHOLD -> {
                    _isCashLimitLow.value= true
                }
                else -> {
                    Dialogs.INSTANCE.showToast(DriverApp.getContext().getString(R.string.something_went_wrong))
                }
            }
        } else {
            Dialogs.INSTANCE.showToast(reasonMsg)
        }
    }
}