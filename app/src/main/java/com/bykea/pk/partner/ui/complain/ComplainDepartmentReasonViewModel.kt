package com.bykea.pk.partner.ui.complain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bykea.pk.partner.DriverApp
import com.bykea.pk.partner.dal.source.JobsRepository
import com.bykea.pk.partner.dal.source.remote.data.ComplainReason
import com.bykea.pk.partner.dal.util.Injection
import com.tilismtech.tellotalksdk.entities.DepartmentConversations

class ComplainDepartmentReasonViewModel : ViewModel() {

    private val jobRespository: JobsRepository = Injection.provideJobsRepository(DriverApp.getContext())

    private val _items = MutableLiveData<ArrayList<ComplainReason>>().apply { value = ArrayList() }
    val items: LiveData<ArrayList<ComplainReason>>
        get() = _items

    fun fetchDepartmentReason(departmentId: String) {
        /*jobRespository.getJobComplainReasons()*/
    }
}