package com.bykea.pk.partner.ui.nodataentry

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.source.remote.request.nodataentry.DeliveryDetails
import com.bykea.pk.partner.databinding.ActivityViewDeliveryDetailsBinding
import com.bykea.pk.partner.ui.activities.BaseActivity
import com.bykea.pk.partner.ui.common.obtainViewModel
import com.bykea.pk.partner.utils.Constants
import com.zendesk.util.StringUtils
import kotlinx.android.synthetic.main.custom_toolbar.*

class ViewDeliveryDetailsActivity : BaseActivity() {
    lateinit var binding: ActivityViewDeliveryDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_delivery_details)
        binding.viewModel = obtainViewModel(ViewDeliveryDetailViewsModel::class.java)
        binding.lifecycleOwner = this

        binding.viewModel?.getActiveTrip()

        if (intent?.extras!!.containsKey(Constants.Extras.DELIVERY_DETAILS_OBJECT)) {
            binding.viewModel?.deliveryDetails?.value = intent?.extras!!.getParcelable(Constants.Extras.DELIVERY_DETAILS_OBJECT) as DeliveryDetails
        }

        setTitleCustomToolbarWithUrdu(binding.viewModel?.deliveryDetails?.value?.details?.trip_no, StringUtils.EMPTY_STRING)
        fLLocation.visibility = View.VISIBLE
        tVLocationAlphabet.text = binding.viewModel?.deliveryDetails?.value?.details?.display_tag
    }
}
