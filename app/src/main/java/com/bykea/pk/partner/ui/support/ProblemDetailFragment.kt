package com.bykea.pk.partner.ui.support


import android.os.Bundle
import androidx.fragment.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.bykea.pk.partner.R
import com.bykea.pk.partner.models.response.ProblemPostResponse
import com.bykea.pk.partner.repositories.UserDataHandler
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.utils.Dialogs
import com.bykea.pk.partner.utils.Utils
import com.bykea.pk.partner.widgets.FontEditText
import com.zendesk.service.ErrorResponse
import com.zendesk.service.ZendeskCallback

import org.apache.commons.lang3.StringUtils

import java.util.ArrayList

import com.bykea.pk.partner.databinding.FragmentProblemDetailBinding
import com.bykea.pk.partner.ui.support.ProblemActivity.Companion.DETAIL_SUBMITTED_FRAGMENT
import kotlinx.android.synthetic.main.fragment_problem_detail.*
import zendesk.support.CreateRequest
import zendesk.support.CustomField
import zendesk.support.Request
import zendesk.support.RequestProvider
import zendesk.support.Support

/**
 * A simple [Fragment] subclass.
 */
class ProblemDetailFragment : Fragment() {
    private var mCurrentActivity: ProblemActivity? = null
    private lateinit var requestProvider: RequestProvider
    private lateinit var rootView: View
    private lateinit var binding: FragmentProblemDetailBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_problem_detail, container, false)
        rootView = binding.root
        mCurrentActivity = activity as ProblemActivity?

        requestProvider = Support.INSTANCE.provider()!!.requestProvider()

        binding.listener = object : ProblemFragmentListener {
            override fun onSubmitClicked() {
                if (isValid) {
                    createRequest()
                }
            }
        }
        return rootView
    }


    private val isValid: Boolean
        get() {
            if (StringUtils.isBlank(etEmail!!.text!!.toString().trim { it <= ' ' })) {
                setError(etEmail!!, "Please Enter Email")
                return false
            }
            if (StringUtils.isNotBlank(etEmail!!.text!!.toString().trim { it <= ' ' }) && !Utils.isValidEmail(etEmail!!.text!!.toString().trim { it <= ' ' })) {
                setError(etEmail!!, "Email address is not valid")
                return false
            }
            if (StringUtils.isBlank(etDetails!!.text!!.toString().trim { it <= ' ' })) {
                setError(etDetails!!, "Please Enter Some Details")
                return false
            }
            return true
        }

    private val mCallBack = object : UserDataHandler() {

        override fun onProblemPosted(response: ProblemPostResponse) {
            mCurrentActivity!!.runOnUiThread {
                if (response.isSuccess) {
                    //                        changeUI();
                }
                Utils.appToastDebug(mCurrentActivity, response.message)
                Dialogs.INSTANCE.dismissDialog()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (StringUtils.isNotBlank(AppPreferences.getPilotData().email)) {
            etEmail!!.setText(AppPreferences.getPilotData().email)
            etDetails!!.requestFocus()
        } else {
            etEmail!!.requestFocus()
        }
    }

    private fun setError(editText: FontEditText, message: String) {
        editText.error = message
        editText.requestFocus()
    }

    private fun createRequest() {
        Dialogs.INSTANCE.showLoader(mCurrentActivity)

        requestProvider.createRequest(buildCreateRequest(), object : ZendeskCallback<Request>() {
            override fun onSuccess(request: Request) {
                Dialogs.INSTANCE.dismissDialog()
                Utils.appToastDebug(mCurrentActivity, "Zendesk(createRequest) - onSuccess")
                mCurrentActivity?.changeFragment(ProblemSubmittedFragment(), DETAIL_SUBMITTED_FRAGMENT);
            }

            override fun onError(errorResponse: ErrorResponse) {
                Dialogs.INSTANCE.dismissDialog()
                Utils.appToastDebug(mCurrentActivity, "Zendesk(createRequest) - onError")
            }
        })

        /*new UserRepository().postProblem(mCurrentActivity,
                mCallBack,
                mCurrentActivity.selectedReason,
                mCurrentActivity.tripId,
                etEmail.getText().toString(),
                "",
                etDetails.getText().toString(),
                false);*/
    }

    private fun getAllRequests() {
        requestProvider.getAllRequests(object : ZendeskCallback<List<Request>>() {
            override fun onSuccess(requests: List<Request>) {
                Utils.appToastDebug(mCurrentActivity, "Zendesk(createRequest) - onSuccess")
            }

            override fun onError(errorResponse: ErrorResponse) {
                Utils.appToastDebug(mCurrentActivity, "Zendesk(createRequest) - onError")
            }
        })
    }

    private fun buildCreateRequest(): CreateRequest {
        val createRequest = CreateRequest()
        createRequest.subject = "Ticket Subject"
        createRequest.description = "Ticket Description"
        createRequest.customFields = buildCustomFields()

        return createRequest
    }

    private fun buildCustomFields(): List<CustomField> {
        return ArrayList()
    }
}
