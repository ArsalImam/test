package com.bykea.pk.partner.dal.source.withdraw

import com.bykea.pk.partner.dal.source.remote.data.PersonalInfoData
import com.bykea.pk.partner.dal.source.remote.data.WithdrawPaymentMethod

interface WithdrawDataSource {

    /**
     * Get all payment methods by executing server API or from local datasource
     * @param callback to get results in case of failure or success
     */
    fun getAllPaymentMethods(callback: WithdrawRepository.LoadWithdrawalCallback<List<WithdrawPaymentMethod>>)

    /**
     * Get driver's profile by executing server API
     * @param callback to get results in case of failure or success
     */
    fun getDriverProfile(callback: WithdrawRepository.LoadWithdrawalCallback<PersonalInfoData?>)
}