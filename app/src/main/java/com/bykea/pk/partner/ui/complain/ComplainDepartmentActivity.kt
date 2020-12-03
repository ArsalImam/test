package com.bykea.pk.partner.ui.complain

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.bykea.pk.partner.R
import com.bykea.pk.partner.databinding.ActivityComplainDepartmentBinding
import com.bykea.pk.partner.ui.activities.BaseActivity
import com.bykea.pk.partner.ui.common.LastAdapter
import com.bykea.pk.partner.ui.helpers.ActivityStackManager
import com.bykea.pk.partner.utils.Constants.DIGIT_ONE
import com.bykea.pk.partner.utils.TelloTalkManager
import com.tilismtech.tellotalksdk.entities.DepartmentConversations
import kotlinx.android.synthetic.main.activity_complain_department.*

class ComplainDepartmentActivity : BaseActivity() {
    lateinit var binding: ActivityComplainDepartmentBinding
    var lastAdapter: LastAdapter<DepartmentConversations>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_complain_department)

        setTitleCustomToolbarUrdu(getString(R.string.chat))

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
                } else {
                    ActivityStackManager.getInstance().startComplainDepartmentReasonActivity(this@ComplainDepartmentActivity, item.department.deptTag, null, null)
                }
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