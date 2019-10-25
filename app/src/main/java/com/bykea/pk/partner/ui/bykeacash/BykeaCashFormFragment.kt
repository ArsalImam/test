package com.bykea.pk.partner.ui.bykeacash

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.source.remote.request.UpdateBykeaCashBookingRequest
import com.bykea.pk.partner.databinding.FragmentBykeaCashFormBinding
import com.bykea.pk.partner.models.response.NormalCallData
import com.bykea.pk.partner.ui.activities.BookingActivity
import com.bykea.pk.partner.ui.common.obtainViewModel
import com.bykea.pk.partner.ui.complain.GenericFragmentListener
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.ui.helpers.FontUtils
import com.bykea.pk.partner.utils.Constants.MAX_LENGTH_CNIC
import com.bykea.pk.partner.utils.Constants.MAX_LENGTH_IBAN
import com.bykea.pk.partner.utils.Constants.ServiceCode.*
import com.bykea.pk.partner.utils.Dialogs
import com.bykea.pk.partner.utils.TripStatus
import com.bykea.pk.partner.utils.Util
import com.bykea.pk.partner.utils.Utils
import kotlinx.android.synthetic.main.fragment_bykea_cash_form.*
import org.apache.commons.validator.routines.IBANValidator


private const val ARG_PARAM1 = "param1"

class BykeaCashFormFragment : DialogFragment() {
    private lateinit var binding: FragmentBykeaCashFormBinding
    private lateinit var mCurrentActivity: BookingActivity
    private var normalCallData: NormalCallData? = null
    private var isUpdateAllowed = false
    private var mCallback: BykeaCashDetailsListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mCurrentActivity = activity as BookingActivity
        mCallback = mCurrentActivity

        setStyle(STYLE_NO_TITLE, R.style.AppTheme_Dialog_Custom)
        arguments?.let { normalCallData = it.getParcelable(ARG_PARAM1) }

        if (normalCallData?.status.equals(TripStatus.ON_ACCEPT_CALL, ignoreCase = true) ||
                normalCallData?.status.equals(TripStatus.ON_ARRIVED_TRIP, ignoreCase = true))
            isUpdateAllowed = true

        return object : Dialog(mCurrentActivity, theme) {
            override fun onBackPressed() {
                super.onBackPressed()
                dismiss()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bykea_cash_form, container, false)
        binding.lifecycleOwner = this
        binding.viewmodel = obtainViewModel(BykeaCashFormViewModel::class.java).apply {
            callData.value = normalCallData
            responseFromServer.observe(this@BykeaCashFormFragment, Observer {
                if (it) {
                    dismiss()
                    normalCallData?.serviceCode.let {
                        when (it!!) {
                            MOBILE_TOP_UP -> {
                                normalCallData?.extraParams?.phone = eTMobileNumber.text.toString()
                            }
                            MOBILE_WALLET -> {
                                normalCallData?.extraParams?.cnic = eTCNIC.text.toString()
                                normalCallData?.extraParams?.phone = eTMobileNumber.text.toString()
                            }
                            BANK_TRANSFER -> {
                                normalCallData?.extraParams?.iban = eTIBAN.text.toString()
                            }
                            UTILITY -> {
                                normalCallData?.extraParams?.account_number = eTAccountNumber.text.toString()
                            }
                        }
                        normalCallData?.codAmount = eTAmount.text.toString()
                    }
                }
            })

            cashAmount.observe(this@BykeaCashFormFragment, Observer {
                if (cashAmount.value != null)
                    mCallback?.onBykeaCashAmountUpdated(cashAmount.value!!)
            })
        }

        setListeners()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFormDataAndVisibility(normalCallData?.serviceCode)
        setFormSpannableStrings()

        if (isUpdateAllowed) setTextChangeListeners()
        else setFieldDisabled()
    }

    /**
     * Set Listeners
     */
    private fun setListeners() {
        binding.listener = object : GenericFragmentListener {
            override fun onUpdateDetails() {
                if (!isUpdateAllowed) {
                    dismiss()
                } else if (isValidate(normalCallData?.serviceCode)) {
                    Dialogs.INSTANCE.showLoader(mCurrentActivity)
                    binding.viewmodel?.updateBykeaCashFormDetails(normalCallData?.tripId!!, createUpdateBookingRequest())
                }
            }

            override fun onCancelDialog() {
                dismiss()
            }
        }
    }

