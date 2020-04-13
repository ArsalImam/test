package com.bykea.pk.partner.ui.nodataentry

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.source.remote.response.DeliveryDetails
import com.bykea.pk.partner.databinding.ActivityListDeliveryDetailsBinding
import com.bykea.pk.partner.ui.common.LastAdapter
import com.bykea.pk.partner.ui.common.obtainViewModel
import com.bykea.pk.partner.utils.Dialogs
import com.bykea.pk.partner.utils.GenericListeners
import kotlinx.android.synthetic.main.activity_list_delivery_details.*

class ListDeliveryDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityListDeliveryDetailsBinding
    lateinit var lastAdapter: LastAdapter<DeliveryDetails>
    lateinit var viewModel: ListDeliveryDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_list_delivery_details)
        binding.viewModel = obtainViewModel(ListDeliveryDetailsViewModel::class.java)
        binding.listener = object : GenericListeners {
            override fun addDeliveryDetails() {
                // ADD DELIVERY DETAILS
                Dialogs.INSTANCE.showToast("Add Delivery Details")
            }
        }


        lastAdapter = LastAdapter(R.layout.list_item_delivery_detail, object : LastAdapter.OnItemClickListener<DeliveryDetails> {
            override fun onItemClick(item: DeliveryDetails) {}

            override fun onSubItemOneClick(item: DeliveryDetails) {
                // VIEW DELIVERY DETAILS
                Dialogs.INSTANCE.showToast("View Delivery Details")
            }

            override fun onSubItemTwoClick(item: DeliveryDetails) {
                // EDIT DELIVERY DETAILS
                Dialogs.INSTANCE.showToast("Edit Delivery Details")
            }

            override fun onSubItemThreeClick(item: DeliveryDetails) {
                // REMOVE DELIVERY DETAILS
                Dialogs.INSTANCE.showToast("Remove Delivery Details")

            }
        })

        recViewDeliveries.adapter = lastAdapter
        binding.viewModel?.requestDeliveryDetails()
    }
}