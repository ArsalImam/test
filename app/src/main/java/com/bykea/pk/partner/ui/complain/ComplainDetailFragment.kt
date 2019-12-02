package com.bykea.pk.partner.ui.complain


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.util.COMPLAIN_WRONGE_FARE_CALCULATION
import com.bykea.pk.partner.dal.util.isFilledEditText
import com.bykea.pk.partner.databinding.FragmentComplainDetailBinding
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.utils.Constants
import com.bykea.pk.partner.utils.Dialogs
import com.bykea.pk.partner.utils.Utils
import com.zendesk.service.ErrorResponse
import com.zendesk.service.ZendeskCallback
import kotlinx.android.synthetic.main.fragment_complain_detail.*
import org.apache.commons.lang3.StringUtils
import zendesk.support.*
import java.util.*

/**
 * Fragment Used To Get The Input Details Regarding Ticket
 */
class ComplainDetailFragment : Fragment() {
    private var mCurrentActivity: ComplaintSubmissionActivity? = null
    private var requestProvider: RequestProvider? = null
    private var ticketSubject: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: FragmentComplainDetailBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_complain_detail, container, false)
        mCurrentActivity = activity as ComplaintSubmissionActivity?

        requestProvider = Support.INSTANCE.provider()?.requestProvider()

        binding.listener = object : GenericFragmentListener {
            override fun onSubmitClicked() {
                if (isValid)
                    createRequest()
            }
        }

        if (mCurrentActivity?.tripHistoryDate != null) {
            //CREATE TICKET FOR RIDE REASONS
            ticketSubject = mCurrentActivity?.tripHistoryDate?.tripNo
        } else {
            //CREATE TICKET FOR FINANCIAL AND SUPERVISOR REASONS
            ticketSubject = AppPreferences.getPilotData().id
        }
        val code = mCurrentActivity?.selectedReason?.code
        binding.wrongFareLayout.visibility = if (StringUtils.isEmpty(code) || code != COMPLAIN_WRONGE_FARE_CALCULATION) {
            View.GONE
        } else {
            View.VISIBLE
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editText_lay.setOnClickListener { }
    }

    /**
     * Check Is Details Are Empty Or Not
     */
    private val isValid: Boolean
        get() {
            val errorToShow = getString(R.string.enter_some_details)

            var isValid = etDetails.isFilledEditText(errorToShow)
            if (StringUtils.isNotEmpty(mCurrentActivity?.selectedReason?.code)) {
                if (mCurrentActivity?.selectedReason?.code == COMPLAIN_WRONGE_FARE_CALCULATION) {
                    isValid =
                            editTextPickUpAddress.isFilledEditText(errorToShow) &&
                                    edittextDropOffAddress.isFilledEditText(errorToShow) &&
                                    editTextStops.isFilledEditText(errorToShow) &&
                                    editTextKilometersTravelled.isFilledEditText(errorToShow) &&
                                    editTextBookingTime.isFilledEditText(errorToShow) &&
                                    editTextPaidAmount.isFilledEditText(errorToShow) &&
                                    editTextPaidAmount.isFilledEditText(errorToShow)
                    try {
                        if (editTextKilometersTravelled.text.toString().toInt() > Constants.MAX_COMPLAIN_KILOMETRES_TRAVELLED) {
                            editTextKilometersTravelled.error = getString(R.string.error_kilometres_msg)
                            editTextKilometersTravelled.requestFocus()
                            isValid = false
                        }

                        if (editTextBookingTime.text.toString().toInt() > Constants.MAX_COMPLAIN_MINIUTES_TRAVELLED) {
                            editTextBookingTime.error = getString(R.string.error_booking_time_msg)
                            editTextBookingTime.requestFocus()
                            isValid = false
                        }
                    } catch (e: NumberFormatException) {
                        isValid = false
                    }
                }
            }
            return isValid
        }

    /**
     * Get All Submitted Tickets For Zendesk
     */
    private fun createRequest() {
        Dialogs.INSTANCE.showLoader(mCurrentActivity)
        requestProvider.let {
            requestProvider?.createRequest(buildCreateRequest(), object : ZendeskCallback<Request>() {
                override fun onSuccess(request: Request) {
                    Dialogs.INSTANCE.dismissDialog()
                    mCurrentActivity?.isTicketSubmitted = true
                    mCurrentActivity?.changeFragment(ComplainSubmittedFragment());
                }

                override fun onError(errorResponse: ErrorResponse) {
                    Utils.setZendeskIdentity()
                    Dialogs.INSTANCE.dismissDialog()
                    Utils.appToast(getString(R.string.error_try_again))
                }
            })
        } ?: run {
            Utils.setZendeskIdentity()
            Dialogs.INSTANCE.dismissDialog()
            Utils.appToast(getString(R.string.error_try_again))
        }
    }

    /**
     * Genereate Create Request Body For Zendesk
     */
    private fun buildCreateRequest(): CreateRequest {
        return CreateRequest().apply {
            subject = ticketSubject
            description = etDetails.text.toString()
            customFields = buildCustomFields()
        }
    }

    /**
     * Generate Custom Fields For Ticket For Zendesk
     */
    private fun buildCustomFields(): List<CustomField> {
        return ArrayList<CustomField>().apply {
            add(CustomField(Constants.ZendeskCustomFields.Subject, ticketSubject))
            add(CustomField(Constants.ZendeskCustomFields.Description, etDetails.text.toString()))
            add(CustomField(Constants.ZendeskCustomFields.Partner_Number, AppPreferences.getPilotData().phoneNo))
            add(CustomField(Constants.ZendeskCustomFields.Partner_Name, AppPreferences.getPilotData().fullName))
            add(CustomField(Constants.ZendeskCustomFields.Partner_Email, AppPreferences.getDriverEmail()))
            add(CustomField(Constants.ZendeskCustomFields.Problem_Topic_Selected, mCurrentActivity?.selectedReason?.message!!))

            if (mCurrentActivity?.selectedReason?.code!! == COMPLAIN_WRONGE_FARE_CALCULATION) {
                add(CustomField(Constants.ZendeskCustomFields.Wrong_Fare_Pick_Up_Address, editTextPickUpAddress.text.toString()))
                add(CustomField(Constants.ZendeskCustomFields.Wrong_Fare_Drop_Off_Address, edittextDropOffAddress.text.toString()))
                add(CustomField(Constants.ZendeskCustomFields.Wrong_Fare_Stops, editTextStops.text.toString()))
                add(CustomField(Constants.ZendeskCustomFields.Wrong_Fare_Kilometers_Travelled, editTextKilometersTravelled.text.toString()))
                add(CustomField(Constants.ZendeskCustomFields.Wrong_Fare_Booking_Time, editTextBookingTime.text.toString()))
                add(CustomField(Constants.ZendeskCustomFields.Wrong_Fare_Paid_Amount, editTextPaidAmount.text.toString()))
            }

            mCurrentActivity?.tripHistoryDate.let {
                add(CustomField(Constants.ZendeskCustomFields.Booking_Type, mCurrentActivity?.tripHistoryDate?.trip_type))
                add(CustomField(Constants.ZendeskCustomFields.Trip_Start_Address, mCurrentActivity?.tripHistoryDate?.startAddress))
                add(CustomField(Constants.ZendeskCustomFields.Trip_End_Address, mCurrentActivity?.tripHistoryDate?.endAddress))

                if (!mCurrentActivity?.tripHistoryDate?.tripNo.isNullOrEmpty())
                    add(CustomField(Constants.ZendeskCustomFields.Booking_ID, mCurrentActivity?.tripHistoryDate?.tripNo))
                if (!mCurrentActivity?.tripHistoryDate?.cancelBy.isNullOrEmpty()) {
                    add(CustomField(Constants.ZendeskCustomFields.Cancelled_by, mCurrentActivity?.tripHistoryDate?.cancelBy))
                    when (mCurrentActivity?.tripHistoryDate?.cancelBy?.toLowerCase()) {
                        mCurrentActivity?.getString(R.string.partner_label) -> {
                            add(CustomField(Constants.ZendeskCustomFields.Partner_Penalty_Amount, mCurrentActivity?.tripHistoryDate?.cancel_fee))
                        }
                        mCurrentActivity?.getString(R.string.customer_labee) -> {
                            add(CustomField(Constants.ZendeskCustomFields.Customer_Penalty_Amount, mCurrentActivity?.tripHistoryDate?.cancel_fee))
                        }
                    }
                }
                mCurrentActivity?.tripHistoryDate?.invoice.let {
                    if (!mCurrentActivity?.tripHistoryDate?.invoice?.wallet_deduction.isNullOrEmpty())
                        add(CustomField(Constants.ZendeskCustomFields.Wallet_Deduction, mCurrentActivity?.tripHistoryDate?.invoice?.wallet_deduction))
                    if (!mCurrentActivity?.tripHistoryDate?.invoice?.tripCharges.isNullOrEmpty())
                        add(CustomField(Constants.ZendeskCustomFields.Trip_Fare, mCurrentActivity?.tripHistoryDate?.invoice?.tripCharges))
                    if (!mCurrentActivity?.tripHistoryDate?.invoice?.waitMins.isNullOrEmpty())
                        add(CustomField(Constants.ZendeskCustomFields.Wait_Time, mCurrentActivity?.tripHistoryDate?.invoice?.waitMins))
                    if (!mCurrentActivity?.tripHistoryDate?.invoice?.km.isNullOrEmpty())
                        add(CustomField(Constants.ZendeskCustomFields.Trip_Distance, mCurrentActivity?.tripHistoryDate?.invoice?.km))
                    if (!mCurrentActivity?.tripHistoryDate?.invoice?.minutes.isNullOrEmpty())
                        add(CustomField(Constants.ZendeskCustomFields.Trip_Time, mCurrentActivity?.tripHistoryDate?.invoice?.minutes))
                }

                mCurrentActivity?.tripHistoryDate?.passenger.let {
                    if (!mCurrentActivity?.tripHistoryDate?.passenger?.name.isNullOrEmpty())
                        add(CustomField(Constants.ZendeskCustomFields.Customer_Name, mCurrentActivity?.tripHistoryDate?.passenger?.name))
                }
            }
            //WILL SEE LATER ON
//            add(CustomField(Constants.ZendeskCustomFields.COD_Amount, StringUtils.EMPTY))
//            add(CustomField(Constants.ZendeskCustomFields.Received_Amount, StringUtils.EMPTY))

            //NOT RECEIVING FROM API
//            add(CustomField(Constants.ZendeskCustomFields.Customer_Number,StringUtils.EMPTY))
//            add(CustomField(Constants.ZendeskCustomFields.Cancellation_Reason, StringUtils.EMPTY))

//            add(CustomField(Constants.ZendeskCustomFields.Receivers_Name, StringUtils.EMPTY))
//            add(CustomField(Constants.ZendeskCustomFields.Receivers_Number, StringUtils.EMPTY))
//            add(CustomField(Constants.ZendeskCustomFields.Distance_to_Pickup, StringUtils.EMPTY))
//            add(CustomField(Constants.ZendeskCustomFields.Last_Trip_Status, StringUtils.EMPTY))
//            add(CustomField(Constants.ZendeskCustomFields.Parcel_Value, StringUtils.EMPTY))
        }
    }
}
