package com.bykea.pk.partner.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.bykea.pk.partner.DriverApp
import com.bykea.pk.partner.models.data.PlacesResult
import com.bykea.pk.partner.repositories.places.IPlacesDataHandler
import com.bykea.pk.partner.repositories.places.PlacesRepository
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.apache.commons.collections.CollectionUtils
import org.apache.commons.lang3.StringUtils
import java.util.*

/**
 * this class is responsible to obtain address after performing several location strategies
 * @author ArsalImam
 */
class GeocodeStrategyManager
/**
 * @param context context to create new instance (also required by [Geocoder])
 * @param placesDataHandler manage as a callback on location obtained from any source
 * @param prefix for address to manage redundant API Calls (will show only if [LatLng] are within mentioned radius)
 */
(val context: Context, val placesDataHandler: IPlacesDataHandler, val prefix: String) {

    private val TAG: String = GeocodeStrategyManager.javaClass.name

    /**
     * last address fetched by this manager per instance
     */
    private var lastReceivedLocation: String? = null

    /**
     * [currentLatLng] of which address is required
     */
    private var currentLatLng: LatLng? = null

    /**
     * last lat/lng fetched by this manager per instance
     */
    private var lastLatLng: LatLng? = null

    /**
     * repository instance to call google geocode api
     */
    private val placesRepository: PlacesRepository = PlacesRepository()

    /**
     * on the basis of mentioned latitude/longitude by performing different strategies
     * will trigger places data handler's callback
     *
     * @param latitude  obtained from gps
     * @param longitude obtained from gps
     */
    fun fetchLocation(latitude: Double, longitude: Double, isObtainFromGoogleApi: Boolean = true) {
        currentLatLng = LatLng(latitude, longitude)

        if (!StringUtils.isEmpty(lastReceivedLocation) && Utils.calculateDistance(currentLatLng?.latitude!!, currentLatLng?.longitude!!,
                        lastLatLng?.latitude!!, lastLatLng?.longitude!!) <= 25) {
            if (lastReceivedLocation?.contains(prefix, ignoreCase = true)!!)
                performCallback(lastReceivedLocation!!)
            else
                performCallback(prefix + lastReceivedLocation!!)
        } else {
            obtainLocationByApi(isObtainFromGoogleApi)
            lastLatLng = currentLatLng
        }
    }

    /**
     * this method will try to find address from [currentLatLng] from [Geocoder]/[PlacesRepository]
     * prioritization is mentioned as follows,
     * <ul>
     *     <li>[Geocoder] for better user experience, also a cost effective API</li>
     *     <li>[PlacesRepository] will make a network call to Google Geocode API, more expensive solution</li>
     * </ul>
     */
    private fun obtainLocationByApi(isObtainFromGoogleApi: Boolean) {
        var address = StringUtils.EMPTY
        Observable.fromCallable {
            try {
                val addresses: MutableList<Address>
                val geocoder = Geocoder(context, Locale.ENGLISH)
                addresses = geocoder.getFromLocation(currentLatLng?.latitude!!, currentLatLng?.longitude!!, 1)
                if (!CollectionUtils.isEmpty(addresses)) {
                    val fetchedAddress = addresses[Constants.DIGIT_ZERO]
                    var strAddress = String()
                    if (fetchedAddress.maxAddressLineIndex > -1) {
                        strAddress = fetchedAddress.getAddressLine(Constants.DIGIT_ZERO)
                    } else {
                        fetchedAddress.thoroughfare?.let {
                            strAddress = "$strAddress $it"
                        }

                        fetchedAddress.featureName?.let {
                            if (!strAddress.contains(fetchedAddress.featureName))
                                strAddress = "$strAddress $it"
                        }

                        fetchedAddress.locality?.let {
                            if (!strAddress.contains(fetchedAddress.locality))
                                strAddress = "$strAddress $it"
                        }
                    }

                    if (strAddress.contains(Constants.COMMA)) {
                        var commaSeperatedAddress = strAddress.split(Constants.COMMA)
                        if (commaSeperatedAddress.size > Constants.DIGIT_TWO) {
                            strAddress = commaSeperatedAddress.take(Constants.DIGIT_TWO).joinToString()
                        }
                    }

                    address = strAddress
                }
                address
            } catch (e: Exception) {
                Log.e(TAG, "Geocoding getFromLocation Failed")
                e.printStackTrace()
                address
            }
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<String> {
                    override fun onSubscribe(d: Disposable) {}

                    override fun onNext(address: String) {
                        if (StringUtils.isNotBlank(address)) {
                            Log.e(TAG, "location obtained from android.location.GeoCoder -> $address")
                            performCallback(address.trim().replace(Constants.COMMA, StringUtils.SPACE))
                        } else {
                            if (isObtainFromGoogleApi) {
                                obtainFromGoogleApi()
                            } else {
                                performCallback(null)
                            }
                        }
                    }

                    override fun onError(e: Throwable) {}

                    override fun onComplete() {}
                })
    }


    private fun performCallback(strAddress: String?) {
        lastReceivedLocation = strAddress
        val placeObject = PlacesResult(StringUtils.EMPTY, strAddress,
                currentLatLng?.latitude!!, currentLatLng?.longitude!!)
        placeObject.address?.let {
            placesDataHandler.onPlacesResponse(it)
        }
    }

    /**
     * will obtain location from Google API, calling this method in case of [Geocoder] failure
     */
    private fun obtainFromGoogleApi() {

        if (AppPreferences.getSettings().settings.isGoogleGeocodeEnable) {
            placesRepository.getGoogleGeoCoder(placesDataHandler, currentLatLng?.latitude.toString(), currentLatLng?.longitude.toString(), DriverApp.getContext())
            Log.e(TAG, "location obtained from PlacesRepository")
        } else {
            placesRepository.getOSMGeoCoder(placesDataHandler, currentLatLng?.latitude.toString(), currentLatLng?.longitude.toString(), DriverApp.getContext())
            Log.e(TAG, "location obtained from PlacesRepository")
        }
    }

    companion object {
        /**
         * This will return a new instance of this manager
         *
         * @param context context to create new instance (also required by [Geocoder])
         * @param placesDataHandler manage as a callback on location obtained from any source
         * @param prefix for address to manage redundant API Calls (will show only if [LatLng] are within mentioned radius)
         */
        @JvmStatic
        fun newInstance(context: Context, placesDataHandler: IPlacesDataHandler, prefix: String) =
                GeocodeStrategyManager(context = context, placesDataHandler = placesDataHandler, prefix = prefix)
    }
}