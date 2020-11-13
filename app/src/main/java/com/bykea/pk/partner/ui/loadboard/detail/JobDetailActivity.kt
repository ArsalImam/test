package com.bykea.pk.partner.ui.loadboard.detail

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bykea.pk.partner.R
import com.bykea.pk.partner.analytics.AnalyticsEventsJsonObjects
import com.bykea.pk.partner.dal.Trips
import com.bykea.pk.partner.dal.util.DIGIT_ZERO
import com.bykea.pk.partner.databinding.JobDetailActBinding
import com.bykea.pk.partner.models.data.MultiDeliveryCallDriverData
import com.bykea.pk.partner.models.response.CheckDriverStatusResponse
import com.bykea.pk.partner.repositories.UserDataHandler
import com.bykea.pk.partner.ui.activities.BaseActivity
import com.bykea.pk.partner.ui.common.LastAdapter
import com.bykea.pk.partner.ui.common.obtainViewModel
import com.bykea.pk.partner.ui.common.setupSnackbar
import com.bykea.pk.partner.ui.helpers.ActivityStackManager
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.utils.Constants
import com.bykea.pk.partner.utils.Constants.DIGIT_ONE
import com.bykea.pk.partner.utils.Dialogs
import com.bykea.pk.partner.utils.Util
import com.bykea.pk.partner.utils.Utils
import com.bykea.pk.partner.utils.audio.BykeaAmazonClient
import com.bykea.pk.partner.utils.audio.Callback
import com.bykea.pk.partner.utils.audio.MediaPlayerHolder
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_confirm_drop_off_address.*
import kotlinx.android.synthetic.main.job_detail_act.*
import java.io.File
import java.io.FileInputStream
import java.lang.StringBuilder
import java.util.*

/**
 * Loadboard booking detail screen ACTIVITY - opening from homeScreen's loadboard listing items
 */
class JobDetailActivity : BaseActivity() {

    private val EARTHRADIUS = 6366198.0
    private lateinit var binding: JobDetailActBinding
    private var bookingId: Long = 0
    private lateinit var mMediaPlayerHolder: MediaPlayerHolder
    private val mMarkerList = ArrayList<Marker>()
    lateinit var mapView: SupportMapFragment
    private lateinit var mGoogleMap: GoogleMap
    private var driverMarker: Marker? = null

