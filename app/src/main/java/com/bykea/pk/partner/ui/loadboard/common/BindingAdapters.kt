package com.bykea.pk.partner.ui.loadboard.common

import android.view.View
import android.widget.ImageView
import android.widget.ListView
import androidx.databinding.BindingAdapter
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.Booking
import com.bykea.pk.partner.ui.loadboard.list.BookingsAdapter

/**
 * Contains [BindingAdapter]s.
 *
 * @author Yousuf Sohail
 */
object BindingAdapters {

    @BindingAdapter("app:items")
    @JvmStatic
    fun setItems(listView: ListView, items: List<Booking>) {
        with(listView.adapter as BookingsAdapter) {
            replaceData(items)
        }
    }

    @BindingAdapter("app:serviceCode")
    @JvmStatic
    fun setServiceCode(imageView: ImageView, serviceCode: Int) {
        when (serviceCode) {
            21 -> imageView.setImageResource(R.drawable.bhejdo_no_caption)
            22 -> imageView.setImageResource(R.drawable.bhejdo_no_caption)
            else -> imageView.setImageResource(R.drawable.bhejdo_no_caption)
        }
    }

    @BindingAdapter("app:goneUnless")
    @JvmStatic
    fun setGoneUnless(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }
}
