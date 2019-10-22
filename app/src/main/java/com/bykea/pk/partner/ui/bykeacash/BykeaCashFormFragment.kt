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
import com.bykea.pk.partner.dal.source.remote.request.UpdateBookingRequest
import com.bykea.pk.partner.databinding.FragmentBykeaCashFormBinding
import com.bykea.pk.partner.models.response.NormalCallData
import com.bykea.pk.partner.ui.activities.BookingActivity
import com.bykea.pk.partner.ui.common.obtainViewModel
import com.bykea.pk.partner.ui.complain.GenericFragmentListener
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.ui.helpers.FontUtils
import com.bykea.pk.partner.utils.Constants.MAX_LENGTH_CNIC
import com.bykea.pk.partner.utils.Constants.ServiceCode.*
import com.bykea.pk.partner.utils.Dialogs
import com.bykea.pk.partner.utils.Utils
import kotlinx.android.synthetic.main.fragment_bykea_cash_form.*
import android.content.DialogInterface
import android.view.WindowManager
import com.bykea.pk.partner.utils.Constants.MAX_LENGTH_IBAN


private const val ARG_PARAM1 = "param1"

class BykeaCashFormFragment : DialogFragment() {
    private lateinit var binding: FragmentBykeaCashFormBinding
    private lateinit var mCurrentActivity: BookingActivity
    private var normalCallData: NormalCallData? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mCurrentActivity = activity as BookingActivity
        setStyle(STYLE_NO_TITLE, R.style.AppTheme_Dialog_Custom)
        arguments?.let { normalCallData = it.getParcelable(ARG_PARAM1) }
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
        }

        setListeners()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFormDataAndVisibility(normalCallData?.serviceCode)
        setFormSpannableStrings()
        setTextChangeListeners()
    }

    /**
     * Set Listeners
     */
    private fun setListeners() {
        binding.listener = object : GenericFragmentListener {
            override fun onUpdateDetails() {
                if (isValidate(normalCallData?.serviceCode)) {
                    Dialogs.INSTANCE.showLoader(mCurrentActivity)
                    binding.viewmodel?.updateFormDetails(normalCallData?.tripId!!, createUpdateBookingRequest())
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
                    eTMobileNumber.setText(normalCallData?.extraParams?.phone)
                }
                MOBILE_WALLET -> {
                    linLayoutCNIC.visibility = View.VISIBLE
                    linLayoutMobileNumber.visibility = View.VISIBLE
                    eTCNIC.setText(normalCallData?.extraParams?.cnic)
                    eTMobileNumber.setText(normalCallData?.extraParams?.phone)
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
                SpannableStringBuilder(FontUtils.getStyledTitle(mCurrentActivity, context?.getString(R.string.iban_1), "jameel_noori_nastaleeq.ttf"))
                        .append(FontUtils.getStyledTitle(mCurrentActivity, context?.getString(R.string.iban_2), "roboto_medium.ttf"))

        tVCNICError.text =
                SpannableStringBuilder(FontUtils.getStyledTitle(mCurrentActivity, context?.getString(R.string.cnic_error1), "roboto_medium.ttf"))
                        .append(FontUtils.getStyledTitle(mCurrentActivity, context?.getString(R.string.cnic_error2), "jameel_noori_nastaleeq.ttf"))

        tVMobileNumberError.text =
                SpannableStringBuilder(FontUtils.getStyledTitle(mCurrentActivity, context?.getString(R.string.mobile_number_error1), "roboto_medium.ttf"))
                        .append(FontUtils.getStyledTitle(mCurrentActivity, context?.getString(R.string.mobile_number_error2), "jameel_noori_nastaleeq.ttf"))

        tVIBANError.text =
                SpannableStringBuilder(FontUtils.getStyledTitle(mCurrentActivity, context?.getString(R.string.iban_error1), "roboto_medium.ttf"))
                        .append(FontUtils.getStyledTitle(mCurrentActivity, context?.getString(R.string.iban_error2), "jameel_noori_nastaleeq.ttf"))
                        .append(FontUtils.getStyledTitle(mCurrentActivity, context?.getString(R.string.iban_error3), "roboto_medium.ttf"))
                        .append(FontUtils.getStyledTitle(mCurrentActivity, context?.getString(R.string.iban_error4), "jameel_noori_nastaleeq.ttf"))

        tVAmountError.text =
                SpannableStringBuilder(FontUtils.getStyledTitle(mCurrentActivity, context?.getString(R.string.amount_error1), "jameel_noori_nastaleeq.ttf"))
                        .append(FontUtils.getStyledTitle(mCurrentActivity, AppPreferences.getSettings().settings.bykeaCashMaxAmount.toString(), "roboto_medium.ttf"))
                        .append(FontUtils.getStyledTitle(mCurrentActivity, context?.getString(R.string.amount_error2), "jameel_noori_nastaleeq.ttf"))
    }

    /**
     * Set Texh Change Listeners For Fields
     */
    private fun setTextChangeListeners() {
        eTAccountNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (eTAccountNumber.text.isNullOrEmpty()) {
                    eTAccountNumber.requestFocus()
                    eTAccountNumber.setBackgroundResource(R.drawable.red_bordered_bg)
                } else {
                    eTAccountNumber.setBackgroundResource(R.drawable.gray_bordered_bg)
                }
            }
        })
        eTCNIC.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty() || (!s.isNullOrEmpty() && s.length < MAX_LENGTH_CNIC)) {
                    eTCNIC.requestFocus()
                    tVCNICError.visibility = View.VISIBLE
                    eTCNIC.setBackgroundResource(R.drawable.red_bordered_bg)
                } else {
                    tVCNICError.visibility = View.GONE
                    eTCNIC.setBackgroundResource(R.drawable.gray_bordered_bg)
                }
            }
        })
        eTIBAN.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty() || (!s.isNullOrEmpty() && s.length < MAX_LENGTH_IBAN)) {
                    eTIBAN.requestFocus()
                    tVIBANError.visibility = View.VISIBLE
                    eTIBAN.setBackgroundResource(R.drawable.red_bordered_bg)
                } else {
                    tVIBANError.visibility = View.GONE
                    eTIBAN.setBackgroundResource(R.drawable.gray_bordered_bg)
                }
            }
        })
        eTMobileNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!Utils.isValidNumber(eTMobileNumber)) {
                    eTMobileNumber.requestFocus()
                    tVMobileNumberError.visibility = View.VISIBLE
                    eTMobileNumber.setBackgroundResource(R.drawable.red_bordered_bg)
                } else {
                    tVMobileNumberError.visibility = View.GONE
                    eTMobileNumber.setBackgroundResource(R.drawable.gray_bordered_bg)
                }
            }
        })
        eTAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty() ||
                        (!s.isNullOrEmpty() && s.toString().toInt() == 0) ||
                        (!s.isNullOrEmpty() && s.toString().toInt() > AppPreferences.getSettings().settings.bykeaCashMaxAmount)) {
                    eTAmount.requestFocus()
                    tVAmountError.visibility = View.VISIBLE
                    eTAmount.setBackgroundResource(R.drawable.red_bordered_bg)
                } else {
                    tVAmountError.visibility = View.GONE
                    eTAmount.setBackgroundResource(R.drawable.gray_bordered_bg)
                }
            }
        })
    }

    /**
     * Perform Form Validation Using Service Code
     */
    private fun isValidate(serviceCode: Int?): Boolean {
        serviceCode.let {
            when (it!!) {
                MOBILE_TOP_UP -> {
                    return Utils.isValidNumber(eTMobileNumber)
                }
                MOBILE_WALLET -> {
                    return ((!eTCNIC.text.isNullOrEmpty() && eTCNIC.text.toString().length < MAX_LENGTH_CNIC) && Utils.isValidNumber(eTMobileNumber))
                }
                BANK_TRANSFER -> {
                    return !eTIBAN.text.isNullOrEmpty() && eTIBAN.text.toString().length < MAX_LENGTH_IBAN
                }
                UTILITY -> {
                    return !eTAccountNumber.text.isNullOrEmpty()
                }
                else -> {
                }
            }
        }
        return (!eTAmount.text.isNullOrEmpty() &&
                eTAmount.text.toString().toInt() > 0 &&
                eTAmount.text.toString().toInt() <= AppPreferences.getSettings().settings.bykeaCashMaxAmount)
    }

    /**
     * Create Update Booking Request Object
     */
    private fun createUpdateBookingRequest(): UpdateBookingRequest {
        return UpdateBookingRequest().apply {
            trip = UpdateBookingRequest.Trip()
            trip?.amount = eTAmount.text.toString().toInt()

            extra_info = UpdateBookingRequest.ExtraInfo()
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