    private var mediaPlayer: MediaPlayer? = null
    private val handler: Handler = Handler()
    private var lastAdapter: LastAdapter<Trips>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.job_detail_act)

        binding.viewmodel = obtainViewModel(JobDetailViewModel::class.java).apply {
            view?.setupSnackbar(this@JobDetailActivity, this.snackbarMessage, Snackbar.LENGTH_LONG)

            dataLoading.observe(this@JobDetailActivity, Observer {
                if (it) Dialogs.INSTANCE.showLoader(this@JobDetailActivity)
                else Dialogs.INSTANCE.dismissDialog()
            })

            acceptBookingCommand.observe(this@JobDetailActivity, Observer {
                AppPreferences.setTopUpPassengerWalletAllowed(true)
                Utils.logEvent(this@JobDetailActivity, AppPreferences.getDriverId(),
                        Constants.AnalyticsEvents.ON_LB_BOOKING_ACCEPT,
                        AnalyticsEventsJsonObjects.getEventLoadBoardJson(Constants.AnalyticsEvents.ON_LB_BOOKING_ACCEPT, job.value))
                if (!binding.viewmodel?.job?.value?.voice_note.isNullOrEmpty()) {
                    AppPreferences.setBookingVoiceNoteUrlAvailable(binding.viewmodel?.job?.value?.voice_note)
                } else {
                    AppPreferences.removeBookingVoiceNoteUrl()
                }
                if (Utils.isNewBatchService(binding.viewmodel?.job?.value?.service_code!!)) {
                    ActivityStackManager.getInstance().startJobActivity(this@JobDetailActivity, false)
                } else if (binding.viewmodel?.job?.value?.trip_type?.equals(Constants.TripTypes.BATCH_TYPE, true)!!) {
                    Dialogs.INSTANCE.showLoader(this@JobDetailActivity)
                    Handler().postDelayed({
                        binding.viewmodel?.getTripDetails(object : UserDataHandler() {
                            override fun onRunningTrips(response: CheckDriverStatusResponse?) {
                                super.onRunningTrips(response)
                                Dialogs.INSTANCE.dismissDialog()
                                if (response?.isSuccess()!!) {
                                    val trip = Gson().toJson(response.data.trip)
                                    val type = object : TypeToken<MultiDeliveryCallDriverData>() {
                                    }.type
                                    AppPreferences.setDeliveryType(Constants.CallType.BATCH)
                                    val deliveryCallDriverData: MultiDeliveryCallDriverData = Gson().fromJson(trip, type)
                                    AppPreferences.setMultiDeliveryCallDriverData(deliveryCallDriverData)
                                    ActivityStackManager.getInstance().startMultiDeliveryBookingActivity(this@JobDetailActivity)
                                }
                            }
                        })
                    }, 3000)
                } else {
                    ActivityStackManager.getInstance().startJobActivity(this@JobDetailActivity, false)
                }
            })

            bookingTakenCommand.observe(this@JobDetailActivity, Observer {
                Dialogs.INSTANCE
                        .showAlertDialogTick(this@JobDetailActivity,
                                this@JobDetailActivity.resources.getString(R.string.booking_already_taken_title),
                                this@JobDetailActivity.resources.getString(R.string.booking_already_taken_msg)
                        ) { finish() }
            })
            driverBlockedByAdmin.observe(this@JobDetailActivity, Observer {
                if (it) {
                    binding.viewmodel?.driverBlockedByAdmin?.value = false
                    Dialogs.INSTANCE.showRegionOutErrorDialog(this@JobDetailActivity,
                            Utils.getSupportHelplineNumber(),
                            getString(R.string.account_blocked_wallet_amount_not_paid))
                }
            })
        }
        binding.listener = object : JobDetailActionsListener {
            override fun onPlayAudio(url: String?) {
                if (url != null) {
                    voiceClipPlayDownload(url)
                } else {
                    binding.viewmodel?.showSnackbarMessage(R.string.no_voice_note_available)
                }
            }

            override fun onStopAudio() {
                if (mediaPlayer != null) {
                    imgViewAudioPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_audio_play));
                    imgViewAudioPlay.setEnabled(true);
                    progressBarForAudioPlay.setVisibility(View.GONE);
                    mediaPlayer?.pause();
                }
            }

            override fun onNavigateToMap(isPickUp: Boolean, pickLat: Double, pickLng: Double, dropLat: Double, dropLng: Double) {
                Utils.logEvent(this@JobDetailActivity, AppPreferences.getDriverId(),
                        Constants.AnalyticsEvents.ON_LB_PICKUP_DIRECTION,
                        AnalyticsEventsJsonObjects.getEventLoadBoardJson(Constants.AnalyticsEvents.ON_LB_PICKUP_DIRECTION, binding.viewmodel?.job?.value))
                Utils.navigateToGoogleMap(this@JobDetailActivity, pickLat, pickLng, dropLat, dropLng)
            }

            override fun onNavigateToMap() {
                Utils.logEvent(this@JobDetailActivity, AppPreferences.getDriverId(),
                        Constants.AnalyticsEvents.ON_LB_DROPOFF_DIRECTION,
                        AnalyticsEventsJsonObjects.getEventLoadBoardJson(Constants.AnalyticsEvents.ON_LB_DROPOFF_DIRECTION, binding.viewmodel?.job?.value))

                if (binding.viewmodel?.job?.value?.trips.isNullOrEmpty() ||
                        binding.viewmodel?.job?.value?.trips?.size == DIGIT_ONE) {
                    Util.safeLet(binding.viewmodel?.job?.value?.pickup?.lat,
                            binding.viewmodel?.job?.value?.pickup?.lng,
                            binding.viewmodel?.job?.value?.dropoff?.lat,
                            binding.viewmodel?.job?.value?.dropoff?.lng) { pickLat, pickLng, dropLat, dropLng ->

                        Utils.navigateToGoogleMap(this@JobDetailActivity, pickLat, pickLng, binding.viewmodel?.job?.value?.trips)
                    }
                } else {
                    binding.viewmodel?.job?.value?.trips?.let { trips ->
                        Utils.navigateToGoogleMap(this@JobDetailActivity, binding.viewmodel?.job?.value?.pickup?.lat!!, binding.viewmodel?.job?.value?.pickup?.lng!!, binding.viewmodel?.job?.value?.trips)
                    }
                }
            }

            override fun onAcceptBooking() {
                Dialogs.INSTANCE.showLoader(this@JobDetailActivity)
                binding.viewmodel?.accept()
            }

            override fun onBackClicked() {
                Utils.logEvent(this@JobDetailActivity, AppPreferences.getDriverId(),
                        Constants.AnalyticsEvents.ON_LB_BACK_FROM_BOOKING_DETAIL,
                        AnalyticsEventsJsonObjects.getEventLoadBoardJson(Constants.AnalyticsEvents.ON_LB_BACK_FROM_BOOKING_DETAIL, binding.viewmodel?.job?.value))
                finish()
            }
        }
        binding.lifecycleOwner = this
        bookingId = intent.getLongExtra(EXTRA_BOOKING_ID, 0)
        binding.viewmodel?.start(bookingId)

        try {
            loadBoardMapFragment.onCreate(savedInstanceState);
            MapsInitializer.initialize(this);
        } catch (e: Exception) {
            Utils.redLog("HomeScreenException", e.message);
            e.printStackTrace();
        }

        (loadBoardMapFragment as SupportMapFragment).getMapAsync { p0 ->
            p0.setOnMapLoadedCallback {

                mGoogleMap = p0
                mGoogleMap.uiSettings.setAllGesturesEnabled(false)
                getDriverRoadPosition(AppPreferences.getLatitude(), AppPreferences.getLongitude())
                setMarkersForPickUpAndDropOff(p0)
            }
        }
        AppPreferences.removeReceivedMessageCount();

        lastAdapter = LastAdapter(R.layout.list_item_trips)
        recViewTrips.adapter = lastAdapter
    }

    /**
     * Download audio resource via Amazon SDK
     *
     * @param url Url to download from
     */
    private fun voiceClipPlayDownload(url: String) {
        if (mediaPlayer != null) {
            imgViewAudioPlay.setImageDrawable(resources.getDrawable(R.drawable.ic_audio_stop))
            imgViewAudioPlay.isEnabled = false
            progressBarForAudioPlay.visibility = View.VISIBLE

            mediaPlayer?.start()
            startPlayProgressUpdater()
        } else {
            Dialogs.INSTANCE.showLoader(this@JobDetailActivity)
            AppPreferences.getDriverSettings()?.data?.s3BucketVoiceNotes?.let {
                BykeaAmazonClient.getFileObject(url, object : Callback<File> {
                    override fun success(obj: File) {
                        Dialogs.INSTANCE.dismissDialog()

                        imgViewAudioPlay.setImageDrawable(resources.getDrawable(R.drawable.ic_audio_stop))
                        imgViewAudioPlay.isEnabled = false
                        progressBarForAudioPlay.visibility = View.VISIBLE

                        mediaPlayer = MediaPlayer()
                        mediaPlayer?.setDataSource(FileInputStream(obj).fd);
                        mediaPlayer?.prepare()
                        progressBarForAudioPlay.setMax(mediaPlayer?.duration!!);
                        mediaPlayer?.start()
                        startPlayProgressUpdater()
                    }

                    override fun fail(errorCode: Int, errorMsg: String) {
                        Dialogs.INSTANCE.dismissDialog()
                        binding.viewmodel?.showSnackbarMessage(R.string.no_voice_note_available)
                    }
                }, it)
            } ?: run {
                Dialogs.INSTANCE.dismissDialog()
                Dialogs.INSTANCE.showToast(getString(R.string.settings_are_not_updated))
            }
        }
    }

    /**
     * Set Markers for PickUp and DropOff
     */
    private fun setMarkersForPickUpAndDropOff(mMap: GoogleMap) {
        Util.safeLet(binding.viewmodel?.job?.value?.pickup?.lat, binding.viewmodel?.job?.value?.pickup?.lng) { lat, lng ->
            setMarker(mMap, LatLng(lat, lng), R.drawable.ic_marker_pickup)
        }
        Util.safeLet(binding.viewmodel?.job?.value?.dropoff?.lat, binding.viewmodel?.job?.value?.dropoff?.lng) { lat, lng ->
            if (binding.viewmodel?.job?.value?.dropoff?.lat != 0.0)
                setMarker(mMap, LatLng(lat, lng), R.drawable.ic_marker_dropoff)
        }
        setPickupBounds(mMap)
    }

    /**
     * Set Markets For Latitude and Longitude
     */
    private fun setMarker(mMap: GoogleMap, mLatLngPickUp: LatLng, drawable: Int) {
        mLatLngPickUp.let {
            val mMarker = mMap
                    .addMarker(MarkerOptions()
                            .icon(bitmapDescriptorFromVector(this, drawable))
                            .position(mLatLngPickUp))
            mMarkerList.add(mMarker)
        }
    }

    private fun bitmapDescriptorFromVector(context: Context?, vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context!!, vectorResId)
        vectorDrawable?.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(vectorDrawable?.intrinsicWidth!!, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun setPickupBounds(mMap: GoogleMap) {
        val cu = CameraUpdateFactory.newLatLngBounds(getCurrentLatLngBounds(), 30)
        val padding = resources.getDimension(R.dimen._70sdp).toInt()
        mMap.setPadding(0, padding, 0, 0)
        mMap.moveCamera(cu)
    }

    private fun getCurrentLatLngBounds(): LatLngBounds {
        val builder = LatLngBounds.Builder()
        for (marker in mMarkerList) {
            builder.include(marker.position)
        }
        val tmpBounds = builder.build()
        /* Add 2 points 1000m northEast and southWest of the center.
         * They increase the bounds only, if they are not already larger
         * than this.
         * 1000m on the diagonal translates into about 709m to each direction. */
        val center = tmpBounds.center
        val northEast = move(center, 709.0, 709.0)
        val southWest = move(center, -709.0, -709.0)

        builder.include(southWest)
        builder.include(northEast)
        return builder.build()
    }

    private fun move(startLL: LatLng, toNorth: Double, toEast: Double): LatLng {
        val lonDiff = meterToLongitude(toEast, startLL.latitude)
        val latDiff = meterToLatitude(toNorth)
        return LatLng(startLL.latitude + latDiff, startLL.longitude + lonDiff)
    }

    private fun meterToLongitude(meterToEast: Double, latitude: Double): Double {
        val latArc = Math.toRadians(latitude)
        val radius = Math.cos(latArc) * EARTHRADIUS
        val rad = meterToEast / radius
        return Math.toDegrees(rad)
    }

    private fun meterToLatitude(meterToNorth: Double): Double {
        val rad = meterToNorth / EARTHRADIUS
        return Math.toDegrees(rad)
    }

    @Synchronized
    fun getDriverRoadPosition(lat: Double, lng: Double) {
        if (lat != 0.0 && lng != 0.0) {
            updateDriverMarker(lat, lng)
        }
    }

    private fun updateDriverMarker(snappedLatitude: Double, snappedLongitude: Double) {
        if (null != mGoogleMap) {
            //if driver marker is null add driver marker on google map
            if (null == driverMarker) {
                val driverIcon = Utils.getBitmap(this@JobDetailActivity, R.drawable.ic_delivery_bike)
                driverMarker = mGoogleMap.addMarker(MarkerOptions().icon(
                        BitmapDescriptorFactory.fromBitmap(driverIcon))
                        .position(LatLng(snappedLatitude, snappedLongitude)))
                mMarkerList.add(driverMarker!!)
            }
        }
    }

    companion object {
        const val EXTRA_BOOKING_ID = "EXTRA_BOOKING_ID"
    }

    fun startPlayProgressUpdater() {
        progressBarForAudioPlay.setProgress(mediaPlayer?.currentPosition!!);
        if (mediaPlayer?.isPlaying!!) {
            val notification = Runnable {
                startPlayProgressUpdater();
            }
            handler.postDelayed(notification, 1000)
        } else {
            mediaPlayer?.pause();

            imgViewAudioPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_audio_play));
            imgViewAudioPlay.setEnabled(true);
            progressBarForAudioPlay.setVisibility(View.GONE);
            progressBarForAudioPlay.setProgress(0);
        }
    }

    override fun onPause() {
        if (mediaPlayer != null) {
            mediaPlayer?.pause();
            startPlayProgressUpdater();
        }
        super.onPause()
    }
}
