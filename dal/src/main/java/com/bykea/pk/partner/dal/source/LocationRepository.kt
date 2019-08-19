package com.bykea.pk.partner.dal.source

import com.bykea.pk.partner.dal.source.local.LocationLocalDataSource

class LocationRepository(private val db: LocationLocalDataSource) : LocationDataSource {

    override fun insert(lat: Double, lng: Double) {
        db.insert(lat, lng)
    }

    override fun get(startTime: Long, endTime: Long, callback: LocationDataSource.LoadPathCallback) {
        db.get(startTime, endTime, callback)
    }

    override fun clear() {
        db.clear()
    }

    companion object {

        private var INSTANCE: LocationRepository? = null

        /**
         * Returns the single instance of this class, creating it if necessary.
         * *
         * @return the [LocationRepository] instance
         */
        @JvmStatic
        fun getInstance(db: LocationLocalDataSource) =
                INSTANCE ?: synchronized(LocationRepository::class.java) {
                    INSTANCE
                            ?: LocationRepository(db).also { INSTANCE = it }
                }


        /**
         * Used to force [getInstance] to create a new instance
         * next time it's called.
         */
        @JvmStatic
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
