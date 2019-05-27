//package com.bykea.pk.partner.utils
//
//import android.content.Context
//import com.bykea.pk.partner.dal.source.local.AppDatabase
//import com.bykea.pk.partner.loadboard.BookingDetailViewModelFactory
//import com.bykea.pk.partner.loadboard.BookingListViewModelFactory
//import com.bykea.pk.partner.loadboard.BookingRepository
//
//object InjectorUtils {
//
//    private fun getBookingRepository(context: Context): com.bykea.pk.partner.loadboard.BookingRepository {
//        return com.bykea.pk.partner.loadboard.BookingRepository.getInstance(AppDatabase.getInstance(context.applicationContext).bookingDao())
//    }
//
//    fun provideBookingListViewModelFactory(context: Context): com.bykea.pk.partner.loadboard.BookingListViewModelFactory {
//        return com.bykea.pk.partner.loadboard.BookingListViewModelFactory(getBookingRepository(context))
//    }
//
//    fun provideBookingDetailViewModelFactory(context: Context, bookingId: Long): com.bykea.pk.partner.loadboard.BookingDetailViewModelFactory {
//        return com.bykea.pk.partner.loadboard.BookingDetailViewModelFactory(getBookingRepository(context), bookingId)
//    }
//}
