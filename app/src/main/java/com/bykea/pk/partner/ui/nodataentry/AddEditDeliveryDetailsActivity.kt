package com.bykea.pk.partner.ui.nodataentry

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.util.DIGIT_ZERO
import com.bykea.pk.partner.databinding.ActivityAddEditDeliveryDetailsBinding
import com.bykea.pk.partner.utils.Constants.Extras.ADD_DELIVERY_DETAILS
import com.bykea.pk.partner.utils.Constants.Extras.FLOW_FOR

class AddEditDeliveryDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddEditDeliveryDetailsBinding
    var flowForAddOrEdit: Int = DIGIT_ZERO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_edit_delivery_details)

        //CHECK FLOW IS FOR ADD OR EDIT DELIVERY DETAILS
        if (intent != null && intent?.extras != null && intent?.extras!!.containsKey(FLOW_FOR)) {
            flowForAddOrEdit = intent?.extras!!.get(FLOW_FOR) as Int
        }
    }
}
