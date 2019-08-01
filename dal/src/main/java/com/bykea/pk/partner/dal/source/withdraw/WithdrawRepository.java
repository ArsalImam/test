package com.bykea.pk.partner.dal.source.withdraw;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.bykea.pk.partner.dal.source.local.WithdrawLocalDataSource;
import com.bykea.pk.partner.dal.source.pref.AppPref;
import com.bykea.pk.partner.dal.source.remote.WithdrawRemoteDataSource;
import com.bykea.pk.partner.dal.source.remote.data.PersonalInfoData;
import com.bykea.pk.partner.dal.source.remote.data.WithdrawPaymentMethod;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WithdrawRepository implements WithdrawDataSource {

    private static WithdrawRepository INSTANCE = null;
    private final WithdrawRemoteDataSource remoteDataSource;
    private final WithdrawLocalDataSource localDataSource;
    private final SharedPreferences preferences;
    private boolean isCacheEnabled;

    public WithdrawRepository(@NotNull WithdrawRemoteDataSource remoteDataSource,
                              @NotNull WithdrawLocalDataSource localDataSource,
                              @NotNull SharedPreferences preferences) {
        this.remoteDataSource = remoteDataSource;
        this.localDataSource = localDataSource;
        this.preferences = preferences;
        this.isCacheEnabled = false;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @NotNull
    public static WithdrawRepository getInstance(@NotNull WithdrawRemoteDataSource remoteDataSource,
                                                 @NotNull WithdrawLocalDataSource localDataSource,
                                                 @NotNull SharedPreferences preferences) {
        if (INSTANCE == null) {
            synchronized (WithdrawRepository.class) {
                INSTANCE = new WithdrawRepository(remoteDataSource,
                        localDataSource, preferences);
            }
        }
        return INSTANCE;
    }

    @Override
    public void getAllPaymentMethods(WithdrawRepository.LoadWithdrawalCallback callback) {
        if (isCacheEnabled) {
            //TODO need to load from database
        }
        getPaymentMethodsFromRemoteSource(callback);
    }

    @Override
    public void getDriverProfile(WithdrawRepository.LoadWithdrawalCallback callback) {
        this.remoteDataSource.getDriverProfile(
                AppPref.INSTANCE.getDriverId(preferences),
                AppPref.INSTANCE.getAccessToken(preferences),
                new LoadWithdrawalCallback<PersonalInfoData>() {
                    @Override
                    public void onDataLoaded(PersonalInfoData data) {
                        callback.onDataLoaded(data);
                    }

                    @Override
                    public void onDataNotAvailable(String errorMsg) {
                        callback.onDataNotAvailable(errorMsg);
                    }
                });
    }


    private void getPaymentMethodsFromRemoteSource(LoadWithdrawalCallback callback) {
        this.remoteDataSource.getAllPaymentMethods(
                AppPref.INSTANCE.getDriverId(preferences),
                AppPref.INSTANCE.getAccessToken(preferences),
                new LoadWithdrawalCallback<List<WithdrawPaymentMethod>>() {
                    @Override
                    public void onDataLoaded(List<WithdrawPaymentMethod> data) {
                        callback.onDataLoaded(data);
                    }

                    @Override
                    public void onDataNotAvailable(String errorMsg) {
                        callback.onDataNotAvailable(errorMsg);
                    }
                });
    }

    public void performWithdraw(@NonNull int amount, @NonNull String paymentMethod, LoadWithdrawalCallback<Boolean> callback) {
        this.remoteDataSource.performWithdraw(
                amount,
                AppPref.INSTANCE.getDriverId(preferences),
                AppPref.INSTANCE.getAccessToken(preferences),
                paymentMethod,
                new LoadWithdrawalCallback<Boolean>() {
                    @Override
                    public void onDataLoaded(Boolean data) {
                        callback.onDataLoaded(data);
                    }

                    @Override
                    public void onDataNotAvailable(String errorMsg) {
                        callback.onDataNotAvailable(errorMsg);
                    }
                });
    }

    public interface LoadWithdrawalCallback<T> {
        void onDataLoaded(T data);

        void onDataNotAvailable(String errorMsg);
    }
}