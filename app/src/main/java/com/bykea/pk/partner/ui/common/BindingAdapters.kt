package com.bykea.pk.partner.ui.common

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bykea.pk.partner.DriverApp
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.Job
import com.bykea.pk.partner.dal.Rules
import com.bykea.pk.partner.dal.source.remote.request.nodataentry.DeliveryDetails
import com.bykea.pk.partner.dal.util.SEPERATOR
import com.bykea.pk.partner.ui.helpers.FontUtils
import com.bykea.pk.partner.ui.loadboard.list.JobListAdapter
import com.bykea.pk.partner.utils.Constants
import com.bykea.pk.partner.utils.Constants.*
import com.bykea.pk.partner.utils.Constants.ServiceCode.*
import com.bykea.pk.partner.utils.TripStatus.*
import com.bykea.pk.partner.utils.Util
import com.bykea.pk.partner.utils.Utils
import com.bykea.pk.partner.widgets.AutoFitFontTextView
import com.bykea.pk.partner.widgets.FontTextView
import org.apache.commons.lang3.StringUtils
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * Contains [BindingAdapter]s.
 *
 * @author Yousuf Sohail
 */
object BindingAdapters {

    @BindingAdapter("app:items")
    @JvmStatic
    fun setItems(recyclerView: RecyclerView, list: List<Any>?) {
        list?.let {
            with(recyclerView.adapter as LastAdapter<Any>) {
                items = ArrayList(it)
            }
        }
    }

    @BindingAdapter("app:hexTextColor")
    @JvmStatic
    fun setHexTextColor(textView: TextView, color: String?) {
        textView.apply {
            setTextColor(if (StringUtils.isNotEmpty(color)) {
                Color.parseColor(color)
            } else {
                Color.BLACK
            })
        }
    }

    @BindingAdapter("app:fontType")
    @JvmStatic
    fun setFontType(textView: TextView, type: String?) {
        type?.let {
            if (type.equals("ur", false))
                textView.typeface =
                        FontUtils.getFonts(textView.context, "jameel_noori_nastaleeq.ttf")
        }
    }

    @BindingAdapter("app:activeRadio")
    @JvmStatic
    fun activeRadio(imageView: ImageView, isActive: Boolean) {
        imageView.setImageResource(if (isActive) {
            R.drawable.selected_radio_button
        } else {
            R.drawable.unselected_radio_button
        })
    }

    @BindingAdapter("app:items")
    @JvmStatic
    fun setItems(listView: ListView, items: List<Job>) {
        with(listView.adapter as JobListAdapter) {
            replaceData(items)
        }
    }

