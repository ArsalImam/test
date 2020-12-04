package com.bykea.pk.partner.ui.complain

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.bykea.pk.partner.DriverApp
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.source.remote.data.ComplainReason
import com.bykea.pk.partner.databinding.ActivityComplainDepartmentReasonBinding
import com.bykea.pk.partner.models.data.TripHistoryData
import com.bykea.pk.partner.models.response.TripHistoryResponse
import com.bykea.pk.partner.repositories.UserDataHandler
import com.bykea.pk.partner.repositories.UserRepository
import com.bykea.pk.partner.ui.activities.BaseActivity
import com.bykea.pk.partner.ui.common.LastAdapter
import com.bykea.pk.partner.ui.common.obtainViewModel
import com.bykea.pk.partner.utils.Constants
import com.bykea.pk.partner.utils.Constants.Extras.DEPARTMENT_TAG
import com.bykea.pk.partner.utils.Dialogs
import com.bykea.pk.partner.utils.TelloTalkManager
import com.bykea.pk.partner.utils.Utils
import com.tilismtech.tellotalksdk.entities.DepartmentConversations
import kotlinx.android.synthetic.main.activity_complain_department_reason.*
import kotlinx.android.synthetic.main.custom_toolbar_right.*
import org.apache.commons.collections.CollectionUtils

class ComplainDepartmentReasonActivity : BaseActivity() {
    lateinit var binding: ActivityComplainDepartmentReasonBinding
    lateinit var viewModel: ComplainDepartmentReasonViewModel
    var tripHistoryData: TripHistoryData? = null
    var tripHistoryId: String? = null

    var lastAdapter: LastAdapter<ComplainReason>? = null
    var departmentConversations: DepartmentConversations? = null
    var departmentTag: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_complain_department_reason)
        binding.lifecycleOwner = this
        viewModel = obtainViewModel(ComplainDepartmentReasonViewModel::class.java)
        binding.viewModel = viewModel

        intent?.extras?.let { intentExtras ->
            if (intentExtras.containsKey(Constants.INTENT_TRIP_HISTORY_DATA)) {
                tripHistoryData = intent.getSerializableExtra(Constants.INTENT_TRIP_HISTORY_DATA) as TripHistoryData
            }
            if (intentExtras.containsKey(Constants.INTENT_TRIP_HISTORY_ID)) {
                tripHistoryId = intent.getStringExtra(Constants.INTENT_TRIP_HISTORY_ID)
                tripHistoryId?.let { updateTripDetailsById(it) }
            }
            if (intentExtras.containsKey(DEPARTMENT_TAG)) {
                departmentTag = intentExtras.getString(DEPARTMENT_TAG)
                TelloTalkManager.instance().getDepartments().find { departmentConversations ->
                    !departmentTag.isNullOrEmpty() && departmentTag.equals(departmentConversations.department.deptTag, ignoreCase = true)
                }?.let {
                    departmentConversations = it
                    setTitleCustomToolbarUrdu(it.department.name_u)
                    tvTitleUrdu.textSize = resources.getDimension(R.dimen._11sdp)
                    imgViewCategory.visibility = View.VISIBLE
                    if (!it.department.dptImage.isNullOrEmpty()) {
                        Utils.loadImgPicasso(imgViewCategory, R.color.white, it.department.dptImage)
                    }
                }
            }
        }

        setAdapter()

        departmentTag?.let {
            Dialogs.INSTANCE.showLoader(this@ComplainDepartmentReasonActivity)
            viewModel.fetchDepartmentReason(it)
        }
    }

    /**
     * Set Adapter For Complain Department Reasons
     */
    fun setAdapter() {
        lastAdapter = LastAdapter(R.layout.item_complain_department_reason, object : LastAdapter.OnItemClickListener<ComplainReason> {
            override fun onItemClick(item: ComplainReason) {
                var template = item.message

                tripHistoryData?.let {
                    template = String.format(DriverApp.getContext().getString(R.string.tello_trip_template), it.tripNo, item.message)
                }

                TelloTalkManager
                        .instance()
                        .openCorporateChat(this@ComplainDepartmentReasonActivity, template, departmentConversations)
            }
        })
        recViewComplainDepartmentReason.adapter = lastAdapter
    }

    /**
     * this method will update the trip details by id
     *
     * @param tripHistoryId (optional) if any specific trip details needed
     */
    private fun updateTripDetailsById(tripHistoryId: String) {
        Dialogs.INSTANCE.showLoader(this@ComplainDepartmentReasonActivity)
        UserRepository().requestTripHistory(this@ComplainDepartmentReasonActivity, object : UserDataHandler() {
            override fun onGetTripHistory(tripHistoryResponse: TripHistoryResponse?) {
                super.onGetTripHistory(tripHistoryResponse)
                Dialogs.INSTANCE.dismissDialog()
                if (tripHistoryResponse?.isSuccess!! && CollectionUtils.isNotEmpty(tripHistoryResponse.data)) {
                    tripHistoryData = tripHistoryResponse.data[Constants.DIGIT_ZERO]
                } else {
                    Utils.appToast(tripHistoryResponse?.message)
                }
            }
        }, Constants.DIGIT_ONE.toString(), tripHistoryId)
    }
}