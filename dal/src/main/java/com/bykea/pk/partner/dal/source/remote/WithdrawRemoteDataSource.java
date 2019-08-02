package com.bykea.pk.partner.dal.source.remote;

import android.util.Log;

import com.bykea.pk.partner.dal.source.remote.data.PersonalInfoData;
import com.bykea.pk.partner.dal.source.remote.response.BaseResponse;
import com.bykea.pk.partner.dal.source.remote.response.GetDriverProfile;
import com.bykea.pk.partner.dal.source.remote.response.GetWithdrawalPaymentMethods;
import com.bykea.pk.partner.dal.source.remote.response.WithdrawPostResponse;
import com.bykea.pk.partner.dal.source.withdraw.WithdrawRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WithdrawRemoteDataSource {

    /**
     * API to get all payment method list from server
     *
     * @param userId of driver
     * @param tokenId of driver
     * @param callback to get results in case of failure or success
     */
    public void getAllPaymentMethods(String userId, String tokenId, WithdrawRepository.LoadWithdrawalCallback callback) {
        Backend.Companion.getTelos().getWithdrawalPaymentMethods(/*"http://www.mocky.io/v2/5d417cb83100007bc2539436", */tokenId, userId)
                .enqueue(new Callback<GetWithdrawalPaymentMethods>() {
                    @Override
                    public void onResponse(Call<GetWithdrawalPaymentMethods> call, Response<GetWithdrawalPaymentMethods> response) {
                        Log.v(WithdrawRemoteDataSource.class.getSimpleName(), response.toString());
                        if (response.isSuccessful()) {
                            GetWithdrawalPaymentMethods methods = response.body();
                            Log.v(WithdrawRemoteDataSource.class.getSimpleName(), methods.getData().toString());
                            callback.onDataLoaded(methods.getData());
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

    /**
     * API to get complete driver profile from server
     *
     * @param userId of driver
     * @param tokenId of driver
     * @param callback to get results in case of failure or success
     */
    public void getDriverProfile(String userId, String tokenId, WithdrawRepository.LoadWithdrawalCallback<PersonalInfoData> callback) {
        Backend.Companion.getTelos().getDriverProfile(userId, tokenId, "d")
                .enqueue(new Callback<GetDriverProfile>() {
                    @Override
                    public void onResponse(Call<GetDriverProfile> call, Response<GetDriverProfile> response) {
                        Log.v(WithdrawRemoteDataSource.class.getSimpleName(), response.toString());
                        if (response.isSuccessful()) {
                            GetDriverProfile methods = response.body();
                            callback.onDataLoaded(methods.getData());
                        } else {
                            callback.onDataNotAvailable("No Payment Methods Found");
                        }
                    }

                    @Override
                    public void onFailure(Call<GetDriverProfile> call, Throwable t) {
                        t.printStackTrace();
                        callback.onDataNotAvailable(t.getLocalizedMessage());
                    }
                });
    }

    /**
     * Perform withdrawal operation by executing API
     *
     * @param amount to withdraw
     * @param userId of driver
     * @param tokenId of driver
     * @param paymentMethod from which payment needs to delivered
     * @param callback to get results in case of failure or success
     */
    public void performWithdraw(int amount, String userId, String tokenId, int paymentMethod,
                                WithdrawRepository.LoadWithdrawalCallback<Boolean> callback) {
        Backend.Companion.getTelos().getPerformWithdraw(/*"http://www.mocky.io/v2/5d42ee9a3200005700764438", */
                tokenId, userId, paymentMethod, amount)
                .enqueue(new Callback<WithdrawPostResponse>() {
                    @Override
                    public void onResponse(Call<WithdrawPostResponse> call, Response<WithdrawPostResponse> response) {
                        Log.v(WithdrawRemoteDataSource.class.getSimpleName(), response.toString());
                        if (response.isSuccessful()) {
                            BaseResponse baseResponse = response.body();
                            callback.onDataLoaded(baseResponse.isSuccess());
                        } else {
                            callback.onDataNotAvailable("No Payment Methods Found");
                        }
                    }

                    @Override
                    public void onFailure(Call<WithdrawPostResponse> call, Throwable t) {
                        t.printStackTrace();
                        callback.onDataNotAvailable(t.getLocalizedMessage());
                    }
                });
    }
}