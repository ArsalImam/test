package com.bykea.pk.partner.dal.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bykea.pk.partner.dal.Job

/**
 * The Data Access Object for the [Job] class.
 *
 * @Author: Yousuf Sohail
 */
@Dao
interface JobsDao {

    @Query("SELECT * FROM Jobs")
    fun getJobs(): List<Job>

    @Query("SELECT * FROM Jobs WHERE id = :jobId")
    fun getJob(jobId: Long): Job?

    /**
     * Insert a jobRequest in the database. If the jobRequest already exists, replace it.
     *
     * @param job the jobRequest to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(job: Job)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(jobs: List<Job>)

    /**
     * Delete all jobRequests.
     */
    @Query("DELETE FROM Jobs")
    fun deleteAll()

    /**
     * Delete a jobRequest by id.
     */
    @Query("DELETE FROM Jobs WHERE id = :jobRequestId")
    fun delete(jobRequestId: Long)
}
