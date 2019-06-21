package com.bykea.pk.partner.dal.source

import com.bykea.pk.partner.dal.Booking

/**
 * Main entry point for accessing job requests data.
 *
 *
 * @Author: Yousuf Sohail
 */
interface JobRequestsDataSource {

    /**
     * Get Booking Listing
     *
     * @param callback Callback to executed
     */
    fun getJobRequests(callback: LoadJobRequestsCallback)

    /**
     * Fetch Booking details
     *
     * @param bookingId Id of Booking to be fetched
     * @param callback Callback to executed
     */
    fun getJobRequest(bookingId: Long, callback: GetJobRequestCallback)

    /**
     * Save Booking to data source
     *
     * @param booking
     */
    fun saveJobRequest(booking: Booking)

    /**
     * Accept booking
     *
     * @param bookingId Id of Booking to be accepted
     */
    fun acceptJobRequest(bookingId: Long, callback: AcceptJobRequestCallback)

    /**
     * Re-fetch booking listing
     *
     */
    fun refreshJobRequestList()

    /**
     * Delete all booking from data source
     *
     */
    fun deleteAllJobRequests()

    /**
     * Delete booking from data source
     *
     * @param bookingId Id of booking to be deleted
     */
    fun deleteJobRequest(bookingId: Long)

    /**
     * Callback interface used for fetch Booking listing
     *
     */
    interface LoadJobRequestsCallback {

        /**
         * On successfully Booking listing loaded
         *
         * @param jobRequests
         */
        fun onJobRequestsLoaded(jobRequests: List<Booking>)

        /**
         * On data not available on data source
         *
         * @param errorMsg
         */
        fun onDataNotAvailable(errorMsg: String?)
    }

    /**
     * Callback interface used for fetch Booking details
     *
     */
    interface GetJobRequestCallback {

        /**
         * On successfully Booking detail loaded
         *
         * @param jobRequest
         */
        fun onBookingLoaded(jobRequest: Booking)

        /**
         * On data not available on data source
         *
         * @param message
         */
        fun onDataNotAvailable(message: String?)
    }

    /**
     * Callback interface used for accepting Booking
     *
     */
    interface AcceptJobRequestCallback {

        fun onJobRequestAccepted()

        fun onJobRequestAcceptFailed(message: String?, taken: Boolean)
    }
}