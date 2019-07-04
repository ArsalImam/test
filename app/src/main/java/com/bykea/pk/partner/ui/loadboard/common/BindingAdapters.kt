package com.bykea.pk.partner.ui.loadboard.common

import android.view.View
import android.widget.ImageView
import android.widget.ListView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.JobRequest
import com.bykea.pk.partner.ui.loadboard.list.JobRequestListAdapter
import zendesk.support.Request

/**
 * Contains [BindingAdapter]s.
 *
 * @author Yousuf Sohail
 */
object BindingAdapters {

    @BindingAdapter("app:items")
    @JvmStatic
    fun setItems(recyclerView: RecyclerView, list: List<Request>) {
        with(recyclerView.adapter as LastAdapter<Request>) {
            items = list
        }
    }

    @BindingAdapter("app:items")
    @JvmStatic
    fun setItems(listView: ListView, items: List<JobRequest>) {
        with(listView.adapter as JobRequestListAdapter) {
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
