package com.bykea.pk.partner.dal.source.withdraw;

public interface WithdrawDataSource {

    /**
     * Get all payment methods by executing server API or from local datasource
     * @param callback to get results in case of failure or success
     */
    void getAllPaymentMethods(WithdrawRepository.LoadWithdrawalCallback callback);

    /**
     * Get driver's profile by executing server API
     * @param callback to get results in case of failure or success
     */
    void getDriverProfile(WithdrawRepository.LoadWithdrawalCallback callback);
}