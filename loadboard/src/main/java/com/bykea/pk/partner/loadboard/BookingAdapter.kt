package com.bykea.pk.partner.loadboard

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bykea.pk.partner.dal.Booking
import kotlinx.android.synthetic.main.fragment_booking_list_dialog_item.view.*

class BookingAdapter(val items: ArrayList<Booking>) : RecyclerView.Adapter<BookingAdapter.ViewHolder>() {
    private var mListener: BookingAdapterListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context), parent)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: Booking = items.get(position)

        /*if (item.getDropoffZone() != null) {
            holder.li_LoadboardDropOffTV.setText(context!!.resources.getString(R.string.pick_drop_name_ur, item.getDropoffZone().getUrduName()))
        } else {
            holder.li_LoadboardDropOffTV.setText(context!!.resources.getString(R.string.not_selected_ur))
        }
        if (item.getFare() != null) {
            holder.tvFare.setText(context!!.resources.getString(R.string.seleted_amount_rs, item.getFare()))
        } else {
            holder.tvFare.setText(context!!.resources.getString(R.string.dash))
        }*/
    }


    inner class ViewHolder internal constructor(inflater: LayoutInflater, parent: ViewGroup)
        : RecyclerView.ViewHolder(inflater.inflate(R.layout.fragment_booking_list_dialog_item, parent, false)) {

        internal val li_LoadboardDropOffTV: TextView = itemView.li_LoadboardDropOffTV
        internal val tvFare: TextView = itemView.tv_fare

        init {
            itemView.setOnClickListener {
                mListener?.onBookingItemClicked(adapterPosition)
            }
        }
    }

    interface BookingAdapterListener {
        fun onBookingItemClicked(position: Int)
    }
}