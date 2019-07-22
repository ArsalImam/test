package com.bykea.pk.partner.ui.support

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.utils.Dialogs
import com.bykea.pk.partner.utils.Utils
import com.zendesk.service.ErrorResponse
import com.zendesk.service.ZendeskCallback
import zendesk.support.Request
import zendesk.support.Support
import java.util.*
import kotlin.collections.ArrayList

/**
 * ViewModel for Complaint list
 */
class ComplaintListViewModel : ViewModel() {

    private val _items = MutableLiveData<List<Request>>().apply { value = emptyList() }
    val items: LiveData<List<Request>>
        get() = _items

    val empty: LiveData<Boolean> = Transformations.map(_items) {
        it.isEmpty()
    }

    fun start() {
        if (AppPreferences.isEmailVerified()) {
            val requestProvider = Support.INSTANCE.provider()?.requestProvider()

            requestProvider?.getAllRequests(object : ZendeskCallback<List<Request>>() {
                override fun onSuccess(requests: List<Request>) {
                    Dialogs.INSTANCE.dismissDialog()
                    Collections.sort(requests, object : Comparator<Request> {
                        override fun compare(p1: Request, p2: Request): Int {
                            return p2.createdAt!!.compareTo(p1.createdAt)
                        }
                    })
                    _items.value = ArrayList(requests)
                }

                override fun onError(errorResponse: ErrorResponse) {
                    Dialogs.INSTANCE.dismissDialog()
                    Utils.setZendeskIdentity()
                }
            })
        }
    }

}