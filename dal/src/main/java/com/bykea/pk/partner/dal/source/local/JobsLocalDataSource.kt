package com.bykea.pk.partner.dal.source.local

import androidx.annotation.VisibleForTesting
import com.bykea.pk.partner.dal.Job
import com.bykea.pk.partner.dal.LocCoordinatesInTrip
import com.bykea.pk.partner.dal.source.JobsDataSource
import com.bykea.pk.partner.dal.util.AppExecutors

/**
 * Concrete implementation of Job Request data source as a db.
 *
 * @Author: Yousuf Sohail
 */
class JobsLocalDataSource private constructor(
        private val appExecutors: AppExecutors,
        private val jobsDao: JobsDao
) : JobsDataSource {

    override fun getJobs(callback: JobsDataSource.LoadJobsCallback) {
        appExecutors.diskIO.execute {
            val jobRequests = jobsDao.getJobs()
            appExecutors.mainThread.execute {
                if (jobRequests.isEmpty()) {
                    // This will be called if the table is new or just empty.
                    callback.onDataNotAvailable("No job requests found")
                } else {
                    callback.onJobsLoaded(jobRequests)
                }
            }
        }
    }

    override fun getJob(jobId: Long, callback: JobsDataSource.GetJobRequestCallback) {
        appExecutors.diskIO.execute {
            val jobRequest = jobsDao.getJob(jobId)
            appExecutors.mainThread.execute {
                if (jobRequest != null) {
                    callback.onJobLoaded(jobRequest)
                } else {
                    callback.onDataNotAvailable("No job request found")
                }
            }
        }
    }

    override fun saveJob(job: Job) {
        appExecutors.diskIO.execute { jobsDao.insert(job) }
    }

    override fun refreshJobRequestList() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAllJobRequests() {
        appExecutors.diskIO.execute {
            jobsDao.deleteAll()
        }
    }

    override fun deleteJobRequest(jobRequestId: Long) {
        appExecutors.diskIO.execute {
            jobsDao.delete(jobRequestId)
        }
    }

    override fun acceptJobRequest(jobRequestId: Long, callback: JobsDataSource.AcceptJobRequestCallback) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun finishJob(jobId: String, route: ArrayList<LocCoordinatesInTrip>, callback: JobsDataSource.FinishJobCallback) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun concludeJob(jobId: String, rate: Int, receivedAmount: Int, callback: JobsDataSource.ConcludeJobCallback, deliveryMessage: String?, deliveryStatus: Boolean?, purchaseAmount: Int?, receiverName: String?, receiverPhone: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        private var INSTANCE: JobsLocalDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors, jobsDao: JobsDao): JobsLocalDataSource {
            if (INSTANCE == null) {
                synchronized(JobsLocalDataSource::javaClass) {
                    INSTANCE = JobsLocalDataSource(appExecutors, jobsDao)
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