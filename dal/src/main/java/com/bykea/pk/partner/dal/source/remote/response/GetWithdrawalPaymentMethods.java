package com.bykea.pk.partner.dal.source.remote.response;

import com.bykea.pk.partner.dal.source.remote.response.data.WithdrawPaymentMethod;

import java.util.List;

public class GetWithdrawalPaymentMethods extends BaseResponse {

    private List<WithdrawPaymentMethod> data;

    public List<WithdrawPaymentMethod> getData() {
        return data;
    }

    public void setData(List<WithdrawPaymentMethod> data) {
        this.data = data;
    }
}