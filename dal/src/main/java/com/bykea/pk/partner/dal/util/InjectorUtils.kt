package com.bykea.pk.partner.dal.util

import android.content.Context
import com.bykea.pk.partner.dal.AppDatabase
import com.bykea.pk.partner.dal.BookingDetailViewModelFactory
import com.bykea.pk.partner.dal.BookingListViewModelFactory
import com.bykea.pk.partner.dal.BookingRepository

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
