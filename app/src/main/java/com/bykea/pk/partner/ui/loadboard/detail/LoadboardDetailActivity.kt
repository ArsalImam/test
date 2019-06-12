package com.bykea.pk.partner.ui.loadboard.detail

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bykea.pk.partner.R
import com.bykea.pk.partner.databinding.LoadboardDetailActBinding
import com.bykea.pk.partner.models.data.loadboard.LoadboardBookingDetailData
import com.bykea.pk.partner.ui.activities.BaseActivity
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.ui.loadboard.common.obtainViewModel
import com.bykea.pk.partner.utils.Constants
import com.bykea.pk.partner.utils.Dialogs
import com.bykea.pk.partner.utils.Utils
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.loadboard_detail_act.*
import java.util.ArrayList

/**
 * Loadboard booking detail screen ACTIVITY - opening from homeScreen's loadboard listing items
 */
class LoadboardDetailActivity : BaseActivity() {

    private lateinit var binding: LoadboardDetailActBinding
    private var bookingId: Long = 0

    lateinit var mapView : SupportMapFragment
    private val mMarkerList = ArrayList<Marker>()
    private lateinit var mGoogleMap: GoogleMap
    private var driverMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.loadboard_detail_act)
        binding.viewmodel = obtainViewModel(BookingDetailViewModel::class.java).apply {
            dataLoading.observe(this@LoadboardDetailActivity, Observer {
                if (it) Dialogs.INSTANCE.showLoader(this@LoadboardDetailActivity)
                else Dialogs.INSTANCE.dismissDialog()
            })
        }
        binding.listener = object : BookingDetailUserActionsListener {
            override fun onPlayAudio(url: String?) {
                Dialogs.INSTANCE.showLoader(this@LoadboardDetailActivity)
            }

            override fun onNavigateToMap(pickLat: Double, pickLng: Double, dropLat: Double, dropLng: Double) {
                Utils.navigateToGoogleMap(this@LoadboardDetailActivity,pickLat, pickLng, dropLat, dropLng)
            }

            override fun onAcceptBooking() {

            }
        }

        bookingId = intent.getLongExtra(EXTRA_BOOKING_ID, 0)
        binding.viewmodel!!.start(bookingId)

        try {
            loadBoardMapFragment.onCreate(savedInstanceState);
            MapsInitializer.initialize(this);
        } catch (e:Exception) {
            Utils.redLog("HomeScreenException", e.message);
            e.printStackTrace();
        }
        (loadBoardMapFragment as SupportMapFragment).getMapAsync { p0 ->
            p0.setOnMapLoadedCallback {

                mGoogleMap = p0
                getDriverRoadPosition(AppPreferences.getLatitude(), AppPreferences.getLongitude())
                setMarkersForPickUpAndDropOff(p0)
            }
        }
    }

    companion object {
        const val EXTRA_BOOKING_ID = "EXTRA_BOOKING_ID"
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
    private val EARTHRADIUS = 6366198.0

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
                        .position(LatLng(snappedLatitude,snappedLongitude)))
                mMarkerList.add(driverMarker!!)
            }
        }
    }
/*

    */
    /**
     * initialize views and objects related to this screen
     */
    /*

    private fun initViews() {

        mRepository!!.loadboardBookingDetail(mCurrentActivity, bookingId, object : UserDataHandler() {
            override fun onLoadboardBookingDetailResponse(response: LoadboardBookingDetailResponse) {
                Dialogs.INSTANCE.dismissDialog()
                tVEstimatedFare!!.text = "Rs." + response.data.amount + ""
                tVCODAmount!!.text = "Rs." + response.data.cartAmount + ""

                //bookingNoTV.setText(response.getData().getOrderNo());
                //                bookingTypeIV.setImageResource();
                supportFragmentManager.beginTransaction()
                        .replace(R.id.bookingDetailContainerFL, LoadboardDetailFragment.newInstance(response.data))
                        .commitAllowingStateLoss()
            }

            override fun onError(errorCode: Int, errorMessage: String) {
                Dialogs.INSTANCE.dismissDialog()
                if (errorCode == HTTPStatus.UNAUTHORIZED) {
                    Utils.onUnauthorized(mCurrentActivity)
                } else {
                    Dialogs.INSTANCE.showToast(mCurrentActivity, errorMessage)
                }
            }
        })
    }

    */
    /**
     * initialize click listeners for this screen's button or widgets
     */
    /*

    private fun initListeners() {
        backBtn!!.setOnClickListener(this)
        imgViewDelivery!!.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.backBtn -> finish()

            R.id.imgViewDelivery -> Utils.appToast(applicationContext, "imgViewDelivery")
        }
    }
*/


}
