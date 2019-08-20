package com.bykea.pk.partner.ui.withdraw

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bykea.pk.partner.DriverApp
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.source.remote.data.PersonalInfoData
import com.bykea.pk.partner.dal.source.remote.data.WithdrawPaymentMethod
import com.bykea.pk.partner.dal.source.withdraw.WithdrawRepository
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.utils.Constants
import org.apache.commons.lang3.StringUtils

/**
 * This is the view model class of {WithdrawalActivity}
 *
 * @author Arsal Imam
 */
class WithdrawalViewModel
/**
 * Constructor of this viewModel
 * @param withdrawRepository instance to get and update data
 */
(private val withdrawRepository: WithdrawRepository) : ViewModel() {

    /**
     * list of payment methods
     */
    private val _paymentMethods = MutableLiveData<List<WithdrawPaymentMethod>>().apply { value = emptyList() }
    val paymentMethods: LiveData<List<WithdrawPaymentMethod>>
        get() = _paymentMethods

    /**
     * observable to show/hide confirmation dialog
     */
    private val _showConfirmationDialog = MutableLiveData<Boolean>().apply { value = false }
    val showConfirmationDialog: LiveData<Boolean>
        get() = _showConfirmationDialog

    /**
     * callback event to view on withdrawal process completed
     */
    private val _onWithdrawCompleted = MutableLiveData<Boolean>().apply { value = false }
    val onWithdrawCompleted: LiveData<Boolean>
        get() = _onWithdrawCompleted

    /**
     * observable to show/hide loader
     */
    private val _showLoader = MutableLiveData<Boolean>().apply { value = false }
    val showLoader: LiveData<Boolean>
        get() = _showLoader

    /**
     * balance entered by user
     */
    private val _balanceInt = MutableLiveData<Int>().apply { value = 0 }
    val balanceInt: LiveData<Int>
        get() = _balanceInt

    /**
     * validation message to show on view
     */
    private val _errorMessage = MutableLiveData<String>().apply { value = null }
    val errorMessage: LiveData<String>
        get() = _errorMessage

    /**
     * driver profile obtained from API {@link #loadUserProfile()}
     */
    private val _driverProfile = MutableLiveData<PersonalInfoData>().apply { value = null }
    val driverProfile: LiveData<PersonalInfoData>
        get() = _driverProfile

    /**
     * observable for selected payment method
     */
    private var _selectedPaymentMethod: WithdrawPaymentMethod? = null
    var selectedPaymentMethod: WithdrawPaymentMethod? = null
        get() = _selectedPaymentMethod

    /**
     * Load user (driver) profile from repository
     */
    fun loadUserProfile() {
        _showLoader?.value = true
        withdrawRepository.getDriverProfile(object : WithdrawRepository.LoadWithdrawalCallback<PersonalInfoData?> {
            override fun onDataLoaded(data: PersonalInfoData?) {
                _driverProfile.value = data
                loadWithdrawalMethods()
            }

            override fun onDataNotAvailable(errorMsg: String) {
                _showLoader?.value = false
            }
        })
    }

    /**
     * returns driver's CNIC number if profile loaded
     *
     * @return cnic number
     */
    fun getDriverCnicNumber(): String {
        return if (driverProfile != null && driverProfile.value != null) {
            driverProfile.value!!.cnic
        } else {
            StringUtils.EMPTY
        }
    }

    /**
     * Load all payment methods from repository
     */
    fun loadWithdrawalMethods() {
        withdrawRepository.getAllPaymentMethods(object : WithdrawRepository.LoadWithdrawalCallback<List<WithdrawPaymentMethod>> {
            override fun onDataLoaded(data: List<WithdrawPaymentMethod>?) {
                if (!data.isNullOrEmpty()) {
                    data[0].isSelected = true
                    _selectedPaymentMethod = data[0]
                    _paymentMethods?.value = data
                }
                _showLoader?.value = false
            }

            override fun onDataNotAvailable(errorMsg: String) {
                _showLoader?.value = false
                _errorMessage?.value = DriverApp.getContext().getString(R.string.something_went_wrong)
            }
        })
    }

    /**
     * Executes withdrawal PUT API from repository to perform withdrawal operation.
     */
    fun confirmWithdraw() {
        val enteredBalance = balanceInt?.value!!
        if (enteredBalance != null) {

            val amount = balanceInt?.value!!
            _showLoader?.value = true

            withdrawRepository.performWithdraw(amount, selectedPaymentMethod?.code!!, object : WithdrawRepository.LoadWithdrawalCallback<Boolean> {

                override fun onDataLoaded(data: Boolean?) {
                    _showLoader?.value = false
                    _onWithdrawCompleted?.value = true
                }

                override fun onDataNotAvailable(errorMsg: String) {
                    _showLoader?.value = false
                    if (errorMsg.contentEquals(Constants.ApiError.ERROR_MSG_CODE.toString())) {
                        _errorMessage?.value = DriverApp.getContext().getString(R.string.withdraw_error_threshold_exceed)
                    } else {
                        _errorMessage?.value = DriverApp.getContext().getString(R.string.something_went_wrong)
                    }
                }
            })
        }
    }

    /**
     * Binded with button's onclick,
     * @param amt amount entered by the user
     */
    fun onSubmitClicked(amt: String?) {
        try {
            val amount = Integer.valueOf(amt)
            val errorMsg = validateContent(amount)
            _errorMessage?.value = errorMsg
            if (errorMsg == null) {
                _balanceInt?.value = amount
                _showConfirmationDialog?.value = true
            }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }

    }

    /**
     * this method will show/hide the confirmation dialog
     *
     * @param isShow show(true) / hide(false)
     */
    fun showConfirmationDialog(isShow: Boolean) {
        _showConfirmationDialog.value = isShow
    }

    /**
     * Performing validation on form submit
     * @param amount to withdraw
     *
     * @return **null** if validation done successfully, otherwise returns error message
     */
    private fun validateContent(amount: Int): String? {
        try {
            val settingsData = AppPreferences.getSettings()

            val maxValue = settingsData.settings.withdrawPartnerMaxLimit
            val minValue = settingsData.settings.withdrawPartnerMinLimit

            val accountBalance = driverProfile?.value?.wallet!!

            if (accountBalance < amount) {
                return DriverApp.getContext().getString(R.string.dont_have_enough_to_withdraw)
            }
            if (amount < minValue) {
                return String.format(
                        DriverApp.getContext().getString(R.string.minimum_amount_to_withdraw_2)
                                + StringUtils.SPACE
                                + DriverApp.getContext().getString(R.string.formatted_price)
                                + StringUtils.SPACE
                                + DriverApp.getContext().getString(R.string.minimum_amount_to_withdraw_1), Math.round(minValue))
            }
            if (amount > maxValue) {
                return String.format(DriverApp.getContext().getString(R.string.maximum_amount_to_withdraw_1)
                        + StringUtils.SPACE
                        + DriverApp.getContext().getString(R.string.formatted_price)
                        + StringUtils.SPACE
                        + DriverApp.getContext().getString(R.string.maximum_amount_to_withdraw_2), Math.round(maxValue))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return DriverApp.getContext().getString(R.string.something_went_wrong)
        }
        return null
    }

    /**
     * this method will hide the validation layout
     */
    fun removeWarnings() {
        _errorMessage.value = null
    }
}