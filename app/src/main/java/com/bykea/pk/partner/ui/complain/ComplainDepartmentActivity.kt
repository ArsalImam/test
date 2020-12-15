package com.bykea.pk.partner.ui.complain

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.bykea.pk.partner.R
import com.bykea.pk.partner.databinding.ActivityComplainDepartmentBinding
import com.bykea.pk.partner.ui.activities.BaseActivity
import com.bykea.pk.partner.ui.common.LastAdapter
import com.bykea.pk.partner.ui.helpers.ActivityStackManager
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.utils.Constants
import com.bykea.pk.partner.utils.Constants.*
import com.bykea.pk.partner.utils.TelloTalkManager
import com.bykea.pk.partner.utils.Utils
import com.tilismtech.tellotalksdk.entities.DepartmentConversations
import kotlinx.android.synthetic.main.activity_complain_department.*
import kotlinx.android.synthetic.main.custom_toolbar_right.*

class ComplainDepartmentActivity : BaseActivity() {
    lateinit var binding: ActivityComplainDepartmentBinding
    var lastAdapter: LastAdapter<DepartmentConversations>? = null

    override fun onResume() {
        super.onResume()
        fetchComplainDepartments()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_complain_department)

        setTitleCustomToolbarUrdu(getString(R.string.chat))
        tvTitleUrdu.textSize = resources.getDimension(R.dimen._11sdp)

        setAdapter()
        fetchComplainDepartments()
    }

    /**
     * Set Adapter For Complain Departments
     */
    private fun setAdapter() {
        lastAdapter = LastAdapter(R.layout.item_complain_departments, object : LastAdapter.OnItemClickListener<DepartmentConversations> {
            override fun onItemClick(item: DepartmentConversations) {
                if (item.department.dptType == DIGIT_ONE.toString()) {
                    TelloTalkManager.instance().openCorporateChat(this@ComplainDepartmentActivity, null, item)
                } else if (item.department.dptType == DIGIT_TWO.toString()) {
                    if (item.department.deptTag.equals(Utils.fetchTelloTalkTag(TelloTalkTags.TELLO_TALK_TRIP_HISTORY_KEY), ignoreCase = true)) {
                        ActivityStackManager.getInstance().startComplainDepartmentBookingActivity(this@ComplainDepartmentActivity, item.department.deptTag)
                    } else {
                        ActivityStackManager.getInstance().startComplainDepartmentReasonActivity(this@ComplainDepartmentActivity, item.department.deptTag, null, null)
                    }
                }
            }

            override fun onSubItemOneClick(view: View, item: DepartmentConversations) {
                Utils.preventMultipleTap(view)
                TelloTalkManager.instance().openCorporateChat(this@ComplainDepartmentActivity, null, item)
            }
        })
        recViewComplainDepartment.adapter = lastAdapter
    }

    /**
     * Fetch Complain Department From Tello Talk SDK
     */
    private fun fetchComplainDepartments() {
        TelloTalkManager.instance().getDepartments().let {
            lastAdapter?.items = ArrayList(it)
        }
    }
}