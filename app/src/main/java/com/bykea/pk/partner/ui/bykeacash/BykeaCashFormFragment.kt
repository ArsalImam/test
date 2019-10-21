package com.bykea.pk.partner.ui.bykeacash

import android.os.Bundle
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
import com.bykea.pk.partner.utils.Constants.MAX_LENGTH_CNIC
import com.bykea.pk.partner.utils.Constants.ServiceCode.*
import com.bykea.pk.partner.utils.Dialogs
import com.bykea.pk.partner.utils.Utils
import kotlinx.android.synthetic.main.fragment_bykea_cash_form.*

private const val ARG_PARAM1 = "param1"

class BykeaCashFormFragment : DialogFragment() {
    private lateinit var binding: FragmentBykeaCashFormBinding
    private lateinit var mCurrentActivity: BookingActivity
    private var normalCallData: NormalCallData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.AppTheme_Dialog_Custom)
        arguments?.let { normalCallData = it.getParcelable(ARG_PARAM1) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bykea_cash_form, container, false)
        mCurrentActivity = activity as BookingActivity
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
     * Perform Form Validation Using Service Code
     */
    private fun isValidate(serviceCode: Int?): Boolean {
        serviceCode.let {
            when (it!!) {
                MOBILE_TOP_UP -> {
                    if (eTMobileNumber.text.isNullOrEmpty()) {
                        eTMobileNumber.requestFocus()
                        eTMobileNumber.setBackgroundResource(R.drawable.red_bordered_bg)
                        return false
                    } else if (!Utils.isValidNumber(mCurrentActivity, eTMobileNumber)) {
                        eTMobileNumber.requestFocus()
                        eTMobileNumber.setBackgroundResource(R.drawable.red_bordered_bg)
                        return false
                    } else {
                        eTMobileNumber.setBackgroundResource(R.drawable.gray_bordered_bg)
                    }
                }
                MOBILE_WALLET -> {
                    if (eTCNIC.text.isNullOrEmpty()) {
                        eTCNIC.requestFocus()
                        eTCNIC.setBackgroundResource(R.drawable.red_bordered_bg)
                        return false
                    } else if (eTCNIC.text.toString().length < MAX_LENGTH_CNIC) {
                        eTCNIC.setBackgroundResource(R.drawable.red_bordered_bg)
                        eTCNIC.requestFocus()
                        return false
                    } else if (eTMobileNumber.text.isNullOrEmpty()) {
                        eTCNIC.setBackgroundResource(R.drawable.gray_bordered_bg)
                        eTMobileNumber.requestFocus()
                        eTMobileNumber.setBackgroundResource(R.drawable.red_bordered_bg)
                        return false
                    } else if (!Utils.isValidNumber(eTMobileNumber)) {
                        eTCNIC.setBackgroundResource(R.drawable.gray_bordered_bg)
                        eTMobileNumber.requestFocus()
                        eTMobileNumber.setBackgroundResource(R.drawable.red_bordered_bg)
                        return false
                    } else {
                        eTMobileNumber.setBackgroundResource(R.drawable.gray_bordered_bg)
                    }
                }
                BANK_TRANSFER -> {
                    if (eTIBAN.text.isNullOrEmpty()) {
                        eTIBAN.requestFocus()
                        eTIBAN.setBackgroundResource(R.drawable.red_bordered_bg)
                        return false
                    } else {
                        eTIBAN.setBackgroundResource(R.drawable.gray_bordered_bg)
                    }
                }
                UTILITY -> {
                    if (eTAccountNumber.text.isNullOrEmpty()) {
                        eTAccountNumber.requestFocus()
                        eTAccountNumber.setBackgroundResource(R.drawable.red_bordered_bg)
                        return false
                    } else {
                        eTAccountNumber.setBackgroundResource(R.drawable.gray_bordered_bg)
                    }
                }
            }
        }
        return checkAmountValue()
    }

    /**
     * Perform Validation For Amount Value
     */
    private fun checkAmountValue(): Boolean {
        if (eTAmount.text.isNullOrEmpty() || (!eTAmount.text.isNullOrEmpty() && eTAmount.text.toString().toInt() == 0)) {
            eTAmount.requestFocus()
            eTAmount.setBackgroundResource(R.drawable.red_bordered_bg)
            return false
        } else if (eTAmount.text.toString().toInt() > AppPreferences.getSettings().settings.bykeaCashMaxAmount) {
            eTAmount.requestFocus()
            eTAmount.setBackgroundResource(R.drawable.red_bordered_bg)
            return false
        }
        eTAmount.setBackgroundResource(R.drawable.gray_bordered_bg)
        return true
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
