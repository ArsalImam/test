package com.bykea.pk.partner.utils;


import com.bykea.pk.partner.BuildConfig;

public class Constants {
    public static final String GCM_PROJECT_NO = "764640458585";
    public static final String MIX_PANEL_API_KEY = BuildConfig.DEBUG ? "ccfff911cf68c43185f8fe35c1efb964" : "b97eeebca45ee4e90b79b470ae28f2da";
    public static final String APP_NAME = "BYKEA PARTNER";

    public static final String DEVICE_TYPE = "android";
    public static final String USER_TYPE = "d";

    public static final String BYKEA = "BYKEA";
    public static final String BYKEA_URL = "BYKEA URL";
    public static final String BYKEA_ERROR = "BYKEA ERROR";
    public static final String BYKEA_WARNING = "BYKEA WARNING";
    public static final String TAG_CALL = "Call";
    public static final String TAG_ADVANCE_CALL = "AdvanceCall";
    public static final String TAG_NORMAL_CALL = "NORMAL_CALL";
    public static final String TAG_LOCATION = "LocationUpdate";
    public static final String TAG_GOOGLE_MAP = "GOOGLE MAP TRACKING";

    public static final String SETTINGS_DATA_EXTRAS = "SETTINGS_DATA_EXTRAS";

    public static final String CLOUDINARY_BASE_URL = "http://res.cloudinary.com/bykea/image/upload/";

    public static final double rwpLat = 33.598881, rwpLng = 73.043982, lhrLat = 31.5546, lhrLng = 74.3572, khiLat = 24.8615, khiLng = 67.0099;

    public static final String STATUS_CODE_OK = "OK";

    public static final String GEOCODE_RESULT_TYPE_POST_CODE = "postal_code";
    public static final String GEOCODE_RESULT_TYPE_STREET_NUMBER = "street_number";
    public static final String GEOCODE_RESULT_TYPE_COUNTRY_NAME = "country";
    public static final String GEOCODE_RESULT_TYPE_ADDRESS = "route";
    public static final String GEOCODE_RESULT_TYPE_ADDRESS_SUB_LOCALITY = "sublocality";
    public static final String GEOCODE_RESULT_TYPE_ADDRESS_1 = "premise";
    public static final String GEOCODE_RESULT_TYPE_CITY = "locality";
    public static final long MILISEC_IN_DAY = 86400000;
    public static final long MILISEC_IN_HALF_DAY = 43200000;
    public static final String REG_EX_DIGIT = "\\d+";
    // Partner Phase 2
    public static final String INSTA_BUG_BETA_KEY = "1a22f9efd3017c87f9fa8ad33645cafb";
    public static final String INSTA_BUG_LIVE_KEY = "36070216421223afd484f7b67802c146";

    public static final long MILLI_SEC_IN_1_AND_HALF_DAYS = 129600000;
    public static final int CONFIRM_DROPOFF_REQUEST_CODE = 101;
    public static final int UPDATE_DROPOFF_REQUEST_CODE = 102;
    public static final String TOOLBAR_TITLE = "toolbar_title";
    public static final String SEARCHBOX_TITLE = "searchBox_title";


    public final static String ON_NEW_NOTIFICATION = "checkNotification";
    public final static String ON_PERMISSIONS_GRANTED = "ON_PERMISSIONS_GRANTED";

    public static final String INVALID_REQUEST = "INVALID_REQUEST";
    public static final String OVER_QUERY_LIMIT = "OVER_QUERY_LIMIT";


    public static final String GOOGLE_PLACE_SERVER_API_KEY = BuildConfig.DEBUG ?
            "AIzaSyBWdn986iBdCt-K8PzVe_8ne3gEeGmu_8Y" : "AIzaSyBWfX7y01M4x03xDl-yOBJ9gqEifB7HPDY";

    //    Live new 12/01/17
//    public static final String GOOGLE_PLACE_SERVER_API_KEY = "AIzaSyDDSVksBi_d5aBo0WgXun0ZWG-Z2IUTYQQ";
    public static final String CONFIRM_DROPOFF_ADDRESS_RESULT = "CONFIRM_DROPOFF_ADDRESS_RESULT";

    public static final String REPLACE_CITY = "-replace-";


    public static class Extras {
        public static final String SELECTED_VEHICLE_DATA = "SELECTED_VEHICLE_DATA";
        public static final String RIDE_VEHICLE_DATA = "RIDE_VEHICLE_DATA";
        public static final String TRIP_DETAILS = "TRIP_DETAILS";
        public static final String LOCATION_SERVICE_STATUS = "LOCATION_SERVICE_STATUS";
    }

    public static class AnalyticsEvents {
        public final static String REPLACE = "_R_";
        public final static String CANCEL_TRIP = "Ride-Cancel";
        public final static String EYE_BALL = "Eyeball-";
        public final static String RIDE_FARE = EYE_BALL + REPLACE +"-Finish";
        public final static String RIDE_COMPLETE = EYE_BALL + REPLACE +"-Complete";
        public final static String ON_RECEIVE_NEW_JOB = EYE_BALL + REPLACE + "-Request";
        public final static String ON_ACCEPT = EYE_BALL + REPLACE + "-Accept";
        public final static String ON_ARRIVED = EYE_BALL + REPLACE + "-Arrived";
        public final static String ON_START = EYE_BALL + REPLACE + "-Started";
        public final static String ON_STATUS_UPDATE = EYE_BALL + "-StatusUpdate";

//        public final static String ON_FINISH = EYE_BALL + REPLACE  + "-Finished";//already logged against passenger
//        public final static String ON_FEEDBACK = EYE_BALL+ REPLACE   + "-Feedback";//already logged against passenger

    }

    public static final float ANDROID_OPACITY = 255;
    public static final String ON_SOCKET_CONNECTED = "ON_SOCKET_CONNECTED";
}
