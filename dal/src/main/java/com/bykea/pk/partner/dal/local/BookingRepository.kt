package com.bykea.pk.partner.dal.local

/**
 * Repository module for handling data operations.
 *
 * @Author: Yousuf Sohail
 */
class BookingRepository private constructor(private val bookingDao: BookingDao) {

    fun getBookings() = bookingDao.getBookings()

    fun getBooking(bookingId: Long) = bookingDao.getBooking(bookingId)

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: BookingRepository? = null

        fun getInstance(bookingDao: BookingDao) =
                instance ?: synchronized(this) {
                    instance
                            ?: BookingRepository(bookingDao).also { instance = it }
                }
    }
}
