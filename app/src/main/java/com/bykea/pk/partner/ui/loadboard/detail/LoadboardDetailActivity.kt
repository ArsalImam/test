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
}
