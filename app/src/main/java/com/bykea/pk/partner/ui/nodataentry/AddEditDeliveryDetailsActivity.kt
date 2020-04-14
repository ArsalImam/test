package com.bykea.pk.partner.ui.nodataentry

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.source.remote.request.nodataentry.DeliveryDetails
import com.bykea.pk.partner.dal.util.DIGIT_ZERO
import com.bykea.pk.partner.databinding.ActivityAddEditDeliveryDetailsBinding
import com.bykea.pk.partner.ui.activities.BaseActivity
import com.bykea.pk.partner.ui.common.obtainViewModel
import com.bykea.pk.partner.utils.Constants.AMOUNT_LIMIT
import com.bykea.pk.partner.utils.Constants.Extras.*
import com.bykea.pk.partner.utils.GenericListener
import kotlinx.android.synthetic.main.activity_add_edit_delivery_details.*
import kotlinx.android.synthetic.main.custom_toolbar.*

class AddEditDeliveryDetailsActivity : BaseActivity() {
    lateinit var binding: ActivityAddEditDeliveryDetailsBinding
    lateinit var viewModel: AddEditDeliveryDetailsViewModel
    var flowForAddOrEdit: Int = DIGIT_ZERO
    var deliveryDetails: DeliveryDetails? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_edit_delivery_details)
        binding.viewModel = obtainViewModel(AddEditDeliveryDetailsViewModel::class.java)

        // CHECK FLOW IS FOR ADD OR EDIT DELIVERY DETAILS
        if (intent != null && intent?.extras != null) {
            if (intent?.extras!!.containsKey(FLOW_FOR)) {
                flowForAddOrEdit = intent?.extras!!.get(FLOW_FOR) as Int
            }
            if (intent?.extras!!.containsKey(DELIVERY_DETAILS_OBJECT)) {
                deliveryDetails = intent?.extras!!.getParcelable(DELIVERY_DETAILS_OBJECT) as DeliveryDetails
                viewModel.deliveryDetails.value = deliveryDetails
            }
        }

        setTitleCustomToolbarUrdu(getString(R.string.parcel_details))
        fLLocation.visibility = View.VISIBLE

        binding.listener = object : GenericListener {
            override fun addOrEditDeliveryDetails() {
                if (isValidate()) {
                    when (flowForAddOrEdit) {
                        ADD_DELIVERY_DETAILS -> {
                            viewModel.requestAddDeliveryDetails()
                        }
                        EDIT_DELIVERY_DETAILS -> {
                            viewModel.requestEditDeliveryDetail()
                        }
                    }
                }
            }
        }

    }

    fun isValidate(): Boolean {
        if (editTextMobileNumber.text.isNullOrEmpty()) {
            return false
        } else if (editTextGPSAddress.text.isNullOrEmpty()) {
            return false
        } else if (editTextParcelValue.text.isNullOrEmpty() || editTextParcelValue.text.toString().toInt() == DIGIT_ZERO) {
            return false
        } else if (!(editTextParcelValue.text.toString().toInt() > DIGIT_ZERO &&
                        editTextParcelValue.text.toString().toInt() > AMOUNT_LIMIT)) {
            return false
        }
        return true
    }
}
