package com.bykea.pk.partner.ui.complain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bykea.pk.partner.DriverApp
import com.bykea.pk.partner.dal.source.JobsDataSource
import com.bykea.pk.partner.dal.source.JobsRepository
import com.bykea.pk.partner.dal.source.remote.data.ComplainReason
import com.bykea.pk.partner.dal.source.remote.response.ComplainReasonResponse
import com.bykea.pk.partner.dal.util.Injection
import com.bykea.pk.partner.dal.util.MESSAGE_TYPE
import com.bykea.pk.partner.utils.Constants.DIGIT_ONE
import com.bykea.pk.partner.utils.Dialogs
import com.tilismtech.tellotalksdk.entities.DepartmentConversations

class ComplainDepartmentReasonViewModel : ViewModel() {

    private val jobRespository: JobsRepository = Injection.provideJobsRepository(DriverApp.getContext())

    private val _items = MutableLiveData<ArrayList<ComplainReason>>().apply { value = ArrayList() }
    val items: LiveData<ArrayList<ComplainReason>>
        get() = _items

    /**
     * Fetch Department Reasons By Department Tag
     * @param departmentTag : Department Tag
     */
    fun fetchDepartmentReason(departmentTag: String) {
        jobRespository.getJobComplainReasons(MESSAGE_TYPE, object : JobsDataSource.ComplainReasonsCallback {
            override fun onSuccess(complainReasonResponse: ComplainReasonResponse) {
                complainReasonResponse.data?.forEachIndexed { index, complainReason ->
                    complainReason.messageModified = (index + DIGIT_ONE).toString().plus(") ").plus(complainReason.message)
                }
                Dialogs.INSTANCE.dismissDialog()
                _items.value = complainReasonResponse.data
            }

            override fun onFail(code: Int, subCode: Int?, message: String?) {
                Dialogs.INSTANCE.dismissDialog()
            }
        })
    }
}