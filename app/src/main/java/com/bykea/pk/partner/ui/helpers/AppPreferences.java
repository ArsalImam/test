package com.bykea.pk.partner.ui.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.bykea.pk.partner.BuildConfig;
import com.bykea.pk.partner.models.data.CitiesData;
import com.bykea.pk.partner.models.data.LocCoordinatesInTrip;
import com.bykea.pk.partner.models.data.NotificationData;
import com.bykea.pk.partner.models.data.PlacesResult;
import com.bykea.pk.partner.models.response.GetCitiesResponse;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.TripStatus;
import com.bykea.pk.partner.utils.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.bykea.pk.partner.models.data.PilotData;
import com.bykea.pk.partner.models.data.SettingsData;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AppPreferences {

    public static void clear(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.clear().commit();
    }

    public static void saveSettingsData(Context context, SettingsData data) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(Keys.SETTING_DATA, new Gson().toJson(data));
        ed.commit();
    }

    public static SettingsData getSettings(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String data = sp.getString(Keys.SETTING_DATA, StringUtils.EMPTY);
        SettingsData settingsData = null;
        if (StringUtils.isBlank(data)) {
            settingsData = new SettingsData();
        } else {
            settingsData = new Gson().fromJson(data, SettingsData.class);
        }
        return settingsData;
    }

    public static void setDropOffData(Context context, String address, double lat, double lng) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("dropOffAddress", address);
        ed.putString("dropOffLat", lat + "");
        ed.putString("dropOffLng", lng + "");
        ed.commit();
    }

    public static String getDropOffAddress(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString("dropOffAddress", "");
    }

    public static String getDropOffLat(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString("dropOffLat", "0");
    }

    public static String getDropOffLng(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString("dropOffLng", "0");
    }

    public static void setPilotData(Context context, PilotData user) {
        Gson gson = new Gson();
        JSONObject userJson = null;
        if (null != user) {
            try {
                userJson = new JSONObject(gson.toJson(user));
                Utils.infoLog("Saving User Profile object: ", userJson.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor ed = sp.edit();
            ed.putString(Keys.EMAIL, user.getEmail());
            ed.putString(Keys.ACCESS_TOKEN, user.getAccessToken());
            ed.putString(Keys.DRIVER_ID, user.getId());
            ed.putString(Keys.PHONE_NUMBER, user.getPhoneNo());
            ed.putString(Keys.DRIVER_DATA, userJson.toString());
            ed.commit();
        } else {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor ed = sp.edit();
            ed.putString(Keys.EMAIL, "");
            ed.putString(Keys.ACCESS_TOKEN, "");
            ed.putString(Keys.DRIVER_ID, "");
            ed.putString(Keys.DRIVER_DATA, "");
            ed.clear();
            ed.commit();
        }
    }

    public static PilotData getPilotData(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String data = sp.getString(Keys.DRIVER_DATA, StringUtils.EMPTY);
        Utils.infoLog("Get Driver Profile: ", data);
        if (StringUtils.isNotBlank(data)) {
            return gson.fromJson(data, PilotData.class);
        } else {
            return new PilotData();
        }
    }

    public static void setEta(Context context, String eta) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("eta", eta);
        ed.putLong("etaTime", System.currentTimeMillis());
        ed.commit();
    }

    public static String getEta(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString("eta", "0");
    }

    public static long getEtaUpdatedTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong("etaTime", 0);
    }

    public static void setEstimatedDistance(Context context, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("distance", value);
        ed.commit();
    }

    public static String getEstimatedDistance(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString("distance", "0");
    }

    public static void setPhoneNumber(Context context, String phone) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        Utils.infoLog("Saving phone no", phone);
        ed.putString(Keys.PHONE_NUMBER, phone);
        ed.commit();
    }

    public static String getPhoneNumber(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(Keys.PHONE_NUMBER, StringUtils.EMPTY);
    }

    public static String getDriverEmail(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(Keys.EMAIL, StringUtils.EMPTY);
    }

    public static String getDriverId(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(Keys.DRIVER_ID, StringUtils.EMPTY);
    }

    public static void setRegId(Context context, String id) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(Keys.FCM_REGISTRATION_ID, id);
        ed.commit();
    }

    public static String getRegId(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(Keys.FCM_REGISTRATION_ID, "");
    }

    public static String getAccessToken(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(Keys.ACCESS_TOKEN, StringUtils.EMPTY);
    }

    public static void savePhoneNumberVerified(Context context, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(Keys.PHONE_NUMBER_VERIFIED, value);
        ed.commit();
    }

    public static boolean isPhoneNumberVerified(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(Keys.PHONE_NUMBER_VERIFIED, false);
    }


    public static void setAvailableStatus(Context context, boolean status) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(Keys.AVAILABLE_STATUS, status);
        ed.commit();
    }

    public static boolean getAvailableStatus(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(Keys.AVAILABLE_STATUS, false);
    }

    public static void saveLoginStatus(Context context, boolean isLoggedIn) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(Keys.LOGIN_STATUS, isLoggedIn);
        ed.commit();
    }

    public static boolean isLoggedIn(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(Keys.LOGIN_STATUS, false);
    }


    public static void saveLocation(Context context, LatLng location, String bearing, float accuracy, boolean isMock) {
        if (location.latitude != 0.0 && location.longitude != 0.0 && accuracy < 100f) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor ed = sp.edit();
            ed.putString(Keys.LATITUDE, location.latitude + "");
            ed.putString(Keys.LONGITUDE, location.longitude + "");
            ed.putString(Keys.BEARING, bearing + "");
            ed.putBoolean(Keys.IS_MOCK_LOCATION, isMock);
            ed.putFloat(Keys.LOCATION_ACCURACY, accuracy);
            ed.commit();
        }

    }

    public static void saveLocationFromLogin(Context context, LatLng location, String bearing, float accuracy, boolean isMock) {
        if (location.latitude != 0.0 && location.longitude != 0.0 /*&& accuracy < 100f*/) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor ed = sp.edit();
            ed.putString(Keys.LATITUDE, location.latitude + "");
            ed.putString(Keys.LONGITUDE, location.longitude + "");
            ed.putString(Keys.BEARING, bearing + "");
            ed.putBoolean(Keys.IS_MOCK_LOCATION, isMock);
            ed.putFloat(Keys.LOCATION_ACCURACY, accuracy);
            ed.commit();
        }

    }

    public static double getLatitude(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return Double.parseDouble(sp.getString(Keys.LATITUDE, "0.0"));
    }

    public static double getLongitude(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return Double.parseDouble(sp.getString(Keys.LONGITUDE, "0.0"));
    }

    public static boolean isFromMockLocation(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return !BuildConfig.DEBUG && sp.getBoolean(Keys.IS_MOCK_LOCATION, false);
    }

    public static double getBearing(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String bearing = sp.getString(Keys.BEARING, "0.0");
        if (StringUtils.isBlank(bearing)) {
            bearing = "0.0";
        }
        return Double.parseDouble(bearing);
    }

    public static void setCallData(Context context, NormalCallData callData) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        Utils.infoLog("Saving Calling object: ", new Gson().toJson(callData));
        ed.putString(Keys.CALLDATA_OBJECT, new Gson().toJson(callData));
        ed.commit();
    }

    public static void setRating(Context context, String rating) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        Utils.infoLog("Saving Rating : ", Float.parseFloat(rating) + "");
        ed.putFloat(getDriverId(context) + "driverRating", Float.parseFloat(rating));
        ed.commit();
    }

    public static float getRating(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Utils.infoLog("Saving Rating : ", sharedPreferences.getFloat("driverRating", 0) + "");
        return sharedPreferences.getFloat(getDriverId(context) + "driverRating", 0);
    }


    public static NormalCallData getCallData(Context context) {
        Gson gson = new Gson();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return gson.fromJson(sp.getString(Keys.CALLDATA_OBJECT, StringUtils.EMPTY),
                NormalCallData.class);
    }


    public static void setIsOnTrip(Context context, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(Keys.ON_TRIP, value);
        ed.commit();
    }

    public static boolean isOnTrip(Context context) {
        SharedPreferences sp = PreferenceManager.
                getDefaultSharedPreferences(context);

        Utils.infoLog(Constants.APP_NAME + " isOnTrip", sp.getBoolean(Keys.ON_TRIP, false) + "");
        return sp.getBoolean(Keys.ON_TRIP, false);
    }

    public static void setTripStatus(Context context, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(Keys.TRIP_STATUS, value);
        ed.commit();
    }

    public static String getTripStatus(Context context) {
        SharedPreferences sp = PreferenceManager.
                getDefaultSharedPreferences(context);
        return sp.getString(Keys.TRIP_STATUS, TripStatus.ON_FREE);
    }

    public static void setBeginTrip(Context context, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(Keys.ON_BEGIN_TRIP, value);
        ed.commit();
        Utils.redLog(Constants.APP_NAME + " BeginTripStatus", value + "");
    }

    public static boolean isOnBeginTrip(Context context) {
        SharedPreferences sp = PreferenceManager.
                getDefaultSharedPreferences(context);
        Utils.redLog(Constants.APP_NAME + " isOnBeginTripStatus", sp.getBoolean(Keys.ON_BEGIN_TRIP, false) + "");
        return sp.getBoolean(Keys.ON_BEGIN_TRIP, false);
    }

    public static void setEndTrip(Context context, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(Keys.ON_END_TRIP, value);
        ed.commit();
        // Clear distance only on begin trip not on end trip
        if (value) {
            ed.putFloat(Keys.TRIP_TOTAL_DISTANCE, 0.0f);
        }
        // It makes sure that no time and distance is being added
        AppPreferences.setBeginTrip(context, false);
        Utils.redLog(Constants.APP_NAME + " isOnEndTripStatus", value + "");
    }

    public static boolean isOnEndTrip(Context context) {
        SharedPreferences sp = PreferenceManager.
                getDefaultSharedPreferences(context);
        Utils.redLog(Constants.APP_NAME + " isOnEndTripStatus", sp.getBoolean(Keys.ON_END_TRIP, false) + "");
        return sp.getBoolean(Keys.ON_END_TRIP, false);
    }

    public static long getStartTripTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(Keys.TRIP_START_TIME, 0);
    }

    public static void setStartTripTime(Context context, long startTime) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putLong(Keys.TRIP_START_TIME, startTime);
        ed.commit();
    }

    public static void setIncomingCall(Context context, boolean flag) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(Keys.INCOMING_CALL, flag);
        ed.commit();
    }

    public static boolean isIncomingCall(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(Keys.INCOMING_CALL, true);
    }

    public static void setCallType(Context context, String callType) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(Keys.CALL_TYPE, callType);
        ed.commit();
    }

    public static String getCallType(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(Keys.CALL_TYPE, "");
    }

    public static void setLastMessageID(Context context, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(Keys.LAST_MESSAGE_ID, value);
        ed.commit();
    }

    public static String getLastMessageID(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(Keys.LAST_MESSAGE_ID, StringUtils.EMPTY);
    }

    public static void setAdminMsg(Context context, NotificationData data) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(Keys.ADMIN_MSG, data != null ? new Gson().toJson(data) : StringUtils.EMPTY);
        ed.commit();
    }

    public static String getAdminMsg(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(Keys.ADMIN_MSG, StringUtils.EMPTY);
    }

    public static boolean isJobActivityOnForeground(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(Keys.JOB_ACTIVITY_FOREGROUND, false);
    }

    public static void setJobActivityOnForeground(Context context, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(Keys.JOB_ACTIVITY_FOREGROUND, value);
        ed.commit();
    }

    public static boolean isChatActivityOnForeground(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(Keys.CHAT_ACTIVITY_FOREGROUND, false);
    }

    public static void setChatActivityOnForeground(Context context, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(Keys.CHAT_ACTIVITY_FOREGROUND, value);
        ed.commit();
    }

    public static boolean isHomeActivityOnForeground(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(Keys.HOME_ACTIVITY_FOREGROUND, false);
    }

    public static void setHomeActivityOnForeground(Context context, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(Keys.HOME_ACTIVITY_FOREGROUND, value);
        ed.commit();
    }

    public static boolean isCallingActivityOnForeground(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(Keys.CALLING_ACTIVITY_FOREGROUND, false);
    }

    public static void setCallingActivityOnForeground(Context context, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(Keys.CALLING_ACTIVITY_FOREGROUND, value);
        ed.commit();
    }

    public static void saveLastUpdatedLocation(Context context, LatLng location) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        if (location.latitude != 0.0 && location.longitude != 0.0) {
            ed.putString(Keys.LATITUDE_LAST_UPDATED, location.latitude + "");
            ed.putString(Keys.LONGITUDE_LAST_UPDATED, location.longitude + "");
            ed.putString(Keys.TIME_LAST_UPDATED, "0");
        }
        ed.commit();
    }

    public static String getLastUpdatedLatitude(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(Keys.LATITUDE_LAST_UPDATED, "0.0");
    }

    public static String getLastUpdatedLongitude(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(Keys.LONGITUDE_LAST_UPDATED, "0.0");
    }

    public static String getLastUpdatedTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(Keys.TIME_LAST_UPDATED, "0");
    }

    public static void saveLocationAccuracy(Context context, LatLng location) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        if (location.latitude != 0.0 && location.longitude != 0.0) {
            ed.putString(Keys.LATITUDE_LAST_UPDATED, location.latitude + "");
            ed.putString(Keys.LONGITUDE_LAST_UPDATED, location.longitude + "");
        }
        ed.commit();
    }

    public static float getLocationAccuracy(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getFloat(Keys.LOCATION_ACCURACY, 200f);
    }


    /*
    * Sync Server Time with Device Time.
    * */
    public static void setServerTimeDifference(Context context, long value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putLong(Keys.SERVER_TIME_DIFFERENCE, value);
        Utils.redLog("Time Difference", "" + value);
        ed.commit();
    }

    public static long getServerTimeDifference(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(Keys.SERVER_TIME_DIFFERENCE, 0);
    }

    public synchronized static void setLocationEmitTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putLong(Keys.LOCATION_EMIT_TIME, System.currentTimeMillis());
        ed.commit();
    }

    public static long getLocationEmitTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(Keys.LOCATION_EMIT_TIME, System.currentTimeMillis());
    }

    public static boolean isOutOfFence(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(Keys.IS_OUT_OF_FENCE, false);
    }

    public static void setOutOfFence(Context context, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(Keys.IS_OUT_OF_FENCE, value);
        ed.commit();
    }


    public static boolean isWalletAmountIncreased(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(Keys.IS_WALLET_AMOUNT_INCREASED, false);
    }

    public static void setWalletAmountIncreased(Context context, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(Keys.IS_WALLET_AMOUNT_INCREASED, value);
        ed.commit();
    }

    public static void setProfileUpdated(Context context, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(Keys.IS_PROFILE_UPDATED, value);
        ed.commit();
    }

    public static boolean isProfileUpdated(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(Keys.IS_PROFILE_UPDATED, true);
    }


    public static void setLastDirectionsApiCallTime(Context context, long value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putLong(Keys.LAST_DIRECTIONS_API_CALL_TIME, value);
        ed.commit();
    }


    public static long getLastDirectionsApiCallTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(Keys.LAST_DIRECTIONS_API_CALL_TIME, 0);
    }

    public static float getDistanceCoveredInMeters(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getFloat(Keys.DISTANCE_COVERED, 0);
    }

    public static void setDistanceCoveredInMeters(Context context, float value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putFloat(Keys.DISTANCE_COVERED, value + getDistanceCoveredInMeters(context));
        ed.commit();
    }

    public static void clearTripDistanceData(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putFloat(Keys.DISTANCE_COVERED, 0f);
        ed.putString(Keys.LATITUDE_PREV_DISTANCE, "0.0");
        ed.putString(Keys.LONGITUDE_PREV_DISTANCE, "0.0");
        ed.putLong(Keys.TIME_PREV_DISTANCE, 0);
        ed.putString(Keys.IN_TRIP_LAT_LNG_ARRAY, StringUtils.EMPTY);
        ed.commit();
    }

    public static void setVersionCheckTime(Context context, long value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putLong(Keys.VERSION_CHECK_TIME, value);
        ed.commit();
    }

    public static long getVersionCheckTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(Keys.VERSION_CHECK_TIME, 0);
    }

    public static void setPrevDistanceLatLng(Context context, double lat, double lon, boolean updatePrevTime) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        if (lat != 0.0 && lon != 0.0) {
            ed.putString(Keys.LATITUDE_PREV_DISTANCE, lat + "");
            ed.putString(Keys.LONGITUDE_PREV_DISTANCE, lon + "");
            if (updatePrevTime) {
                ed.putLong(Keys.TIME_PREV_DISTANCE, System.currentTimeMillis());
            }
        }
        ed.commit();
    }

    public static void setPrevDistanceLatLng(Context context, double lat, double lon, long time) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        if (lat != 0.0 && lon != 0.0) {
            ed.putString(Keys.LATITUDE_PREV_DISTANCE, lat + "");
            ed.putString(Keys.LONGITUDE_PREV_DISTANCE, lon + "");
            ed.putLong(Keys.TIME_PREV_DISTANCE, time);
        }
        ed.commit();
    }

    public static void setPrevDistanceTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putLong(Keys.TIME_PREV_DISTANCE, System.currentTimeMillis());
        ed.commit();
    }

    public static String getPrevDistanceLatitude(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(Keys.LATITUDE_PREV_DISTANCE, "0.0");
    }

    public static String getPrevDistanceLongitude(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(Keys.LONGITUDE_PREV_DISTANCE, "0.0");
    }

    public static long getPrevDistanceTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(Keys.TIME_PREV_DISTANCE, 0);
    }

    public static boolean isStopServiceCalled(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(Keys.IS_STOP_SERVICE_CALLED, false);
    }

    public static void setStopService(Context context, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(Keys.IS_STOP_SERVICE_CALLED, value);
        ed.commit();
    }

    public static void addLocCoordinateInTrip(Context context, double lat, double lng) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        LocCoordinatesInTrip currentLatLng = new LocCoordinatesInTrip();
        currentLatLng.setDate("" + Utils.getIsoDate());
        currentLatLng.setLat("" + lat);
        currentLatLng.setLng("" + lng);
        ArrayList<LocCoordinatesInTrip> prevLatLngList = getLocCoordinatesInTrip(context);
        prevLatLngList.add(currentLatLng);
        String value = new Gson().toJson(prevLatLngList, new TypeToken<ArrayList<LocCoordinatesInTrip>>() {
        }.getType());
        Utils.redLog("InTripLoc", value);
        ed.putString(Keys.IN_TRIP_LAT_LNG_ARRAY, value);
        ed.commit();
    }

    public static void addLocCoordinateInTrip(Context context, ArrayList<LocCoordinatesInTrip> routeLatLngList, int index) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ArrayList<LocCoordinatesInTrip> prevLatLngList = getLocCoordinatesInTrip(context);
        if (index < prevLatLngList.size()) {
            prevLatLngList.addAll(index, routeLatLngList);
            String value = new Gson().toJson(prevLatLngList, new TypeToken<ArrayList<LocCoordinatesInTrip>>() {
            }.getType());
            Utils.redLog("InTripLoc", value);
            ed.putString(Keys.IN_TRIP_LAT_LNG_ARRAY, value);
            ed.commit();
        }
    }


    public static ArrayList<LocCoordinatesInTrip> getLocCoordinatesInTrip(Context c) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        String placesJson = sp.getString(Keys.IN_TRIP_LAT_LNG_ARRAY, StringUtils.EMPTY);
        ArrayList<LocCoordinatesInTrip> latLngList = new ArrayList<>();
        if (StringUtils.isNotBlank(placesJson)) {
            latLngList = new Gson().fromJson(placesJson, new TypeToken<ArrayList<LocCoordinatesInTrip>>() {
            }.getType());
        }
        return latLngList;
    }

    public static String getLocCoordinatesInTripString(Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getString(Keys.IN_TRIP_LAT_LNG_ARRAY, StringUtils.EMPTY);
    }

    public static void setLastStatsApiCallTime(Context context, long value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putLong(Keys.STATS_API_CALL_TIME, value);
        ed.commit();
    }

    public static long getLastStatsApiCallTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(Keys.STATS_API_CALL_TIME, 0);
    }

    public static void setStatsApiCallRequired(Context context, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(Keys.IS_STATS_API_CALL_REQUIRED, value);
        ed.commit();
    }

    public static boolean isStatsApiCallRequired(Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getBoolean(Keys.IS_STATS_API_CALL_REQUIRED, true);
    }

    public static void setGeoCoderApiKeyRequired(Context context, boolean status) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(Keys.IS_GEO_CODER_API_KEY_REQUIRED, status);
        if (status) {
            if (getGeoCoderApiKeyCheckTime(context) == 0) {
                ed.putLong(Keys.API_KEY_CHECK_TIME_GEO_CODER, System.currentTimeMillis());
            }
        } else {
            ed.putLong(Keys.API_KEY_CHECK_TIME_GEO_CODER, 0);
        }
        ed.apply();
    }

    public static long getGeoCoderApiKeyCheckTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(Keys.API_KEY_CHECK_TIME_GEO_CODER, 0);
    }

    public static boolean isGeoCoderApiKeyRequired(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(Keys.IS_GEO_CODER_API_KEY_REQUIRED, false);
    }


    public static void setDirectionsApiKeyRequired(Context context, boolean status) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(Keys.IS_DIRECTIONS_API_KEY_REQUIRED, status);
        if (status) {
            if (getDirectionsApiKeyCheckTime(context) == 0) {
                ed.putLong(Keys.API_KEY_CHECK_TIME_DIRECTIONS, System.currentTimeMillis());
            }
        } else {
            ed.putLong(Keys.API_KEY_CHECK_TIME_DIRECTIONS, 0);
        }
        ed.apply();
    }

    public static long getDirectionsApiKeyCheckTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(Keys.API_KEY_CHECK_TIME_DIRECTIONS, 0);
    }

    public static boolean isDirectionsApiKeyRequired(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(Keys.IS_DIRECTIONS_API_KEY_REQUIRED, false);
    }


    public static void setAvailableCities(Context context, GetCitiesResponse response) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
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
        ed.apply();
    }

    public static long getCitiesApiCallTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(Keys.AVAILABLE_CITIES_API_CALL_TIME, 0);
    }

    public static ArrayList<PlacesResult> getAvailableCities(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = sp.getString(Keys.AVAILABLE_CITIES, StringUtils.EMPTY);
        ArrayList<PlacesResult> citiesData = new ArrayList<>();
        if (StringUtils.isNotBlank(jsonString)) {
            citiesData = new Gson().fromJson(jsonString, new TypeToken<ArrayList<PlacesResult>>() {
            }.getType());
        }
        return citiesData;
    }


    public static void setADID(Context context, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(Keys.ADID, value);
        ed.apply();
    }

    public static String getADID(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(Keys.ADID, StringUtils.EMPTY);
    }

    public static void setLastAckTripID(Context context, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(Keys.LAST_ACK_TRIP_ID, value);
        ed.apply();
    }

    public static String getLastAckTripID(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(Keys.LAST_ACK_TRIP_ID, StringUtils.EMPTY);
    }


    public static void setOneSignalPlayerId(Context context, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(Keys.ONE_SIGNAL_PALYER_ID, value);
        ed.apply();
    }

    public static String getOneSignalPlayerId(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(Keys.ONE_SIGNAL_PALYER_ID, StringUtils.EMPTY);
    }

    public static void setWalletIncreasedError(Context context, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(Keys.WALLET_ERROR, value);
        ed.apply();
    }

    public static String getWalletIncreasedError(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(Keys.WALLET_ERROR, "Mazeed booking lainay kay liyay pehlay paisay jamma karein");
    }

    public static void setSettingsVersion(Context context, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(Keys.SETTINGS_VERSION, value);
        ed.apply();
    }

    public static String getSettingsVersion(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(Keys.SETTINGS_VERSION, StringUtils.EMPTY);
    }

    public static void setCashInHands(Context context, int value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt(Keys.CASH_IN_HANDS, value);
        ed.apply();
    }

    public static int getCashInHands(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(Keys.CASH_IN_HANDS, 0);
    }

    public static void setCashInHandsRange(Context context, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(Keys.CASH_IN_HANDS_RANGE, value);
        ed.apply();
    }

    public static int[] getCashInHandsRange(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String value = sp.getString(Keys.CASH_IN_HANDS_RANGE, StringUtils.EMPTY);
        if (StringUtils.isNotBlank(value)) {
            return new Gson().fromJson(value, int[].class);
        } else {
            return new int[]{0, 500, 1000, 1500, 2000};
        }
    }


}
