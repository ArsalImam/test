package com.bykea.pk.partner.ui.support


import android.os.Bundle
import androidx.fragment.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.bykea.pk.partner.R
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
import com.bykea.pk.partner.utils.Constants
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

        binding.listener = object : GenericFragmentListener {
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
            if (StringUtils.isBlank(etDetails!!.text!!.toString().trim { it <= ' ' })) {
                setError(etDetails!!, "Please Enter Some Details")
                return false
            }
            return true
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
        val customerFields = ArrayList<CustomField>()
        customerFields.add(CustomField(Constants.ZendeskCustomFields.Subject, ""));
        customerFields.add(CustomField(Constants.ZendeskCustomFields.Description, ""));
        customerFields.add(CustomField(Constants.ZendeskCustomFields.Status, ""));
        customerFields.add(CustomField(Constants.ZendeskCustomFields.Type, ""));
        customerFields.add(CustomField(Constants.ZendeskCustomFields.Priority, ""));
        customerFields.add(CustomField(Constants.ZendeskCustomFields.Group, ""));
        customerFields.add(CustomField(Constants.ZendeskCustomFields.Assignee, ""));
        customerFields.add(CustomField(Constants.ZendeskCustomFields.Booking_ID, mCurrentActivity?.tripHistoryDate?.trip_id));
        customerFields.add(CustomField(Constants.ZendeskCustomFields.Customer_Number, ""));
        customerFields.add(CustomField(Constants.ZendeskCustomFields.Receivers_Number, ""));
        customerFields.add(CustomField(Constants.ZendeskCustomFields.Receivers_Name, ""));
        customerFields.add(CustomField(Constants.ZendeskCustomFields.Trip_Time, ""));
        customerFields.add(CustomField(Constants.ZendeskCustomFields.Cancelled_by, mCurrentActivity?.tripHistoryDate?.cancel_by));
        customerFields.add(CustomField(Constants.ZendeskCustomFields.Cancellation_Reason, ""));
        customerFields.add(CustomField(Constants.ZendeskCustomFields.Wallet_Deduction, mCurrentActivity?.tripHistoryDate?.invoice?.wallet_deduction));
        customerFields.add(CustomField(Constants.ZendeskCustomFields.Customer_Penalty_Amount, ""));
        customerFields.add(CustomField(Constants.ZendeskCustomFields.Partner_Penalty_Amount, ""));
        customerFields.add(CustomField(Constants.ZendeskCustomFields.Distance_to_Pickup, ""));
        customerFields.add(CustomField(Constants.ZendeskCustomFields.Customer_Name, mCurrentActivity?.tripHistoryDate?.passenger?.name));
        customerFields.add(CustomField(Constants.ZendeskCustomFields.Partner_Number, AppPreferences.getPilotData().phoneNo));
        customerFields.add(CustomField(Constants.ZendeskCustomFields.Partner_Name, AppPreferences.getPilotData().fullName));
        customerFields.add(CustomField(Constants.ZendeskCustomFields.Partner_Email, AppPreferences.getDriverEmail()));
        customerFields.add(CustomField(Constants.ZendeskCustomFields.Booking_Type, mCurrentActivity?.tripHistoryDate?.trip_type));
        customerFields.add(CustomField(Constants.ZendeskCustomFields.Parcel_Value, mCurrentActivity?.tripHistoryDate?.trip_type));
        customerFields.add(CustomField(Constants.ZendeskCustomFields.COD_Amount, ""));
        customerFields.add(CustomField(Constants.ZendeskCustomFields.Trip_Fare, mCurrentActivity?.tripHistoryDate?.invoice?.tripCharges));
        customerFields.add(CustomField(Constants.ZendeskCustomFields.Trip_Distance, ""));
        customerFields.add(CustomField(Constants.ZendeskCustomFields.Trip_Start_Address, mCurrentActivity?.tripHistoryDate?.startAddress));
        customerFields.add(CustomField(Constants.ZendeskCustomFields.Trip_End_Address, mCurrentActivity?.tripHistoryDate?.endAddress));
        customerFields.add(CustomField(Constants.ZendeskCustomFields.Received_Amount, ""));
        customerFields.add(CustomField(Constants.ZendeskCustomFields.Wait_Time, mCurrentActivity?.tripHistoryDate?.invoice?.waitMins));
        customerFields.add(CustomField(Constants.ZendeskCustomFields.Problem_Topic_Selected, ""));
        customerFields.add(CustomField(Constants.ZendeskCustomFields.Last_Trip_Status, ""));
        customerFields.add(CustomField(Constants.ZendeskCustomFields.Testing, ""));

        return customerFields
    }
}
