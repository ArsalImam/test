package com.bykea.pk.partner.ui.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.bykea.pk.partner.BuildConfig;
import com.bykea.pk.partner.DriverApp;
import com.bykea.pk.partner.dal.LocCoordinatesInTrip;
import com.bykea.pk.partner.models.ReceivedMessageCount;
import com.bykea.pk.partner.models.data.CitiesData;
import com.bykea.pk.partner.models.data.MultiDeliveryCallDriverData;
import com.bykea.pk.partner.models.data.NotificationData;
import com.bykea.pk.partner.models.data.PilotData;
import com.bykea.pk.partner.models.data.PlacesResult;
import com.bykea.pk.partner.models.data.SavedPlaces;
import com.bykea.pk.partner.models.data.SettingsData;
import com.bykea.pk.partner.models.data.TrackingData;
import com.bykea.pk.partner.models.data.ZoneData;
import com.bykea.pk.partner.models.response.GetCitiesResponse;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.models.data.MultiDeliveryCallDriverData;
import com.bykea.pk.partner.models.response.ZoneAreaResponse;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.TripStatus;
import com.bykea.pk.partner.utils.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import static com.bykea.pk.partner.utils.Constants.DIGIT_ZERO;

public class AppPreferences {

    private static SharedPreferences mSharedPreferences = DriverApp.getApplication().getBasicComponent().getSharedPref();

    public static void clear() {
        mSharedPreferences
                .edit()
                .clear()
                .apply();
    }

    public static void saveSettingsData(SettingsData data) {
        mSharedPreferences
                .edit()
                .putString(Keys.SETTING_DATA, new Gson().toJson(data))
                .apply();
    }


    /**
     * @param key : Required Key
     * @return : true if key is present in shared preferences
     */
    public static boolean checkKeyExist(String key) {
        return mSharedPreferences.contains(key);
    }

    public static SettingsData getSettings() {
        String data = mSharedPreferences.getString(Keys.SETTING_DATA, StringUtils.EMPTY);
        SettingsData settingsData = null;
        if (StringUtils.isBlank(data)) {
            settingsData = new SettingsData();
        } else {
            settingsData = new Gson().fromJson(data, SettingsData.class);
        }
        return settingsData;
    }

    public static void setDropOffData(String address, double lat, double lng) {
        SharedPreferences.Editor ed = mSharedPreferences.edit();
        ed.putString("dropOffAddress", address);
        ed.putString("dropOffLat", lat + "");
        ed.putString("dropOffLng", lng + "");
        ed.apply();
    }

    public static String getDropOffAddress() {
        return mSharedPreferences.getString("dropOffAddress", "");
    }

    public static String getDropOffLat() {
        return mSharedPreferences.getString("dropOffLat", "0");
    }

    public static String getDropOffLng() {
        return mSharedPreferences.getString("dropOffLng", "0");
    }

    public static void setPilotData(PilotData user) {
        if (null != user) {
            SharedPreferences.Editor ed = mSharedPreferences.edit();
            ed.putString(Keys.EMAIL, user.getEmail());
            ed.putString(Keys.ACCESS_TOKEN, user.getAccessToken());
            ed.putString(Keys.DRIVER_ID, user.getId());
            ed.putString(Keys.PHONE_NUMBER, user.getPhoneNo());
            ed.putString(Keys.DRIVER_DATA, new Gson().toJson(user));
            ed.apply();
        } else {
            SharedPreferences.Editor ed = mSharedPreferences.edit();
            ed.putString(Keys.EMAIL, "");
            ed.putString(Keys.ACCESS_TOKEN, "");
            ed.putString(Keys.DRIVER_ID, "");
            ed.putString(Keys.DRIVER_DATA, "");
            ed.apply();
        }
    }

    public static PilotData getPilotData() {
        String data = mSharedPreferences.getString(Keys.DRIVER_DATA, StringUtils.EMPTY);
        if (StringUtils.isNotBlank(data)) {
            return new Gson().fromJson(data, PilotData.class);
        } else {
            return new PilotData();
        }
    }

    public static void setEta(String eta) {
        SharedPreferences.Editor ed = mSharedPreferences.edit();
        ed.putString("eta", eta);
        ed.putLong("etaTime", System.currentTimeMillis());
        ed.apply();
    }

    public static String getEta() {
        return mSharedPreferences
                .getString("eta", "0");
    }

    public static long getEtaUpdatedTime() {
        return mSharedPreferences.getLong("etaTime", 0);
    }

    public static void setEstimatedDistance(String value) {
        mSharedPreferences
                .edit()
                .putString("distance", value)
                .apply();
    }

    public static String getEstimatedDistance() {
        return mSharedPreferences.getString("distance", "0");
    }

    public static void setPhoneNumber(String phone) {
        SharedPreferences.Editor ed = mSharedPreferences.edit();
        ed.putString(Keys.PHONE_NUMBER, phone);
        ed.commit();
    }

    public static String getPhoneNumber() {
        return mSharedPreferences.getString(Keys.PHONE_NUMBER, StringUtils.EMPTY);
    }


    public static String getDriverEmail() {
        return mSharedPreferences.getString(Keys.EMAIL, StringUtils.EMPTY);
    }

    /**
     * @Boolean Is Email Verified
     */
    public static boolean isEmailVerified() {
        return mSharedPreferences.getBoolean(Keys.EMAIL_VERIFIED, false);
    }

    /**
     * Set Email Verified
     */
    public static void setEmailVerified() {
        SharedPreferences.Editor ed = mSharedPreferences.edit();
        ed.putBoolean(Keys.EMAIL_VERIFIED, true);
        ed.commit();
    }

    /**
     * @Date Get Zendesk SDK Initialization Time
     */
    public static Date getZendeskSDKSetupTime() {
        return new Gson().fromJson(mSharedPreferences.getString(Keys.ZENDESK_IDENTITY_SETUP_TIME, StringUtils.EMPTY), Date.class);
    }

    /**
     * Set Zendesk SDK Initialization Time  (Current Time)
     */
    public static void setZendeskSDKSetupTime() {
        SharedPreferences.Editor ed = mSharedPreferences.edit();
        ed.putString(Keys.ZENDESK_IDENTITY_SETUP_TIME, new Gson().toJson(new Date()));
        ed.commit();
    }

    /**
     * @Boolean Is Zendesk Is Ready Or Not
     */
    public static boolean isZendeskSDKReady() {
        return mSharedPreferences.getBoolean(Keys.ZENDESK_SDK_READY, false);
    }

    /**
     * Set Zendesk SDK Is Ready To Use
     */
    public static void setZendeskSDKReady() {
        SharedPreferences.Editor ed = mSharedPreferences.edit();
        ed.putBoolean(Keys.ZENDESK_SDK_READY, true);
        ed.commit();
    }

    /**
     * Set Email For Driver
     *
     * @param email : Requested Email
     */
    public static void setDriverEmail(String email) {
        mSharedPreferences
                .edit()
                .putString(Keys.EMAIL, email)
                .apply();
    }

    public static String getDriverId() {
        return mSharedPreferences.getString(Keys.DRIVER_ID, StringUtils.EMPTY);
    }

