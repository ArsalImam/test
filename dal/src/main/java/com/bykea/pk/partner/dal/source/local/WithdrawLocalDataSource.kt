package com.bykea.pk.partner.dal.source.local

import com.bykea.pk.partner.dal.util.AppExecutors


/**
 * This is the local datasource class of withdrawal repository
 *
 * @author Arsal Imam
 */
class WithdrawLocalDataSource
/**
 * Constructs a new object of this source
 *
 * @param appExecutors app executors
 * @param withDrawDao dao of the source
 */
(private val appExecutors: AppExecutors, private val withDrawDao: WithDrawDao) {
    companion object {

        /**
         * singleton object of this datasource
         */
        private var INSTANCE: WithdrawLocalDataSource? = null

        /**
         * this will return a singleton object of this datasource
         *
         * @param appExecutors app executors
         * @param withDrawDao dao of the source
         *
         * @return singleton object
         */
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