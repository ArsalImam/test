package com.bykea.pk.partner.ui.nodataentry

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import com.bykea.pk.partner.DriverApp
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.source.remote.request.nodataentry.DeliveryDetails
import com.bykea.pk.partner.databinding.ActivityListDeliveryDetailsBinding
import com.bykea.pk.partner.ui.common.LastAdapter
import com.bykea.pk.partner.ui.common.obtainViewModel
import com.bykea.pk.partner.ui.helpers.ActivityStackManager
import com.bykea.pk.partner.utils.Constants.DIGIT_ZERO
import com.bykea.pk.partner.utils.Constants.Extras.*
import com.bykea.pk.partner.utils.Constants.RequestCode.RC_ADD_DELIVERY_DETAILS
import com.bykea.pk.partner.utils.Constants.RequestCode.RC_EDIT_DELIVERY_DETAILS
import com.bykea.pk.partner.utils.Dialogs
import com.bykea.pk.partner.utils.GenericListeners
import com.bykea.pk.partner.utils.RecyclerItemTouchHelper
import kotlinx.android.synthetic.main.activity_list_delivery_details.*

class ListDeliveryDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityListDeliveryDetailsBinding
    lateinit var lastAdapter: LastAdapter<DeliveryDetails>
    lateinit var viewModel: ListDeliveryDetailsViewModel
    private var removeDeliveryDetail: DeliveryDetails? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_list_delivery_details)
        binding.viewModel = obtainViewModel(ListDeliveryDetailsViewModel::class.java).apply {
            itemRemoved.observe(this@ListDeliveryDetailsActivity, Observer {
                if (it) {
                    binding.viewModel?.itemRemoved?.value = false
                    if (::lastAdapter.isInitialized) {
                        removeDeliveryDetail?.let { item -> lastAdapter.removeItem(item) }
                        if (lastAdapter.items.isEmpty()) {
                            recViewDeliveries.visibility = View.GONE
                        }
                    }
                    Dialogs.INSTANCE.dismissDialog()
                }
            })
        }

        binding.lifecycleOwner = this
        binding.listener = object : GenericListeners {
            override fun addDeliveryDetails() {
                // Add DELIVERY DETAILS
                ActivityStackManager.getInstance()
                        .startAddEditDeliveryDetails(this@ListDeliveryDetailsActivity,
                                ADD_DELIVERY_DETAILS, null);

            }
        }

        binding.viewModel?.getActiveTrip()
        setAdapter()
        setAdapterSwipeItemCallback()
        Dialogs.INSTANCE.showLoader(this@ListDeliveryDetailsActivity)
        binding.viewModel?.getAllDeliveryDetails()
    }

    private fun setAdapter() {
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
    }

    private fun setAdapterSwipeItemCallback() {
        val itemTouchHelperCallback = RecyclerItemTouchHelper(DIGIT_ZERO, ItemTouchHelper.LEFT,
                RecyclerItemTouchHelper.RecyclerItemTouchHelperListener { _, _, position ->
                    Dialogs.INSTANCE.showCancelDialog(this@ListDeliveryDetailsActivity,
                            DriverApp.getContext().getString(R.string.cancel_with_question_mark),
                            {
                                removeDeliveryDetail = lastAdapter.items[position]
                                removeDeliveryDetail?.let { viewModel.removeDeliveryDetail(it) }
                                Dialogs.INSTANCE.dismissDialog()
                                Dialogs.INSTANCE.showLoader(this@ListDeliveryDetailsActivity)
                            },
                            {
                                lastAdapter.notifyItemChanged(position)
                                Dialogs.INSTANCE.dismissDialog()
                            })
                })
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recViewDeliveries)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var deliveryDetails: DeliveryDetails? = null

        if (resultCode == RESULT_OK) {
            data?.let { deliveryDetails = data.getParcelableExtra(DELIVERY_DETAILS_OBJECT) as DeliveryDetails }
            if (requestCode == RC_ADD_DELIVERY_DETAILS) {
                deliveryDetails?.let {
                    binding.viewModel?.items?.value?.add(it)
                    lastAdapter.addItem(it)
                }
            } else if (requestCode == RC_EDIT_DELIVERY_DETAILS) {
                deliveryDetails?.let { delivery ->
                    for (i in lastAdapter.items.indices) {
                        if (lastAdapter.items[i].details?.trip_id.equals(delivery.details?.trip_id)) {
                            binding.viewModel?.items?.value?.set(i, delivery)
                            lastAdapter.items[i] = delivery
                            lastAdapter.notifyItemChanged(i)
                            break
                        }
                    }
                }
            }
        }
    }
}