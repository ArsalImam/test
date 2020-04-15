package com.bykea.pk.partner.ui.offlinerides


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bykea.pk.partner.DriverApp
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.source.JobsDataSource
import com.bykea.pk.partner.dal.source.JobsRepository
import com.bykea.pk.partner.dal.source.remote.request.ride.RideCreateLocationInfoData
import com.bykea.pk.partner.dal.source.remote.request.ride.RideCreateRequestObject
import com.bykea.pk.partner.dal.source.remote.request.ride.RideCreateTripData
import com.bykea.pk.partner.dal.source.remote.response.FareEstimationResponse
import com.bykea.pk.partner.dal.source.remote.response.VerifyNumberResponse
import com.bykea.pk.partner.dal.util.*
import com.bykea.pk.partner.databinding.FragmentOfflineRidesBinding
import com.bykea.pk.partner.models.data.PlacesResult
import com.bykea.pk.partner.repositories.places.IPlacesDataHandler
import com.bykea.pk.partner.repositories.places.PlacesDataHandler
import com.bykea.pk.partner.ui.activities.HomeActivity
import com.bykea.pk.partner.ui.activities.SelectPlaceActivity
import com.bykea.pk.partner.ui.helpers.ActivityStackManager
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.ui.helpers.FontUtils
import com.bykea.pk.partner.utils.Constants
import com.bykea.pk.partner.utils.Constants.APP
import com.bykea.pk.partner.utils.Constants.Extras.FLOW_FOR
import com.bykea.pk.partner.utils.Constants.Extras.FROM
import com.bykea.pk.partner.utils.Constants.ServiceCode.OFFLINE_DELIVERY
import com.bykea.pk.partner.utils.Constants.ServiceCode.OFFLINE_RIDE
import com.bykea.pk.partner.utils.Constants.USER_TYPE
import com.bykea.pk.partner.utils.Dialogs
import com.bykea.pk.partner.utils.GeocodeStrategyManager
import com.bykea.pk.partner.utils.Utils
import com.bykea.pk.partner.widgets.FontTextView
import kotlinx.android.synthetic.main.activity_ride_code_verification.*
import kotlinx.android.synthetic.main.fragment_offline_rides.*
import org.apache.commons.lang3.StringUtils

class OfflineRidesFragment : Fragment() {