    @BindingAdapter("app:showLineOverText")
    @JvmStatic
    fun showStrikeLine(textView: TextView, enable: Boolean) {
        textView.paintFlags = if (enable) {
            textView.paintFlags or STRIKE_THRU_TEXT_FLAG
        } else {
            textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    /**
     * this binding adapter can be used to load image from url directly from xml
     */
    @BindingAdapter("app:loadUrl")
    @JvmStatic
    fun loadImageUrl(imageView: ImageView, url: String? = null) {
        if (url == null) return
        Utils.loadImgPicasso(imageView, R.color.white, url)
    }

    @BindingAdapter("app:urduWrappingText")
    @JvmStatic
    fun urduWrappingText(textView: TextView, url: String? = null) {
        if (url == null) return
        textView.text = String.format(textView.resources.getString(R.string.empty_string_sandwich_placeholder), url)
    }

    @BindingAdapter("app:serviceCode")
    @JvmStatic
    fun setServiceCode(imageView: ImageView, serviceCode: Int) {
        when (serviceCode) {
            SEND -> imageView.setImageResource(R.drawable.bhejdo_no_caption)
            SEND_COD -> imageView.setImageResource(R.drawable.bhejdo_no_caption)
            RIDE, DISPATCH_RIDE -> imageView.setImageResource(R.drawable.ride_right)
            MART -> imageView.setImageResource(R.drawable.ic_purchase)
            COURIER, MULTI_DELIVERY, NEW_BATCH_DELIVERY -> imageView.setImageResource(R.drawable.courier_no_caption)
            MOBILE_TOP_UP -> imageView.setImageResource(R.drawable.ic_pay)
            MOBILE_WALLET -> imageView.setImageResource(R.drawable.ic_pay)
            FOOD -> imageView.setImageResource(R.drawable.ic_food)
            BANK_TRANSFER -> imageView.setImageResource(R.drawable.ic_pay)
            UTILITY -> imageView.setImageResource(R.drawable.ic_pay)
            else -> imageView.setImageResource(R.drawable.ride_right)
        }
    }

    @BindingAdapter("app:goneUnless")
    @JvmStatic
    fun setGoneUnless(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

    @BindingAdapter("app:invisibleUnless")
    @JvmStatic
    fun setInvisibleUnless(view: View, value: Any? = null) {
        value?.let {
            when (value) {
                is Boolean -> {
                    view.visibility = if (value) View.VISIBLE else View.INVISIBLE
                }
                is String -> {
                    view.visibility = if (value.isNotEmpty()) View.VISIBLE else View.INVISIBLE
                }
            }
        } ?: run {
            view.visibility = View.INVISIBLE
        }
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

    @BindingAdapter("app:priorityColor")
    @JvmStatic
    fun setPriorityColor(autoFontTextView: AutoFitFontTextView, rules: Rules?) {
        if (rules?.priority != null) {
            // PRIORITIES COLOR - RGBY - Red,Green,Blue and Yellow
            autoFontTextView.setTextColor(ContextCompat.getColor(autoFontTextView.context, R.color.white))
            when (rules.priority) {
                PRIORITY_ONE -> {
                    // PRIORITY ONE - RED COLOR
                    autoFontTextView.setBackgroundResource(R.drawable.red_left_top_bottom_bg)
                }
                PRIORITY_TWO -> {
                    // PRIORITY TWO - GREEN COLOR
                    autoFontTextView.setBackgroundResource(R.drawable.green_left_top_bottom_bg)
                }
                PRIORITY_THREE -> {
                    // PRIORITY THREE - BLUE COLOR
                    autoFontTextView.setBackgroundResource(R.drawable.blue_left_top_bottom_bg)
                }
                PRIORITY_FOUR -> {
                    // PRIORITY FOUR - YELLOW COLOR
                    autoFontTextView.setBackgroundResource(R.drawable.yellow_left_top_bottom_bg)
                }
            }
        } else {
            autoFontTextView.setTextColor(ContextCompat.getColor(autoFontTextView.context, R.color.textColorPerformance))
            autoFontTextView.setBackgroundResource(R.drawable.gray_left_top_bottom_bordered_bg)
        }
    }

    @BindingAdapter("app:amountFormatted")
    @JvmStatic
    fun setAmountFormatted(fontTextView: FontTextView, amount: Any? = null) {
        amount?.let {
            when (it) {
                is Int -> {
                    fontTextView.text = String.format(DriverApp.getContext().getString(R.string.amount_rs), it.toString())
                }
                is String -> {
                    fontTextView.text = String.format(DriverApp.getContext().getString(R.string.amount_rs), it)
                }
            }
        }
    }

    @BindingAdapter("app:batchTripItemAccordingToStatus")
    @JvmStatic
    fun setBatchTripItemAccordingToStatus(view: View, deliveryDetails: DeliveryDetails?) {
        val imageViewShowDetails: AppCompatImageView = view.findViewById(R.id.imageViewShowDetails)
        val imageViewEdit: AppCompatImageView = view.findViewById(R.id.imageViewEdit)
        val textViewStatus: FontTextView = view.findViewById(R.id.textViewStatus)

        Util.safeLet(deliveryDetails,
                deliveryDetails?.details,
                deliveryDetails?.details?.status) { _, _, status ->
            when (status.toLowerCase()) {
                ON_START_TRIP.toLowerCase() -> {
                    imageViewShowDetails.visibility = View.VISIBLE
                    imageViewEdit.visibility = View.INVISIBLE
                    textViewStatus.visibility = View.GONE
                }
                ON_FEEDBACK_TRIP.toLowerCase(), ON_COMPLETED_TRIP.toLowerCase(), ON_FINISH_TRIP.toLowerCase() -> {
                    imageViewShowDetails.visibility = View.INVISIBLE
                    imageViewEdit.visibility = View.INVISIBLE
                    deliveryDetails?.details?.delivery_status?.let {
                        textViewStatus.visibility = View.VISIBLE
                        if (it) {
                            textViewStatus.text = DriverApp.getContext().getText(R.string.successful)
                            textViewStatus.setTextColor(ContextCompat.getColor(DriverApp.getContext(), R.color.color_blue_fleet))
                        } else {
                            textViewStatus.text = DriverApp.getContext().getText(R.string.unsuccessful)
                            textViewStatus.setTextColor(ContextCompat.getColor(DriverApp.getContext(), R.color.booking_red))
                        }
                    } ?: run {
                        textViewStatus.visibility = View.GONE
                    }
                }
                else -> {
                    imageViewShowDetails.visibility = View.VISIBLE
                    imageViewEdit.visibility = View.VISIBLE
                    textViewStatus.visibility = View.GONE
                }
            }
        }
    }

}
