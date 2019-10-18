package com.bykea.pk.partner.ui.bykeacash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.bykea.pk.partner.DriverApp
import com.bykea.pk.partner.R
import com.bykea.pk.partner.models.response.NormalCallData
import com.bykea.pk.partner.utils.Dialogs
import com.bykea.pk.partner.utils.Utils
import com.zendesk.service.ErrorResponse
import com.zendesk.service.ZendeskCallback
import zendesk.support.Request
import zendesk.support.Support
import java.util.*
import kotlin.collections.ArrayList

/**
 * ViewModel for Bykea Cash Form
 */
class BykeaCashFormViewModel : ViewModel() {

    private val _callData = MutableLiveData<NormalCallData>().apply { value = null }
    val callData: MutableLiveData<NormalCallData>
        get() = _callData


    fun updateFormDetails() {
        Dialogs.INSTANCE.dismissDialog()
    }
}