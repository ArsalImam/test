package com.bykea.pk.partner.dal.util

import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Executor that runs a task on a new background thread.
 *
 * @Author: Yousuf Sohail
 */
class DiskIOThreadExecutor : Executor {

    private val diskIO = Executors.newSingleThreadExecutor()

    override fun execute(command: Runnable) {
        diskIO.execute(command)
    }
}
