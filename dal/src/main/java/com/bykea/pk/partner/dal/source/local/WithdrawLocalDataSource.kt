package com.bykea.pk.partner.dal.source.local

import com.bykea.pk.partner.dal.util.AppExecutors

class WithdrawLocalDataSource(private val appExecutors: AppExecutors, private val withDrawDao: WithDrawDao) {
    companion object {

        private var INSTANCE: WithdrawLocalDataSource? = null

        fun getInstance(appExecutors: AppExecutors, withDrawDao: WithDrawDao): WithdrawLocalDataSource {
            if (INSTANCE == null) {
                synchronized(WithdrawLocalDataSource::class.java) {
                    INSTANCE = WithdrawLocalDataSource(appExecutors, withDrawDao)
                }
            }
            return INSTANCE!!
        }
    }
}