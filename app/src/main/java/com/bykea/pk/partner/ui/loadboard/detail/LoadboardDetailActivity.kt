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
import com.bykea.pk.partner.databinding.LoadboardDetailActBinding
import com.bykea.pk.partner.ui.activities.BaseActivity
import com.bykea.pk.partner.ui.helpers.ActivityStackManager
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.ui.loadboard.common.obtainViewModel
import com.bykea.pk.partner.ui.loadboard.common.setupSnackbar
import com.bykea.pk.partner.utils.Dialogs
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
import kotlinx.android.synthetic.main.activity_confirm_drop_off_address.*
import kotlinx.android.synthetic.main.loadboard_detail_act.*
import java.io.File
import java.io.FileInputStream
import java.util.*

/**
 * Loadboard booking detail screen ACTIVITY - opening from homeScreen's loadboard listing items
 */
class LoadboardDetailActivity : BaseActivity() {

    private val EARTHRADIUS = 6366198.0
    private lateinit var binding: LoadboardDetailActBinding
    private var bookingId: Long = 0
    private lateinit var mMediaPlayerHolder: MediaPlayerHolder
    private val mMarkerList = ArrayList<Marker>()
    lateinit var mapView: SupportMapFragment
    private lateinit var mGoogleMap: GoogleMap
    private var driverMarker: Marker? = null

    private var mediaPlayer: MediaPlayer? = null;
    private val handler: Handler = Handler();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.loadboard_detail_act)

        binding.viewmodel = obtainViewModel(BookingDetailViewModel::class.java).apply {
            view?.setupSnackbar(this@LoadboardDetailActivity, this.snackbarMessage, Snackbar.LENGTH_LONG)

            dataLoading.observe(this@LoadboardDetailActivity, Observer {
                if (it) Dialogs.INSTANCE.showLoader(this@LoadboardDetailActivity)
                else Dialogs.INSTANCE.dismissDialog()
            })

            acceptBookingCommand.observe(this@LoadboardDetailActivity, Observer {
                ActivityStackManager.getInstance().startJobActivity(this@LoadboardDetailActivity)
            })

            acceptFailedBookingCommand.observe(this@LoadboardDetailActivity, Observer {
                Dialogs.INSTANCE
                        .showAlertDialogTick(this@LoadboardDetailActivity,
                                this@LoadboardDetailActivity.resources.getString(R.string.booking_already_taken_title),
                                this@LoadboardDetailActivity.resources.getString(R.string.booking_already_taken_msg))
            })
        }
        binding.listener = object : BookingDetailUserActionsListener {
            override fun onPlayAudio(url: String?) {
                if (url != null) {
                    voiceClipPlayDownload(url)
                } else {
                    binding.viewmodel!!.showSnackbarMessage(R.string.no_voice_note_available)
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

            override fun onNavigateToMap(pickLat: Double, pickLng: Double, dropLat: Double, dropLng: Double) {
                Utils.navigateToGoogleMap(this@LoadboardDetailActivity, pickLat, pickLng, dropLat, dropLng)
            }

            override fun onAcceptBooking() {
                binding.viewmodel!!.accept()
            }

            override fun onBackClicked() {
                finish()
            }
        }
        binding.lifecycleOwner = this
        bookingId = intent.getLongExtra(EXTRA_BOOKING_ID, 0)
        binding.viewmodel!!.start(bookingId)

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
    }

    private fun voiceClipPlayDownload(url: String) {
        if (mediaPlayer != null) {
            imgViewAudioPlay.setImageDrawable(resources.getDrawable(R.drawable.ic_audio_stop))
            imgViewAudioPlay.isEnabled = false
            progressBarForAudioPlay.visibility = View.VISIBLE

            mediaPlayer?.start()
            startPlayProgressUpdater()
        } else {
            Dialogs.INSTANCE.showLoader(this@LoadboardDetailActivity)
            BykeaAmazonClient.getFileObject(url, object : Callback<File> {
                override fun success(obj: File) {
                    Dialogs.INSTANCE.dismissDialog()

                    imgViewAudioPlay.setImageDrawable(resources.getDrawable(R.drawable.ic_audio_stop))
                    imgViewAudioPlay.isEnabled = false
                    progressBarForAudioPlay.visibility = View.VISIBLE

                    mediaPlayer = MediaPlayer()
                    mediaPlayer?.setDataSource(FileInputStream(obj).fd);
                    mediaPlayer?.prepare()
                    progressBarForAudioPlay.setMax(mediaPlayer!!.duration);
                    mediaPlayer?.start()
                    startPlayProgressUpdater()
                }

                override fun fail(errorCode: Int, errorMsg: String) {
                    Dialogs.INSTANCE.dismissDialog()
                    binding.viewmodel!!.showSnackbarMessage(R.string.no_voice_note_available)
                }
            })
        }
    }

    private fun setMarkersForPickUpAndDropOff(mMap: GoogleMap) {
        val mLatLngPickUp = LatLng(binding.viewmodel?.booking?.value?.pickup?.lat!!, binding.viewmodel?.booking?.value?.pickup?.lng!!)
        setMarker(mMap, mLatLngPickUp, R.drawable.ic_marker_pickup)

        val mLatLngDropOff = LatLng(binding.viewmodel?.booking?.value?.dropoff?.lat!!, binding.viewmodel?.booking?.value?.dropoff?.lng!!)
        setMarker(mMap, mLatLngDropOff, R.drawable.ic_marker_dropoff)

        setPickupBounds(mMap)
    }

    private fun setMarker(mMap: GoogleMap, mLatLngPickUp: LatLng, drawable: Int) {
        val mMarker = mMap
                .addMarker(MarkerOptions()
                        .icon(bitmapDescriptorFromVector(this, drawable))
                        .position(mLatLngPickUp))
        mMarkerList.add(mMarker)
    }

    private fun bitmapDescriptorFromVector(context: Context?, vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context!!, vectorResId)
        vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
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
                val driverIcon = Utils.getBitmap(this@LoadboardDetailActivity, R.drawable.ic_delivery_bike)
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
        progressBarForAudioPlay.setProgress(mediaPlayer!!.currentPosition);
        if (mediaPlayer!!.isPlaying) {
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
