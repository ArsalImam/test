package com.bykea.pk.partner.dal.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bykea.pk.partner.dal.JobRequest

/**
 * The Data Access Object for the [JobRequest] class.
 *
 * @Author: Yousuf Sohail
 */
@Dao
interface JobRequestsDao {

    @Query("SELECT * FROM JobRequests")
    fun getJobRequests(): List<JobRequest>

    @Query("SELECT * FROM JobRequests WHERE id = :jobRequestId")
    fun getJobRequest(jobRequestId: Long): JobRequest?

    /**
     * Insert a jobRequest in the database. If the jobRequest already exists, replace it.
     *
     * @param jobRequest the jobRequest to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(jobRequest: JobRequest)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(jobRequests: List<JobRequest>)

    /**
     * Delete all jobRequests.
     */
    @Query("DELETE FROM JobRequests")
    fun deleteAll()

    /**
     * Delete a jobRequest by id.
     */
    @Query("DELETE FROM JobRequests WHERE id = :jobRequestId")
    fun delete(jobRequestId: Long)
}
