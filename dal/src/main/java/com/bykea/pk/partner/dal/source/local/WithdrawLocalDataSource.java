package com.bykea.pk.partner.dal.source.local;

import com.bykea.pk.partner.dal.util.AppExecutors;

import org.jetbrains.annotations.NotNull;

public class WithdrawLocalDataSource {

    private final AppExecutors appExecutors;
    private final WithDrawDao withDrawDao;

    private static WithdrawLocalDataSource INSTANCE = null;

    public WithdrawLocalDataSource(AppExecutors appExecutors, WithDrawDao withDrawDao) {
        this.appExecutors = appExecutors;
        this.withDrawDao = withDrawDao;
    }

    @NotNull
    public static WithdrawLocalDataSource getInstance(@NotNull AppExecutors appExecutors, @NotNull WithDrawDao withDrawDao) {
        if (INSTANCE == null) {
            synchronized (WithdrawLocalDataSource.class) {
                INSTANCE = new WithdrawLocalDataSource(appExecutors, withDrawDao);
            }
        }
        return INSTANCE;
    }
}