package com.bykea.pk.partner.dal.source.withdraw

import android.content.SharedPreferences

import com.bykea.pk.partner.dal.source.local.WithdrawLocalDataSource
import com.bykea.pk.partner.dal.source.pref.AppPref
import com.bykea.pk.partner.dal.source.remote.WithdrawRemoteDataSource
import com.bykea.pk.partner.dal.source.remote.data.PersonalInfoData
import com.bykea.pk.partner.dal.source.remote.data.WithdrawPaymentMethod

/**
 * This is the repository class of {Withdrawal Process}
 *
 * @author Arsal Imam
 */
class WithdrawRepository
/**
 * Constructs a new object of this repo
 * @param remoteDataSource Network datasource object for withdrawal
 * @param localDataSource Local datasource object for withdrawal
 * @param preferences Shared preference object
 */
(
        /**
         * Network datasource object for withdrawal
         */
        private val remoteDataSource: WithdrawRemoteDataSource,
        /**
         * Local datasource object for withdrawal
         */
        private val localDataSource: WithdrawLocalDataSource,
        /**
         * Shared preference object
         */
        private val preferences: SharedPreferences) : WithdrawDataSource {

    /**
     * Get all payment methods by executing server API or from local datasource
     * @param callback to get results in case of failure or success
     */
    override fun getAllPaymentMethods(callback: WithdrawRepository.LoadWithdrawalCallback<*>) {
        getPaymentMethodsFromRemoteSource(callback)
    }

    /**
     * Get driver's profile by executing server API
     * @param callback to get results in case of failure or success
     */
    override fun getDriverProfile(callback: WithdrawRepository.LoadWithdrawalCallback<*>) {
        this.remoteDataSource.getDriverProfile(
                AppPref.getDriverId(preferences),
                AppPref.getAccessToken(preferences),
                object : LoadWithdrawalCallback<PersonalInfoData> {
                    override fun onDataLoaded(data: PersonalInfoData) {
                        callback.onDataLoaded(data)
                    }

                    override fun onDataNotAvailable(errorMsg: String) {
                        callback.onDataNotAvailable(errorMsg)
                    }
                })
    }

    /**
     * Get all payment methods by executing server API
     * @param callback to get results in case of failure or success
     */
    private fun getPaymentMethodsFromRemoteSource(callback: LoadWithdrawalCallback<*>) {
        this.remoteDataSource.getAllPaymentMethods(
                AppPref.getDriverId(preferences),
                AppPref.getAccessToken(preferences),
                object : LoadWithdrawalCallback<List<WithdrawPaymentMethod>> {
                    override fun onDataLoaded(data: List<WithdrawPaymentMethod>) {
                        callback.onDataLoaded(data)
                    }

                    override fun onDataNotAvailable(errorMsg: String) {
                        callback.onDataNotAvailable(errorMsg)
                    }
                })
    }

    /**
     * Perform withdrawal operation by executing API
     *
     * @param amount to withdraw
     * @param paymentMethod from which payment needs to delivered
     * @param callback to get results in case of failure or success
     */
    fun performWithdraw(amount: Int, paymentMethod: Int, callback: LoadWithdrawalCallback<Boolean>) {
        this.remoteDataSource.performWithdraw(
                amount,
                AppPref.getDriverId(preferences),
                AppPref.getAccessToken(preferences),
                paymentMethod,
                object : LoadWithdrawalCallback<Boolean> {
                    override fun onDataLoaded(data: Boolean?) {
                        callback.onDataLoaded(data)
                    }

                    override fun onDataNotAvailable(errorMsg: String) {
                        callback.onDataNotAvailable(errorMsg)
                    }
                })
    }

    /**
     * Callback on success
     * @param <T> Result
    </T> */
    interface LoadWithdrawalCallback<T> {
        fun onDataLoaded(data: T?)

        fun onDataNotAvailable(errorMsg: String)
    }

    companion object {

        /**
         * Singleton object of the repository
         */
        private var INSTANCE: WithdrawRepository? = null

        /**
         * Destroy the singleton instance of this repository
         */
        fun destroyInstance() {
            INSTANCE = null
        }

        /**
         * Returns a singleton instance of this repository
         * @param remoteDataSource Network datasource object for withdrawal
         * @param localDataSource Local datasource object for withdrawal
         * @param preferences Shared preference object
         * @return a singleton instance of this repository
         */
        fun getInstance(remoteDataSource: WithdrawRemoteDataSource,
                        localDataSource: WithdrawLocalDataSource,
                        preferences: SharedPreferences): WithdrawRepository {
            if (INSTANCE == null) {
                synchronized(WithdrawRepository::class.java) {
                    INSTANCE = WithdrawRepository(remoteDataSource,
                            localDataSource, preferences)
                }
            }
            return INSTANCE
        }
    }
}