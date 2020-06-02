package com.bykea.pk.partner.ui.nodataentry

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import com.bykea.pk.partner.DriverApp
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.source.remote.request.nodataentry.DeliveryDetails
import com.bykea.pk.partner.dal.source.remote.response.DeliveryDetailListResponse
import com.bykea.pk.partner.databinding.ActivityListDeliveryDetailsBinding
import com.bykea.pk.partner.ui.activities.BaseActivity
import com.bykea.pk.partner.ui.common.LastAdapter
import com.bykea.pk.partner.ui.common.obtainViewModel
import com.bykea.pk.partner.ui.helpers.ActivityStackManager
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.ui.helpers.StringCallBack
import com.bykea.pk.partner.utils.*
import com.bykea.pk.partner.utils.Constants.DIGIT_THOUSAND
import com.bykea.pk.partner.utils.Constants.DIGIT_ZERO
import com.bykea.pk.partner.utils.Constants.Extras.*
import com.bykea.pk.partner.utils.Constants.RequestCode.*
import com.bykea.pk.partner.utils.TripStatus.ON_ARRIVED_TRIP
import com.bykea.pk.partner.utils.TripStatus.ON_START_TRIP
import kotlinx.android.synthetic.main.activity_list_delivery_details.*
import org.apache.commons.lang3.StringUtils