    public static void setRegId(String id) {
        mSharedPreferences
                .edit()
                .putString(Keys.FCM_REGISTRATION_ID, id)
                .apply();
    }

    public static String getRegId() {
        return mSharedPreferences.getString(Keys.FCM_REGISTRATION_ID, StringUtils.EMPTY);
    }

    public static String getAccessToken() {
        return mSharedPreferences.getString(Keys.ACCESS_TOKEN, StringUtils.EMPTY);
    }

    public static void savePhoneNumberVerified(boolean value) {
        mSharedPreferences
                .edit()
                .putBoolean(Keys.PHONE_NUMBER_VERIFIED, value)
                .apply();
    }

    public static boolean isPhoneNumberVerified() {
        return mSharedPreferences.getBoolean(Keys.PHONE_NUMBER_VERIFIED, false);
    }


    public static void setAvailableStatus(boolean status) {
        mSharedPreferences
                .edit()
                .putBoolean(Keys.AVAILABLE_STATUS, status)
                .apply();
    }

    public static void setAvailableAPICalling(boolean status) {
        mSharedPreferences
                .edit()
                .putBoolean(Keys.AVAILABLE_STATUS_API_CALL, status)
                .apply();
    }

    public static boolean isAvailableStatusAPICalling() {
        return mSharedPreferences.getBoolean(Keys.AVAILABLE_STATUS_API_CALL, false);
    }

    public static boolean getAvailableStatus() {
        return mSharedPreferences.getBoolean(Keys.AVAILABLE_STATUS, false);
    }

    public static void saveLoginStatus(boolean isLoggedIn) {
        mSharedPreferences
                .edit()
                .putBoolean(Keys.LOGIN_STATUS, isLoggedIn)
                .apply();
    }

    public static boolean isLoggedIn() {
        return mSharedPreferences.getBoolean(Keys.LOGIN_STATUS, false);
    }

    public static void setSignUpApiCalled(boolean value) {
        mSharedPreferences
                .edit()
                .putBoolean(Keys.SIGN_UP_API_CALL_CHECK, value)
                .apply();
    }

    public static boolean isSignUpApiCalled() {
        return mSharedPreferences.getBoolean(Keys.SIGN_UP_API_CALL_CHECK, false);
    }

    private static boolean isLocationAccurate(float accuracy) {
        if (AppPreferences.isLoggedIn()) {
            return accuracy < 100f;
        } else {
            return accuracy < 10000f;
        }
    }

    public static void saveLocation(LatLng location, String bearing, float accuracy, boolean isMock) {
        if (location.latitude != 0.0 && location.longitude != 0.0 && isLocationAccurate(accuracy)) {
            SharedPreferences.Editor ed = mSharedPreferences.edit();
            ed.putString(Keys.LATITUDE, location.latitude + "");
            ed.putString(Keys.LONGITUDE, location.longitude + "");
            ed.putString(Keys.BEARING, bearing + "");
            ed.putBoolean(Keys.IS_MOCK_LOCATION, isMock);
            ed.putFloat(Keys.LOCATION_ACCURACY, accuracy);
            ed.apply();
            updateTrackingData(location);
        }

    }


    private static void updateTrackingData(LatLng location) {
        if (isOnTrip()) {
            TrackingData latLng = new TrackingData();
            latLng.setLat(location.latitude + "");
            latLng.setLng(location.longitude + "");
            ArrayList<TrackingData> prevLatLngList = getTrackingData();
//            int size = prevLatLngList.size();
//            if (size > 0 && prevLatLngList.get(size - 1))
            prevLatLngList.add(latLng);
            String value = new Gson().toJson(prevLatLngList, new TypeToken<ArrayList<TrackingData>>() {
            }.getType());
            mSharedPreferences.edit().putString(Keys.TRACKING_DATA, value).apply();
        }
    }

    public static void clearTrackingData() {
        mSharedPreferences.edit().putString(Keys.TRACKING_DATA, StringUtils.EMPTY).apply();
    }

    public static ArrayList<TrackingData> getTrackingData() {
        String jsonString = mSharedPreferences.getString(Keys.TRACKING_DATA, StringUtils.EMPTY);
        ArrayList<TrackingData> latLngList = new ArrayList<>();
        if (StringUtils.isNotBlank(jsonString)) {
            latLngList = new Gson().fromJson(jsonString, new TypeToken<ArrayList<TrackingData>>() {
            }.getType());
        }
        return latLngList;
    }

    public static void saveLocation(double latitude, double longitude) {
        if (latitude != 0.0 && longitude != 0.0) {
            SharedPreferences.Editor ed = mSharedPreferences.edit();
            ed.putString(Keys.LATITUDE, latitude + "");
            ed.putString(Keys.LONGITUDE, longitude + "");
            ed.putBoolean(Keys.IS_MOCK_LOCATION, false);
            ed.putFloat(Keys.LOCATION_ACCURACY, 10);
            ed.apply();
        }
    }

    public static void saveLocationFromLogin(Context context, LatLng location, String bearing, float accuracy, boolean isMock) {
        if (location.latitude != 0.0 && location.longitude != 0.0 /*&& accuracy < 100f*/) {
            SharedPreferences.Editor ed = mSharedPreferences.edit();
            ed.putString(Keys.LATITUDE, location.latitude + "");
            ed.putString(Keys.LONGITUDE, location.longitude + "");
            ed.putString(Keys.BEARING, bearing + "");
            ed.putBoolean(Keys.IS_MOCK_LOCATION, isMock);
            ed.putFloat(Keys.LOCATION_ACCURACY, accuracy);
            ed.apply();
        }

    }

    public static double getLatitude() {
        return Double.parseDouble(mSharedPreferences.getString(Keys.LATITUDE, "0.0"));
    }

    public static double getLongitude() {
        return Double.parseDouble(mSharedPreferences.getString(Keys.LONGITUDE, "0.0"));
    }

    public static boolean isFromMockLocation() {
        return !BuildConfig.DEBUG && mSharedPreferences.getBoolean(Keys.IS_MOCK_LOCATION, false);
    }

    public static double getBearing() {
        String bearing = mSharedPreferences.getString(Keys.BEARING, "0.0");
        if (StringUtils.isBlank(bearing)) {
            bearing = "0.0";
        }
        return Double.parseDouble(bearing);
    }

    public static void setCallData(NormalCallData callData) {
        mSharedPreferences
                .edit()
                .putString(Keys.CALLDATA_OBJECT, new Gson().toJson(callData))
                .apply();
    }

    public static void setRating(String rating) {
        mSharedPreferences
                .edit()
                .putFloat(getDriverId() + "driverRating", Float.parseFloat(rating))
                .apply();
    }

    public static float getRating() {
        return mSharedPreferences.getFloat(getDriverId() + "driverRating", 0);
    }


    public static NormalCallData getCallData() {
        Gson gson = new Gson();
        return gson.fromJson(mSharedPreferences.getString(Keys.CALLDATA_OBJECT, StringUtils.EMPTY),
                NormalCallData.class);
    }


