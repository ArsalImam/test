package com.bykea.pk.partner.ui.offlinerides


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.source.JobsDataSource
import com.bykea.pk.partner.dal.source.JobsRepository
import com.bykea.pk.partner.dal.source.remote.request.ride.RideCreateLocationInfoData
import com.bykea.pk.partner.dal.source.remote.request.ride.RideCreateRequestObject
import com.bykea.pk.partner.dal.source.remote.request.ride.RideCreateTripData
import com.bykea.pk.partner.dal.source.remote.response.FareEstimationResponse
import com.bykea.pk.partner.dal.source.remote.response.VerifyNumberResponse
import com.bykea.pk.partner.dal.util.Injection
import com.bykea.pk.partner.dal.util.OTP_SMS
import com.bykea.pk.partner.databinding.FragmentOfflineRidesBinding
import com.bykea.pk.partner.models.data.PlacesResult
import com.bykea.pk.partner.ui.activities.HomeActivity
import com.bykea.pk.partner.ui.activities.SelectPlaceActivity
import com.bykea.pk.partner.ui.helpers.ActivityStackManager
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.ui.helpers.FontUtils
import com.bykea.pk.partner.utils.Constants
import com.bykea.pk.partner.utils.Constants.APP
import com.bykea.pk.partner.utils.Constants.Extras.FLOW_FOR
import com.bykea.pk.partner.utils.Constants.Extras.FROM
import com.bykea.pk.partner.utils.Constants.ServiceType.OFFLINE_RIDE
import com.bykea.pk.partner.utils.Constants.USER_TYPE
import com.bykea.pk.partner.utils.Dialogs
import com.bykea.pk.partner.utils.Utils
import com.bykea.pk.partner.widgets.FontTextView
import kotlinx.android.synthetic.main.fragment_offline_rides.*
import kotlinx.android.synthetic.main.toolbar.*
import org.apache.commons.lang3.StringUtils

class OfflineRidesFragment : Fragment() {
    private var mCurrentActivity: HomeActivity? = null
    lateinit var binding: FragmentOfflineRidesBinding
    var mDropOffResult: PlacesResult? = null
    private lateinit var jobsRepository: JobsRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_offline_rides, container, false)
        mCurrentActivity = activity as HomeActivity?
        jobsRepository = Injection.provideJobsRepository(mCurrentActivity!!)

        binding.listener = object : OfflineFragmentListener {
            override fun onDropOffClicked() {
                if (StringUtils.isEmpty(eTMobileNumber.text?.trim()))
                    eTMobileNumber.clearFocus()
                val returndropoffIntent = Intent(mCurrentActivity, SelectPlaceActivity::class.java)
                returndropoffIntent.putExtra(FROM, Constants.CONFIRM_DROPOFF_REQUEST_CODE)
                returndropoffIntent.putExtra(FLOW_FOR, Constants.Extras.OFFLINE_RIDE)
                startActivityForResult(returndropoffIntent, Constants.CONFIRM_DROPOFF_REQUEST_CODE)
            }

            override fun onReceiveCodeClicked() {
                if (isValid()) {
                    Dialogs.INSTANCE.showLoader(mCurrentActivity)
                    jobsRepository.requestOtpGenerate(Utils.phoneNumberForServer(binding.eTMobileNumber.text.toString()), OTP_SMS,
                            object : JobsDataSource.OtpGenerateCallback {
                                override fun onSuccess(verifyNumberResponse: VerifyNumberResponse) {
                                    Dialogs.INSTANCE.dismissDialog()
                                    ActivityStackManager
                                            .getInstance()
                                            .startWaitingActivity(mCurrentActivity, createRequestBody(),
                                                    binding.eTMobileNumber.text.toString())
                                }

                                override fun onFail(code: Int, message: String?) {
                                    Dialogs.INSTANCE.dismissDialog()
                                    Utils.appToast(mCurrentActivity?.getString(R.string.error_try_again))
                                }
                            })
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            iVDropOffAddress.setImageDrawable(mCurrentActivity?.resources?.getDrawable(R.drawable.ic_pencil_icon, null))
        } else {
            iVDropOffAddress.setImageDrawable(mCurrentActivity?.resources?.getDrawable(R.drawable.ic_pencil_icon))
        }

        eTMobileNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (Utils.isValidNumber(eTMobileNumber) && !StringUtils.isEmpty(tVDropOffAddress.text)) {
                    setBackgroundColor()
                }
            }
        })

        tVDropOffAddress.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!StringUtils.isEmpty(tVDropOffAddress.text)) {
                    Utils.setImageDrawable(iVDropOffAddress, R.drawable.ic_pencil_icon)
                    if (Utils.isValidNumber(eTMobileNumber))
                        setBackgroundColor()
                }
            }
        })
    }

    /**
     * Set Background Color For Bottom Button
     */
    private fun setBackgroundColor() {
        mCurrentActivity.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tVReceiveCode.setBackgroundColor(mCurrentActivity!!.resources.getColor(R.color.colorAccent, null))
            } else {
                tVReceiveCode.setBackgroundColor(mCurrentActivity!!.resources.getColor(R.color.colorAccent))
            }
        }
    }

    override fun onDestroyView() {
        mCurrentActivity?.showToolbar()
        mCurrentActivity?.hideUrduTitle()
        super.onDestroyView()
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

    /**
     * Check If Drop Off Is Selected Or Not and Mobile Number is correctly entered or not
     * return True:False, accordingly
     */
    fun isValid(): Boolean {
        if (StringUtils.isEmpty(tVDropOffAddress.text)) {
            Utils.appToast(context?.getString(R.string.search_drop_off_toast))
            return false
        }
        return Utils.isValidNumber(mCurrentActivity, eTMobileNumber)
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
                APP, Constants.ServiceType.OFFLINE_RIDE_STRING,
                object : JobsDataSource.FareEstimationCallback {
                    override fun onSuccess(fareEstimationResponse: FareEstimationResponse) {
                        try {
                            if (fareEstimationResponse.callData != null) {
                                tVDropOffAddress.text = mDropOffResult?.name
                                setFare(fareEstimationResponse.callData?.maxLimitPrice?.toInt().toString())
                            }
                            Dialogs.INSTANCE.dismissDialog()
                        } catch (e: Exception) {
                            Utils.appToast(mCurrentActivity?.getString(R.string.error_try_again))
                            Dialogs.INSTANCE.dismissDialog()
                        }
                    }

                    override fun onFail(code: Int, message: String?) {
                        Dialogs.INSTANCE.dismissDialog()
                        Utils.appToast(mCurrentActivity?.getString(R.string.error_try_again))
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
            trip.service_code = OFFLINE_RIDE
            trip.lat = AppPreferences.getLatitude().toString()
            trip.lng = AppPreferences.getLongitude().toString()

            pickup_info = RideCreateLocationInfoData()
            pickup_info.lat = AppPreferences.getLatitude().toString()
            pickup_info.lng = AppPreferences.getLongitude().toString()
            pickup_info.address = Utils.getLocationAddress(AppPreferences.getLatitude().toString(), AppPreferences.getLongitude().toString(), mCurrentActivity)

            dropoff_info = RideCreateLocationInfoData()
            dropoff_info?.lat = mDropOffResult?.latLng?.latitude.toString()
            dropoff_info?.lng = mDropOffResult?.latLng?.longitude.toString()
            dropoff_info?.address = mDropOffResult?.address
        }
    }
}