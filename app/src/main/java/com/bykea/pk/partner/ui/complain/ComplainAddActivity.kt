package com.bykea.pk.partner.ui.complain

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.source.remote.data.ComplainReason
import com.bykea.pk.partner.dal.util.COMPLAIN_WRONGE_FARE_CALCULATION
import com.bykea.pk.partner.dal.util.isFilledEditText
import com.bykea.pk.partner.databinding.FragmentComplainDetailBinding
import com.bykea.pk.partner.models.data.TripHistoryData
import com.bykea.pk.partner.ui.activities.BaseActivity
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.utils.Constants
import com.bykea.pk.partner.utils.Dialogs
import com.bykea.pk.partner.utils.Utils
import com.zendesk.service.ErrorResponse
import com.zendesk.service.ZendeskCallback
import kotlinx.android.synthetic.main.fragment_complain_detail.*
import org.apache.commons.lang3.StringUtils
import zendesk.support.*

class ComplainAddActivity : BaseActivity() {
    var selectedReason: ComplainReason? = null
    var tripHistoryDate: TripHistoryData? = null
    private var requestProvider: RequestProvider? = null
    private var ticketSubject: String? = null


    var binding: FragmentComplainDetailBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.fragment_complain_detail)

        requestProvider = Support.INSTANCE.provider()?.requestProvider()

        binding?.listener = object : GenericFragmentListener {
            override fun onSubmitClicked() {
                if (isValid)
                    createRequest()
            }
        }


        if (intent?.extras != null) {
            if (intent.extras.containsKey(Constants.INTENT_TRIP_HISTORY_DATA))
                tripHistoryDate = intent.getSerializableExtra(Constants.INTENT_TRIP_HISTORY_DATA) as TripHistoryData
        }

        if (tripHistoryDate != null) {
            //CREATE TICKET FOR RIDE REASONS
            ticketSubject = tripHistoryDate?.tripNo
        } else {
            //CREATE TICKET FOR FINANCIAL AND SUPERVISOR REASONS
            ticketSubject = AppPreferences.getPilotData().id
        }
        val code = selectedReason?.code
        binding?.wrongFareLayout?.visibility = if (StringUtils.isEmpty(code) || code != COMPLAIN_WRONGE_FARE_CALCULATION) {
            View.GONE
        } else {
            View.VISIBLE
        }

