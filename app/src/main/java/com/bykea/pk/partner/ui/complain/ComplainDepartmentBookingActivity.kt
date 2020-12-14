package com.bykea.pk.partner.ui.complain

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.bykea.pk.partner.R
import com.bykea.pk.partner.databinding.ActivityComplainDepartmentBinding
import com.bykea.pk.partner.databinding.ActivityComplainDepartmentBookingBinding
import com.bykea.pk.partner.ui.activities.BaseActivity
import com.bykea.pk.partner.ui.helpers.ActivityStackManager
import com.bykea.pk.partner.utils.Constants
import com.bykea.pk.partner.utils.TelloTalkManager
import com.bykea.pk.partner.utils.Utils
import com.tilismtech.tellotalksdk.entities.DepartmentConversations
import kotlinx.android.synthetic.main.activity_complain_department_booking.*
import kotlinx.android.synthetic.main.custom_toolbar_right.*
import org.apache.commons.lang3.StringUtils

class ComplainDepartmentBookingActivity : BaseActivity() {
    lateinit var binding: ActivityComplainDepartmentBookingBinding
    var departmentConversations: DepartmentConversations? = null
    var departmentTag: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_complain_department_booking)
        intent?.extras?.let { intentExtras ->
            if (intentExtras.containsKey(Constants.Extras.DEPARTMENT_TAG)) {
                departmentTag = intentExtras.getString(Constants.Extras.DEPARTMENT_TAG)
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

        setListeners()
    }

    /**
     * Set Listeners
     * 1) Submit New Complain
     * 2) View Old Complain
     */
    private fun setListeners() {
        submitNewComplain.setOnClickListener {
            ActivityStackManager.getInstance().startHomeActivityWithBookingHistory(this@ComplainDepartmentBookingActivity)
        }

        viewOldComplain.setOnClickListener {
            TelloTalkManager
                    .instance()
                    .openCorporateChat(this@ComplainDepartmentBookingActivity, StringUtils.EMPTY, departmentConversations)
        }
    }
}