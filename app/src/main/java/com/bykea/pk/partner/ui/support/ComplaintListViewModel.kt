package com.bykea.pk.partner.ui.support

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zendesk.service.ErrorResponse
import com.zendesk.service.ZendeskCallback
import zendesk.support.Request
import zendesk.support.Support

/**
 * ViewModel for Complaint list
 */
class ComplaintListViewModel : ViewModel() {

    private val _items = MutableLiveData<List<Request>>().apply { value = emptyList() }
    val items: LiveData<List<Request>>
        get() = _items

    fun start() {
        val requestProvider = Support.INSTANCE.provider()!!.requestProvider()
        requestProvider.getAllRequests(object : ZendeskCallback<List<Request>>() {
            override fun onSuccess(requests: List<Request>) {
                _items.value = ArrayList(requests)
            }

            override fun onError(errorResponse: ErrorResponse) {
//                TODO: Utils.appToast()
            }
        })
    }

}