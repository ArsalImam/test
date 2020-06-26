package com.bykea.pk.partner.ui.nodataentry

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.LocCoordinatesInTrip
import com.bykea.pk.partner.dal.source.JobsDataSource.FinishJobCallback
import com.bykea.pk.partner.dal.source.JobsRepository
import com.bykea.pk.partner.dal.source.remote.response.FinishJobResponseData
import com.bykea.pk.partner.dal.util.Injection
import com.bykea.pk.partner.databinding.ActivityFinishBookingListingsBinding
import com.bykea.pk.partner.models.response.BatchBooking
import com.bykea.pk.partner.models.response.NormalCallData
import com.bykea.pk.partner.repositories.places.IPlacesDataHandler
import com.bykea.pk.partner.repositories.places.PlacesDataHandler
import com.bykea.pk.partner.ui.activities.BaseActivity
import com.bykea.pk.partner.ui.common.LastAdapter
import com.bykea.pk.partner.ui.helpers.ActivityStackManager
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.utils.*
import com.bykea.pk.partner.utils.Constants.GoogleMap.GOOGLE_MAP_PACKAGE
import com.crashlytics.android.Crashlytics
import com.zendesk.util.StringUtils
import kotlinx.android.synthetic.main.map_toolbar.*
import java.util.*

class FinishBookingListingActivity : BaseActivity() {

    private var callData: NormalCallData? = null
    private var binding: ActivityFinishBookingListingsBinding? = null
    private var selectedBooking: BatchBooking? = null
    private val geocodeStrategyManager: GeocodeStrategyManager by lazy { GeocodeStrategyManager(this, placesDataHandler, Constants.NEAR_LBL) }
    private val jobRepo: JobsRepository by lazy { Injection.provideJobsRepository(this@FinishBookingListingActivity) }


    private val placesDataHandler: IPlacesDataHandler = object : PlacesDataHandler() {
        override fun onPlacesResponse(response: String) {
            super.onPlacesResponse(response)
            finishJobRestApi(response)
        }
    }

    /**
     * Request finish job on Rest API
     */
    private fun finishJobRestApi(endAddress: String) {
        AppPreferences.removeReceivedMessageCount()
        var endLatString = AppPreferences.getLatitude().toString() + StringUtils.EMPTY_STRING
        var endLngString = AppPreferences.getLongitude().toString() + StringUtils.EMPTY_STRING
        val lastLat = AppPreferences.getPrevDistanceLatitude()
        val lastLng = AppPreferences.getPrevDistanceLongitude()
        if (!lastLat.equals("0.0", ignoreCase = true) && !lastLng.equals("0.0", ignoreCase = true)) {
            if (!Utils.isValidLocation(endLatString.toDouble(), endLngString.toDouble(), lastLat.toDouble(), lastLng.toDouble())) {
                endLatString = lastLat
                endLngString = lastLng
            }
        }
        val startLatLng = LocCoordinatesInTrip()
        startLatLng.lat = AppPreferences.getCallData().startLat
        startLatLng.lng = AppPreferences.getCallData().startLng
        startLatLng.date = Utils.getIsoDate(AppPreferences.getStartTripTime())
        val endLatLng = LocCoordinatesInTrip()
        endLatLng.lat = endLatString
        endLatLng.lng = endLngString
        endLatLng.date = Utils.getIsoDate()
        val prevLatLngList = AppPreferences.getLocCoordinatesInTrip()
        val latLngList = ArrayList<LocCoordinatesInTrip>()
        latLngList.add(startLatLng)
        if (prevLatLngList != null && prevLatLngList.size > 0) {
            latLngList.addAll(prevLatLngList)
        }
        latLngList.add(endLatLng)
        jobRepo.finishJob(selectedBooking?.id!!, latLngList, endAddress, object : FinishJobCallback {
            override fun onJobFinished(data: FinishJobResponseData, request: String, resp: String) {
                AppPreferences.removeReceivedMessageCount()
                Crashlytics.setUserIdentifier(AppPreferences.getPilotData().id)
                Crashlytics.setString("Finish Job Request Trip ID", callData!!.tripId)
                Crashlytics.setString("Finish Job Response", resp)
                onFinished(data)
            }

            override fun onJobFinishFailed(message: String?, code: Int?) {
                Dialogs.INSTANCE.showError(this@FinishBookingListingActivity, binding?.imgViewButton, message)
            }
        })
    }

