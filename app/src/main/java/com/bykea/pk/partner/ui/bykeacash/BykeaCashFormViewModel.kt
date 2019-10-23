package com.bykea.pk.partner.ui.bykeacash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bykea.pk.partner.dal.source.JobsDataSource
import com.bykea.pk.partner.dal.source.JobsRepository
import com.bykea.pk.partner.dal.source.remote.request.UpdateBykeaCashBookingRequest
import com.bykea.pk.partner.dal.source.remote.response.UpdateBykeaCashBookingResponse
import com.bykea.pk.partner.models.response.NormalCallData
import com.bykea.pk.partner.utils.Dialogs
import com.bykea.pk.partner.utils.Utils

/**
 * ViewModel for Bykea Cash Form
 */
class BykeaCashFormViewModel(private val jobsRepository: JobsRepository) : ViewModel() {

    private val _callData = MutableLiveData<NormalCallData>().apply { value = null }
    val callData: MutableLiveData<NormalCallData>
        get() = _callData

    private val _responseFromServer = MutableLiveData<Boolean>().apply { value = false }
    val responseFromServer: LiveData<Boolean>
        get() = _responseFromServer

    private val _cashAmount = MutableLiveData<Int>()
    val cashAmount: LiveData<Int>
        get() = _cashAmount


    fun updateBykeaCashFormDetails(tripId: String, updateBykeaCashBookingRequest: UpdateBykeaCashBookingRequest) {
        updateBykeaCashBookingRequest.extra_info?.phone = Utils.phoneNumberForServer(updateBykeaCashBookingRequest.extra_info?.phone)
        jobsRepository.updateBykeaCashBookingDetails(tripId, updateBykeaCashBookingRequest, object : JobsDataSource.UpdateBykeaCashBookingCallback {
            override fun onSuccess(updateBykeaCashBookingResponse: UpdateBykeaCashBookingResponse) {
                Dialogs.INSTANCE.dismissDialog()
                _responseFromServer.value = true
                _cashAmount.value = updateBykeaCashBookingRequest.trip?.amount
            }

            override fun onFail(code: Int, subCode: Int?, message: String?) {
                Dialogs.INSTANCE.dismissDialog()
            }
        })
    }
}