package com.bykea.pk.partner.ui.support


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bykea.pk.partner.R
import com.bykea.pk.partner.databinding.FragmentComplainDetailBinding
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.utils.Constants
import com.bykea.pk.partner.utils.Dialogs
import com.bykea.pk.partner.utils.Utils
import com.google.android.gms.common.util.Strings
import com.google.android.gms.common.util.Strings.emptyToNull
import com.google.android.gms.common.util.Strings.isEmptyOrWhitespace
import com.zendesk.service.ErrorResponse
import com.zendesk.service.ZendeskCallback
import kotlinx.android.synthetic.main.fragment_complain_detail.*
import org.apache.commons.lang3.StringUtils
import zendesk.support.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class ComplainDetailFragment : Fragment() {
    private var mCurrentActivity: ComplaintSubmissionActivity? = null
    private lateinit var requestProvider: RequestProvider

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: FragmentComplainDetailBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_complain_detail, container, false)
        mCurrentActivity = activity as ComplaintSubmissionActivity?

        requestProvider = Support.INSTANCE.provider()!!.requestProvider()

        binding.listener = object : GenericFragmentListener {
            override fun onSubmitClicked() {
                if (isValid)
                    createRequest()
            }
        }
        return binding.root
    }

    /**
     * Check Is Details Are Empty Or Not
     */
    private val isValid: Boolean
        get() {
            if (isEmptyOrWhitespace(etDetails.text.toString().trim())) {
                etDetails.error = "Please Enter Some Details"
                etDetails.requestFocus()
                return false
            }
            return true
        }

    private fun createRequest() {
        Dialogs.INSTANCE.showLoader(mCurrentActivity)

        requestProvider.createRequest(buildCreateRequest(), object : ZendeskCallback<Request>() {
            override fun onSuccess(request: Request) {
                Dialogs.INSTANCE.dismissDialog()
                Utils.appToastDebug(mCurrentActivity, "Zendesk(createRequest) - onSuccess")
                mCurrentActivity?.changeFragment(ComplainSubmittedFragment());
            }

            override fun onError(errorResponse: ErrorResponse) {
                Dialogs.INSTANCE.dismissDialog()
                Utils.appToastDebug(mCurrentActivity, "Zendesk(createRequest) - onError")
            }
        })
    }

    /**
     * Genereate Create Request Body For Zendesk
     */
    private fun buildCreateRequest(): CreateRequest {
        return CreateRequest().apply {
            subject = mCurrentActivity?.tripHistoryDate?.tripNo
            description = etDetails.text.toString()
            customFields = buildCustomFields()
        }
    }

    /**
     * Generate Custom Fields For Ticket For Zendesk
     */
    private fun buildCustomFields(): List<CustomField> {
        return ArrayList<CustomField>().apply {
            add(CustomField(Constants.ZendeskCustomFields.Subject, mCurrentActivity?.tripHistoryDate?.tripNo))
            add(CustomField(Constants.ZendeskCustomFields.Description, etDetails.text.toString()))
            add(CustomField(Constants.ZendeskCustomFields.Status, ""))
            add(CustomField(Constants.ZendeskCustomFields.Type, ""))
            add(CustomField(Constants.ZendeskCustomFields.Priority, ""))
            add(CustomField(Constants.ZendeskCustomFields.Group, ""))
            add(CustomField(Constants.ZendeskCustomFields.Assignee, ""))
            add(CustomField(Constants.ZendeskCustomFields.Booking_ID, mCurrentActivity?.tripHistoryDate?.trip_id))
            add(CustomField(Constants.ZendeskCustomFields.Customer_Number, ""))
            add(CustomField(Constants.ZendeskCustomFields.Receivers_Number, ""))
            add(CustomField(Constants.ZendeskCustomFields.Receivers_Name, ""))
            add(CustomField(Constants.ZendeskCustomFields.Trip_Time, ""))
            add(CustomField(Constants.ZendeskCustomFields.Cancelled_by, mCurrentActivity?.tripHistoryDate?.cancel_by))
            add(CustomField(Constants.ZendeskCustomFields.Cancellation_Reason, mCurrentActivity?.selectedReason))
            add(CustomField(Constants.ZendeskCustomFields.Wallet_Deduction, mCurrentActivity?.tripHistoryDate?.invoice?.wallet_deduction))
            add(CustomField(Constants.ZendeskCustomFields.Customer_Penalty_Amount, ""))
            add(CustomField(Constants.ZendeskCustomFields.Partner_Penalty_Amount, ""))
            add(CustomField(Constants.ZendeskCustomFields.Distance_to_Pickup, ""))
            add(CustomField(Constants.ZendeskCustomFields.Customer_Name, mCurrentActivity?.tripHistoryDate?.passenger?.name))
            add(CustomField(Constants.ZendeskCustomFields.Partner_Number, AppPreferences.getPilotData().phoneNo))
            add(CustomField(Constants.ZendeskCustomFields.Partner_Name, AppPreferences.getPilotData().fullName))
            add(CustomField(Constants.ZendeskCustomFields.Partner_Email, AppPreferences.getDriverEmail()))
            add(CustomField(Constants.ZendeskCustomFields.Booking_Type, mCurrentActivity?.tripHistoryDate?.trip_type))
            add(CustomField(Constants.ZendeskCustomFields.Parcel_Value, ""))
            add(CustomField(Constants.ZendeskCustomFields.COD_Amount, ""))
            add(CustomField(Constants.ZendeskCustomFields.Trip_Fare, mCurrentActivity?.tripHistoryDate?.invoice?.tripCharges))
            add(CustomField(Constants.ZendeskCustomFields.Trip_Distance, ""))
            add(CustomField(Constants.ZendeskCustomFields.Trip_Start_Address, mCurrentActivity?.tripHistoryDate?.startAddress))
            add(CustomField(Constants.ZendeskCustomFields.Trip_End_Address, mCurrentActivity?.tripHistoryDate?.endAddress))
            add(CustomField(Constants.ZendeskCustomFields.Received_Amount, ""))
            add(CustomField(Constants.ZendeskCustomFields.Wait_Time, mCurrentActivity?.tripHistoryDate?.invoice?.waitMins))
            add(CustomField(Constants.ZendeskCustomFields.Problem_Topic_Selected, ""))
            add(CustomField(Constants.ZendeskCustomFields.Last_Trip_Status, ""))
        }
    }
}
