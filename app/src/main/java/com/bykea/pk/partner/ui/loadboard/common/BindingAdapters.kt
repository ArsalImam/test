package com.bykea.pk.partner.ui.loadboard.common

import android.view.View
import android.widget.ImageView
import android.widget.ListView
import androidx.databinding.BindingAdapter
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

    @BindingAdapter("app:srcName")
    fun setImageViewResource(imageView: ImageView, resource: String) {
        val id = imageView.context.resources.getIdentifier(resource, "drawable", imageView.context.packageName)
        val drawable = imageView.context.resources.getDrawable(id)
        imageView.setImageDrawable(drawable)
    }

    @BindingAdapter("app:goneUnless")
    fun goneUnless(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }
}