    private var geocodeStrategyManager: GeocodeStrategyManager? = null
    private var mCurrentActivity: HomeActivity? = null
    lateinit var binding: FragmentOfflineRidesBinding
    var mDropOffResult: PlacesResult? = null
    private lateinit var jobsRepository: JobsRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_offline_rides, container, false)

        mCurrentActivity = activity as HomeActivity?
        jobsRepository = Injection.provideJobsRepository(mCurrentActivity!!)
        geocodeStrategyManager = GeocodeStrategyManager(mCurrentActivity!!, placesDataHandler, Constants.NEAR_LBL)

        binding.listener = object : OfflineFragmentListener {
            override fun onDropOffClicked() {
                eTMobileNumber.clearFocus()
                eTCustomerName.clearFocus()
                val returndropoffIntent = Intent(mCurrentActivity, SelectPlaceActivity::class.java)
                returndropoffIntent.putExtra(FROM, Constants.CONFIRM_DROPOFF_REQUEST_CODE)
                returndropoffIntent.putExtra(FLOW_FOR, Constants.Extras.OFFLINE_RIDE)
                startActivityForResult(returndropoffIntent, Constants.CONFIRM_DROPOFF_REQUEST_CODE)
            }

            override fun onReceiveCodeClicked() {
                if ((rBSawari.isChecked && validateMobileNumber()) ||
                        (rBDelivery.isChecked && validateMobileNumber() && validateCustomerName())) {
                    Dialogs.INSTANCE.showLoader(mCurrentActivity)
                    geocodeStrategyManager?.fetchLocation(AppPreferences.getLatitude(), AppPreferences.getLongitude())
                }
            }
            override fun onOfflineKamaiClicked() {
                Utils.startCustomWebViewActivity(mCurrentActivity, Constants.OFFLINE_KAMAI_WEB_URL, DriverApp.getContext().getString(R.string.offline_kamai));
            }
        }
        return binding.root
    }

    private val placesDataHandler: IPlacesDataHandler = object : PlacesDataHandler() {
        override fun onPlacesResponse(response: String?) {
            super.onPlacesResponse(response)
            val requestBody: RideCreateRequestObject = createRequestBody()
            requestBody.pickup_info.address = response
            navigateToVerifyCode(requestBody)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (AppPreferences.getSettings() != null && AppPreferences.getSettings().settings != null &&
                AppPreferences.getSettings().settings.isOfflineDeliveryEnable) {
            rGOfflineRide.visibility = View.VISIBLE
            ViewCompat.setLayoutDirection(rGOfflineRide, ViewCompat.LAYOUT_DIRECTION_RTL)
            rBSawari.text = FontUtils.getStyledTitle(mCurrentActivity, mCurrentActivity?.getString(R.string.sawari), Constants.FontNames.JAMEEL_NASTALEEQI)
            rBDelivery.text = FontUtils.getStyledTitle(mCurrentActivity, mCurrentActivity?.getString(R.string.delivery), Constants.FontNames.JAMEEL_NASTALEEQI)
        } else {
            rGOfflineRide.visibility = View.GONE
        }

        val spannableStringBuilder = SpannableStringBuilder()
        spannableStringBuilder.append(FontUtils.getStyledTitle(mCurrentActivity, mCurrentActivity?.getString(R.string.offline_rides_en),
                "roboto_medium.ttf"))
        spannableStringBuilder.append(FontUtils.getStyledTitle(mCurrentActivity, mCurrentActivity?.getString(R.string.offline_rides_ur),
                "jameel_noori_nastaleeq.ttf"))

        mCurrentActivity?.hideToolbarLogo()
        mCurrentActivity?.findViewById<FontTextView>(R.id.title)?.text = spannableStringBuilder
        mCurrentActivity?.findViewById<FontTextView>(R.id.title)?.visibility = View.VISIBLE
        mCurrentActivity?.findViewById<View>(R.id.toolbarLine)?.visibility = View.VISIBLE
        mCurrentActivity?.findViewById<View>(R.id.statusLayout)?.visibility = View.VISIBLE
        mCurrentActivity?.findViewById<View>(R.id.connectionStatusIv)?.visibility = View.GONE
        mCurrentActivity?.findViewById<View>(R.id.achaconnectionTv)?.visibility = View.GONE
        mCurrentActivity?.hideStatusCompletely()
        mCurrentActivity?.hideWalletIcon()
        setFare()

        eTMobileNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (validateMobileNumber()) {
                    setBackgroundColor(R.color.colorAccent)
                } else {
                    setBackgroundColor(R.color.color_A7A7A7)
                }
            }
        })

        eTCustomerName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (rBDelivery.isChecked)
                    if (validateCustomerName()) {
                        setBackgroundColor(R.color.colorAccent)
                    } else {
                        setBackgroundColor(R.color.color_A7A7A7)
                    }
            }
        })
        rGOfflineRide.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                when (checkedId) {
                    rBSawari.id -> {
                        if (linLayoutSenderName.visibility == View.VISIBLE)
                            linLayoutSenderName.visibility = View.GONE
                        eTCustomerName.error = null
                        eTCustomerName.clearFocus()
                        if (Utils.isValidNumber(eTMobileNumber)) {
                            setBackgroundColor(R.color.colorAccent)
                        } else {
                            setBackgroundColor(R.color.color_A7A7A7)
                        }
                    }
                    rBDelivery.id -> {
                        if (linLayoutSenderName.visibility == View.GONE)
                            linLayoutSenderName.visibility = View.VISIBLE

                        if (Utils.isValidNumber(eTMobileNumber) && validateCustomerName(false)) {
                            setBackgroundColor(R.color.colorAccent)
                        } else {
                            setBackgroundColor(R.color.color_A7A7A7)
                        }
                    }
                }
            }
        })
    }

    /**
     * Validate Mobile Number (Valid Number or Not)
     */
    private fun validateMobileNumber(): Boolean {
        return if (rBSawari.isChecked)
            Utils.isValidNumber(mCurrentActivity, eTMobileNumber)
        else
            Utils.isValidNumber(mCurrentActivity, eTMobileNumber) && validateCustomerName()
    }

    /**
     * Validate Customer Field (Empty or Not)
     */
    private fun validateCustomerName(showError: Boolean = true): Boolean {
        if (eTCustomerName.text.toString().trim().isEmpty()) {
            if (showError) {
                eTCustomerName.error = context?.getString(R.string.enter_correct_customer_name);
                eTCustomerName.requestFocus()
            }
            return false
        }
        return Utils.isValidNumber(eTMobileNumber)
    }

    /**
     * Set Background Color For Bottom Button
     * @param colorId
     */
    private fun setBackgroundColor(colorId: Int) {
        mCurrentActivity.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tVReceiveCode.setBackgroundColor(mCurrentActivity!!.resources.getColor(colorId, null))
            } else {
                tVReceiveCode.setBackgroundColor(mCurrentActivity!!.resources.getColor(colorId))
            }
        }
    }

    /**
     * Set Fare In Drop Off Field
     * @param fare : Return From FareEstimation API
     */
    fun setFare(fare: String? = null) {
        if (fare != null) {
            tVOfflineFare.text = SpannableStringBuilder(StringUtils.EMPTY)
                    .append(FontUtils.getStyledTitle(mCurrentActivity, String.format(getString(R.string.amount_rs), fare), "roboto_medium.ttf"))
                    .append(FontUtils.getStyledTitle(mCurrentActivity, StringUtils.SPACE, "roboto_medium.ttf"))
                    .append(FontUtils.getStyledTitle(mCurrentActivity, StringUtils.SPACE, "roboto_medium.ttf"))
                    .append(FontUtils.getStyledTitle(mCurrentActivity, context?.getString(R.string.offline_ride_fare), "jameel_noori_nastaleeq.ttf"))
                    .append(StringUtils.SPACE)

            Utils.setImageDrawable(iVDropOffAddress, R.drawable.ic_pencil_icon)
        } else {
            tVOfflineFare.text = SpannableStringBuilder(StringUtils.EMPTY)
                    .append(FontUtils.getStyledTitle(mCurrentActivity, String.format(getString(R.string.amount_rs), StringUtils.EMPTY), "roboto_medium.ttf"))
                    .append(FontUtils.getStyledTitle(mCurrentActivity, StringUtils.SPACE, "roboto_medium.ttf"))
                    .append(FontUtils.getStyledTitle(mCurrentActivity, String.format(getString(R.string.dash)), "roboto_medium.ttf"))
                    .append(FontUtils.getStyledTitle(mCurrentActivity, StringUtils.SPACE, "roboto_medium.ttf"))
                    .append(FontUtils.getStyledTitle(mCurrentActivity, context?.getString(R.string.offline_ride_fare), "jameel_noori_nastaleeq.ttf"))
                    .append(StringUtils.SPACE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (mCurrentActivity != null && resultCode == RESULT_OK && requestCode == Constants.CONFIRM_DROPOFF_REQUEST_CODE && data != null) {
            mDropOffResult = data.getParcelableExtra(Constants.CONFIRM_DROPOFF_ADDRESS_RESULT)
            if (mDropOffResult != null) {
                setCallForETA(mDropOffResult!!)
            } else {
                Utils.appToast(mCurrentActivity?.getString(R.string.error_try_again))
            }
        }
    }

    /**
     * Set The DropOff and Fare Fields
     * @param placesResult : Return On Activity Result
     */
    private fun setCallForETA(placesResult: PlacesResult) {
        Dialogs.INSTANCE.showLoader(mCurrentActivity)
        jobsRepository.getFairEstimation(
                AppPreferences.getLatitude().toString(), AppPreferences.getLongitude().toString(),
                placesResult.latLng.latitude.toString(), placesResult.latLng.longitude.toString(),
                if (rBSawari.isChecked) OFFLINE_RIDE else OFFLINE_DELIVERY,
                object : JobsDataSource.FareEstimationCallback {
                    override fun onSuccess(fareEstimationResponse: FareEstimationResponse) {
                        try {
                            if (fareEstimationResponse.fareEstimateData != null) {
                                tVDropOffAddress.text = mDropOffResult?.name
                                setFare(fareEstimationResponse.fareEstimateData?.maxLimitPrice?.toInt().toString())
                            }
                            Dialogs.INSTANCE.dismissDialog()
                        } catch (e: Exception) {
                            Utils.appToast(mCurrentActivity?.getString(R.string.error_try_again))
                            Dialogs.INSTANCE.dismissDialog()
                        }
                    }

                    override fun onFail(code: Int, subCode: Int?, message: String?) {
                        Dialogs.INSTANCE.dismissDialog()
                        tVDropOffAddress.text = StringUtils.EMPTY
                        setFare()
                        displayErrorToast(code, subCode, message)
                    }
                })
    }

    /**
     * Create Request Body For Ride Create API
     * Return Object Of Request Body - RideCreateRequestObject
     */
    private fun createRequestBody(): RideCreateRequestObject {
        return RideCreateRequestObject().apply {
            user_type = USER_TYPE
            _id = AppPreferences.getDriverId()
            token_id = AppPreferences.getAccessToken()

            trip = RideCreateTripData()
            trip.creator = APP

            if (rBSawari.isChecked) {
                trip.service_code = OFFLINE_RIDE
            } else {
                trip.service_code = OFFLINE_DELIVERY
                if (eTCustomerName.text.toString().trim().isNotEmpty())
                    customer_name = eTCustomerName.text?.trim().toString()
            }

            trip.lat = AppPreferences.getLatitude().toString()
            trip.lng = AppPreferences.getLongitude().toString()

            pickup_info = RideCreateLocationInfoData()
            pickup_info.lat = AppPreferences.getLatitude().toString()
            pickup_info.lng = AppPreferences.getLongitude().toString()

            if (mDropOffResult != null) {
                dropoff_info = RideCreateLocationInfoData()
                dropoff_info?.lat = mDropOffResult?.latLng?.latitude.toString()
                dropoff_info?.lng = mDropOffResult?.latLng?.longitude.toString()
                dropoff_info?.address = mDropOffResult?.address
            }
        }
    }

    /**
     * Navigate To Verify Code Screen
     */
    private fun navigateToVerifyCode(requestBody: RideCreateRequestObject) {
        jobsRepository.requestOtpGenerate(Utils.phoneNumberForServer(binding.eTMobileNumber.text.toString()), OTP_SMS,
                object : JobsDataSource.OtpGenerateCallback {
                    override fun onSuccess(verifyNumberResponse: VerifyNumberResponse) {
                        Dialogs.INSTANCE.dismissDialog()
                        ActivityStackManager
                                .getInstance()
                                .startWaitingActivity(mCurrentActivity, requestBody,
                                        binding.eTMobileNumber.text.toString())
                    }

                    override fun onFail(code: Int, subCode: Int?, message: String?) {
                        Dialogs.INSTANCE.dismissDialog()
                        displayErrorToast(code, subCode, message)
                    }
                })
    }

    /**
     * Display Error Toast
     * @param code : Server Code
     * @param subCode : Server Sub Code
     * @param message : Error Message For Toast
     */
    private fun displayErrorToast(code: Int, subCode: Int?, message: String?) {
        if (subCode != null) {
            when (subCode) {
                SUB_CODE_1009 -> Utils.appToast(message)
                SUB_CODE_1019 -> Utils.appToast(message)
                SUB_CODE_1028 -> Utils.appToast(message)
                SUB_CODE_1051 -> Utils.appToast(message)
                SUB_CODE_1052 -> Utils.appToast(SUB_CODE_1052_MSG)
                SUB_CODE_1053 -> linLayoutOtpWrongEntered.visibility = View.VISIBLE
                SUB_CODE_1054 -> Utils.appToast(SUB_CODE_1054_MSG)
                else -> Utils.appToast(getString(R.string.error_try_again))
            }
        } else {
            if ((!message.isNullOrEmpty() && StringUtils.containsIgnoreCase(message, getString(R.string.invalid_code_error_message))) ||
                    code.equals(Constants.ApiError.BUSINESS_LOGIC_ERROR)) {
                linLayoutOtpWrongEntered.visibility = View.VISIBLE
            } else {
                Utils.appToast(getString(R.string.error_try_again))
            }
        }
    }

    override fun onDestroyView() {
        mCurrentActivity?.showToolbar()
        mCurrentActivity?.hideUrduTitle()
        super.onDestroyView()
    }

}