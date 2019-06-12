package com.bykea.pk.partner.ui.loadboard.detail

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bykea.pk.partner.R
import com.bykea.pk.partner.databinding.LoadboardDetailActBinding
import com.bykea.pk.partner.ui.activities.BaseActivity
import com.bykea.pk.partner.ui.loadboard.common.obtainViewModel
import com.bykea.pk.partner.utils.Dialogs

/**
 * Loadboard booking detail screen ACTIVITY - opening from homeScreen's loadboard listing items
 */
class LoadboardDetailActivity : BaseActivity() {

    private lateinit var binding: LoadboardDetailActBinding
    private var bookingId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.loadboard_detail_act)
        binding.viewmodel = obtainViewModel(BookingDetailViewModel::class.java).apply {
            dataLoading.observe(this@LoadboardDetailActivity, Observer {
                if (it) Dialogs.INSTANCE.showLoader(this@LoadboardDetailActivity)
                else Dialogs.INSTANCE.dismissDialog()
            })
        }
        binding.listener = object : BookingDetailUserActionsListener {
            override fun onPlayAudio(url: String) {
                Dialogs.INSTANCE.showLoader(this@LoadboardDetailActivity)
            }

            override fun onNavigateToMap(lat: Double, lng: Double) {

            }

            override fun onAcceptBooking() {

            }
        }

        bookingId = intent.getLongExtra(EXTRA_BOOKING_ID, 0)
        binding.viewmodel!!.start(bookingId)
    }

    companion object {
        const val EXTRA_BOOKING_ID = "EXTRA_BOOKING_ID"
    }


/*

    */
    /**
     * initialize views and objects related to this screen
     */
    /*

    private fun initViews() {

        mRepository!!.loadboardBookingDetail(mCurrentActivity, bookingId, object : UserDataHandler() {
            override fun onLoadboardBookingDetailResponse(response: LoadboardBookingDetailResponse) {
                Dialogs.INSTANCE.dismissDialog()
                tVEstimatedFare!!.text = "Rs." + response.data.amount + ""
                tVCODAmount!!.text = "Rs." + response.data.cartAmount + ""

                //bookingNoTV.setText(response.getData().getOrderNo());
                //                bookingTypeIV.setImageResource();
                supportFragmentManager.beginTransaction()
                        .replace(R.id.bookingDetailContainerFL, LoadboardDetailFragment.newInstance(response.data))
                        .commitAllowingStateLoss()
            }

            override fun onError(errorCode: Int, errorMessage: String) {
                Dialogs.INSTANCE.dismissDialog()
                if (errorCode == HTTPStatus.UNAUTHORIZED) {
                    Utils.onUnauthorized(mCurrentActivity)
                } else {
                    Dialogs.INSTANCE.showToast(mCurrentActivity, errorMessage)
                }
            }
        })
    }

    */
    /**
     * initialize click listeners for this screen's button or widgets
     */
    /*

    private fun initListeners() {
        backBtn!!.setOnClickListener(this)
        imgViewDelivery!!.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.backBtn -> finish()

            R.id.imgViewDelivery -> Utils.appToast(applicationContext, "imgViewDelivery")
        }
    }
*/


}
