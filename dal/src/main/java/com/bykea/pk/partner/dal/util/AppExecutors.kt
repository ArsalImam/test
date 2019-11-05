package com.bykea.pk.partner.dal.util

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

const val THREAD_COUNT = 3

/**
 * Global executor pools for the whole application.
 *
 * Grouping tasks like this avoids the effects of task starvation (e.g. disk reads don't wait behind
 * webservice requests).
 *
 * @author Yousuf Sohail
 */
open class AppExecutors constructor(
        val diskIO: Executor = DiskIOThreadExecutor(),
        val networkIO: Executor = Executors.newFixedThreadPool(THREAD_COUNT),
        val mainThread: Executor = MainThreadExecutor()
) {

    /**
     * Executor to run runnable on main tread
     *
     */
    private class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }
}