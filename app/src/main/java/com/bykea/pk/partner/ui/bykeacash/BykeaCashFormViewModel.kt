package com.bykea.pk.partner.ui.bykeacash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bykea.pk.partner.dal.source.JobsDataSource
import com.bykea.pk.partner.dal.source.JobsRepository
import com.bykea.pk.partner.dal.source.pref.AppPref
import com.bykea.pk.partner.dal.source.remote.request.UpdateBookingRequest
import com.bykea.pk.partner.dal.source.remote.response.UpdateBookingResponse
import com.bykea.pk.partner.models.response.NormalCallData
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.utils.Dialogs

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

    fun updateFormDetails(tripId: String, updateBookingRequest: UpdateBookingRequest) {
        jobsRepository.updateBookingDetails(tripId, updateBookingRequest, object : JobsDataSource.UpdateBookingCallback {
            override fun onSuccess(updateBookingResponse: UpdateBookingResponse) {
                Dialogs.INSTANCE.dismissDialog()
                _responseFromServer.value = true
            }

            override fun onFail(code: Int, subCode: Int?, message: String?) {
                Dialogs.INSTANCE.dismissDialog()
            }
        })
    }
}