class ListDeliveryDetailsActivity : BaseActivity() {
    lateinit var binding: ActivityListDeliveryDetailsBinding
    lateinit var lastAdapter: LastAdapter<DeliveryDetails>
    private var removeDeliveryDetail: DeliveryDetails? = null
    lateinit var viewModel: ListDeliveryDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_list_delivery_details)
        viewModel = obtainViewModel(ListDeliveryDetailsViewModel::class.java).apply {
            passengerWalletUpdated.observe(this@ListDeliveryDetailsActivity, Observer {
                if (it) {
                    viewModel.passengerWalletUpdated.value = false
                    ivTopUp.visibility = View.INVISIBLE
                    setPassengerWallet()
                }
            })

            isReturnRunEnable.observe(this@ListDeliveryDetailsActivity, Observer {
                if (it) {
                    linLayoutReturnRun.visibility = View.VISIBLE
                } else {
                    if (::lastAdapter.isInitialized && lastAdapter.items.size > DIGIT_ZERO) {
                        linLayoutReturnRun.visibility = View.VISIBLE
                    } else {
                        linLayoutReturnRun.visibility = View.GONE
                    }
                }
            })

            deliveryDetailsEditOrView.observe(this@ListDeliveryDetailsActivity, Observer {
                it?.let {
                    when (it.first) {
                        VIEW_DELIVERY_DETAILS -> {
                            ActivityStackManager.getInstance()
                                    .startViewDeliveryDetails(this@ListDeliveryDetailsActivity, it.second)
                        }
                        EDIT_DELIVERY_DETAILS -> {
                            ActivityStackManager.getInstance()
                                    .startAddEditDeliveryDetails(this@ListDeliveryDetailsActivity,
                                            EDIT_DELIVERY_DETAILS, it.second)
                        }
                    }
                }
            })

            isFieldsUpdateRequired.observe(this@ListDeliveryDetailsActivity, Observer {
                it?.let {
                    setCodValue()
                    setFareAmount()
                }
            })
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        viewModel.getActiveTrip()
        viewModel.setReturnRunEnableOrNot()

        setListeners()
        setVisibilityForTopUp()
        setPassengerWallet()
        setCodValue()
        setFareAmount()

        setAdapter()
        viewModel.callData.value?.status?.let {
            //ENABLE SWIPE TO DELETE ON ARRIVED STATE ONLY
            if (it.equals(ON_ARRIVED_TRIP, ignoreCase = true)) {
                setAdapterSwipeItemCallback()
            } else if (it.equals(ON_START_TRIP, ignoreCase = true)) {
                linLayoutReturnRun.visibility = View.GONE
                imageViewAddDelivery.visibility = View.GONE
            }
        }

        Dialogs.INSTANCE.showLoader(this@ListDeliveryDetailsActivity)
        viewModel.getAllDeliveryDetails()
    }

    /**
     * Set Listeners using generic listener interface
     */
    private fun setListeners() {
        binding.listener = object : GenericListener {
            override fun addDeliveryDetails(view: View) {
                preventMultipleTap(view)
                // Add DELIVERY DETAILS
                ActivityStackManager.getInstance()
                        .startAddEditDeliveryDetails(this@ListDeliveryDetailsActivity,
                                ADD_DELIVERY_DETAILS, null)

            }

            override fun topUpPassengerWallet() {
                Dialogs.INSTANCE.showTopUpDialog(this@ListDeliveryDetailsActivity,
                        Utils.isCourierService(viewModel.callData.value?.callType), object : StringCallBack {
                    override fun onCallBack(msg: String) {
                        if (StringUtils.isNotBlank(msg)) {
                            Dialogs.INSTANCE.showLoader(this@ListDeliveryDetailsActivity)
                            viewModel.requestTopUpPassengerWallet(msg)
                        }
                    }
                })
            }

            override fun onCheckChangedListener(compoundButton: CompoundButton, isChecked: Boolean) {
                if (viewModel.callData.value?.status.toString().equals(ON_START_TRIP, ignoreCase = true)) {
                    viewModel.setReturnRunEnableOrNot(!isChecked)
                } else {
                    Dialogs.INSTANCE.showLoader(this@ListDeliveryDetailsActivity)
                    viewModel.updateBatchReturnRun(isChecked)
                }
            }

            override fun onBackClicked() {
                onBackPressed()
            }
        }
    }

    /**
     * Set visibility for top up icon considering the call type and ride status.
     */
    private fun setVisibilityForTopUp() {
        viewModel.callData.value?.serviceCode?.let {
            if ((Utils.isNewBatchService(it)) &&
                    TripStatus.ON_ARRIVED_TRIP.equals(viewModel.callData.value?.status, ignoreCase = true) &&
                    AppPreferences.isTopUpPassengerWalletAllowed()) {
                ivTopUp.visibility = View.VISIBLE
            } else {
                ivTopUp.visibility = View.INVISIBLE
            }
        } ?: run {
            ivTopUp.visibility = View.INVISIBLE
        }
    }

    /**
     * Set passenger wallet from trip data
     */
    private fun setPassengerWallet() {
        viewModel.callData.value?.actualPassWallet?.let {
            if (it <= DIGIT_ZERO) {
                rLPWalletAmount.setBackgroundColor(ContextCompat.getColor(DriverApp.getContext(), R.color.red))
                tvPWalletAmount.setTextColor(ContextCompat.getColor(DriverApp.getContext(), R.color.white))
                tvPWalletAmountLabel.setTextColor(ContextCompat.getColor(DriverApp.getContext(), R.color.white))
            } else {
                rLPWalletAmount.setBackgroundColor(ContextCompat.getColor(DriverApp.getContext(), R.color.blue_light))
                tvPWalletAmount.setTextColor(ContextCompat.getColor(DriverApp.getContext(), R.color.black))
                tvPWalletAmountLabel.setTextColor(ContextCompat.getColor(DriverApp.getContext(), R.color.black))
            }
            tvPWalletAmount.text = String.format(getString(R.string.amount_rs), viewModel.callData.value?.passWallet)
        }
    }

    /**
     * Set COD value from trip data
     */
    private fun setCodValue() {
        viewModel.callData.value?.cashKiWasooli?.let {
            val cashKiWasooliValue = it
            /*cashKiWasooliValue += Integer.valueOf(viewModel.callData.value?.codAmountNotFormatted?.trim { it <= ' ' })*/
            tvCodAmount.text = String.format(getString(R.string.amount_rs), cashKiWasooliValue.toString())
        }
    }

    /**
     * Set Fare Amount from trip data
     */
    private fun setFareAmount() {
        when {
            viewModel.callData.value?.kraiKiKamai != DIGIT_ZERO -> {
                tvFareAmount.text = String.format(getString(R.string.amount_rs_int), viewModel.callData.value?.kraiKiKamai).plus(Constants.PLUS)
            }
            AppPreferences.getEstimatedFare() != DIGIT_ZERO -> {
                tvFareAmount.text = String.format(getString(R.string.amount_rs_int), AppPreferences.getEstimatedFare()).plus(Constants.PLUS)
            }
            else -> tvFareAmount.setText(R.string.dash)
        }
    }

    /**
     * Set adapter for delivery details item
     */
    private fun setAdapter() {
        lastAdapter = LastAdapter(R.layout.list_item_delivery_detail, object : LastAdapter.OnItemClickListener<DeliveryDetails> {
            override fun onItemClick(item: DeliveryDetails) {}

            override fun onSubItemOneClick(view: View, item: DeliveryDetails) {
                preventMultipleTap(view)
                // VIEW DELIVERY DETAILS
                Dialogs.INSTANCE.showLoader(this@ListDeliveryDetailsActivity)
                viewModel.getSingleDeliveryDetails(VIEW_DELIVERY_DETAILS, item.details?.trip_id.toString())
            }

            override fun onSubItemTwoClick(view: View, item: DeliveryDetails) {
                preventMultipleTap(view)
                // EDIT DELIVERY DETAILS
                Dialogs.INSTANCE.showLoader(this@ListDeliveryDetailsActivity)
                viewModel.getSingleDeliveryDetails(EDIT_DELIVERY_DETAILS, item.details?.trip_id.toString())
            }
        })

        recViewDeliveries.adapter = lastAdapter
    }

    private fun preventMultipleTap(view: View) {
        view.isEnabled = false
        Handler().postDelayed({ view.isEnabled = true }, DIGIT_THOUSAND.toLong())
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
        if (resultCode == RESULT_OK) {
            if (requestCode == RC_ADD_EDIT_DELIVERY_DETAILS) {
                Dialogs.INSTANCE.showLoader(this@ListDeliveryDetailsActivity)
                viewModel.getAllDeliveryDetails()
            }
        }
    }
}