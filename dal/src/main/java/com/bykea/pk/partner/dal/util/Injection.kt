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
import com.bykea.pk.partner.dal.source.JobRequestsRepository
import com.bykea.pk.partner.dal.source.local.AppDatabase
import com.bykea.pk.partner.dal.source.local.JobRequestsLocalDataSource
import com.bykea.pk.partner.dal.source.remote.JobRequestsRemoteDataSource

/**
 * Enables injection of production implementations for
 * [BookingsDataSource] at compile time.
 */
object Injection {

    /**
     * Provides Booking repository with all of it's dependencies resolved.
     *
     * @param context App Context
     * @return [JobRequestsRepository]
     */
    fun provideBookingsRepository(context: Context): JobRequestsRepository {
        val database = AppDatabase.getInstance(context)
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return JobRequestsRepository.getInstance(
                JobRequestsRemoteDataSource(),
                JobRequestsLocalDataSource.getInstance(AppExecutors(), database.jobRequestsDao()),
                preferences)
    }
}
