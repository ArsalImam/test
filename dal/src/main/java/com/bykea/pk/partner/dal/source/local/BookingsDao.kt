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

    /**
     * Insert a booking in the database. If the booking already exists, replace it.
     *
     * @param booking the booking to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(booking: Booking)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(bookings: List<Booking>)

    /**
     * Delete all bookings.
     */
    @Query("DELETE FROM Bookings")
    fun deleteAll()

    /**
     * Delete a booking by id.
     */
    @Query("DELETE FROM Bookings WHERE id = :bookingId")
    fun delete(bookingId: Long)
}
