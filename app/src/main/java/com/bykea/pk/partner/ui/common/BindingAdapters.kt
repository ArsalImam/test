package com.bykea.pk.partner.ui.common

import android.view.View
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.Job
import com.bykea.pk.partner.dal.util.SEPERATOR
import com.bykea.pk.partner.ui.loadboard.list.JobListAdapter
import com.bykea.pk.partner.utils.Constants
import com.bykea.pk.partner.utils.Constants.REQUIRED_DATE_FORMAT
import com.bykea.pk.partner.utils.Constants.ServiceCode.*
import com.bykea.pk.partner.utils.Utils
import com.bykea.pk.partner.widgets.FontTextView
import org.apache.commons.lang3.StringUtils
import java.text.SimpleDateFormat
import java.util.*


/**
 * Contains [BindingAdapter]s.
 *
 * @author Yousuf Sohail
 */
object BindingAdapters {

    @BindingAdapter("app:items")
    @JvmStatic
    fun setItems(recyclerView: RecyclerView, list: List<Any>) {
        with(recyclerView.adapter as LastAdapter<Any>) {
            items = list
        }
    }

    @BindingAdapter("app:items")
    @JvmStatic
    fun setItems(listView: ListView, items: List<Job>) {
        with(listView.adapter as JobListAdapter) {
            replaceData(items)
        }
    }

    @BindingAdapter("app:loadUrl")
    @JvmStatic
    fun loadImageUrl(imageView: ImageView, url: String) {
        Utils.loadImgPicasso(imageView, R.color.grey, url)
    }

    @BindingAdapter("app:serviceCode")
    @JvmStatic
    fun setServiceCode(imageView: ImageView, serviceCode: Int) {
        when (serviceCode) {
            SEND -> imageView.setImageResource(R.drawable.bhejdo_no_caption)
            SEND_COD -> imageView.setImageResource(R.drawable.bhejdo_no_caption)
            RIDE -> imageView.setImageResource(R.drawable.ride_right)
            MART -> imageView.setImageResource(R.drawable.ic_purchase)
            MOBILE_TOP_UP -> imageView.setImageResource(R.drawable.ic_pay)
            MOBILE_WALLET -> imageView.setImageResource(R.drawable.ic_pay)
            BANK_TRANSFER -> imageView.setImageResource(R.drawable.ic_pay)
            UTILITY -> imageView.setImageResource(R.drawable.ic_pay)
            else -> imageView.setImageResource(R.drawable.bhejdo_no_caption)
        }
    }

    @BindingAdapter("app:goneUnless")
    @JvmStatic
    fun setGoneUnless(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

    @BindingAdapter("app:dateFormat")
    @JvmStatic
    fun setDateTimeFormat(fontTextView: FontTextView, date: Date?) {
        if (date != null) {
            try {
                val sdf = SimpleDateFormat(REQUIRED_DATE_FORMAT)
                fontTextView.text = sdf.format(date)
            } catch (e: Exception) {
                fontTextView.text = ""
            }
        }
    }

    @BindingAdapter("app:ticketStatus")
    @JvmStatic
    fun setTicketStatus(fontTextView: FontTextView, ticketStatus: String?) {
        when (ticketStatus) {
            Constants.ZendeskTicketStatus.Pending -> fontTextView.setText(R.string.ticket_status_pending)
            Constants.ZendeskTicketStatus.Solved -> fontTextView.setText(R.string.ticket_status_solved)
            else -> fontTextView.setText(R.string.ticket_status_open)
        }
    }

    @BindingAdapter("app:bykeaCashFormHeaderIcon")
    @JvmStatic
    fun setBykeaCashFormHeaderIcon(imageView: ImageView, serviceCode: Int) {
        when (serviceCode) {
            MOBILE_TOP_UP -> {
                imageView.setImageResource(R.drawable.ic_bykeacash_mobile_topup)
            }
            MOBILE_WALLET -> {
                imageView.setImageResource(R.drawable.ic_bykeacash_mobile_wallet)
            }
            BANK_TRANSFER -> {
                imageView.setImageResource(R.drawable.ic_bykeacash_bank_transfer)
            }
            UTILITY -> {
                imageView.setImageResource(R.drawable.ic_bykeacash_utility_bill)
            }
        }
    }

    @BindingAdapter("app:bykeaCashFormHeaderText")
    @JvmStatic
    fun setBykeaCashFormHeaderText(fontTextView: FontTextView, serviceCode: Int) {
        when (serviceCode) {
            MOBILE_TOP_UP -> {
                fontTextView.text = fontTextView.resources.getString(R.string.bykea_cash_mobile_top_up)
            }
            MOBILE_WALLET -> {
                fontTextView.text = StringBuilder(StringUtils.SPACE)
                        .append(fontTextView.resources.getString(R.string.bykea_cash_wallet_easy_paisa))
                        .append(SEPERATOR)
                        .append(fontTextView.resources.getString(R.string.bykea_cash_wallet_jazz_cash))
                        .append(StringUtils.SPACE)
                        .append(fontTextView.resources.getString(R.string.bykea_cash_wallet_deposit))
            }
            BANK_TRANSFER -> {
                fontTextView.text = fontTextView.resources.getString(R.string.bykea_cash_bank_transfer)
            }
            UTILITY -> {
                fontTextView.text = fontTextView.resources.getString(R.string.bykea_cash_utility_bill)
            }
        }
    }
}
