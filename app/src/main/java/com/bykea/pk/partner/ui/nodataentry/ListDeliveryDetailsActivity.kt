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
import com.bykea.pk.partner.models.response.TopUpPassWalletResponse
import com.bykea.pk.partner.repositories.UserDataHandler
import com.bykea.pk.partner.ui.common.LastAdapter
import com.bykea.pk.partner.ui.common.obtainViewModel
import com.bykea.pk.partner.ui.helpers.ActivityStackManager
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.ui.helpers.StringCallBack
import com.bykea.pk.partner.utils.*
import com.bykea.pk.partner.utils.Constants.DIGIT_ZERO
import com.bykea.pk.partner.utils.Constants.Extras.*
import com.bykea.pk.partner.utils.Constants.RequestCode.RC_ADD_DELIVERY_DETAILS
import com.bykea.pk.partner.utils.Constants.RequestCode.RC_EDIT_DELIVERY_DETAILS
import kotlinx.android.synthetic.main.activity_list_delivery_details.*
import org.apache.commons.lang3.StringUtils

class ListDeliveryDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityListDeliveryDetailsBinding
    lateinit var lastAdapter: LastAdapter<DeliveryDetails>
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
                        binding.viewModel?.items?.value = lastAdapter.items
                    }
                }
            })

            passengerWalletUpdated.observe(this@ListDeliveryDetailsActivity, Observer {
                if (it) {
                    binding.viewModel?.passengerWalletUpdated?.value = false
                    ivTopUp.visibility = View.INVISIBLE
                    setPassengerWallet()
                }
            })
        }

        binding.lifecycleOwner = this
        binding.listener = object : GenericListener {
            override fun addDeliveryDetails() {
                // Add DELIVERY DETAILS
                ActivityStackManager.getInstance()
                        .startAddEditDeliveryDetails(this@ListDeliveryDetailsActivity,
                                ADD_DELIVERY_DETAILS, null)

            }

            override fun topUpPassengerWallet() {
                Dialogs.INSTANCE.showTopUpDialog(this@ListDeliveryDetailsActivity,
                        Utils.isCourierService(binding.viewModel?.callData?.value?.callType), object : StringCallBack {
                    override fun onCallBack(msg: String) {
                        if (StringUtils.isNotBlank(msg)) {
                            Dialogs.INSTANCE.showLoader(this@ListDeliveryDetailsActivity)
                            binding.viewModel?.requestTopUpPassengerWallet(msg)
                        }
                    }
                })
            }
        }

        binding.viewModel?.getActiveTrip()
        setAdapter()
        setAdapterSwipeItemCallback()
        Dialogs.INSTANCE.showLoader(this@ListDeliveryDetailsActivity)
        binding.viewModel?.getAllDeliveryDetails()

        setVisibilityForTopUp()
        setPassengerWallet()
        setCodValue()
        setFareAmount()
    }

    /**
     * Set visibility for top up icon considering the call type and ride status.
     */
    private fun setVisibilityForTopUp() {
        // TODO SET AGAINST BATCH
        if ((Utils.isDeliveryService(binding.viewModel?.callData?.value?.callType) ||
                        Utils.isCourierService(binding.viewModel?.callData?.value?.callType)) &&
                TripStatus.ON_ARRIVED_TRIP.equals(binding.viewModel?.callData?.value?.callType, ignoreCase = true) &&
                AppPreferences.isTopUpPassengerWalletAllowed()) {
            ivTopUp.visibility = View.VISIBLE
        } else {
            ivTopUp.visibility = View.INVISIBLE
        }
    }

    /**
     * Set adapter for delivery details item
     */
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
                                EDIT_DELIVERY_DETAILS, item)
            }
        })

        recViewDeliveries.adapter = lastAdapter
    }

    /**
     * Set swipe to delete on recycler view
     */
    private fun setAdapterSwipeItemCallback() {
        val itemTouchHelperCallback = RecyclerItemTouchHelper(DIGIT_ZERO, ItemTouchHelper.LEFT,
                RecyclerItemTouchHelper.RecyclerItemTouchHelperListener { _, _, position ->
                    Dialogs.INSTANCE.showCancelDialog(this@ListDeliveryDetailsActivity,
                            DriverApp.getContext().getString(R.string.cancel_with_question_mark),
                            {
                                removeDeliveryDetail = lastAdapter.items[position]
                                removeDeliveryDetail?.let { binding.viewModel?.removeDeliveryDetail(it) }
                                Dialogs.INSTANCE.showLoader(this@ListDeliveryDetailsActivity)
                            },
                            {
                                lastAdapter.notifyItemChanged(position)
                                Dialogs.INSTANCE.dismissDialog()
                            })
                })
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recViewDeliveries)
    }

    /**
     * Set passenger wallet from trip data
     */
    private fun setPassengerWallet() {
        tvPWalletAmount.text = String.format(getString(R.string.amount_rs), binding.viewModel?.callData?.value?.passWallet)
    }

    /**
     * Set COD value from trip data
     */
    private fun setCodValue() {
        //TODO : SET CODE VALUE
    }

    /**
     * Set Fare Amount from trip data
     */
    private fun setFareAmount() {
        when {
            binding.viewModel?.callData?.value?.kraiKiKamai != DIGIT_ZERO -> {
                tvFareAmount.text = String.format(getString(R.string.amount_rs_int), binding.viewModel?.callData?.value?.kraiKiKamai)
            }
            AppPreferences.getEstimatedFare() != DIGIT_ZERO -> {
                tvFareAmount.text = String.format(getString(R.string.amount_rs_int), AppPreferences.getEstimatedFare())
            }
            else -> tvFareAmount.setText(R.string.dash)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var deliveryDetails: DeliveryDetails? = null

        if (resultCode == RESULT_OK) {
            data?.let { deliveryDetails = data.getParcelableExtra(DELIVERY_DETAILS_OBJECT) as DeliveryDetails }
            if (requestCode == RC_ADD_DELIVERY_DETAILS) {
                deliveryDetails?.let {
                    binding.viewModel?.items?.value?.add(it)
                    binding.viewModel?.items?.value = binding.viewModel?.items?.value
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