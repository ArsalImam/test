package com.bykea.pk.partner.ui.booking

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.source.remote.data.Invoice
import com.bykea.pk.partner.databinding.ActivityBookingDetailBinding
import com.bykea.pk.partner.ui.activities.BaseActivity
import com.bykea.pk.partner.ui.common.LastAdapter
import com.bykea.pk.partner.ui.common.obtainViewModel
import com.bykea.pk.partner.ui.helpers.ActivityStackManager
import com.bykea.pk.partner.utils.Dialogs


/**
 * This class will responsible to manage the complete withdrawal process
 *
 * @author Arsal Imam
 */
class BookingDetailActivity : BaseActivity() {

    private lateinit var invoiceAdapter: LastAdapter<Invoice>
    /**
     * Binding object between activity and xml file, it contains all objects
     * of UI components used by activity
     */
    private var binding: ActivityBookingDetailBinding? = null

    /**
     * ViewModel object of {BookingDetailActivity} View
     */
    private var viewModel: BookingDetailViewModel? = null

    /**
     * {@inheritDoc}
     *
     *
     * This will calls on every new initialization of this activity,
     * It can be used for any initializations or on start executions
     *
     * @param savedInstanceState to get data on activity state changed
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_booking_detail)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

        viewModel = this.obtainViewModel(BookingDetailViewModel::class.java)
        binding?.lifecycleOwner = this
        binding?.viewModel = viewModel

        updateToolbar()
        setupObservers()
    }

    public fun onComplainButtonClicked(view: View) {
        ActivityStackManager.getInstance()
                .startComplainSubmissionActivity(this, null, viewModel?.bookingDetailData?.value?.bookingId!!)
    }

    private fun updateToolbar() {
        setBackNavigation()
        hideToolbarLogo()
    }

    /**
     * This method is binding view model properties with view components
     * through LiveData API available in Android MVVM
     *
     * @see [LiveData](https://developer.android.com/topic/libraries/architecture/livedata)
     */
    private fun setupObservers() {
        viewModel?.showLoader?.observe(this, Observer {
            if (it) Dialogs.INSTANCE.showLoader(this@BookingDetailActivity)
            else Dialogs.INSTANCE.dismissDialog()
        })

        viewModel?.bookingDetailData?.observe(this, Observer {
            if (it == null) return@Observer
            setToolbarTitle(it.bookingCode?.toUpperCase())
            if (it.invoice != null)
                invoiceAdapter.items = it.invoice!!
        })
        viewModel?.updateBookingDetailById(intent.extras[EXTRA_BOOKING_DETAIL_ID].toString())
        invoiceAdapter = LastAdapter(R.layout.adapter_booking_detail_invoice, object : LastAdapter.OnItemClickListener<Invoice> {
            override fun onItemClick(item: Invoice) {

            }
        })
        binding?.invoiceAdapter = invoiceAdapter
    }

    /**
     * Removing activity from stack, this method is calling from view's onclick event
     *
     * @param v back button
     */
    fun finishActivity(v: View) {
        finish()
    }

    companion object {

        /**
         * this can be used to receive data from other activities to this activity,
         */
        const val EXTRA_BOOKING_DETAIL_ID = "booking_detail_id"

        /**
         * This method is used to open booking activity by using intent API mentioned by android docs.
         * For more info on intents, refers the below URL,
         *
         * @param [activity] context to open withdrawal activity
         * @param [bookingId] id of the booking those details need to show
         * @see [Intent](https://developer.android.com/reference/android/content/Intent)
         */
        fun openActivity(activity: Activity, bookingId: String) {
            val i = Intent(activity, BookingDetailActivity::class.java)
            i.putExtra(EXTRA_BOOKING_DETAIL_ID, bookingId)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            activity.startActivity(i)
        }
    }
}