    public static void setIsOnTrip(boolean value) {
        mSharedPreferences
                .edit()
                .putBoolean(Keys.ON_TRIP, value)
                .apply();
    }

    public static boolean isOnTrip() {
        return mSharedPreferences.getBoolean(Keys.ON_TRIP, false);
    }

    public static void setTripStatus(String value) {
        Log.d("FREEONCALL", value);
        mSharedPreferences
                .edit()
                .putString(Keys.TRIP_STATUS, value)
                .apply();
    }

    public static String getTripStatus() {
        return mSharedPreferences.getString(Keys.TRIP_STATUS, TripStatus.ON_FREE);
    }

    public static void setBeginTrip(boolean value) {
        mSharedPreferences
                .edit()
                .putBoolean(Keys.ON_BEGIN_TRIP, value)
                .apply();
        Utils.redLog(Constants.APP_NAME + " BeginTripStatus", value + "");
    }

    public static boolean isOnBeginTrip() {
        Utils.redLog(Constants.APP_NAME + " isOnBeginTripStatus", mSharedPreferences.getBoolean(Keys.ON_BEGIN_TRIP, false) + "");
        return mSharedPreferences.getBoolean(Keys.ON_BEGIN_TRIP, false);
    }

    public static void setEndTrip(boolean value) {
        SharedPreferences.Editor ed = mSharedPreferences.edit();
        ed.putBoolean(Keys.ON_END_TRIP, value);
        ed.apply();
        // Clear distance only on begin trip not on end trip
        if (value) {
            ed.putFloat(Keys.TRIP_TOTAL_DISTANCE, 0.0f);
        }
        // It makes sure that no time and distance is being added
        AppPreferences.setBeginTrip(false);
        Utils.redLog(Constants.APP_NAME + " isOnEndTripStatus", value + "");
    }

    public static boolean isOnEndTrip() {
        Utils.redLog(Constants.APP_NAME + " isOnEndTripStatus", mSharedPreferences.getBoolean(Keys.ON_END_TRIP, false) + "");
        return mSharedPreferences.getBoolean(Keys.ON_END_TRIP, false);
    }

    /**
     * Fetch TRIP_ACCEPT_TIME from App Shared Pref
     *
     * @return TRIP_ACCEPT_TIME
     */
    public static long getTripAcceptTime() {
        return mSharedPreferences.getLong(Keys.TRIP_ACCEPT_TIME, 0);
    }

    /**
     * Put TRIP_ACCEPT_TIME to App Shared Pref
     *
     * @param time TRIP_ACCEPT_TIME
     */
    public static void setTripAcceptTime(long time) {
        mSharedPreferences
                .edit()
                .putLong(Keys.TRIP_ACCEPT_TIME, time)
                .apply();
    }

    public static long getStartTripTime() {
        return mSharedPreferences.getLong(Keys.TRIP_START_TIME, 0);
    }

    public static void setStartTripTime(long startTime) {
        mSharedPreferences
                .edit()
                .putLong(Keys.TRIP_START_TIME, startTime)
                .apply();
    }

    public static void setIncomingCall(boolean flag) {
        mSharedPreferences
                .edit()
                .putBoolean(Keys.INCOMING_CALL, flag)
                .apply();
    }

    public static boolean isIncomingCall() {
        return mSharedPreferences.getBoolean(Keys.INCOMING_CALL, true);
    }

    public static void setCallType(String callType) {
        mSharedPreferences
                .edit()
                .putString(Keys.CALL_TYPE, callType)
                .apply();
    }

    public static String getCallType() {
        return mSharedPreferences.getString(Keys.CALL_TYPE, "");
    }

    public static void setLastMessageID(String value) {
        mSharedPreferences
                .edit()
                .putString(Keys.LAST_MESSAGE_ID, value)
                .apply();
    }

    public static String getLastMessageID() {
        return mSharedPreferences.getString(Keys.LAST_MESSAGE_ID, StringUtils.EMPTY);
    }

    public static void setAdminMsg(NotificationData data) {
        mSharedPreferences
                .edit()
                .putString(Keys.ADMIN_MSG, data != null ? new Gson().toJson(data) : StringUtils.EMPTY)
                .apply();
    }

    public static String getAdminMsg() {
        return mSharedPreferences.getString(Keys.ADMIN_MSG, StringUtils.EMPTY);
    }

    public static boolean isJobActivityOnForeground() {
        return mSharedPreferences.getBoolean(Keys.JOB_ACTIVITY_FOREGROUND, false);
    }

    public static void setJobActivityOnForeground(boolean value) {
        mSharedPreferences
                .edit()
                .putBoolean(Keys.JOB_ACTIVITY_FOREGROUND, value)
                .apply();
    }

    /**
     * Set Mutlidelivery activity is visible or not
     * Return true if visible else false
     */
    public static boolean isMultiDeliveryJobActivityOnForeground() {
        return mSharedPreferences.getBoolean(Keys.MULTIDELIVERY_JOB_ACTIVITY_FOREGROUND, false);
    }

    /**
     * Set Mutlidelivery activity is visible or not
     * @param value : True or False
     */
    public static void setMultiDeliveryJobActivityOnForeground(boolean value) {
        mSharedPreferences
                .edit()
                .putBoolean(Keys.MULTIDELIVERY_JOB_ACTIVITY_FOREGROUND, value)
                .apply();
    }


    /**
     * Check If Distance Matrix Is Required To Call For Mutlidelivery Or Not
     * @return true if required else false
     */
    public static boolean isMultiDeliveryDistanceMatrixCalledRequired() {
        return mSharedPreferences.getBoolean(Keys.MULTIDELIVERY_DISTANCE_MATRIX_CALLED_REQUIRED, false);
    }

    /**
     * Set Distance Matrix Is Required To Call For Mutlidelivery Or Not
     * @param value : True or False
     */
    public static void setMultiDeliveryDistanceMatrixCalledRequired(boolean value) {
        mSharedPreferences
                .edit()
                .putBoolean(Keys.MULTIDELIVERY_DISTANCE_MATRIX_CALLED_REQUIRED, value)
                .apply();
    }

    /**
     * Check If Drop Of Update Is Required Or Not
     * @return  True/False
     */
    public static boolean isDropOffUpdateRequired() {
        return mSharedPreferences.getBoolean(Keys.DROP_OFF_UPDATE_REQUIRED, false);
    }

    /**
     * Set Drop Off Update Required
     * If Application Is Running But Booking Activity Is In Background or Chat Activity Is In Foreground
     * Set To False After Execution Of True
     * @param value : True/False (On Behalf Of Above Decision)
     */
    public static void setDropOffUpdateRequired(boolean value) {
        mSharedPreferences
                .edit()
                .putBoolean(Keys.DROP_OFF_UPDATE_REQUIRED, value)
                .apply();
    }

    public static boolean isChatActivityOnForeground() {
        return mSharedPreferences.getBoolean(Keys.CHAT_ACTIVITY_FOREGROUND, false);
    }

    public static void setChatActivityOnForeground(boolean value) {
        mSharedPreferences
                .edit()
                .putBoolean(Keys.CHAT_ACTIVITY_FOREGROUND, value)
                .apply();
    }

