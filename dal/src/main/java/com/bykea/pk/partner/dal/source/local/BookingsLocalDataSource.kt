package com.bykea.pk.partner.dal.source.local

import androidx.annotation.VisibleForTesting
import com.bykea.pk.partner.dal.Booking
import com.bykea.pk.partner.dal.source.BookingsDataSource
import com.bykea.pk.partner.dal.util.AppExecutors

/**
 * Concrete implementation of a data source as a db.
 *
 * @Author: Yousuf Sohail
 */
class BookingsLocalDataSource private constructor(val appExecutors: AppExecutors, val bookingsDao: BookingsDao) : BookingsDataSource {

    override fun getBookings(callback: BookingsDataSource.LoadBookingsCallback) {
        appExecutors.diskIO.execute {
            val bookings = bookingsDao.getBookings()
            appExecutors.mainThread.execute {
                if (bookings.isEmpty()) {
                    // This will be called if the table is new or just empty.
                    callback.onDataNotAvailable("No bookings found")
                } else {
                    callback.onBookingsLoaded(bookings)
                }
            }
        }
    }

    override fun getBooking(bookingId: Long, callback: BookingsDataSource.GetBookingCallback) {
        appExecutors.diskIO.execute {
            val booking = bookingsDao.getBooking(bookingId)
            appExecutors.mainThread.execute {
                if (booking != null) {
                    callback.onBookingLoaded(booking)
                } else {
                    callback.onDataNotAvailable("No booking found")
                }
            }
        }
    }

    override fun saveBooking(booking: Booking) {
        appExecutors.diskIO.execute { bookingsDao.insert(booking) }
    }

    override fun acceptBooking(booking: Booking) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun acceptBooking(bookingId: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun refreshBookings() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAllBookings() {
        appExecutors.diskIO.execute {
            bookingsDao.deleteAll()
        }
    }

    override fun deleteBooking(bookingId: Long) {
        appExecutors.diskIO.execute {
            bookingsDao.delete(bookingId)
        }
    }

    companion object {
        private var INSTANCE: BookingsLocalDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors, bookingsDao: BookingsDao): BookingsLocalDataSource {
            if (INSTANCE == null) {
                synchronized(BookingsLocalDataSource::javaClass) {
                    INSTANCE = BookingsLocalDataSource(appExecutors, bookingsDao)
                }
            }
            return INSTANCE!!
        }

        @VisibleForTesting
        fun clearInstance() {
            INSTANCE = null
        }
    }
}