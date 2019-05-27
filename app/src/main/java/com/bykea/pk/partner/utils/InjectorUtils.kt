package com.bykea.pk.partner.utils

import android.content.Context
import com.bykea.pk.partner.dal.local.AppDatabase
import com.bykea.pk.partner.ui.loadboard.list.BookingDetailViewModelFactory
import com.bykea.pk.partner.ui.loadboard.list.BookingListViewModelFactory
import com.bykea.pk.partner.ui.loadboard.list.BookingRepository

object InjectorUtils {

    private fun getBookingRepository(context: Context): BookingRepository {
        return BookingRepository.getInstance(AppDatabase.getInstance(context.applicationContext).bookingDao())
    }

    fun provideBookingListViewModelFactory(context: Context): BookingListViewModelFactory {
        return BookingListViewModelFactory(getBookingRepository(context))
    }

    fun provideBookingDetailViewModelFactory(context: Context, bookingId: Long): BookingDetailViewModelFactory {
        return BookingDetailViewModelFactory(getBookingRepository(context), bookingId)
    }
}