    public static boolean isHomeActivityOnForeground() {
        return mSharedPreferences.getBoolean(Keys.HOME_ACTIVITY_FOREGROUND, false);
    }

    public static void setHomeActivityOnForeground(boolean value) {
        mSharedPreferences
                .edit()
                .putBoolean(Keys.HOME_ACTIVITY_FOREGROUND, value)
                .apply();
    }

    public static boolean isCallingActivityOnForeground() {
        return mSharedPreferences.getBoolean(Keys.CALLING_ACTIVITY_FOREGROUND, false);
    }

    public static void setCallingActivityOnForeground(boolean value) {
        mSharedPreferences
                .edit()
                .putBoolean(Keys.CALLING_ACTIVITY_FOREGROUND, value)
                .apply();
    }

    public static void saveLastUpdatedLocation(LatLng location) {
        SharedPreferences.Editor ed = mSharedPreferences.edit();
        if (location.latitude != 0.0 && location.longitude != 0.0) {
            ed.putString(Keys.LATITUDE_LAST_UPDATED, location.latitude + "");
            ed.putString(Keys.LONGITUDE_LAST_UPDATED, location.longitude + "");
            ed.putString(Keys.TIME_LAST_UPDATED, "0");
        }
        ed.apply();
    }

    public static String getLastUpdatedLatitude() {
        return mSharedPreferences.getString(Keys.LATITUDE_LAST_UPDATED, "0.0");
    }

    public static String getLastUpdatedLongitude() {
        return mSharedPreferences.getString(Keys.LONGITUDE_LAST_UPDATED, "0.0");
    }

    public static String getLastUpdatedTime() {
        return mSharedPreferences.getString(Keys.TIME_LAST_UPDATED, "0");
    }

    public static void saveLocationAccuracy(LatLng location) {
        SharedPreferences.Editor ed = mSharedPreferences.edit();
        if (location.latitude != 0.0 && location.longitude != 0.0) {
            ed.putString(Keys.LATITUDE_LAST_UPDATED, location.latitude + "");
            ed.putString(Keys.LONGITUDE_LAST_UPDATED, location.longitude + "");
        }
        ed.apply();
    }

    public static float getLocationAccuracy() {
        return mSharedPreferences.getFloat(Keys.LOCATION_ACCURACY, 200f);
    }


    /*
     * Sync Server Time with Device Time.
     * */
    public static void setServerTimeDifference(long value) {
        Utils.redLog("Time Difference", "" + value);
        mSharedPreferences
                .edit()
                .putLong(Keys.SERVER_TIME_DIFFERENCE, value)
                .apply();
    }

    public static long getServerTimeDifference() {
        return mSharedPreferences.getLong(Keys.SERVER_TIME_DIFFERENCE, 0);
    }

    public synchronized static void setLocationEmitTime() {
        mSharedPreferences
                .edit()
                .putLong(Keys.LOCATION_EMIT_TIME, System.currentTimeMillis())
                .apply();
    }

    public static long getLocationEmitTime() {
        return mSharedPreferences.getLong(Keys.LOCATION_EMIT_TIME, System.currentTimeMillis());
    }

    public static boolean isOutOfFence() {
        return mSharedPreferences.getBoolean(Keys.IS_OUT_OF_FENCE, false);
    }

    public static void setOutOfFence(boolean value) {
        mSharedPreferences
                .edit()
                .putBoolean(Keys.IS_OUT_OF_FENCE, value)
                .apply();
    }


    public static boolean isWalletAmountIncreased() {
        return mSharedPreferences.getBoolean(Keys.IS_WALLET_AMOUNT_INCREASED, false);
    }

    public static void setWalletAmountIncreased(boolean value) {
        mSharedPreferences
                .edit()
                .putBoolean(Keys.IS_WALLET_AMOUNT_INCREASED, value)
                .apply();
    }

    public static void setProfileUpdated(boolean value) {
        mSharedPreferences
                .edit()
                .putBoolean(Keys.IS_PROFILE_UPDATED, value)
                .apply();
    }

    public static boolean isProfileUpdated() {
        return mSharedPreferences.getBoolean(Keys.IS_PROFILE_UPDATED, true);
    }


    public static void setLastDirectionsApiCallTime(long value) {
        mSharedPreferences
                .edit()
                .putLong(Keys.LAST_DIRECTIONS_API_CALL_TIME, value)
                .apply();
    }


    public static long getLastDirectionsApiCallTime() {
        return mSharedPreferences.getLong(Keys.LAST_DIRECTIONS_API_CALL_TIME, 0);
    }

    public static float getDistanceCoveredInMeters() {
        return mSharedPreferences.getFloat(Keys.DISTANCE_COVERED, 0);
    }

    public static void setDistanceCoveredInMeters(float value) {
        mSharedPreferences
                .edit()
                .putFloat(Keys.DISTANCE_COVERED, value + getDistanceCoveredInMeters())
                .apply();
    }

    public static void clearTripDistanceData() {
        SharedPreferences.Editor ed = mSharedPreferences.edit();
        ed.putFloat(Keys.DISTANCE_COVERED, 0f);
        ed.putString(Keys.LATITUDE_PREV_DISTANCE, "0.0");
        ed.putString(Keys.LONGITUDE_PREV_DISTANCE, "0.0");
        ed.putLong(Keys.TIME_PREV_DISTANCE, 0);
        ed.putString(Keys.IN_TRIP_LAT_LNG_ARRAY, StringUtils.EMPTY);
        ed.putLong(Keys.TRIP_START_TIME, 0);
        ed.apply();
    }

    public static void setVersionCheckTime(long value) {
        mSharedPreferences
                .edit()
                .putLong(Keys.VERSION_CHECK_TIME, value)
                .apply();
    }

    public static long getVersionCheckTime() {
        return mSharedPreferences.getLong(Keys.VERSION_CHECK_TIME, 0);
    }

    public static void setPrevDistanceLatLng(double lat, double lon, boolean updatePrevTime) {
        SharedPreferences.Editor ed = mSharedPreferences.edit();
        if (lat != 0.0 && lon != 0.0) {
            ed.putString(Keys.LATITUDE_PREV_DISTANCE, lat + "");
            ed.putString(Keys.LONGITUDE_PREV_DISTANCE, lon + "");
            if (updatePrevTime) {
                ed.putLong(Keys.TIME_PREV_DISTANCE, System.currentTimeMillis());
            }
        }
        ed.apply();
    }

    public static void setPrevDistanceLatLng(double lat, double lon, long time) {
        SharedPreferences.Editor ed = mSharedPreferences.edit();
        if (lat != 0.0 && lon != 0.0) {
            ed.putString(Keys.LATITUDE_PREV_DISTANCE, lat + "");
            ed.putString(Keys.LONGITUDE_PREV_DISTANCE, lon + "");
            ed.putLong(Keys.TIME_PREV_DISTANCE, time);
        }
        ed.apply();
    }

    public static void setPrevDistanceTime() {
        mSharedPreferences
                .edit()
                .putLong(Keys.TIME_PREV_DISTANCE, System.currentTimeMillis())
                .apply();
    }

