package com.bykea.pk.partner.dal.source.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bykea.pk.partner.dal.Booking

/**
 * The Data Access Object for the Plant class.
 *
 * @Author: Yousuf Sohail
 */
@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings")
    fun getBookings(): LiveData<List<Booking>>

    @Query("SELECT * FROM bookings WHERE id = :bookingId")
    fun getBooking(bookingId: Long): LiveData<Booking>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(bookings: List<Booking>)
}
