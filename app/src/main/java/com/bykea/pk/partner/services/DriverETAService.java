package com.bykea.pk.partner.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.bykea.pk.partner.models.response.GoogleDistanceMatrixApi;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.repositories.places.PlacesDataHandler;
import com.bykea.pk.partner.repositories.places.PlacesRepository;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.TripStatus;
import com.bykea.pk.partner.utils.Utils;
import com.google.android.gms.maps.model.LatLng;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

/***
 * Driver ETA Intent Service which handles ETA update
 */
public class DriverETAService extends IntentService {

    private int counter = 0;
    private EventBus mBus = EventBus.getDefault();
    private final int DISTANCE_MATRIX_API_CALL_TIME = 6;

    private LatLng lastApiCallLatLng;

    /**
     * Constructor
     */
    public DriverETAService() {
        super("DriverETAService");
    }

    /***
     * Start Driver ETA intent service.
     * @param context calling context.
     */
    public static void startDriverETAUpdate(Context context) {
        Intent intent = new Intent(context, DriverETAService.class);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            updateETAIfRequired();
        }
    }

    /**
     * Update ETA time and distance in shared preference.
     *
     * @param time     updated time which needs to be saved.
     * @param distance updated distance which needs to be saved.
     */
    private void updateETA(String time, String distance) {
        AppPreferences.setEta(time);
        AppPreferences.setEstimatedDistance(distance);
        mBus.post(Keys.ETA_IN_BG_UPDATED);
    }

    /**
     * when Booking Screen is in background & driver is in any trip then, we need to call distance
     * matrix API in order to get Estimated time & distance, when booking screen is in foreground it
     * is already being handled via Direction API when we are showing Route to driver.
     * counter == DISTANCE_MATRIX_API_CALL_TIME == 6 indicates that API will be called after 60 sec
     */
    private void updateETAIfRequired() {
        if (counter == DISTANCE_MATRIX_API_CALL_TIME) {
            counter = 0;
        }
        counter++;
        if (AppPreferences.isOnTrip() && !AppPreferences.isJobActivityOnForeground() && counter == DISTANCE_MATRIX_API_CALL_TIME) {
            Utils.redLogLocation("Direction -> Trip Status ", AppPreferences.getTripStatus());
            if (TripStatus.ON_START_TRIP.equalsIgnoreCase(AppPreferences.getTripStatus())) {
                NormalCallData callData = AppPreferences.getCallData();
                if (callData != null && StringUtils.isNotBlank(callData.getEndLat()) &&
                        StringUtils.isNotBlank(callData.getEndLng())) {
                    String destination = callData.getEndLat() + "," + callData.getEndLng();
                    callDistanceMatrixApi(destination);
                } else {
                    //in case when there is no drop off add distance covered and time taken
                    updateETA(Utils.getTripTime(), Utils.getTripDistance());

                }
            } else if (TripStatus.ON_ACCEPT_CALL.equalsIgnoreCase(AppPreferences.getTripStatus())
                    || TripStatus.ON_ARRIVED_TRIP.equalsIgnoreCase(AppPreferences.getTripStatus())) {
                NormalCallData callData = AppPreferences.getCallData();
                if (callData != null && StringUtils.isNotBlank(callData.getStartLat()) &&
                        StringUtils.isNotBlank(callData.getStartLng())) {
                    String destination = callData.getStartLat() + "," + callData.getStartLng();
                    callDistanceMatrixApi(destination);
                }
            }

        }
    }

    /***
     * check if last start latlng and current start latlng has at least 15 m difference
     * @param currentApiCallLatLng current Latitude and Longitude returned from API
     * @return True is current lat/lng and start lat/lng have 15m difference else return false
     */
    private boolean isDirectionApiCallRequired(LatLng currentApiCallLatLng) {
        if (lastApiCallLatLng != null &&
                Utils.calculateDistance(currentApiCallLatLng.latitude,
                        currentApiCallLatLng.longitude,
                        lastApiCallLatLng.latitude,
                        lastApiCallLatLng.longitude
                ) < 15) {
            return false;
        } else {
            return true;
        }
    }

    /***
     * Send request to DistanceMatrix API.
     * @param destination destination request address
     */
    private void callDistanceMatrixApi(String destination) {
        LatLng newLatLng = new LatLng(AppPreferences.getLatitude(), AppPreferences.getLongitude());
        if (isDirectionApiCallRequired(newLatLng) && Connectivity.isConnected(this)) {
            lastApiCallLatLng = newLatLng;
            String origin = newLatLng.latitude + "," + newLatLng.longitude;
            new PlacesRepository().getDistanceMatrix(origin, destination, this,
                    new PlacesDataHandler() {
                        @Override
                        public void onDistanceMatrixResponse(GoogleDistanceMatrixApi response) {
                            if (response != null && response.getRows() != null
                                    && response.getRows().length > 0
                                    && response.getRows()[0].getElements() != null
                                    && response.getRows()[0].getElements().length > 0
                                    && response.getRows()[0].getElements()[0].getDuration() != null
                                    && response.getRows()[0].getElements()[0].getDistance() != null) {
                                String time = (response.getRows()[0]
                                        .getElements()[0].getDuration().getValueInt() / 60) + "";
                                String distance = Utils.formatDecimalPlaces(
                                        (response.getRows()[0].getElements()[0]
                                                .getDistance().getValueInt() / 1000.0) + "", 1);
                                updateETA(time, distance);
                                Utils.redLogLocation("onDistanceMatrixResponse",
                                        "Time -> " + time + " Distance ->" + distance);
                            }
                        }
                    });
        }
    }

}