    public static String getPrevDistanceLatitude() {
        return mSharedPreferences.getString(Keys.LATITUDE_PREV_DISTANCE, "0.0");
    }

    public static String getPrevDistanceLongitude() {
        return mSharedPreferences.getString(Keys.LONGITUDE_PREV_DISTANCE, "0.0");
    }

    public static long getPrevDistanceTime() {
        return mSharedPreferences.getLong(Keys.TIME_PREV_DISTANCE, 0);
    }

    public static void addLocCoordinateInTrip(double lat, double lng, String STATUS) {
        LocCoordinatesInTrip currentLatLng = new LocCoordinatesInTrip();
        currentLatLng.setDate("" + Utils.getIsoDate());
        currentLatLng.setLat("" + lat);
        currentLatLng.setLng("" + lng);
        if (!Utils.isGpsEnable()) {
            currentLatLng.setGps("0");
        }
        if (StringUtils.isNotBlank(STATUS)) {
            currentLatLng.setStatus(STATUS);
        }
        ArrayList<LocCoordinatesInTrip> prevLatLngList = getLocCoordinatesInTrip();
        prevLatLngList.add(currentLatLng);
        String value = new Gson().toJson(prevLatLngList, new TypeToken<ArrayList<LocCoordinatesInTrip>>() {
        }.getType());
        Utils.redLog("InTripLoc", value);
        mSharedPreferences.edit().putString(Keys.IN_TRIP_LAT_LNG_ARRAY, value).apply();
    }

    public static void addLocCoordinateInTrip(double lat, double lng) {
        LocCoordinatesInTrip currentLatLng = new LocCoordinatesInTrip();
        currentLatLng.setDate("" + Utils.getIsoDate());
        currentLatLng.setLat("" + lat);
        currentLatLng.setLng("" + lng);
        if (!Utils.isGpsEnable()) {
            currentLatLng.setGps("0");
        }
        ArrayList<LocCoordinatesInTrip> prevLatLngList = getLocCoordinatesInTrip();
        prevLatLngList.add(currentLatLng);
        String value = new Gson().toJson(prevLatLngList, new TypeToken<ArrayList<LocCoordinatesInTrip>>() {
        }.getType());
        Utils.redLog("InTripLoc", value);
        mSharedPreferences.edit().putString(Keys.IN_TRIP_LAT_LNG_ARRAY, value).apply();
    }

    public static void addLocCoordinateInTrip(ArrayList<LocCoordinatesInTrip> routeLatLngList, int index) {
        ArrayList<LocCoordinatesInTrip> prevLatLngList = getLocCoordinatesInTrip();
        if (index < prevLatLngList.size()) {
            prevLatLngList.addAll(index, routeLatLngList);
            String value = new Gson().toJson(prevLatLngList, new TypeToken<ArrayList<LocCoordinatesInTrip>>() {
            }.getType());
            Utils.redLog("InTripLoc", value);
            mSharedPreferences.edit().putString(Keys.IN_TRIP_LAT_LNG_ARRAY, value).apply();
        }
    }


    public static ArrayList<LocCoordinatesInTrip> getLocCoordinatesInTrip() {
        String placesJson = mSharedPreferences.getString(Keys.IN_TRIP_LAT_LNG_ARRAY, StringUtils.EMPTY);
        ArrayList<LocCoordinatesInTrip> latLngList = new ArrayList<>();
        if (StringUtils.isNotBlank(placesJson)) {
            latLngList = new Gson().fromJson(placesJson, new TypeToken<ArrayList<LocCoordinatesInTrip>>() {
            }.getType());
        }
        return latLngList;
    }

    public static String getLocCoordinatesInTripString() {
        return mSharedPreferences.getString(Keys.IN_TRIP_LAT_LNG_ARRAY, StringUtils.EMPTY);
    }

    public static void setLastStatsApiCallTime(long value) {
        mSharedPreferences
                .edit()
                .putLong(Keys.STATS_API_CALL_TIME, value)
                .apply();
    }

    public static long getLastStatsApiCallTime() {
        return mSharedPreferences.getLong(Keys.STATS_API_CALL_TIME, 0);
    }

    public static void setStatsApiCallRequired(boolean value) {
        mSharedPreferences
                .edit()
                .putBoolean(Keys.IS_STATS_API_CALL_REQUIRED, value)
                .apply();
    }

    public static boolean isStatsApiCallRequired() {
        return mSharedPreferences.getBoolean(Keys.IS_STATS_API_CALL_REQUIRED, true);
    }

    public static void setGeoCoderApiKeyRequired(boolean status) {
        SharedPreferences.Editor ed = mSharedPreferences.edit();
        ed.putBoolean(Keys.IS_GEO_CODER_API_KEY_REQUIRED, status);
        if (status) {
            if (getGeoCoderApiKeyCheckTime() == 0) {
                ed.putLong(Keys.API_KEY_CHECK_TIME_GEO_CODER, System.currentTimeMillis());
            }
        } else {
            ed.putLong(Keys.API_KEY_CHECK_TIME_GEO_CODER, 0);
        }
        ed.apply();
    }

    public static long getGeoCoderApiKeyCheckTime() {
        return mSharedPreferences.getLong(Keys.API_KEY_CHECK_TIME_GEO_CODER, 0);
    }

    public static boolean isGeoCoderApiKeyRequired() {
        return mSharedPreferences.getBoolean(Keys.IS_GEO_CODER_API_KEY_REQUIRED, false);
    }


    public static void setDirectionsApiKeyRequired(boolean status) {
        SharedPreferences.Editor ed = mSharedPreferences.edit();
        ed.putBoolean(Keys.IS_DIRECTIONS_API_KEY_REQUIRED, status);
        if (status) {
            if (getDirectionsApiKeyCheckTime() == 0) {
                ed.putLong(Keys.API_KEY_CHECK_TIME_DIRECTIONS, System.currentTimeMillis());
            }
        } else {
            ed.putLong(Keys.API_KEY_CHECK_TIME_DIRECTIONS, 0);
        }
        ed.apply();
    }

    public static long getDirectionsApiKeyCheckTime() {
        return mSharedPreferences.getLong(Keys.API_KEY_CHECK_TIME_DIRECTIONS, 0);
    }

    public static boolean isDirectionsApiKeyRequired() {
        return mSharedPreferences.getBoolean(Keys.IS_DIRECTIONS_API_KEY_REQUIRED, false);
    }


    public static void setAvailableCities(GetCitiesResponse response) {
        SharedPreferences.Editor ed = mSharedPreferences.edit();
        ArrayList<PlacesResult> placesResults = new ArrayList<>();
        for (CitiesData city : response.getData()) {
            PlacesResult placesResult = new PlacesResult(city.getName(), StringUtils.EMPTY,
                    Double.parseDouble(city.getLat()), Double.parseDouble(city.getLng()));
            placesResults.add(placesResult);
        }
        String value = new Gson().toJson(placesResults, new TypeToken<ArrayList<PlacesResult>>() {
        }.getType());
        ed.putLong(Keys.AVAILABLE_CITIES_API_CALL_TIME, System.currentTimeMillis());
        ed.putString(Keys.AVAILABLE_CITIES, value);
        ed.putString(Keys.SERVICE_CITIES, new Gson().toJson(response));
        ed.apply();
    }

