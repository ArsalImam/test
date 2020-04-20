package com.bykea.pk.partner.ui.nodataentry

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bykea.pk.partner.DriverApp
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.source.remote.request.nodataentry.DeliveryDetails
import com.bykea.pk.partner.databinding.ActivityListDeliveryDetailsBinding
import com.bykea.pk.partner.ui.common.LastAdapter
import com.bykea.pk.partner.ui.common.obtainViewModel
import com.bykea.pk.partner.ui.helpers.ActivityStackManager
import com.bykea.pk.partner.utils.Constants.DIGIT_ZERO
import com.bykea.pk.partner.utils.Constants.Extras.ADD_DELIVERY_DETAILS
import com.bykea.pk.partner.utils.Constants.Extras.EDIT_DELIVERY_DETAILS
import com.bykea.pk.partner.utils.Dialogs
import com.bykea.pk.partner.utils.GenericListeners
import com.bykea.pk.partner.utils.RecyclerItemTouchHelper
import kotlinx.android.synthetic.main.activity_list_delivery_details.*

class ListDeliveryDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityListDeliveryDetailsBinding
    lateinit var lastAdapter: LastAdapter<DeliveryDetails>
    lateinit var viewModel: ListDeliveryDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_list_delivery_details)
        binding.viewModel = obtainViewModel(ListDeliveryDetailsViewModel::class.java)
        binding.lifecycleOwner = this
        binding.listener = object : GenericListeners {
            override fun addDeliveryDetails() {
                // Add DELIVERY DETAILS
                ActivityStackManager.getInstance()
                        .startAddEditDeliveryDetails(this@ListDeliveryDetailsActivity,
                                ADD_DELIVERY_DETAILS, null);

            }
        }

        lastAdapter = LastAdapter(R.layout.list_item_delivery_detail, object : LastAdapter.OnItemClickListener<DeliveryDetails> {
            override fun onItemClick(item: DeliveryDetails) {}

            override fun onSubItemOneClick(item: DeliveryDetails) {
                // VIEW DELIVERY DETAILS
                ActivityStackManager.getInstance()
                        .startViewDeliveryDetails(this@ListDeliveryDetailsActivity, item)
            }

            override fun onSubItemTwoClick(item: DeliveryDetails) {
                // EDIT DELIVERY DETAILS
                ActivityStackManager.getInstance()
                        .startAddEditDeliveryDetails(this@ListDeliveryDetailsActivity,
                                EDIT_DELIVERY_DETAILS, item);
            }
        })

        recViewDeliveries.adapter = lastAdapter
        binding.viewModel?.requestDeliveryDetails()

        val itemTouchHelperCallback = RecyclerItemTouchHelper(DIGIT_ZERO, ItemTouchHelper.LEFT,
                RecyclerItemTouchHelper.RecyclerItemTouchHelperListener { viewHolder, direction, position ->
                    Dialogs.INSTANCE.showCancelDialog(this@ListDeliveryDetailsActivity,
                            DriverApp.getContext().getString(R.string.cancel_with_question_mark),
                            {
                                lastAdapter.removeItem(position)
                                if (lastAdapter.items.isEmpty()) {
                                    recViewDeliveries.visibility = View.GONE
                                }
                                Dialogs.INSTANCE.dismissDialog()
                            },
                            {
                                lastAdapter.notifyItemChanged(position)
                                Dialogs.INSTANCE.dismissDialog()
                            })
                })
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recViewDeliveries)
    }
}