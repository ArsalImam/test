package com.bykea.pk.partner.dal.source.withdraw;

import android.content.SharedPreferences;

import com.bykea.pk.partner.dal.source.local.WithdrawLocalDataSource;
import com.bykea.pk.partner.dal.source.remote.WithdrawRemoteDataSource;
import com.bykea.pk.partner.dal.source.remote.response.data.WithdrawPaymentMethod;

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
        getJobsFromRemoteDataSource(callback);
    }

    @Override
    public String getDriverCnicNumber() {
        return "42101"/*preferences.getString()*/;
    }

    private void getJobsFromRemoteDataSource(LoadWithdrawalCallback callback) {
        this.remoteDataSource.getAllPaymentMethods(new LoadWithdrawalCallback() {
            @Override
            public void onPaymentMethodsLoaded(List<WithdrawPaymentMethod> data) {
                callback.onPaymentMethodsLoaded(data);
            }

            @Override
            public void onDataNotAvailable(String errorMsg) {
                callback.onDataNotAvailable(errorMsg);
            }
        });
    }

    public interface LoadWithdrawalCallback {

        void onPaymentMethodsLoaded(List<WithdrawPaymentMethod> data);

        void onDataNotAvailable(String errorMsg);
    }
}