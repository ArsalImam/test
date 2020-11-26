package com.bykea.pk.partner.ui.complain

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.bykea.pk.partner.DriverApp
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.source.remote.data.ComplainReason
import com.bykea.pk.partner.databinding.ActivityComplainDepartmentReasonBinding
import com.bykea.pk.partner.models.data.TripHistoryData
import com.bykea.pk.partner.ui.activities.BaseActivity
import com.bykea.pk.partner.ui.common.LastAdapter
import com.bykea.pk.partner.ui.common.obtainViewModel
import com.bykea.pk.partner.utils.Constants
import com.bykea.pk.partner.utils.Constants.Extras.DEPARTMENT_ID
import com.bykea.pk.partner.utils.Dialogs
import com.bykea.pk.partner.utils.TelloTalkManager
import com.tilismtech.tellotalksdk.entities.DepartmentConversations

class ComplainDepartmentReasonActivity : BaseActivity() {
    lateinit var binding: ActivityComplainDepartmentReasonBinding
    lateinit var viewModel: ComplainDepartmentReasonViewModel
    var tripHistoryData: TripHistoryData? = null

    var lastAdapter: LastAdapter<ComplainReason>? = null
    var departmentConversations: DepartmentConversations? = null
    var departmentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_complain_department_reason)
        viewModel = obtainViewModel(ComplainDepartmentReasonViewModel::class.java)
        binding.viewModel = viewModel

        intent?.extras?.let { intentExtras ->
            if (intentExtras.containsKey(Constants.INTENT_TRIP_HISTORY_DATA)) {
                tripHistoryData = intent.getSerializableExtra(Constants.INTENT_TRIP_HISTORY_DATA) as TripHistoryData
            }
            if (intentExtras.containsKey(DEPARTMENT_ID)) {
                departmentId = intentExtras.getString(DEPARTMENT_ID)
                departmentConversations = TelloTalkManager.instance().getDepartments()?.find { departmentConversations ->
                    !departmentId.isNullOrEmpty() && departmentId == departmentConversations.department.dptId
                }
            }
        }

        setAdapter()

        departmentId?.let {
            Dialogs.INSTANCE.showLoader(this@ComplainDepartmentReasonActivity)
            viewModel.fetchDepartmentReason(it)
        }
    }

    /**
     * Set Adapter For Complain Department Reasons
     */
    fun setAdapter() {
        lastAdapter = LastAdapter(R.layout.drawer_footer_layout, object : LastAdapter.OnItemClickListener<ComplainReason> {
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
    }
}