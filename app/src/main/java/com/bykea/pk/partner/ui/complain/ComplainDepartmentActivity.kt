package com.bykea.pk.partner.ui.complain

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.bykea.pk.partner.R
import com.bykea.pk.partner.databinding.ActivityComplainDepartmentBinding
import com.bykea.pk.partner.ui.activities.BaseActivity
import com.bykea.pk.partner.ui.common.LastAdapter
import com.bykea.pk.partner.ui.helpers.ActivityStackManager
import com.bykea.pk.partner.utils.TelloTalkManager
import com.tilismtech.tellotalksdk.entities.DepartmentConversations

class ComplainDepartmentActivity : BaseActivity() {
    lateinit var binding: ActivityComplainDepartmentBinding
    var lastAdapter: LastAdapter<DepartmentConversations>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_complain_department)

        setTitleCustomToolbarUrdu(getString(R.string.chat))

        lastAdapter = LastAdapter(R.layout.item_complain_departments, object : LastAdapter.OnItemClickListener<DepartmentConversations> {
            override fun onItemClick(item: DepartmentConversations) {
                ActivityStackManager.getInstance().startComplainDepartmentReasonActivity(this@ComplainDepartmentActivity, item.department.dptId, null)
            }
        })

        TelloTalkManager.instance().getDepartments()?.let {
            lastAdapter?.items = ArrayList(it)
        }
    }
}