//        editText_lay.setOnClickListener { }
        Utils.enableScroll(edittextDropOffAddress)

        Utils.enableScroll(editTextPickUpAddress)
        Utils.enableScroll(editTextStops)
    }

    /**
     * Check Is Details Are Empty Or Not
     */
    private val isValid: Boolean
        get() {
            val errorToShow = getString(R.string.enter_some_details)

            var isValid = etDetails.isFilledEditText(errorToShow)
            if (StringUtils.isNotEmpty(selectedReason?.code)) {
                if (selectedReason?.code == COMPLAIN_WRONGE_FARE_CALCULATION) {
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
                    } catch (e: NumberFormatException) {
                        isValid = false
                        editTextKilometersTravelled.error = getString(R.string.error_kilometres_invalid_msg)
                        editTextKilometersTravelled.requestFocus()
                    }

                    try {
                        if (editTextBookingTime.text.toString().toInt() > Constants.MAX_COMPLAIN_MINIUTES_TRAVELLED) {
                            editTextBookingTime.error = getString(R.string.error_booking_time_msg)
                            editTextBookingTime.requestFocus()
                            isValid = false
                        }
                    } catch (e: NumberFormatException) {
                        isValid = false
                        editTextBookingTime.error = getString(R.string.error_booking_time_invalid_msg)
                        editTextBookingTime.requestFocus()
                    }


                    try {
                        if (editTextPaidAmount.text.toString().toInt() > Constants.MAX_COMPLAIN_PAY_AMOUNT) {
                            editTextPaidAmount.error = getString(R.string.error_complain_pay_amount_msg)
                            editTextPaidAmount.requestFocus()
                            isValid = false
                        }
                    } catch (e: NumberFormatException) {
                        isValid = false
                        editTextPaidAmount.error = getString(R.string.error_complain_pay_amount_invalid_msg)
                        editTextPaidAmount.requestFocus()
                    }
                }
            }
            return isValid
        }

    /**
     * Get All Submitted Tickets For Zendesk
     */
    private fun createRequest() {
        Dialogs.INSTANCE.showLoader(this)
        requestProvider.let {
            requestProvider?.createRequest(buildCreateRequest(), object : ZendeskCallback<Request>() {
                override fun onSuccess(request: Request) {
                    Dialogs.INSTANCE.dismissDialog()
                    setResult(Activity.RESULT_OK)
                    finish()
//                    mCurrentActivity?.isTicketSubmitted = true
//                    mCurrentActivity?.changeFragment(ComplainSubmittedFragment());
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
            add(CustomField(Constants.ZendeskCustomFields.Problem_Topic_Selected, selectedReason?.message!!))

            if (selectedReason?.code!! == COMPLAIN_WRONGE_FARE_CALCULATION) {
                add(CustomField(Constants.ZendeskCustomFields.Wrong_Fare_Pick_Up_Address, editTextPickUpAddress.text.toString()))
                add(CustomField(Constants.ZendeskCustomFields.Wrong_Fare_Drop_Off_Address, edittextDropOffAddress.text.toString()))
                add(CustomField(Constants.ZendeskCustomFields.Wrong_Fare_Stops, editTextStops.text.toString()))
                add(CustomField(Constants.ZendeskCustomFields.Wrong_Fare_Kilometers_Travelled, editTextKilometersTravelled.text.toString()))
                add(CustomField(Constants.ZendeskCustomFields.Wrong_Fare_Booking_Time, editTextBookingTime.text.toString()))
                add(CustomField(Constants.ZendeskCustomFields.Wrong_Fare_Paid_Amount, editTextPaidAmount.text.toString()))
            }

            tripHistoryDate.let {
                add(CustomField(Constants.ZendeskCustomFields.Booking_Type, tripHistoryDate?.trip_type))
                add(CustomField(Constants.ZendeskCustomFields.Trip_Start_Address, tripHistoryDate?.startAddress))
                add(CustomField(Constants.ZendeskCustomFields.Trip_End_Address, tripHistoryDate?.endAddress))

                if (!tripHistoryDate?.tripNo.isNullOrEmpty())
                    add(CustomField(Constants.ZendeskCustomFields.Booking_ID, tripHistoryDate?.tripNo))
                if (!tripHistoryDate?.cancelBy.isNullOrEmpty()) {
                    add(CustomField(Constants.ZendeskCustomFields.Cancelled_by, tripHistoryDate?.cancelBy))
                    when (tripHistoryDate?.cancelBy?.toLowerCase()) {
                        getString(R.string.partner_label) -> {
                            add(CustomField(Constants.ZendeskCustomFields.Partner_Penalty_Amount, tripHistoryDate?.cancel_fee))
                        }
                        getString(R.string.customer_labee) -> {
                            add(CustomField(Constants.ZendeskCustomFields.Customer_Penalty_Amount, tripHistoryDate?.cancel_fee))
                        }
                    }
                }
                tripHistoryDate?.invoice.let {
                    if (!tripHistoryDate?.invoice?.wallet_deduction.isNullOrEmpty())
                        add(CustomField(Constants.ZendeskCustomFields.Wallet_Deduction, tripHistoryDate?.invoice?.wallet_deduction))
                    if (!tripHistoryDate?.invoice?.tripCharges.isNullOrEmpty())
                        add(CustomField(Constants.ZendeskCustomFields.Trip_Fare, tripHistoryDate?.invoice?.tripCharges))
                    if (!tripHistoryDate?.invoice?.waitMins.isNullOrEmpty())
                        add(CustomField(Constants.ZendeskCustomFields.Wait_Time, tripHistoryDate?.invoice?.waitMins))
                    if (!tripHistoryDate?.invoice?.km.isNullOrEmpty())
                        add(CustomField(Constants.ZendeskCustomFields.Trip_Distance, tripHistoryDate?.invoice?.km))
                    if (!tripHistoryDate?.invoice?.minutes.isNullOrEmpty())
                        add(CustomField(Constants.ZendeskCustomFields.Trip_Time, tripHistoryDate?.invoice?.minutes))
                }

                tripHistoryDate?.passenger.let {
                    if (!tripHistoryDate?.passenger?.name.isNullOrEmpty())
                        add(CustomField(Constants.ZendeskCustomFields.Customer_Name, tripHistoryDate?.passenger?.name))
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

    companion object {
        fun openActivity(@NonNull activity: Activity, requestCode: Int) {
            val i = Intent(activity!!, ComplainAddActivity::class.java)
            activity?.startActivity(i)
        }
    }
}
