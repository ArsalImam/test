package com.bykea.pk.partner.dal.source.withdraw;

public interface WithdrawDataSource {

    void getAllPaymentMethods(WithdrawRepository.LoadWithdrawalCallback loadWithdrawalCallback);

    String getDriverCnicNumber();
}