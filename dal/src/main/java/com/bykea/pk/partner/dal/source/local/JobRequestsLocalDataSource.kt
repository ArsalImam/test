package com.bykea.pk.partner.dal.source.local

import androidx.annotation.VisibleForTesting
import com.bykea.pk.partner.dal.JobRequest
import com.bykea.pk.partner.dal.source.JobRequestsDataSource
import com.bykea.pk.partner.dal.util.AppExecutors

/**
 * Concrete implementation of Job Request data source as a db.
 *
 * @Author: Yousuf Sohail
 */
class JobRequestsLocalDataSource private constructor(
        private val appExecutors: AppExecutors,
        private val jobRequestsDao: JobRequestsDao
) : JobRequestsDataSource {

    override fun acceptJobRequest(jobRequestId: Long, callback: JobRequestsDataSource.AcceptJobRequestCallback) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getJobRequests(callback: JobRequestsDataSource.LoadJobRequestsCallback) {
        appExecutors.diskIO.execute {
            val jobRequests = jobRequestsDao.getJobRequests()
            appExecutors.mainThread.execute {
                if (jobRequests.isEmpty()) {
                    // This will be called if the table is new or just empty.
                    callback.onDataNotAvailable("No job requests found")
                } else {
                    callback.onJobRequestsLoaded(jobRequests)
                }
            }
        }
    }

    override fun getJobRequest(jobRequestId: Long, callback: JobRequestsDataSource.GetJobRequestCallback) {
        appExecutors.diskIO.execute {
            val jobRequest = jobRequestsDao.getJobRequest(jobRequestId)
            appExecutors.mainThread.execute {
                if (jobRequest != null) {
                    callback.onJobRequestLoaded(jobRequest)
                } else {
                    callback.onDataNotAvailable("No job request found")
                }
            }
        }
    }

    override fun saveJobRequest(jobRequest: JobRequest) {
        appExecutors.diskIO.execute { jobRequestsDao.insert(jobRequest) }
    }

    override fun refreshJobRequestList() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAllJobRequests() {
        appExecutors.diskIO.execute {
            jobRequestsDao.deleteAll()
        }
    }

    override fun deleteJobRequest(jobRequestId: Long) {
        appExecutors.diskIO.execute {
            jobRequestsDao.delete(jobRequestId)
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