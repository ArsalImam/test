package com.bykea.pk.partner.ui.nodataentry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bykea.pk.partner.DriverApp
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.source.JobsDataSource
import com.bykea.pk.partner.dal.source.JobsRepository
import com.bykea.pk.partner.dal.source.remote.request.nodataentry.DeliveryDetails
import com.bykea.pk.partner.dal.source.remote.response.DeliveryDetailRemoveResponse
import com.bykea.pk.partner.dal.util.Injection
import com.bykea.pk.partner.utils.Dialogs
import org.apache.commons.lang3.StringUtils


/**
 * Created by Sibtain Raza on 4/13/2020.
 */
class ListDeliveryDetailsViewModel : ViewModel() {

    val jobRespository: JobsRepository = Injection.provideJobsRepository(DriverApp.getContext())

    private var _items = ArrayList<DeliveryDetails>()
    val items: List<DeliveryDetails>
        get() = _items

    private var _itemRemoved = MutableLiveData<Boolean>()
    val itemRemoved: MutableLiveData<Boolean>
        get() = _itemRemoved

    fun requestDeliveryDetails() {
        val deliveryDetailsList: ArrayList<DeliveryDetails> = ArrayList()
        for (i in 65..90) {
            deliveryDetailsList.add(DeliveryDetails(i.toChar().toString(), ((i % 65 + 1)).toString() + StringUtils.SPACE + DriverApp.getContext().getString(R.string.sawari), "KHIJKL" + ((i % 65 + 1)).toString()))
        }
        _items = deliveryDetailsList
    }

    fun removeDeliveryDetail() {
        jobRespository.removeDeliveryDetail("PENDING", "PENDING", object : JobsDataSource.LoadDataCallback<DeliveryDetailRemoveResponse> {
            override fun onDataLoaded(response: DeliveryDetailRemoveResponse) {
                _itemRemoved.value = true
                Dialogs.INSTANCE.dismissDialog()
            }

            override fun onDataNotAvailable(errorCode: Int, reasonMsg: String) {
                Dialogs.INSTANCE.dismissDialog()
            }
        })
    }
}