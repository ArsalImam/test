package com.bykea.pk.partner.ui.withdraw

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.bykea.pk.partner.dal.source.remote.data.PersonalInfoData
import com.bykea.pk.partner.dal.source.remote.data.WithdrawPaymentMethod
import com.bykea.pk.partner.dal.source.withdraw.WithdrawRepository
import com.bykea.pk.partner.models.data.SettingsData
import com.bykea.pk.partner.ui.helpers.AppPreferences

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

    private var mAvailablePaymentMethods: MutableLiveData<List<WithdrawPaymentMethod>>? = null
    private var showConfirmationDialog: MutableLiveData<Boolean>? = null
    private var onWithdrawCompleted: MutableLiveData<Boolean>? = null
    private var showLoader: MutableLiveData<Boolean>? = null
    private var balanceInt: MutableLiveData<Int>? = null
    private var errorMessage: MutableLiveData<String>? = null
    private var driverProfile: MutableLiveData<PersonalInfoData>? = null
    var selectedPaymentMethod: WithdrawPaymentMethod? = null

    /**
     * Return the cnic number from driver profile
     * @return cnic number
     */
    val driverCnicNumber: String
        get() = if (driverProfile!!.value == null)
            ""
        else
            driverProfile!!.value!!.cnic

    val isWithdrawCompleted: MutableLiveData<Boolean>
        get() {
            if (onWithdrawCompleted == null) {
                onWithdrawCompleted = MutableLiveData()
            }
            return onWithdrawCompleted
        }

    val availablePaymentMethods: LiveData<List<WithdrawPaymentMethod>>
        get() {
            if (mAvailablePaymentMethods == null) {
                mAvailablePaymentMethods = MutableLiveData()
                loadWithdrawalMethods()
            }
            return mAvailablePaymentMethods
        }

    val balanceToWithdraw: MutableLiveData<Int>
        get() {
            if (balanceInt == null) {
                balanceInt = MutableLiveData()
            }
            return balanceInt
        }


    init {
        showLoader = MutableLiveData()
        balanceInt = MutableLiveData()
        errorMessage = MutableLiveData()
        onWithdrawCompleted = MutableLiveData()
        showConfirmationDialog = MutableLiveData()
    }

    /**
     * Load user (driver) profile from repository
     */
    private fun loadUserProfile() {
        withdrawRepository.getDriverProfile(object : WithdrawRepository.LoadWithdrawalCallback<PersonalInfoData> {

            override fun onDataLoaded(data: PersonalInfoData) {
                driverProfile!!.value = data
            }

            override fun onDataNotAvailable(errorMsg: String) {
                showLoader!!.value = false
            }
        })
    }

    /**
     * Load all payment methods from repository
     */
    private fun loadWithdrawalMethods() {
        showLoader!!.value = true
        withdrawRepository.getAllPaymentMethods(object : WithdrawRepository.LoadWithdrawalCallback<List<WithdrawPaymentMethod>> {

            override fun onDataLoaded(data: List<WithdrawPaymentMethod>) {
                showLoader!!.value = false
                data[0].isSelected = true
                selectedPaymentMethod = data[0]
                mAvailablePaymentMethods!!.value = data
            }

            override fun onDataNotAvailable(errorMsg: String) {
                showLoader!!.value = false
                errorMessage!!.value = "معذرت فی الحال آپ کی ردخواست پر عمل نہیں کیا جا سکتا"
            }
        })
    }

    /**
     * Executes withdrawal PUT API from repository to perform withdrawal operation.
     */
    fun confirmWithdraw() {
        val enteredBalance = balanceInt!!.value
        showLoader!!.value = true
        withdrawRepository.performWithdraw(enteredBalance!!, selectedPaymentMethod!!.code!!, object : WithdrawRepository.LoadWithdrawalCallback<Boolean> {

            override fun onDataLoaded(data: Boolean?) {
                showLoader!!.value = false
                onWithdrawCompleted!!.value = true
            }

            override fun onDataNotAvailable(errorMsg: String) {
                showLoader!!.value = false
                errorMessage!!.value = "معذرت فی الحال آپ کی ردخواست پر عمل نہیں کیا جا سکتا"
            }
        })
    }

    /**
     * Binded with button component (in xml),
     * @param amt amount entered by the user
     */
    fun onSubmitClicked(amt: String) {
        try {
            val amount = Integer.valueOf(amt)
            val errorMsg = validateContent(amount)
            errorMessage!!.value = errorMsg
            if (errorMsg == null) {
                balanceInt!!.value = amount
                showConfirmationDialog!!.value = true
            }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }

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
        if (amount < minValue) {
            return String.format("درج کردہ رقم" + " %s " + "سے کم نہیں ہوسکتی", Math.round(minValue))
        }
        if (amount > maxValue) {
            return String.format("ایک وقت میں" + " %s " + "سے زیادہ کی رقم نہیں نکالی جاسکتی", Math.round(maxValue))
        }
        return if (accountBalance < amount) {
            "درج کردہ رقم آپ کے موجودہ بیلنس سے زیادہ ہے"
        } else null
    }


    //-------------------------------------//
    //----- Getters of the observers ------//
    //-------------------------------------//

    fun getDriverProfile(): LiveData<PersonalInfoData> {
        if (driverProfile == null) {
            driverProfile = MutableLiveData()
            loadUserProfile()
        }
        return driverProfile
    }

    fun getShowConfirmationDialog(): MutableLiveData<Boolean> {
        if (showConfirmationDialog == null) {
            showConfirmationDialog = MutableLiveData()
        }
        return showConfirmationDialog
    }

    fun getShowLoader(): MutableLiveData<Boolean> {
        if (showLoader == null) {
            showLoader = MutableLiveData()
        }
        return showLoader
    }

    fun getErrorMessage(): MutableLiveData<String> {
        if (errorMessage == null) {
            errorMessage = MutableLiveData()
        }
        return errorMessage
    }
}