    public static long getCitiesApiCallTime() {
        return mSharedPreferences.getLong(Keys.AVAILABLE_CITIES_API_CALL_TIME, 0);
    }

    public static ArrayList<PlacesResult> getAvailableCities() {
        String jsonString = mSharedPreferences.getString(Keys.AVAILABLE_CITIES, StringUtils.EMPTY);
        ArrayList<PlacesResult> citiesData = new ArrayList<>();
        if (StringUtils.isNotBlank(jsonString)) {
            citiesData = new Gson().fromJson(jsonString, new TypeToken<ArrayList<PlacesResult>>() {
            }.getType());
        }
        return citiesData;
    }


    public static void setADID(String value) {
        mSharedPreferences
                .edit()
                .putString(Keys.ADID, value)
                .apply();
    }

    public static String getADID() {
        return mSharedPreferences.getString(Keys.ADID, StringUtils.EMPTY);
    }

    public static void setLastAckTripID(String value) {
        mSharedPreferences
                .edit()
                .putString(Keys.LAST_ACK_TRIP_ID, value)
                .apply();
    }

    public static String getLastAckTripID() {
        return mSharedPreferences.getString(Keys.LAST_ACK_TRIP_ID, StringUtils.EMPTY);
    }


    public static void setOneSignalPlayerId(String value) {
        mSharedPreferences
                .edit()
                .putString(Keys.ONE_SIGNAL_PALYER_ID, value)
                .apply();
    }

    public static String getOneSignalPlayerId() {
        return mSharedPreferences.getString(Keys.ONE_SIGNAL_PALYER_ID, StringUtils.EMPTY);
    }

    public static void setWalletIncreasedError(String value) {
        mSharedPreferences
                .edit()
                .putString(Keys.WALLET_ERROR, value)
                .apply();
    }

    public static String getWalletIncreasedError() {
        return mSharedPreferences
                .getString(Keys.WALLET_ERROR, "Mazeed booking lainay kay liyay pehlay paisay jamma karein");
    }

    public static void setSettingsVersion(String value) {
        mSharedPreferences
                .edit()
                .putString(Keys.SETTINGS_VERSION, value)
                .apply();
    }

    public static String getSettingsVersion() {
        return mSharedPreferences.getString(Keys.SETTINGS_VERSION, StringUtils.EMPTY);
    }

    public static void setCashInHands(int value) {
        mSharedPreferences
                .edit()
                .putInt(Keys.CASH_IN_HANDS, value)
                .apply();
    }

    /**
     * getting driver's cash/non-cash status
     *
     * @return
     */
    public static boolean getIsCash() {
        return mSharedPreferences.getBoolean(Keys.CASH, false);
    }

    /**
     * saving driver's cash/non-cash status to local storage
     *
     * @param value true/false
     */
    public static void setCash(boolean value) {
        mSharedPreferences
                .edit()
                .putBoolean(Keys.CASH, value)
                .apply();
    }


    /**
     * Sets updated value for location response received count.
     * when we don't receive location response from socket event we update response counter.
     * upon certain counts we make driver offline forcefully.
     * If socket received counter is reset to zero.
     *
     * @param responseCounter updated value for response counter.
     */
    public static void setLocationSocketNotReceivedCount(int responseCounter) {
        mSharedPreferences
                .edit()
                .putInt(Keys.LOCATION_RESPONSE_NOT_RECEIVED_COUNT, responseCounter)
                .apply();
    }

    /***
     * Get updated value store against socket response not received.
     *
     * @return return int of currently stored socket response not received.
     */
    public static int getSocketResponseNotReceivedCount() {
        return mSharedPreferences
                .getInt(Keys.LOCATION_RESPONSE_NOT_RECEIVED_COUNT, 0);
    }


    /***
     * Update value for driver offline forcefully when location response is not received.
     *
     * @param driverOffline latest value for driver status offline
     */
    public static void setDriverOfflineForcefully(boolean driverOffline) {
        mSharedPreferences
                .edit()
                .putBoolean(Keys.DRIVER_OFFLINE_FORCEFULLY, driverOffline)
                .apply();
    }

    /***
     * Validate driver offline forcefully when location socket
     * event is not returned with response after allowed retry window.
     *
     * @return True if response not received after retry windows count, else False;
     */
    public static boolean makeDriverOfflineForcefully() {
        return mSharedPreferences.getBoolean(Keys.DRIVER_OFFLINE_FORCEFULLY, false);
    }


    public static int getCashInHands() {
        return mSharedPreferences.getInt(Keys.CASH_IN_HANDS, 0);
    }

    public static void setCashInHandsRange(String value) {
        mSharedPreferences
                .edit()
                .putString(Keys.CASH_IN_HANDS_RANGE, value)
                .apply();
    }

    public static int[] getCashInHandsRange() {
        String value = mSharedPreferences.getString(Keys.CASH_IN_HANDS_RANGE, StringUtils.EMPTY);
        if (StringUtils.isNotBlank(value)) {
            return new Gson().fromJson(value, int[].class);
        } else {
            return new int[]{0, 500, 1000, 1500, 2000};
        }
    }


    public static void setTripDelay(long value) {
        mSharedPreferences
                .edit()
                .putLong(Keys.TRIP_DELAY, value)
                .apply();
    }

    public static long getTripDelay() {
        return mSharedPreferences.getLong(Keys.TRIP_DELAY, 0);
    }

    public static void setDriverDestination(PlacesResult mDropOff) {
        mSharedPreferences
                .edit()
                .putString(Keys.DRIVER_DEST, mDropOff != null ? new Gson().toJson(mDropOff) : StringUtils.EMPTY)
                .apply();
    }

    public static PlacesResult getDriverDestination() {
        String data = mSharedPreferences.getString(Keys.DRIVER_DEST, StringUtils.EMPTY);
        PlacesResult destData;
        if (StringUtils.isBlank(data)) {
            destData = null;
        } else {
            destData = new Gson().fromJson(data, PlacesResult.class);
        }
        return destData;
    }


    public static void setLastMixPanelDistId(String value) {
        mSharedPreferences
                .edit()
                .putString(Keys.MIX_PANEL_DIST_ID, value)
                .apply();
    }

    public static String getLastMixPanelDistId() {
        return mSharedPreferences.getString(Keys.MIX_PANEL_DIST_ID, StringUtils.EMPTY);
    }


