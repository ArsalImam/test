package com.bykea.pk.partner.ui.nodataentry

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.bykea.pk.partner.R
import com.bykea.pk.partner.databinding.ActivityViewDeliveryDetailsBinding
import com.bykea.pk.partner.ui.activities.BaseActivity
import com.zendesk.util.StringUtils
import kotlinx.android.synthetic.main.custom_toolbar.*

class ViewDeliveryDetailsActivity : BaseActivity() {
    lateinit var binding: ActivityViewDeliveryDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_delivery_details)

        setTitleCustomToolbarWithUrdu("KHILHQY7", StringUtils.EMPTY_STRING)
        fLLocation.visibility = View.VISIBLE
        tVLocationAlphabet.text = "A"
    }
}
