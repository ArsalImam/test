package com.bykea.pk.partner.ui.nodataentry

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.bykea.pk.partner.R
import com.bykea.pk.partner.databinding.ActivityFinishBookingListingsBinding
import com.bykea.pk.partner.models.response.BatchBooking
import com.bykea.pk.partner.models.response.NormalCallData
import com.bykea.pk.partner.ui.activities.BaseActivity
import com.bykea.pk.partner.ui.common.LastAdapter
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.zendesk.util.StringUtils
import kotlinx.android.synthetic.main.map_toolbar.*

class FinishBookingListingActivity : BaseActivity() {

    private val EXTRA_TITLE: String = "title"

    private var callData: NormalCallData? = null
    private var binding: ActivityFinishBookingListingsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_finish_booking_listings)
        callData = AppPreferences.getCallData()
        initToolbar()
        setAdapter()
    }

    private fun setAdapter() {
        val adapter = LastAdapter(R.layout.item_finish_booking_listing, object : LastAdapter.OnItemClickListener<BatchBooking> {
            override fun onItemClick(item: BatchBooking) {

            }
        })
    }

    private fun initToolbar() {
        setSupportActionBar(map_toolbar)
        back.setOnClickListener { finish() }
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        map_toolbar.title = StringUtils.EMPTY_STRING
        map_toolbar.subtitle = StringUtils.EMPTY_STRING
        (map_toolbar.findViewById<View>(R.id.title) as TextView).text = intent.getStringExtra(EXTRA_TITLE)
    }
}