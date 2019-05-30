package com.bykea.pk.partner.dal.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bykea.pk.partner.dal.Booking

/**
 * The Data Access Object for the [Booking] class.
 *
 * @Author: Yousuf Sohail
 */
@Dao
interface BookingsDao {

    @Query("SELECT * FROM Bookings")
    fun getBookings(): List<Booking>

    @Query("SELECT * FROM Bookings WHERE id = :bookingId")
    fun getBooking(bookingId: Long): Booking?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(bookings: List<Booking>)
}
