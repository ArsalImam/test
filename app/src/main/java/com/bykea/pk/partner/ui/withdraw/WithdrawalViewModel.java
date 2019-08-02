package com.bykea.pk.partner.ui.withdraw;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bykea.pk.partner.dal.source.remote.data.PersonalInfoData;
import com.bykea.pk.partner.dal.source.remote.data.WithdrawPaymentMethod;
import com.bykea.pk.partner.dal.source.withdraw.WithdrawRepository;
import com.bykea.pk.partner.models.data.SettingsData;
import com.bykea.pk.partner.ui.helpers.AppPreferences;

import java.util.List;

/**
 * This is the view model class of {WithdrawalActivity}
 *
 * @author Arsal Imam
 */
public class WithdrawalViewModel extends ViewModel {

    private MutableLiveData<List<WithdrawPaymentMethod>> mAvailablePaymentMethods;
    private WithdrawRepository withdrawRepository;
    private MutableLiveData<Boolean> _showConfirmationDialog, _onWithdrawCompleted, _showLoader;
    private MutableLiveData<Integer> _balanceInt;
    private MutableLiveData<String> _errorMessage;
    private MutableLiveData<PersonalInfoData> driverProfile;
    private WithdrawPaymentMethod selectedPaymentMethod;


    /**
     * Constructor of this viewModel
     * @param withdrawRepository instance to get and update data
     */
    public WithdrawalViewModel(WithdrawRepository withdrawRepository) {
        this.withdrawRepository = withdrawRepository;
        _showLoader = new MutableLiveData<>();
        _balanceInt = new MutableLiveData<>();
        _errorMessage = new MutableLiveData<>();
        _onWithdrawCompleted = new MutableLiveData<>();
        _showConfirmationDialog = new MutableLiveData<>();
    }

    /**
     * Load user (driver) profile from repository
     */
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

    /**
     * Load all payment methods from repository
     */
    private void loadWithdrawalMethods() {
        _showLoader.setValue(true);
        withdrawRepository.getAllPaymentMethods(new WithdrawRepository.LoadWithdrawalCallback<List<WithdrawPaymentMethod>>() {

            @Override
            public void onDataLoaded(List<WithdrawPaymentMethod> data) {
                _showLoader.setValue(false);
                data.get(0).setSelected(true);
                selectedPaymentMethod = data.get(0);
                mAvailablePaymentMethods.setValue(data);
            }

            @Override
            public void onDataNotAvailable(String errorMsg) {
                _showLoader.setValue(false);
                _errorMessage.setValue("معذرت فی الحال آپ کی ردخواست پر عمل نہیں کیا جا سکتا");
            }
        });
    }

    /**
     * Executes withdrawal PUT API from repository to perform withdrawal operation.
     */
    public void confirmWithdraw() {
        Integer enteredBalance = _balanceInt.getValue();
        _showLoader.setValue(true);
        withdrawRepository.performWithdraw(enteredBalance, selectedPaymentMethod.getCode(), new WithdrawRepository.LoadWithdrawalCallback<Boolean>() {

            @Override
            public void onDataLoaded(Boolean data) {
                _showLoader.setValue(false);
                _onWithdrawCompleted.setValue(true);
            }

            @Override
            public void onDataNotAvailable(String errorMsg) {
                _showLoader.setValue(false);
                _errorMessage.setValue("معذرت فی الحال آپ کی ردخواست پر عمل نہیں کیا جا سکتا");
            }
        });
    }

    /**
     * Binded with button component (in xml),
     * @param amt amount entered by the user
     */
    public void onSubmitClicked(String amt) {
        try {
            int amount = Integer.valueOf(amt);
            String errorMsg = validateContent(amount);
            _errorMessage.setValue(errorMsg);
            if (errorMsg == null) {
                _balanceInt.setValue(amount);
                _showConfirmationDialog.setValue(true);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return the cnic number from driver profile
     * @return cnic number
     */
    public String getDriverCnicNumber() {
        return driverProfile.getValue() == null ?
                "" : driverProfile.getValue().getCnic();
    }

    /**
     * Performing validation on form submit
     * @param amount to withdraw
     *
     * @return <b>null</b> if validation done successfully, otherwise returns error message
     */
    public String validateContent(int amount) {
        SettingsData settingsData = AppPreferences.getSettings();

        double maxValue = settingsData.getSettings().getWithdrawPartnerMaxLimit(),
                minValue = settingsData.getSettings().getWithdrawPartnerMinLimit(),
                accountBalance = driverProfile.getValue().getWallet();
        if (amount < minValue) {
            return String.format("درج کردہ رقم" + " %s " + "سے کم نہیں ہوسکتی", Math.round(minValue));
        }
        if (amount > maxValue) {
            return String.format("ایک وقت میں" + " %s " + "سے زیادہ کی رقم نہیں نکالی جاسکتی", Math.round(maxValue));
        }
        if (accountBalance < amount) {
            return "درج کردہ رقم آپ کے موجودہ بیلنس سے زیادہ ہے";
        }
        return null;
    }


    //-------------------------------------//
    //----- Getters of the observers ------//
    //-------------------------------------//

    public LiveData<PersonalInfoData> getDriverProfile() {
        if (driverProfile == null) {
            driverProfile = new MutableLiveData<>();
            loadUserProfile();
        }
        return driverProfile;
    }

    public MutableLiveData<Boolean> getShowConfirmationDialog() {
        if (_showConfirmationDialog == null) {
            _showConfirmationDialog = new MutableLiveData<>();
        }
        return _showConfirmationDialog;
    }

    public MutableLiveData<Boolean> getShowLoader() {
        if (_showLoader == null) {
            _showLoader = new MutableLiveData<>();
        }
        return _showLoader;
    }

    public MutableLiveData<Boolean> getIsWithdrawCompleted() {
        if (_onWithdrawCompleted == null) {
            _onWithdrawCompleted = new MutableLiveData<>();
        }
        return _onWithdrawCompleted;
    }

    public MutableLiveData<String> getErrorMessage() {
        if (_errorMessage == null) {
            _errorMessage = new MutableLiveData<>();
        }
        return _errorMessage;
    }

    public LiveData<List<WithdrawPaymentMethod>> getAvailablePaymentMethods() {
        if (mAvailablePaymentMethods == null) {
            mAvailablePaymentMethods = new MutableLiveData<>();
            loadWithdrawalMethods();
        }
        return mAvailablePaymentMethods;
    }

    public WithdrawPaymentMethod getSelectedPaymentMethod() {
        return selectedPaymentMethod;
    }

    public MutableLiveData<Integer> getBalanceToWithdraw() {
        if (_balanceInt == null) {
            _balanceInt = new MutableLiveData<>();
        }
        return _balanceInt;
    }

    public void setSelectedPaymentMethod(WithdrawPaymentMethod selectedPaymentMethod) {
        this.selectedPaymentMethod = selectedPaymentMethod;
    }
}