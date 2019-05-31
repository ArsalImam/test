package com.bykea.pk.partner.loadboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.bykea.pk.partner.loadboard.widgets.AutoFitFontTextView
import kotlinx.android.synthetic.main.activity_booking_detail.*

class BookingDetailActivity : AppCompatActivity() {

    var BOOKING_ID = "BOOKING_ID"
    private var bookingId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_detail)

        bookingId = intent.getStringExtra(BOOKING_ID)
        backBtn.setOnClickListener { finish() }
    }
}
