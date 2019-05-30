//package com.bykea.pk.partner.dal.source
//
//import androidx.lifecycle.LiveData
//import com.bykea.pk.partner.dal.Booking
//import com.bykea.pk.partner.dal.source.local.BookingsDao
//
///**
// * Repository module for handling data operations.
// *
// * @Author: Yousuf Sohail
// */
//class BookingRepository private constructor(private val bookingsDao: BookingsDao) {
//
//    fun getBookings(): LiveData<List<Booking>> {
//        return bookingsDao.getBookings()
//    }
//
//    fun getBooking(bookingId: Long): LiveData<Booking> {
//        return bookingsDao.getBooking(bookingId)
//    }
//
//    companion object {
//
//        // For Singleton instantiation
//        @Volatile
//        private var instance: BookingRepository? = null
//
//        fun getInstance(bookingsDao: BookingsDao) =
//                instance ?: synchronized(this) {
//                    instance
//                            ?: BookingRepository(bookingsDao).also { instance = it }
//                }
//    }
//}
