package com.bykea.pk.partner.dal.source.local

import com.bykea.pk.partner.dal.Location
import com.bykea.pk.partner.dal.source.LocationDataSource
import com.bykea.pk.partner.dal.util.AppExecutors
import java.util.*

class LocationLocalDataSource(
        private val appExecutors: AppExecutors,
        private val dao: LocationDao) {

    fun insert(lat: Double, lng: Double) {
        appExecutors.diskIO.execute {
            dao.insert(Location(Date().time, lat, lng))
        }
    }

    fun get(startTime: Long, endTime: Long, callback: LocationDataSource.LoadPathCallback) {
        appExecutors.diskIO.execute {
            val path = dao.getPath(startTime, endTime)
            appExecutors.mainThread.execute {
                if (path.isNullOrEmpty()) {
                    callback.onDataNotAvailable("No path found between this time frame")
                } else {
                    callback.onPathLoaded(path)
                }
            }
        }
    }

    fun clear() {
        appExecutors.diskIO.execute {
            dao.clear()
        }
    }
}
