package com.bykea.pk.partner.dal.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bykea.pk.partner.dal.Location

/**
 * The Data Access Object for the [Location] class.
 *
 * @Author: Yousuf Sohail
 */
@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(location: Location)

    @Query("SELECT * FROM LocationHistory WHERE time > :startTime AND time < :endTime")
    fun getPath(startTime: Long, endTime: Long): List<Location>

    @Query("DELETE FROM LocationHistory")
    fun clear()
}