    public static void setRecentPlaces(PlacesResult place) {
        ArrayList<PlacesResult> recentPlaces = new ArrayList<>();
        recentPlaces.add(place);
        ArrayList<PlacesResult> prevPlaces = getRecentPlaces();
        if (prevPlaces != null && prevPlaces.size() > 0) {
            //Remove place if already exists to avoid duplicate place
            Iterator<PlacesResult> iterator = prevPlaces.iterator();
            while (iterator.hasNext()) {
                PlacesResult temp = iterator.next();
                if (temp.name.equalsIgnoreCase(place.name)) {
                    iterator.remove();
                }
            }
            //keep only 40 recent places, new places already added at top
            if (prevPlaces.size() > 39) {
                prevPlaces.remove(39);
            }
            recentPlaces.addAll(prevPlaces);
        }
        String value = new Gson().toJson(recentPlaces, new TypeToken<ArrayList<PlacesResult>>() {
        }.getType());
        mSharedPreferences
                .edit()
                .putString(Keys.RECENT_PLACES, value)
                .apply();
    }

    public static ArrayList<PlacesResult> getRecentPlaces() {
        String placesJson = mSharedPreferences.getString(Keys.RECENT_PLACES, "");
        ArrayList<PlacesResult> places;
        places = new Gson().fromJson(placesJson, new TypeToken<ArrayList<PlacesResult>>() {
        }.getType());
        return places;
    }


    public static void setSavedPlace(SavedPlaces place) {
        ArrayList<SavedPlaces> recentPlaces = new ArrayList<>();
        recentPlaces.add(place);
        ArrayList<SavedPlaces> prevPlaces = getSavedPlaces();
        if (prevPlaces != null && prevPlaces.size() > 0) {
            //Remove place if already exists to avoid duplicate place
            Iterator<SavedPlaces> iterator = prevPlaces.iterator();
            while (iterator.hasNext()) {
                SavedPlaces temp = iterator.next();
                if (temp.getAddress().equalsIgnoreCase(place.getAddress())) {
                    float distance = Utils.calculateDistance(temp.getLat(), temp.getLng(), place.getLat(), place.getLng());
                    if (distance < Constants.SAVED_PLACES_RADIUS) {
                        iterator.remove();
                        break;
                    }
                }
            }
            //keep only 19 saved places, new places already added at top
            if (prevPlaces.size() > 19) {
                prevPlaces.remove(19);
            }
            recentPlaces.addAll(prevPlaces);
        }
        String value = new Gson().toJson(recentPlaces, new TypeToken<ArrayList<SavedPlaces>>() {
        }.getType());
        mSharedPreferences
                .edit()
                .putString(Keys.SAVED_PLACES, value)
                .apply();
    }

    public static void updateSavedPlace(ArrayList<SavedPlaces> places) {
        String value = new Gson().toJson(places, new TypeToken<ArrayList<SavedPlaces>>() {
        }.getType());
        mSharedPreferences
                .edit()
                .putString(Keys.SAVED_PLACES, value)
                .apply();
    }

    public static ArrayList<SavedPlaces> getSavedPlaces() {
        String placesJson = mSharedPreferences.getString(Keys.SAVED_PLACES, StringUtils.EMPTY);
        ArrayList<SavedPlaces> places = new ArrayList<>();
        if (StringUtils.isNotBlank(placesJson)) {
            places = new Gson().fromJson(placesJson, new TypeToken<ArrayList<SavedPlaces>>() {
            }.getType());
        }
        return places;
    }

    public static void setSavedPlacesAPICalled(boolean value) {
        mSharedPreferences
                .edit()
                .putBoolean(Keys.IS_SAVED_PLACES_API_CALLED, value)
                .apply();
    }

    public static boolean isSavedPlacesAPICalled() {
        return mSharedPreferences
                .getBoolean(Keys.IS_SAVED_PLACES_API_CALLED, false);
    }


    public static GetCitiesResponse getServiceCities() {
        String jsonString = mSharedPreferences.getString(Keys.SERVICE_CITIES, StringUtils.EMPTY);
        GetCitiesResponse citiesData = null;
        if (StringUtils.isNotBlank(jsonString)) {
            citiesData = new Gson().fromJson(jsonString, GetCitiesResponse.class);
        }
        return citiesData;
    }

    public static void setObjectToSharedPref(Object object) {
        mSharedPreferences
                .edit()
                .putString(object.getClass().getName(), new Gson().toJson(object))
                .apply();
    }

    public static Object getObjectFromSharedPref(Class value) {
        String data = mSharedPreferences.getString(value.getName(), StringUtils.EMPTY);
        Object object = null;
        if (StringUtils.isNotBlank(data)) {
            object = new Gson().fromJson(data, value);
        }
        return object;
    }

    public static void clearObjectFromSharedPref(Class value) {
        mSharedPreferences
                .edit()
                .putString(value.getName(), StringUtils.EMPTY)
                .apply();
    }


    public static void setZoneAreas(ZoneAreaResponse object, String key) {
        mSharedPreferences
                .edit()
                .putString(key, new Gson().toJson(object))
                .apply();
    }

    public static ZoneAreaResponse getZoneAreas(String key) {
        String data = mSharedPreferences.getString(key, StringUtils.EMPTY);
        ZoneAreaResponse object = null;
        if (StringUtils.isNotBlank(data)) {
            object = new Gson().fromJson(data, ZoneAreaResponse.class);
        }
        return object;
    }

    public static void setInactiveCheckTime(long value) {
        mSharedPreferences
                .edit()
                .putLong(Keys.INACTIVE_CHECK_TIME, value)
                .apply();
    }

    public static long getInactiveCheckTime() {
        return mSharedPreferences.getLong(Keys.INACTIVE_CHECK_TIME, 0);
    }

    /**
     * This method gets local server url stored in shared pref.
     *
     * @param key shared pref. key for local url
     * @return Local URL String
     */
    public static String getLocalBaseUrl(String key) {
        return mSharedPreferences.getString(key, BuildConfig.FLAVOR_URL);
    }

    /**
     * This method saves base url in shared pref. that we are getting from users on
     * local flavoured builds via input dialog
     *
     * @param value value for local url
     */
    public static void setLocalBaseUrl(String value) {
        mSharedPreferences
                .edit()
                .putString(Keys.BASE_URL_LOCAL, value)
                .apply();
    }

    /**
     * This method gets app version stored in shared pref.
     *
     * @return App Version String
     */
    public static String getAppVersion() {
        return mSharedPreferences.getString(Keys.APP_VERSION, StringUtils.EMPTY);
    }

    /**
     * This method saves app version in shared pref.
     *
     * @param value value for app version
     */
    public static void setAppVersion(String value) {
        mSharedPreferences
                .edit()
                .putString(Keys.APP_VERSION, value)
                .apply();
    }

    /**
     * This method gets app version code stored in shared pref.
     *
     * @return App Version Code
     */
    public static int getAppVersionCode() {
        return mSharedPreferences.getInt(Keys.APP_VERSION_CODE, 0);
    }

    /**
     * This method saves app Version Code in shared pref.
     *
     * @param value value for app Version Code
     */
    public static void setAppVersionCode(int value) {
        mSharedPreferences
                .edit()
                .putInt(Keys.APP_VERSION_CODE, value)
                .apply();
    }

    /**
     * This method saves a flag when shared pref is recently clear in case of dirty shared pref.
     *
     * @param value isAlreadyCleared
     */
    public static void setIsAlreadyCleared(boolean value) {
        mSharedPreferences
                .edit()
                .putBoolean(Keys.IS_ALREADY_CLEAR, value)
                .apply();
    }

