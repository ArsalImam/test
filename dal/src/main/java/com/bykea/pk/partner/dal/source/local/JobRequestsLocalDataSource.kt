package com.bykea.pk.partner.dal.source.local

import androidx.annotation.VisibleForTesting
import com.bykea.pk.partner.dal.Booking
import com.bykea.pk.partner.dal.source.JobRequestsDataSource
import com.bykea.pk.partner.dal.util.AppExecutors

/**
 * Concrete implementation of a data source as a db.
 *
 * @Author: Yousuf Sohail
 */
class JobRequestsLocalDataSource private constructor(val appExecutors: AppExecutors, val jobRequestsDao: JobRequestsDao) : JobRequestsDataSource {
    override fun acceptJobRequest(bookingId: Long, callback: JobRequestsDataSource.AcceptJobRequestCallback) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getJobRequests(callback: JobRequestsDataSource.LoadJobRequestsCallback) {
        appExecutors.diskIO.execute {
            val bookings = jobRequestsDao.getBookings()
            appExecutors.mainThread.execute {
                if (bookings.isEmpty()) {
                    // This will be called if the table is new or just empty.
                    callback.onDataNotAvailable("No bookings found")
                } else {
                    callback.onJobRequestsLoaded(bookings)
                }
            }
        }
    }

    override fun getJobRequest(bookingId: Long, callback: JobRequestsDataSource.GetJobRequestCallback) {
        appExecutors.diskIO.execute {
            val booking = jobRequestsDao.getBooking(bookingId)
            appExecutors.mainThread.execute {
                if (booking != null) {
                    callback.onBookingLoaded(booking)
                } else {
                    callback.onDataNotAvailable("No booking found")
                }
            }
        }
    }

    override fun saveJobRequest(booking: Booking) {
        appExecutors.diskIO.execute { jobRequestsDao.insert(booking) }
    }

    override fun refreshJobRequestList() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAllJobRequests() {
        appExecutors.diskIO.execute {
            jobRequestsDao.deleteAll()
        }
    }

    override fun deleteJobRequest(bookingId: Long) {
        appExecutors.diskIO.execute {
            jobRequestsDao.delete(bookingId)
        }
    }

    companion object {
        private var INSTANCE: JobRequestsLocalDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors, jobRequestsDao: JobRequestsDao): JobRequestsLocalDataSource {
            if (INSTANCE == null) {
                synchronized(JobRequestsLocalDataSource::javaClass) {
                    INSTANCE = JobRequestsLocalDataSource(appExecutors, jobRequestsDao)
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