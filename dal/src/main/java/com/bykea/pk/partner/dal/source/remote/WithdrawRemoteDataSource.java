package com.bykea.pk.partner.dal.source.remote;

import android.util.Log;

import com.bykea.pk.partner.dal.source.remote.response.GetWithdrawalPaymentMethods;
import com.bykea.pk.partner.dal.source.withdraw.WithdrawRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WithdrawRemoteDataSource {
    public void getAllPaymentMethods(WithdrawRepository.LoadWithdrawalCallback callback) {
        String tokenId = "";
        String driverId = "";
        String mockUrl = "http://www.mocky.io/v2/5d417cb83100007bc2539436";
        Backend.Companion.getTelos().getMockWithdrawalPaymentMethods(mockUrl, tokenId, driverId)
                .enqueue(new Callback<GetWithdrawalPaymentMethods>() {
                    @Override
                    public void onResponse(Call<GetWithdrawalPaymentMethods> call, Response<GetWithdrawalPaymentMethods> response) {
                        Log.v(WithdrawRemoteDataSource.class.getSimpleName(), response.toString());
                        if (response.isSuccessful()) {
                            GetWithdrawalPaymentMethods methods = response.body();
                            Log.v(WithdrawRemoteDataSource.class.getSimpleName(), methods.getData().toString());
                            callback.onPaymentMethodsLoaded(methods.getData());
                        } else {
                            callback.onDataNotAvailable("No Payment Methods Found");
                        }
                    }

                    @Override
                    public void onFailure(Call<GetWithdrawalPaymentMethods> call, Throwable t) {
                        t.printStackTrace();
                        callback.onDataNotAvailable(t.getLocalizedMessage());
                    }
                });
    }
}