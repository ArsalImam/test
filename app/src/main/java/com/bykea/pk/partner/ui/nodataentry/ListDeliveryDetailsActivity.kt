package com.bykea.pk.partner.ui.nodataentry

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.bykea.pk.partner.R
import com.bykea.pk.partner.databinding.ActivityListDeliveryDetailsBinding

class ListDeliveryDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityListDeliveryDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_list_delivery_details)
    }
}
