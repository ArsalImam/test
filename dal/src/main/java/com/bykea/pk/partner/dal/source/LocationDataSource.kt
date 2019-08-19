package com.bykea.pk.partner.dal.source

import com.bykea.pk.partner.dal.Location

interface LocationDataSource {

    fun insert(lat: Double, lng: Double)

    fun get(startTime: Long, endTime: Long, callback: LoadPathCallback)

    fun clear()

    /**
     * Callback interface used for fetch JobRequest listing
     *
     */
    interface LoadPathCallback {

        /**
         * On successfully JobRequest listing loaded
         *
         * @param jobs
         */
        fun onPathLoaded(path: List<Location>)

        /**
         * On data not available on data source
         *
         * @param errorMsg
         */
        fun onDataNotAvailable(errorMsg: String? = "Data Not Available")
    }
}