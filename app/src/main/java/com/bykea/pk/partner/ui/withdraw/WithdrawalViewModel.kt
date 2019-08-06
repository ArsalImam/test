package com.bykea.pk.partner.ui.withdraw

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bykea.pk.partner.dal.source.remote.data.PersonalInfoData
import com.bykea.pk.partner.dal.source.remote.data.WithdrawPaymentMethod
import com.bykea.pk.partner.dal.source.withdraw.WithdrawRepository
import com.bykea.pk.partner.ui.helpers.AppPreferences
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

    private val _paymentMethods = MutableLiveData<List<WithdrawPaymentMethod>>().apply { value = emptyList() }
    val paymentMethods: LiveData<List<WithdrawPaymentMethod>>
        get() = _paymentMethods

    private val _showConfirmationDialog = MutableLiveData<Boolean>().apply { value = false }
    val showConfirmationDialog: LiveData<Boolean>
        get() = _showConfirmationDialog

    private val _onWithdrawCompleted = MutableLiveData<Boolean>().apply { value = false }
    val onWithdrawCompleted: LiveData<Boolean>
        get() = _onWithdrawCompleted

    private val _showLoader = MutableLiveData<Boolean>().apply { value = false }
    val showLoader: LiveData<Boolean>
        get() = _showLoader

    private val _balanceInt = MutableLiveData<Int>().apply { value = 0 }
    val balanceInt: LiveData<Int>
        get() = _balanceInt

    private val _errorMessage = MutableLiveData<String>().apply { value = null }
    val errorMessage: LiveData<String>
        get() = _errorMessage

    private val _driverProfile = MutableLiveData<PersonalInfoData>().apply { value = null }
    val driverProfile: LiveData<PersonalInfoData>
        get() = _driverProfile

    private var _selectedPaymentMethod: WithdrawPaymentMethod? = null
    var selectedPaymentMethod: WithdrawPaymentMethod? = null
        get() = _selectedPaymentMethod

    init {
    }

    /**
     * Load user (driver) profile from repository
     */
    fun loadUserProfile() {
        withdrawRepository.getDriverProfile(object : WithdrawRepository.LoadWithdrawalCallback<PersonalInfoData?> {
            override fun onDataLoaded(data: PersonalInfoData?) {
//                driverProfile!!.apply { this.value = data }
                _driverProfile.value = data
            }

            override fun onDataNotAvailable(errorMsg: String) {
                _showLoader!!.value = false
            }
        })
    }

    fun getDriverCnicNumber(): String {
        if (driverProfile != null && driverProfile.value != null) {
            return driverProfile.value!!.cnic
        } else {
            return StringUtils.EMPTY
        }
//        driverProfile.let {
//            driverProfile.value.let {
//                driverProfile.value?.cnic.let {
//                    return driverProfile.value?.cnic!!
//                }
//            }
//        }
//        return StringUtils.EMPTY
    }

    /**
     * Load all payment methods from repository
     */
    fun loadWithdrawalMethods() {
        _showLoader!!.value = true
        withdrawRepository.getAllPaymentMethods(object : WithdrawRepository.LoadWithdrawalCallback<List<WithdrawPaymentMethod>> {
            override fun onDataLoaded(data: List<WithdrawPaymentMethod>?) {
                _showLoader!!.value = false
                data!![0].isSelected = true
                _selectedPaymentMethod = data[0]
                _paymentMethods!!.value = data
            }

            override fun onDataNotAvailable(errorMsg: String) {
                _showLoader!!.value = false
                _errorMessage!!.value = "معذرت فی الحال آپ کی ردخواست پر عمل نہیں کیا جا سکتا"
            }
        })
    }

    /**
     * Executes withdrawal PUT API from repository to perform withdrawal operation.
     */
    fun confirmWithdraw() {
        val enteredBalance = balanceInt!!.value
        _showLoader!!.value = true
        withdrawRepository.performWithdraw(enteredBalance!!, selectedPaymentMethod!!.code!!, object : WithdrawRepository.LoadWithdrawalCallback<Boolean> {

            override fun onDataLoaded(data: Boolean?) {
                _showLoader!!.value = false
                _onWithdrawCompleted!!.value = true
            }

            override fun onDataNotAvailable(errorMsg: String) {
                _showLoader!!.value = false
                _errorMessage!!.value = "معذرت فی الحال آپ کی ردخواست پر عمل نہیں کیا جا سکتا"
            }
        })
    }

    /**
     * Binded with button's onclick,
     * @param amt amount entered by the user
     */
    fun onSubmitClicked(amt: String) {
        try {
            val amount = Integer.valueOf(amt)
            val errorMsg = validateContent(amount)
            _errorMessage!!.value = errorMsg
            if (errorMsg == null) {
                _balanceInt!!.value = amount
                _showConfirmationDialog!!.value = true
            }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }

    }

    fun showConfirmationDialog(isShow: Boolean) {
        _showConfirmationDialog.value = isShow
    }

    /**
     * Performing validation on form submit
     * @param amount to withdraw
     *
     * @return **null** if validation done successfully, otherwise returns error message
     */
    fun validateContent(amount: Int): String? {
        val settingsData = AppPreferences.getSettings()

        val maxValue = settingsData.settings.withdrawPartnerMaxLimit
        val minValue = settingsData.settings.withdrawPartnerMinLimit
        val accountBalance = driverProfile!!.value!!.wallet

        if (accountBalance < amount) {
            return "درج کردہ رقم آپ کے موجودہ بیلنس سے زیادہ ہے"
        }
        if (amount < minValue) {
            return String.format("درج کردہ رقم" + " %,d " + "سے کم نہیں ہوسکتی", Math.round(minValue))
        }
        if (amount > maxValue) {
            return String.format("ایک وقت میں" + " %,d " + "سے زیادہ کی رقم نہیں نکالی جاسکتی", Math.round(maxValue))
        }
        return null
    }

    fun removeWarnings() {
        _errorMessage.value = null
    }


    //-------------------------------------//
    //----- Getters of the observers ------//
    //-------------------------------------//

//    fun getDriverProfile(): LiveData<PersonalInfoData> {
//        if (driverProfile == null) {
//            driverProfile = MutableLiveData()
//            loadUserProfile()
//        }
//        return driverProfile!!
//    }
}