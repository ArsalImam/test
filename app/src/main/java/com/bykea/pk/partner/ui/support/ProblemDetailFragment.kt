package com.bykea.pk.partner.ui.support


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bykea.pk.partner.R
import com.bykea.pk.partner.databinding.FragmentProblemDetailBinding
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.utils.Constants
import com.bykea.pk.partner.utils.Dialogs
import com.bykea.pk.partner.utils.Utils
import com.zendesk.service.ErrorResponse
import com.zendesk.service.ZendeskCallback
import kotlinx.android.synthetic.main.fragment_problem_detail.*
import org.apache.commons.lang3.StringUtils
import zendesk.support.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class ProblemDetailFragment : Fragment() {
    private var mCurrentActivity: ProblemActivity? = null
    private var requestProvider: RequestProvider? = null
    private lateinit var rootView: View
    private lateinit var binding: FragmentProblemDetailBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_problem_detail, container, false)
        rootView = binding.root
        mCurrentActivity = activity as ProblemActivity?

        requestProvider = Support.INSTANCE.provider()?.requestProvider()

        binding.listener = object : GenericFragmentListener {
            override fun onSubmitClicked() {
                if (isValid) {
                    createRequest()
                }
            }
        }
        return rootView
    }

    /**
     * Check Is Details Are Empty Or Not
     */
    private val isValid: Boolean
        get() {
            if (StringUtils.isBlank(etDetails?.text?.toString()?.trim { it <= ' ' })) {
                etDetails.error = "Please Enter Some Details"
                etDetails.requestFocus()
                return false
            }
            return true
        }

    /**
     * Create The Request To Get All The Submitted Tickets For Zendesk
     */
    private fun createRequest() {
        Dialogs.INSTANCE.showLoader(mCurrentActivity)

        requestProvider?.createRequest(buildCreateRequest(), object : ZendeskCallback<Request>() {
            override fun onSuccess(request: Request) {
                Dialogs.INSTANCE.dismissDialog()
                Utils.appToastDebug(mCurrentActivity, "Zendesk(createRequest) - onSuccess")
                mCurrentActivity?.changeFragment(ProblemSubmittedFragment());
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
        val createRequest = CreateRequest()
        createRequest.subject = mCurrentActivity?.tripHistoryDate?.tripNo
        createRequest.description = etDetails.text.toString()
        createRequest.customFields = buildCustomFields()

        return createRequest
    }


    /**
     * Generate Custom Fields For Ticket For Zendesk
     */
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
        customerFields.add(CustomField(Constants.ZendeskCustomFields.Parcel_Value, ""));
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