    /**
     * Set Form Data and Fields Visibility Using Service Code
     * @param serviceCode: Receive In Get Active Trip Call (Ride Data)
     */
    private fun setFormDataAndVisibility(serviceCode: Int?) {
        serviceCode.let {
            when (it!!) {
                MOBILE_TOP_UP -> {
                    linLayoutMobileNumber.visibility = View.VISIBLE
                    eTMobileNumber.setText(Utils.phoneNumberToShow(normalCallData?.extraParams?.phone))
                }
                MOBILE_WALLET -> {
                    linLayoutCNIC.visibility = View.VISIBLE
                    linLayoutMobileNumber.visibility = View.VISIBLE
                    eTCNIC.setText(normalCallData?.extraParams?.cnic)
                    eTMobileNumber.setText(Utils.phoneNumberToShow(normalCallData?.extraParams?.phone))
                }
                BANK_TRANSFER -> {
                    linLayoutIBAN.visibility = View.VISIBLE
                    eTIBAN.setText(normalCallData?.extraParams?.iban)
                }
                UTILITY -> {
                    linLayoutAccountNumber.visibility = View.VISIBLE
                    eTAccountNumber.setText(normalCallData?.extraParams?.account_number)
                }
            }
        }
        eTAmount.setText(normalCallData?.codAmountNotFormatted.toString())
    }

    /**
     * Set Form Spannable Strings
     */
    private fun setFormSpannableStrings() {
        tVIBAN.text =
                SpannableStringBuilder(FontUtils.getStyledTitle(mCurrentActivity, context?.getString(R.string.iban_bank_account_number), "jameel_noori_nastaleeq.ttf"))
                        .append(FontUtils.getStyledTitle(mCurrentActivity, context?.getString(R.string.iban_with_brackets), "roboto_medium.ttf"))

        tVCNICError.text =
                SpannableStringBuilder(FontUtils.getStyledTitle(mCurrentActivity, context?.getString(R.string.cnic_length), "roboto_medium.ttf"))
                        .append(FontUtils.getStyledTitle(mCurrentActivity, context?.getString(R.string.correct_cnic), "jameel_noori_nastaleeq.ttf"))

        tVMobileNumberError.text =
                SpannableStringBuilder(FontUtils.getStyledTitle(mCurrentActivity, context?.getString(R.string.mobile_number_length), "roboto_medium.ttf"))
                        .append(FontUtils.getStyledTitle(mCurrentActivity, context?.getString(R.string.mobile_number_error2), "jameel_noori_nastaleeq.ttf"))

        tVIBANError.text =
                SpannableStringBuilder(FontUtils.getStyledTitle(mCurrentActivity, context?.getString(R.string.iban_length), "roboto_medium.ttf"))
                        .append(FontUtils.getStyledTitle(mCurrentActivity, context?.getString(R.string.digits_correction), "jameel_noori_nastaleeq.ttf"))
                        .append(FontUtils.getStyledTitle(mCurrentActivity, context?.getString(R.string.iban), "roboto_medium.ttf"))
                        .append(FontUtils.getStyledTitle(mCurrentActivity, context?.getString(R.string.enter_number), "jameel_noori_nastaleeq.ttf"))

        tVAmountError.text =
                SpannableStringBuilder(FontUtils.getStyledTitle(mCurrentActivity, context?.getString(R.string.enter_amount_error), "jameel_noori_nastaleeq.ttf"))
                        .append(FontUtils.getStyledTitle(mCurrentActivity, AppPreferences.getSettings().settings.bykeaCashMaxAmount.toString(), "roboto_medium.ttf"))
                        .append(FontUtils.getStyledTitle(mCurrentActivity, context?.getString(R.string.amount_not_more), "jameel_noori_nastaleeq.ttf"))
    }