    /**
     * @return This method returns a flag when shared pref is recently clear in case of dirty shared pref.
     */
    public static boolean getIsAlreadyCleared() {
        return mSharedPreferences.getBoolean(Keys.IS_ALREADY_CLEAR, false);
    }

    /***
     * Clear preference when driver is unauthoried.
     */
    public static void clearForUnauthorized() {
        saveLoginStatus(false);
        setIncomingCall(false);
        setCallData(null);
        setTripStatus("");
        setPilotData(null);
    }

    /**
     * save selected zone data for loadboard to local storage
     *
     * @param key      Pickup/Dropoff key
     * @param zoneData selected zone data
     */
    public static void setSelectedLoadboardZoneData(String key, ZoneData zoneData) {
        mSharedPreferences
                .edit()
                .putString(key, new Gson().toJson(zoneData))
                .apply();
    }

    /**
     * get selected zone data for loadboard to local storage
     *
     * @param key Pickup/Dropoff ket
     * @return ZoneData
     */
    public static ZoneData getSelectedLoadboardZoneData(String key) {
        String data = mSharedPreferences.getString(key, StringUtils.EMPTY);
        ZoneData zoneData = null;
        if (StringUtils.isNotBlank(data)) {
            zoneData = new Gson().fromJson(data, ZoneData.class);
        }
        return zoneData;
    }

    /**
     * clear only loadboard selected zone data
     */
    public static void clearLoadboardSelectedZoneData() {
        mSharedPreferences
                .edit()
                .putString(Keys.LOADBOARD_SELECTED_PICKUP_ZONE, null)
                .putString(Keys.LOADBOARD_SELECTED_DROPOFF_ZONE, null)
                .apply();
    }

    //region MultiDelivery Shared Preference

    /**
     * Save Multi Delivery Call Driver Data in shared preference.
     *
     * @param response The {@link MultiDeliveryCallDriverData} object.
     */
    public static void setMultiDeliveryCallDriverData(MultiDeliveryCallDriverData response) {
        mSharedPreferences
                .edit()
                .putString(Keys.MULTIDELIVERY_CALLDRIVER_OBJECT, new Gson().toJson(response))
                .apply();
    }

    /**
     * Fetch the Multi Delivery Call Driver Data from shared preference.
     *
     * @return The {@link MultiDeliveryCallDriverData} object.
     */
    public static MultiDeliveryCallDriverData getMultiDeliveryCallDriverData() {
        Gson gson = new Gson();
        return gson.fromJson(mSharedPreferences.getString(
                Keys.MULTIDELIVERY_CALLDRIVER_OBJECT,
                StringUtils.EMPTY
                ),
                MultiDeliveryCallDriverData.class);
    }

    /**
     * Set the multi delivery status.
     *
     * @param type The type of delivery {@linkplain Constants.CallType}
     */
    public static void setDeliveryType(String type) {
        mSharedPreferences
                .edit()
                .putString(Keys.DELIVERY_TYPE, type)
                .apply();
    }

    /**
     * Fetch the status of delivery i.e BATCH & SINGLE.
     *
     * @return The status i.e BATCH & SINGLE.
     */
    public static String getDeliveryType() {
        return mSharedPreferences
                .getString(Keys.DELIVERY_TYPE, Constants.CallType.SINGLE);
    }

    /**
     * Set the ride estimated fare
     *
     * @param estimatedFare estimated fare
     */
    public static void setEstimatedFare(int estimatedFare) {
        mSharedPreferences
                .edit()
                .putInt(Keys.EST_FARE, estimatedFare)
                .apply();
    }

    /**
     * Get Ride estimated fare
     *
     * @return Ride estimated fare
     */
    public static int getEstimatedFare() {
        return mSharedPreferences
                .getInt(Keys.EST_FARE, 0);
    }

    //endregion


    /**
     * Save Chat Message Count Using Conversation ID
     *
     * @param receivedMessageCount The {@link ReceivedMessageCount} object.
     */
    public static void setReceivedMessageCount(ReceivedMessageCount receivedMessageCount) {
        mSharedPreferences
                .edit()
                .putString(Keys.CONVERSATION_BADGE_COUNT, new Gson().toJson(receivedMessageCount))
                .apply();
    }

    /**
     * Fetch the Chat Message Count
     *
     * @return The {@link ReceivedMessageCount} object.
     */
    public static ReceivedMessageCount getReceivedMessageCount() {
        String data = mSharedPreferences.getString(Keys.CONVERSATION_BADGE_COUNT, StringUtils.EMPTY);
        ReceivedMessageCount object = null;
        if (StringUtils.isNotEmpty(data)) {
            Gson gson = new Gson();
            object = gson.fromJson(data, ReceivedMessageCount.class);
        }
        return object;
    }

    /**
     * Remove Chat Message Count
     */
    public static void removeReceivedMessageCount() {
        mSharedPreferences
                .edit()
                .remove(Keys.CONVERSATION_BADGE_COUNT)
                .apply();
    }

    /**
     * @Boolean Is Top Up Passenger Wallet Allowed
     */
    public static boolean isTopUpPassengerWalletAllowed() {
        return mSharedPreferences.getBoolean(Keys.TOP_UP_PASSENGER, true);
    }

    /**
     * Set Top Up Allowed
     */
    public static void setTopUpPassengerWalletAllowed(boolean topUpAllowed) {
        SharedPreferences.Editor ed = mSharedPreferences.edit();
        ed.putBoolean(Keys.TOP_UP_PASSENGER, topUpAllowed);
        ed.commit();
    }

    /**
     * @String Get Booking Voice Note Url Available
     */
    public static String getBookingVoiceNoteUrlAvailable() {
        return mSharedPreferences.getString(Keys.BOOKING_VOICE_NOTE_URL, StringUtils.EMPTY);
    }

    /**
     * Set Booking Voice Note Url Available
     * @param voiceNoteUrl : Voice Note Url
     */
    public static void setBookingVoiceNoteUrlAvailable(String voiceNoteUrl) {
        SharedPreferences.Editor ed = mSharedPreferences.edit();
        ed.putString(Keys.BOOKING_VOICE_NOTE_URL, voiceNoteUrl);
        ed.commit();
    }

    /**
     * Remove Booking Voice Note Url
     */
    public static void removeBookingVoiceNoteUrl() {
        SharedPreferences.Editor ed = mSharedPreferences.edit();
        ed.remove(Keys.BOOKING_VOICE_NOTE_URL);
        ed.commit();
    }

    /**
     * Get last partner submit time.
     * @return long : Time in milliseconds
     */
    public static long getLastPartnerTemperatureSubmitTime() {
        return mSharedPreferences.getLong(Keys.LAST_PARTNER_TEMPERATURE_SUBMIT, DIGIT_ZERO);
    }

    /**
     * Set last partner submit time.
     * @param value : Time in milliseconds
     */
    public static void setLastPartnerTemperatureSubmitTime(long value) {
        mSharedPreferences
                .edit()
                .putLong(Keys.LAST_PARTNER_TEMPERATURE_SUBMIT, value)
                .apply();
    }
}
