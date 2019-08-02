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

/**
 * This is the repository class of {Withdrawal Process}
 *
 * @author Arsal Imam
 */
public class WithdrawRepository implements WithdrawDataSource {

    /**
     * Singleton object of the repository
     */
    private static WithdrawRepository INSTANCE = null;

    /**
     * Network datasource object for withdrawal
     */
    private final WithdrawRemoteDataSource remoteDataSource;

    /**
     * Local datasource object for withdrawal
     */
    private final WithdrawLocalDataSource localDataSource;
    /**
     * Shared preference object
     */
    private final SharedPreferences preferences;


    /**
     * Constructs a new object of this repo
     * @param remoteDataSource Network datasource object for withdrawal
     * @param localDataSource Local datasource object for withdrawal
     * @param preferences Shared preference object
     */
    public WithdrawRepository(@NotNull WithdrawRemoteDataSource remoteDataSource,
                              @NotNull WithdrawLocalDataSource localDataSource,
                              @NotNull SharedPreferences preferences) {
        this.remoteDataSource = remoteDataSource;
        this.localDataSource = localDataSource;
        this.preferences = preferences;
    }

    /**
     * Destroy the singleton instance of this repository
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    /**
     * Returns a singleton instance of this repository
     * @param remoteDataSource Network datasource object for withdrawal
     * @param localDataSource Local datasource object for withdrawal
     * @param preferences Shared preference object
     * @return a singleton instance of this repository
     */
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

    /**
     * Get all payment methods by executing server API or from local datasource
     * @param callback to get results in case of failure or success
     */
    @Override
    public void getAllPaymentMethods(WithdrawRepository.LoadWithdrawalCallback callback) {
        getPaymentMethodsFromRemoteSource(callback);
    }

    /**
     * Get driver's profile by executing server API
     * @param callback to get results in case of failure or success
     */
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

    /**
     * Get all payment methods by executing server API
     * @param callback to get results in case of failure or success
     */
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

    /**
     * Perform withdrawal operation by executing API
     *
     * @param amount to withdraw
     * @param paymentMethod from which payment needs to delivered
     * @param callback to get results in case of failure or success
     */
    public void performWithdraw(@NonNull int amount, @NonNull int paymentMethod, LoadWithdrawalCallback<Boolean> callback) {
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

    /**
     * Callback on success
     * @param <T> Result
     */
    public interface LoadWithdrawalCallback<T> {
        void onDataLoaded(T data);

        void onDataNotAvailable(String errorMsg);
    }
}