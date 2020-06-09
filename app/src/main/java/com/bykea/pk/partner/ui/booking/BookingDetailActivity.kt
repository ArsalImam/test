package com.bykea.pk.partner.ui.booking

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.source.remote.data.Invoice
import com.bykea.pk.partner.databinding.ActivityBookingDetailBinding
import com.bykea.pk.partner.ui.activities.BaseActivity
import com.bykea.pk.partner.ui.common.LastAdapter
import com.bykea.pk.partner.ui.common.obtainViewModel
import com.bykea.pk.partner.ui.helpers.ActivityStackManager
import com.bykea.pk.partner.utils.Constants
import com.bykea.pk.partner.utils.Dialogs


/**
 * This class will responsible to manage partner booking detail journey
 *
 * @author ArsalImam
 */
class BookingDetailActivity : BaseActivity() {

    /**
     * data source for invoice list
     */
    private lateinit var invoiceAdapter: LastAdapter<Invoice>

    /**
     * data source for batch invoice list
     */
    private lateinit var batchInvoiceAdapter: LastAdapter<Invoice>

    /**
     * data source for customer feedback list
     */
    private lateinit var feedbackAdapter: LastAdapter<String>

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

    /**
     * this method is binded with complain submit button and will trigger on its click
     */
    public fun onComplainButtonClicked(view: View) {
        ActivityStackManager.getInstance()
                .startComplainSubmissionActivity(this, null, viewModel?.bookingDetailData?.value?.bookingId!!)
    }

    /**
     * this will update toolbar for the booking detail activity
     */
    private fun updateToolbar() {
        setBackNavigation()
        hideToolbarLogo()
    }

    /**
     * This method is binding view model properties with view components
     * through LiveData API available in Android mvvm
     *
     * @see [LiveData](https://developer.android.com/topic/libraries/architecture/livedata)
     */
    private fun setupObservers() {
        viewModel?.showLoader?.observe(this, Observer {
            if (it) Dialogs.INSTANCE.showLoader(this@BookingDetailActivity)
            else Dialogs.INSTANCE.dismissDialog()
        })

        viewModel?.bookingDetailData?.observe(this, Observer {
            it?.let {
                setToolbarTitle(it.bookingCode?.toUpperCase())
                it.invoice?.let { invoiceAdapter.items = it }
                it.batchInvoice?.let { batchInvoiceAdapter.items = it }
                it.proofOfDelivery?.let {
                    val url = it
                    showRightIcon(R.drawable.ic_remove_red_eye_black_24dp, R.color.colorAccent) {
                        //handle right icon
                        Dialogs.INSTANCE.showChangeImageDialog(this@BookingDetailActivity, null, url,
                                null, null)
                    }
                } ?: run {
                    hideRightIcon()
                }
                it.rate?.driverFeedback?.let {

                    /**
                     * this method will update the span count to center columns horizontally
                     */
                    (binding?.feedbackRecyclerView?.layoutManager as GridLayoutManager).spanCount = Constants.DIGIT_THREE
                    feedbackAdapter.items = it
                }
            }
        })
        viewModel?.updateBookingDetailById(intent.extras[EXTRA_BOOKING_DETAIL_ID].toString())
        invoiceAdapter = LastAdapter(R.layout.adapter_booking_detail_invoice, object : LastAdapter.OnItemClickListener<Invoice> {
            override fun onItemClick(item: Invoice) {

            }
        })
        batchInvoiceAdapter = LastAdapter(R.layout.adapter_booking_detail_invoice, object : LastAdapter.OnItemClickListener<Invoice> {
            override fun onItemClick(item: Invoice) {

            }
        })
        feedbackAdapter = LastAdapter(R.layout.adapter_booking_detail_feedback, object : LastAdapter.OnItemClickListener<String> {
            override fun onItemClick(item: String) {

            }
        })
        binding?.invoiceAdapter = invoiceAdapter
        binding?.feedbackAdapter = feedbackAdapter
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