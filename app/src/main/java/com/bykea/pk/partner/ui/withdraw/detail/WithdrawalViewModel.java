package com.bykea.pk.partner.ui.withdraw.detail;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.dal.source.remote.response.data.WithdrawPaymentMethod;
import com.bykea.pk.partner.dal.source.withdraw.WithdrawRepository;

import java.util.List;

public class WithdrawalViewModel extends ViewModel {

    private MutableLiveData<List<WithdrawPaymentMethod>> mAvailablePaymentMethods;
    private WithdrawRepository withdrawRepository;
    private Context application;

    public WithdrawalViewModel(WithdrawRepository withdrawRepository) {

        this.withdrawRepository = withdrawRepository;
        _showLoader = new MutableLiveData<>();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public LiveData<List<WithdrawPaymentMethod>> getAvailablePaymentMethods() {

        if (mAvailablePaymentMethods == null) {
            mAvailablePaymentMethods = new MutableLiveData<>();
            loadWithdrawalMethods();
        }
        return mAvailablePaymentMethods;
    }

    public MutableLiveData<Boolean> _showLoader;

    private void loadWithdrawalMethods() {

        _showLoader.setValue(true);
        withdrawRepository.getAllPaymentMethods(new WithdrawRepository.LoadWithdrawalCallback() {
            @Override
            public void onPaymentMethodsLoaded(List<WithdrawPaymentMethod> data) {
                _showLoader.setValue(false);
                mAvailablePaymentMethods.setValue(data);
            }

            @Override
            public void onDataNotAvailable(String errorMsg) {
                _showLoader.setValue(false);
            }
        });
    }

    public String getDriverCnicNumber() {
        return withdrawRepository.getDriverCnicNumber();
    }

    public String getPaymentDescriptionText(WithdrawPaymentMethod object) {
        switch (object.getCode()) {
            case Types.JAZZ_CASH_OBJECT:
                return String.format(
                        application.getResources().getString(R.string.fees_template_val) +
                                " %s " + application.getResources().getString(R.string.fees_currency_val),
                        object.getFees()
                );
            default:
                return "";
        }
    }

    public void setApplicationContext(Context applicationContext) {
        this.application = applicationContext;
    }

    public class Types {
        public static final int JAZZ_CASH_OBJECT = 1;
    }
}