    /**
     * Set Texh Change Listeners For Fields
     */
    private fun setTextChangeListeners() {
        eTAccountNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateAccountNumber()
            }
        })
        eTCNIC.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateCNIC()
            }
        })
        eTIBAN.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateIBAN()
            }
        })
        eTMobileNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateMobileNumber()
            }
        })
        eTAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateAmount()
            }
        })
    }

    /**
     * Set Field Disabled
     */
    private fun setFieldDisabled() {
        eTAccountNumber.isEnabled = false
        eTCNIC.isEnabled = false
        eTIBAN.isEnabled = false
        eTMobileNumber.isEnabled = false
        eTAmount.isEnabled = false
        iVNegativeButton.visibility = View.GONE
    }

    /**
     * Perform Form Validation Using Service Code
     */
    private fun isValidate(serviceCode: Int?): Boolean {
        serviceCode.let {
            when (it!!) {
                MOBILE_TOP_UP -> {
                    return validateMobileNumber() && validateAmount()
                }
                MOBILE_WALLET -> {
                    return validateCNIC() && validateMobileNumber() && validateAmount()
                }
                BANK_TRANSFER -> {
                    return validateIBAN() && validateAmount()
                }
                UTILITY -> {
                    return validateAccountNumber() && validateAmount()
                }
                else -> {
                    return false
                }
            }
        }
    }

    // region Validations
    /**
     * Validate Account Number
     */
    private fun validateAccountNumber(): Boolean {
        if (eTAccountNumber.text.isNullOrEmpty()) {
            eTAccountNumber.requestFocus()
            eTAccountNumber.setBackgroundResource(R.drawable.red_bordered_bg)
            return false
        } else {
            eTAccountNumber.setBackgroundResource(R.drawable.gray_bordered_bg)
            return true
        }
    }

    /**
     * Validate CNIC
     */
    private fun validateCNIC(): Boolean {
        if (eTCNIC.text.isNullOrEmpty() || (!eTCNIC.text.isNullOrEmpty() && eTCNIC.text.toString().length < MAX_LENGTH_CNIC)) {
            eTCNIC.requestFocus()
            tVCNICError.visibility = View.VISIBLE
            eTCNIC.setBackgroundResource(R.drawable.red_bordered_bg)
            return false
        } else {
            tVCNICError.visibility = View.GONE
            eTCNIC.setBackgroundResource(R.drawable.gray_bordered_bg)
            return true
        }
    }

    /**
     * Validate IBAN
     */
    private fun validateIBAN(): Boolean {
        val iban = eTIBAN.text.toString()
        return if (iban.isEmpty() || (iban.isNotEmpty() && iban.length < MAX_LENGTH_IBAN) || !IBANValidator.getInstance().isValid(iban)) {
            eTIBAN.requestFocus()
            tVIBANError.visibility = View.VISIBLE
            eTIBAN.setBackgroundResource(R.drawable.red_bordered_bg)
            false
        } else {
            tVIBANError.visibility = View.GONE
            eTIBAN.setBackgroundResource(R.drawable.gray_bordered_bg)
            true
        }
    }

    /**
     * Validate Mobile Number
     */
    private fun validateMobileNumber(): Boolean {
        if (!Utils.isValidNumber(eTMobileNumber)) {
            eTMobileNumber.requestFocus()
            tVMobileNumberError.visibility = View.VISIBLE
            eTMobileNumber.setBackgroundResource(R.drawable.red_bordered_bg)
            return false
        } else {
            tVMobileNumberError.visibility = View.GONE
            eTMobileNumber.setBackgroundResource(R.drawable.gray_bordered_bg)
            return true
        }
    }

    /**
     * Validate Amount Field
     */
    private fun validateAmount(): Boolean {
        if (eTAmount.text.isNullOrEmpty() ||
                (!eTAmount.text.isNullOrEmpty() && eTAmount.text.toString().toInt() == 0) ||
                (!eTAmount.text.isNullOrEmpty() && eTAmount.text.toString().toInt() > AppPreferences.getSettings().settings.bykeaCashMaxAmount)) {
            eTAmount.requestFocus()
            tVAmountError.visibility = View.VISIBLE
            eTAmount.setBackgroundResource(R.drawable.red_bordered_bg)
            return false
        } else {
            tVAmountError.visibility = View.GONE
            eTAmount.setBackgroundResource(R.drawable.gray_bordered_bg)
            return true
        }
    }
    //endregion

    /**
     * Create Update Booking Request Object
     */
    private fun createUpdateBookingRequest(): UpdateBykeaCashBookingRequest {
        return UpdateBykeaCashBookingRequest().apply {
            trip = UpdateBykeaCashBookingRequest.Trip()
            trip?.amount = eTAmount.text.toString().toInt()

            extra_info = UpdateBykeaCashBookingRequest.ExtraInfo()
            normalCallData?.serviceCode.let {
                when (it!!) {
                    MOBILE_TOP_UP -> {
                        extra_info?.telco_name = normalCallData?.extraParams?.telco_name
                        extra_info?.phone = eTMobileNumber.text.toString()
                    }
                    MOBILE_WALLET -> {
                        extra_info?.vendor_name = normalCallData?.extraParams?.vendor_name
                        extra_info?.phone = eTMobileNumber.text.toString()
                        extra_info?.cnic = eTCNIC.text.toString()
                    }
                    BANK_TRANSFER -> {
                        extra_info?.iban = eTIBAN.text.toString()
                    }
                    UTILITY -> {
                        extra_info?.bill_company_name = normalCallData?.extraParams?.bill_company_name
                        extra_info?.account_number = eTAccountNumber.text.toString()
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: NormalCallData) = BykeaCashFormFragment().apply { arguments = Bundle().apply { putParcelable(ARG_PARAM1, param1) } }
    }
}