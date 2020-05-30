package com.bykea.pk.partner.ui.nodataentry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import com.bykea.pk.partner.DriverApp
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.source.JobsDataSource
import com.bykea.pk.partner.dal.source.JobsRepository
import com.bykea.pk.partner.dal.source.remote.request.nodataentry.BatchUpdateReturnRunRequest
import com.bykea.pk.partner.dal.source.remote.response.BatchUpdateReturnRunResponse
import com.bykea.pk.partner.dal.util.DIGIT_ZERO
import com.bykea.pk.partner.dal.util.Injection
import com.bykea.pk.partner.databinding.DialogBatchNaKamiyabBinding
import com.bykea.pk.partner.ui.helpers.ActivityStackManager
import com.bykea.pk.partner.utils.Constants
import com.bykea.pk.partner.utils.Dialogs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.apache.commons.lang3.math.NumberUtils

class BatchNaKamiyabDialog(private val batchId: String, private val callback: OnResult) : BottomSheetDialogFragment() {

    private val jobRespository: JobsRepository = Injection.provideJobsRepository(DriverApp.getContext())
    var selectedCheckPosition: MutableLiveData<Int> = MutableLiveData<Int>().apply { value = DIGIT_ZERO }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding =
                DataBindingUtil.inflate<DialogBatchNaKamiyabBinding>(inflater, R.layout.dialog_batch_na_kamiyab, container, false).apply {
                    lifecycleOwner = this@BatchNaKamiyabDialog
                    `object` = this@BatchNaKamiyabDialog
                }
        return binding.root
    }

    fun updateSelection(selectionId: Int) {
        selectedCheckPosition.value = selectionId
    }

    fun onSelect() {
        when (selectedCheckPosition.value) {
            NumberUtils.INTEGER_ZERO -> updateReturnRun()
            NumberUtils.INTEGER_ONE -> {
                dismiss()
                ActivityStackManager.getInstance()
                        .startAddEditDeliveryDetails(activity, Constants.Extras.ADD_DELIVERY_DETAILS, null)
                this.callback.onReRoute()
            }
        }
    }

    private fun updateReturnRun() {
        Dialogs.INSTANCE.showLoader(activity)
        jobRespository.updateBatchReturnRun(batchId, BatchUpdateReturnRunRequest(true), object : JobsDataSource.LoadDataCallback<BatchUpdateReturnRunResponse> {
            override fun onDataLoaded(response: BatchUpdateReturnRunResponse) {
                Dialogs.INSTANCE.dismissDialog()
                dismiss()
                this@BatchNaKamiyabDialog.callback.onReturnRun()
            }

            override fun onDataNotAvailable(errorCode: Int, reasonMsg: String) {
                Dialogs.INSTANCE.dismissDialog()
                Dialogs.INSTANCE.showToast(reasonMsg)
            }
        })
    }

    fun show(supportFragmentManager: FragmentManager) {
        show(supportFragmentManager, TAG)
    }

    companion object {
        private val TAG = BatchNaKamiyabDialog::class.java.simpleName
    }

    interface OnResult {
        fun onReturnRun()
        fun onReRoute()
    }
}