    private fun onFinished(data: FinishJobResponseData) {
        Dialogs.INSTANCE.dismissDialog()
        callData = AppPreferences.getCallData()
        Handler().postDelayed({
            callData?.ruleIds = data.trip.rule_ids
            selectedBooking?.let {
                var booking = it
                callData?.bookingList?.forEach { singleBooking ->
                    if (singleBooking.id == booking.id) {
                        singleBooking.status = TripStatus.ON_FINISH_TRIP
                        with(singleBooking.pickup) {
                            address = data?.trip?.start_address
                            gpsAddress = data?.trip?.start_address
                            lat = data?.trip?.start_lat
                            lng = data?.trip?.start_lng
                        }
                    }
                }
                AppPreferences.setCallData(callData)
                AppPreferences.clearTripDistanceData()
            }

            ActivityStackManager.getInstance()
                    .startFeedbackActivity(this, true)
            finish()
        }, 1000)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_finish_booking_listings)
        callData = AppPreferences.getCallData()
        initToolbar()
        setAdapter()
    }

    private fun setAdapter() {
        binding?.recyclerView?.apply {
            adapter = LastAdapter(R.layout.item_finish_booking_listing, object : LastAdapter.OnItemClickListener<BatchBooking> {

                override fun onItemClick(item: BatchBooking) {
                    if (item.isCompleted) return
                    callData?.bookingList?.forEach {
                        it.isSelected = it.id == item.id
                    }
                    adapter?.notifyDataSetChanged()
                    selectedBooking = if (item.isSelected) {
                        item
                    } else {
                        null
                    }
                    checkForButton()
                }
            }).apply {
                items = callData?.bookingList!!
            }
        }
    }

    private fun checkForButton() {
        val showButton = selectedBooking != null
        binding?.imgViewButton?.apply {
            isEnabled = showButton
            setBackgroundColor(ContextCompat.getColor(this@FinishBookingListingActivity, if (showButton) {
                R.color.colorAccent
            } else {
                R.color.grey_828683
            }))
        }
    }

    fun onDoneClick(view: View) {
        selectedBooking?.let {
            when (intent.getStringExtra(EXTRA_TYPE)) {
                TYPE_FINISH -> {
                    Dialogs.INSTANCE.showRideStatusDialog(this, { finishJob() }, { Dialogs.INSTANCE.dismissDialog() }, getString(R.string.questino_mukammal))
                }
                TYPE_NAVIGATION -> {
                    openGoogleDirectionsIntent("${it.dropoff.lat},${it.dropoff.lng}")
                }
                else -> {
                }
            }
        }
    }


    /**
     * Request server to finish job. Depending upon service types, it does this
     * communication on either REST Api or socket
     */
    private fun finishJob() {
        AppPreferences.removeReceivedMessageCount()
        Dialogs.INSTANCE.dismissDialog()
        Dialogs.INSTANCE.showLoader(this)
        geocodeStrategyManager.fetchLocation(AppPreferences.getLatitude(), AppPreferences.getLongitude(), false)
    }

    private fun openGoogleDirectionsIntent(end: String) {
        try {
            val gmmIntentUri = Uri.parse("google.navigation:q=$end&mode=d")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage(GOOGLE_MAP_PACKAGE)
            startActivity(mapIntent)
        } catch (ex: Exception) {
            Utils.appToast("Please install Google Maps")
        }
    }

    private fun initToolbar() {
        setSupportActionBar(map_toolbar)
        back.setOnClickListener { finish() }
        supportActionBar?.setDisplayShowTitleEnabled(false)
        map_toolbar.apply {
            title = StringUtils.EMPTY_STRING
            subtitle = StringUtils.EMPTY_STRING
        }
        map_toolbar.findViewById<TextView>(R.id.title).apply {
            text = intent.getStringExtra(EXTRA_TITLE)
            setTextColor(Color.BLACK)
        }
    }

    companion object {
        val EXTRA_TITLE: String = "title"
        val EXTRA_TYPE: String = "type"
        val TYPE_FINISH: String = "finish"
        val TYPE_NAVIGATION: String = "navigate"

        fun openActivity(activity: Activity, title: String, type: String) {
            val intent = Intent(activity, FinishBookingListingActivity::class.java)
            intent.putExtra(EXTRA_TITLE, title)
            intent.putExtra(EXTRA_TYPE, type)
            activity.startActivity(intent)
        }
    }
}