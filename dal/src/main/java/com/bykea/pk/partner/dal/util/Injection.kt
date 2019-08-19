/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bykea.pk.partner.dal.util

import android.content.Context
import android.preference.PreferenceManager
import com.bykea.pk.partner.dal.source.JobsRepository
import com.bykea.pk.partner.dal.source.LocationRepository
import com.bykea.pk.partner.dal.source.WithdrawRepository
import com.bykea.pk.partner.dal.source.local.AppDatabase
import com.bykea.pk.partner.dal.source.local.JobsLocalDataSource
import com.bykea.pk.partner.dal.source.local.LocationLocalDataSource
import com.bykea.pk.partner.dal.source.local.WithdrawLocalDataSource
import com.bykea.pk.partner.dal.source.remote.JobsRemoteDataSource
import com.bykea.pk.partner.dal.source.remote.WithdrawRemoteDataSource

/**
 * Enables injection of production implementations at compile time.
 *
 * @author Yousuf Sohail
 */
object Injection {

    /**
     * Provides Booking repository with all of it's dependencies resolved.
     *
     * @param context App Context
     * @return [JobsRepository]
     */
    fun provideJobsRepository(context: Context): JobsRepository {
        val database = AppDatabase.getInstance(context)
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return JobsRepository.getInstance(
                JobsRemoteDataSource(),
                JobsLocalDataSource.getInstance(AppExecutors(), database.jobRequestsDao()),
                preferences)
    }

    /**
     * Provides withdraw repository with all of it's dependencies resolved.
     * @param applicationContext context of the application
     * @return [WithdrawRepository]
     */
    fun provideWithdrawRepository(applicationContext: Context): WithdrawRepository {
        val database = AppDatabase.getInstance(applicationContext)
        val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        return WithdrawRepository.getInstance(
                WithdrawRemoteDataSource(),
                WithdrawLocalDataSource.getInstance(AppExecutors(), database.withdrawDao()),
                preferences)!!
    }

    /**
     * Provides Location repository with all of it's dependencies resolved
     *
     * @param context Context
     * @return LocationRepository
     */
    fun provideLocationRepository(context: Context): LocationRepository {
        val locationDao = AppDatabase.getInstance(context).locationDao()
        return LocationRepository.getInstance(
                LocationLocalDataSource(AppExecutors(), locationDao)
        )
    }
}
