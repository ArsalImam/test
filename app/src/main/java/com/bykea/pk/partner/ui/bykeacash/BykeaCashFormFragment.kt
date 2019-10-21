package com.bykea.pk.partner.ui.bykeacash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.bykea.pk.partner.R
import com.bykea.pk.partner.databinding.FragmentBykeaCashFormBinding
import com.bykea.pk.partner.models.response.NormalCallData
import com.bykea.pk.partner.ui.activities.BookingActivity
import com.bykea.pk.partner.ui.common.obtainViewModel
import com.bykea.pk.partner.ui.complain.GenericFragmentListener
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.utils.Constants.ServiceCode.*
import com.bykea.pk.partner.utils.Dialogs
import com.bykea.pk.partner.utils.Utils
import kotlinx.android.synthetic.main.activity_save_place.*
import kotlinx.android.synthetic.main.fragment_bykea_cash_form.*

private const val ARG_PARAM1 = "param1"

class BykeaCashFormFragment : DialogFragment() {
    private lateinit var binding: FragmentBykeaCashFormBinding
    private lateinit var mCurrentActivity: BookingActivity
    private var normalCallData: NormalCallData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { normalCallData = it.getParcelable(ARG_PARAM1) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bykea_cash_form, container, false)
        mCurrentActivity = activity as BookingActivity
        binding.lifecycleOwner = this
        binding.listener = object : GenericFragmentListener {
            override fun onUpdateDetails() {
                Dialogs.INSTANCE.showLoader(mCurrentActivity)
                binding.viewmodel?.updateFormDetails()
                isValidate(normalCallData?.serviceCode)
            }

            override fun onCancelDialog() {
                fragmentManager?.popBackStack()
            }
        }

        binding.viewmodel = obtainViewModel(BykeaCashFormViewModel::class.java).apply {
            callData.value = normalCallData
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        normalCallData?.serviceCode = 28
        setFormVisibilityForServiceCode(normalCallData?.serviceCode)
    }

    /**
     * Set Form Visiblity Using Service Code
     * @param serviceCode: Receive In Get Active Trip Call (Ride Data)
     */
    private fun setFormVisibilityForServiceCode(serviceCode: Int?) {
        serviceCode.let {
            when (it!!) {
                MOBILE_TOP_UP -> {
                    linLayoutMobileNumber.visibility = View.VISIBLE
                }
                MOBILE_WALLET -> {
                    linLayoutCNIC.visibility = View.VISIBLE
                    linLayoutMobileNumber.visibility = View.VISIBLE
                }
                BANK_TRANSFER -> {
                    linLayoutIBAN.visibility = View.VISIBLE
                }
                UTILITY -> {
                    linLayoutAccountNumber.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun isValidate(serviceCode: Int?): Boolean {
        serviceCode.let {
            when (it!!) {
                MOBILE_TOP_UP -> {
                    if (eTMobileNumber.text.isNullOrEmpty()) {
                        eTMobileNumber.requestFocus()
                        eTMobileNumber.setBackgroundResource(R.drawable.red_bordered_bg)
                        return false
                    } else if (!Utils.isValidNumber(mCurrentActivity, etMobileNumber)) {
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
                    } else if (eTCNIC.text.toString().length < 13) {
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
            checkAmountValue();
        }
        return true
    }

    private fun checkAmountValue(): Boolean {
        if (eTAmount.text.isNullOrEmpty()) {
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @param param1 Parameter 1.
         * @return A new instance of fragment BlankFragment.
         */
        @JvmStatic
        fun newInstance(param1: NormalCallData) =
                BykeaCashFormFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_PARAM1, param1)
                    }
                }
    }
}
