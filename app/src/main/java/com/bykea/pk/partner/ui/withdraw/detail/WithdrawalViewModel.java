package com.bykea.pk.partner.ui.withdraw.detail;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.dal.source.remote.data.PersonalInfoData;
import com.bykea.pk.partner.dal.source.remote.data.WithdrawPaymentMethod;
import com.bykea.pk.partner.dal.source.withdraw.WithdrawRepository;

import java.util.List;

public class WithdrawalViewModel extends ViewModel {

    private MutableLiveData<List<WithdrawPaymentMethod>> mAvailablePaymentMethods;
    private WithdrawRepository withdrawRepository;
    private Context application;
    private MutableLiveData<Boolean> _showLoader;
    private MutableLiveData<Boolean> _showConfirmationDialog;
    private MutableLiveData<Integer> _balanceInt;
    private MutableLiveData<PersonalInfoData> driverProfile;



    public MutableLiveData<Boolean> getShowConfirmationDialog() {
        return _showConfirmationDialog;
    }

    public MutableLiveData<Boolean> getShowLoader() {
        return _showLoader;
    }

    public WithdrawalViewModel(WithdrawRepository withdrawRepository) {
        this.withdrawRepository = withdrawRepository;
        _showLoader = new MutableLiveData<>();
        _balanceInt = new MutableLiveData<>();
        _showConfirmationDialog = new MutableLiveData<>();
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

    public LiveData<PersonalInfoData> getDriverProfile() {
        if (driverProfile == null) {
            driverProfile = new MutableLiveData<>();
            loadUserProfile();
        }
        return driverProfile;
    }

    private void loadUserProfile() {
        withdrawRepository.getDriverProfile(new WithdrawRepository.LoadWithdrawalCallback<PersonalInfoData>() {

            @Override
            public void onDataLoaded(PersonalInfoData data) {
                driverProfile.setValue(data);
            }

            @Override
            public void onDataNotAvailable(String errorMsg) {
                _showLoader.setValue(false);
            }
        });
    }

    private void loadWithdrawalMethods() {
        _showLoader.setValue(true);
        withdrawRepository.getAllPaymentMethods(new WithdrawRepository.LoadWithdrawalCallback<List<WithdrawPaymentMethod>>() {

            @Override
            public void onDataLoaded(List<WithdrawPaymentMethod> data) {
                _showLoader.setValue(false);
                mAvailablePaymentMethods.setValue(data);
            }

            @Override
            public void onDataNotAvailable(String errorMsg) {
                _showLoader.setValue(false);
            }
        });
    }

    public void confirmWithdraw() {
        _showLoader.setValue(true);
        withdrawRepository.performWithdraw(_balanceInt.getValue(), "1", new WithdrawRepository.LoadWithdrawalCallback<Boolean>() {

            @Override
            public void onDataLoaded(Boolean data) {
                _showLoader.setValue(false);
            }

            @Override
            public void onDataNotAvailable(String errorMsg) {
                _showLoader.setValue(false);
            }
        });
    }

    public void onSubmitClicked(String s) {
        try {
            int amount = Integer.valueOf(s);
            _balanceInt.setValue(amount);
            _showConfirmationDialog.setValue(true);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public String getDriverCnicNumber() {
        return driverProfile.getValue() == null ?
                "" : driverProfile.getValue().getCnic();
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