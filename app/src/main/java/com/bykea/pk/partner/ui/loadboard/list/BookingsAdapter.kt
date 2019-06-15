package com.bykea.pk.partner.ui.loadboard.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import com.bykea.pk.partner.dal.Booking
import com.bykea.pk.partner.databinding.LoadboardListItemBinding

/**
 * List Adapter for [Booking] listing to be shown on Loadboard
 *
 * @property bookings List of booking
 * @property bookingsViewModel ViewModel for [LoadBoardListFragment]
 */
class BookingsAdapter(
        private var bookings: List<Booking>,
        private val bookingsViewModel: BookingListViewModel
) : BaseAdapter() {

    fun replaceData(bookings: List<Booking>) {
        setList(bookings)
    }

    override fun getCount() = bookings.size

    override fun getItem(position: Int) = bookings[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View {
        val binding: LoadboardListItemBinding
        binding = if (view == null) {
            // Inflate
            val inflater = LayoutInflater.from(viewGroup.context)

            // Create the binding
            LoadboardListItemBinding.inflate(inflater, viewGroup, false)
        } else {
            // Recycling view
            DataBindingUtil.getBinding(view) ?: throw IllegalStateException()
        }

        val userActionsListener = object : BookingItemUserActionsListener {

            override fun onBookingClicked(booking: Booking) {
                bookingsViewModel.openBooking(booking.id)
            }
        }

        with(binding) {
            booking = bookings[position]
            listener = userActionsListener
            executePendingBindings()
        }

        return binding.root
    }


    private fun setList(bookings: List<Booking>) {
        this.bookings = bookings
        notifyDataSetChanged()
    }
}
