package com.bykea.pk.partner.utils;


import com.bykea.pk.partner.BuildConfig;

public class Constants {
    public static final String GCM_PROJECT_NO = "764640458585";
    public static final String MIX_PANEL_API_KEY = BuildConfig.DEBUG ? "ccfff911cf68c43185f8fe35c1efb964" : "b97eeebca45ee4e90b79b470ae28f2da";
    public static final String APP_NAME = "BYKEA PARTNER";
    public static final String OS_NAME = "android";

    public static final String DEVICE_TYPE = "android";
    public static final String USER_TYPE = "d";
    public static final String UNAUTH_MESSAGE = "unAuthrozied User";
    public static final int ESTIMATION_SPEED = 30;

    public static final int MINIMUM_VOICE_RECORDING = 1000;

    public static final String BYKEA = "BYKEA";
    public static final String BYKEA_URL = "BYKEA URL";
    public static final String BYKEA_ERROR = "BYKEA ERROR";
    public static final String BYKEA_WARNING = "BYKEA WARNING";
    public static final String TAG_CALL = "Call";
    public static final String TAG_ADVANCE_CALL = "AdvanceCall";
    public static final String TAG_NORMAL_CALL = "NORMAL_CALL";
    public static final String TAG_LOCATION = "LocationUpdate";
    public static final String TAG_GOOGLE_MAP = "GOOGLE MAP TRACKING";

    /*Added these ride statuses to handle making calls to drop off location's person
     * Call option can only be enabled when ride has started or arrived*/
    public static final String BATCH_STARTED = "Started";
    public static final String BATCH_ARRIVED = "Arrived";
    /*added this for making individual's dropOff call enable/disable*/
    public static final String RIDE_FEEDBACK = "feedback";

    public static final int RIDE_ACCEPTANCE_TIMEOUT = 20800;
    public static final float TIME_IN_MILLISECONDS_PERCENTAGE = 5f;

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

    public static final long MILLI_SEC_IN_1_AND_HALF_DAYS = 129600000;
    public static final int CONFIRM_DROPOFF_REQUEST_CODE = 101;
    public static final int UPDATE_DROPOFF_REQUEST_CODE = 102;
    public static final String TOOLBAR_TITLE = "toolbar_title";
    public static final String SEARCHBOX_TITLE = "searchBox_title";


    public final static String ON_NEW_NOTIFICATION = "checkNotification";
    public final static String ON_PERMISSIONS_GRANTED = "ON_PERMISSIONS_GRANTED";

    public static final String INVALID_REQUEST = "INVALID_REQUEST";
    public static final String OVER_QUERY_LIMIT = "OVER_QUERY_LIMIT";
    private static final String GOOGLE_PLACE_SERVER_API_KEY_DEBUG = "AIzaSyBMtLLeM1ubKra2Dyl2B8LGL0bOOk3QwPU";//Staging Server Key of Passenger
    private static final String GOOGLE_PLACE_SERVER_API_KEY_LIVE = "AIzaSyBWfX7y01M4x03xDl-yOBJ9gqEifB7HPDY";

    //AIzaSyClj3C4IYReLc1ioHsiSdKAOz6xpYXK5x4 this key is giving query limit error
    //we have replaced the passenger's app debug autocomplete key
    private static final String GOOGLE_PLACE_AUTOCOMPLETE_API_KEY_DEBUG = "AIzaSyALb6BDq-cw_kWWCaiNw50eIthPzI9wISA";
    private static final String GOOGLE_PLACE_AUTOCOMPLETE_API_KEY_LIVE = "AIzaSyAafW-AwnS0kYt1F5VLVkeBQjWPlM6LDQA";


    public static final String GOOGLE_PLACE_SERVER_API_KEY = BuildConfig.DEBUG ? GOOGLE_PLACE_SERVER_API_KEY_DEBUG : GOOGLE_PLACE_SERVER_API_KEY_LIVE;
    public static final String GOOGLE_PLACE_AUTOCOMPLETE_API_KEY = BuildConfig.DEBUG ? GOOGLE_PLACE_AUTOCOMPLETE_API_KEY_DEBUG : GOOGLE_PLACE_AUTOCOMPLETE_API_KEY_LIVE;

    public static final String CONFIRM_DROPOFF_ADDRESS_RESULT = "CONFIRM_DROPOFF_ADDRESS_RESULT";

    public static final String REPLACE_CITY = "-replace-";
    public static final int PICK_IMAGE_REQUEST = 1001;
    /*moving camera to current location animation delay*/
    public static final int ANIMATION_DELAY_FOR_CURRENT_POSITION = 500;
    public static final int MINUTE_DIVISIBLE_VALUE = 60;
    public static final int KILOMETER_DIVISIBLE_VALUE = 1000;
    public static final float BOTTOM_SHEET_ALPHA_VALUE = 0.7f;
    public static final String LOADBOARD_JOBS_LIMIT = "10";
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000; //10000; 60 seconds

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2; //30 seconds

    public static final float LOCATION_SMALLEST_DISPLACEMENT = 10f;

    public static final int LOCATION_RESPONSE_COUNTER_RESET = 0;
    public static final int LOCATION_RESPONSE_NOT_RECEIEVED_ALLOWED_COUNTER = 3;

    public static final int BATTERY_OPTIMIZATION_RESULT = 2000;

    public static final String RETROFIT_METHOD_POST = "post";
    public static final String RETROFIT_METHOD_GET = "get";

    public static final int TRIP_STATUS_CODE_DELIVERY = 10;
    public static final int TRIP_STATUS_CODE_RIDE = 7;

    public static class CallType {
        public static final String SINGLE = "single";
        public static final String BATCH = "batch";
    }


    public static class Notification {
        public static final String NOTIFICATION_CHANNEL_ID = "bykea_p_channel_id_for_loc";
        public static final String NOTIFICATION_CHANNEL_NAME = "Bykea Active/Inactive Status";
        public static final String NOTIFICATION_CONTENT_TITLE = "Bykea Partner";

        public static final String EVENT_TYPE = "event";
        public static final String DATA_TYPE = "data";
    }

    public static class FCMEvents {
        public static final String MULTIDELIVER_INCOMING_CALL = "10";
        public static final String MULTIDELIVER_CANCEL_BY_ADMIN = "11";
    }

    public static final int SPLASH_SCREEN_FUTURE_TIMER = 2000;// 2 Seconds
    public static final int SPLASH_SCREEN_INTERVAL_TIMER = 2000;// 2 Seconds

    public static final String BUILD_VARIANT_LOCAL_FLAVOR = "local";

    public static final String OTP_SMS = "sms";
    public static final String OTP_CALL = "call";

    public static final long VERIFICATION_WAIT_MAX_TIME = 25000;
    public static final long VERIFICATION_WAIT_COUNT_DOWN = 100;

    public static final String DRIVER_STATUS_CODE = "2";


    public static final int RESET_CASH_TO_DEFAULT_POSITION = 1;
    public static final int RESET_CASH_TO_DEFAULT_AMOUNT = 1000;
    public static final int REQUEST_CODE_GPS_AND_LOCATION = 9090;

    public final static String ACTION = "action";
    public static final int IN_ACTIVE_MUSIC_SOUND = 5000;


    public static String VERIFICATION_CODE_RECEIVED = "VERIFICATION_CODE_RECEIVED";
    public static final String SMS_RECEIVER_TAG = "android.provider.Telephony.SMS_RECEIVED";

    public static final String IS_FROM_GCM = "isGcm";
    public static final String MOBILE_IMEI_ERROR = "IMEI";
    public static final String FRIVOLOUS_CANCELLATIONS_ER = "frivolous cancellations";
    public static final String FRIVILOUS_CANCELLATIONS_UR = "مسلسل کینسل کرنے کی وجہ سے آپکو کچھ دیر کے لیے بلاک کردیا گیا ہے۔";


    public static class Extras {
        public static final String LOCATION_SERVICE_STATUS = "LOCATION_SERVICE_STATUS";
        public static final String CONTACT_TYPE = "cType";


        public static final String SELECTED_VEHICLE_DATA = "SELECTED_VEHICLE_DATA";
        public static final String RIDE_VEHICLE_DATA = "RIDE_VEHICLE_DATA";
        public static final String TRIP_DETAILS = "TRIP_DETAILS";
        public static final String TRIP_DATA = "TRIP_DATA";
        public static final String SELECTED_ITEM = "SELECTED_ITEM";
        public static final String SELECTED_INDEX = "SELECTED_INDEX";
        public static final String SIGN_UP_DATA = "SIGN_UP_DATA";
        public static final String DRIVER_ID = "DRIVER_ID";
        public static final String SIGN_UP_IMG_BASE = "SIGN_UP_IMG_BASE";
        public static final String SELECTED_CITY = "SELECTED_CITY";
        public static final String PHONE_NUMBER = "PHONE_NUMBER";
        public static final String CNIC = "CNIC";
        public static final String LIST_ITEMS = "LIST_ITEMS";
        public static final String CALL_PENDING_API = "CALL_PENDING_API";
        public static final String NAVIGATE_TO_BOOKING_SCREEN = "NAVIGATE_TO_BOOKING_SCREEN";
        public static final String NAVIGATE_TO_HOME_SCREEN = "NAVIGATE_TO_HOME_SCREEN";
        public static final String CHAT_MSG = "CHAT_MSG";
        public static final String HIDE_SEARCH = "HIDE_SEARCH";
        public static final String IS_FROM_VIEW_PAGER = "IS_FROM_VIEW_PAGER";
        public static final String TOP_BAR = "top_bar";
        public static final String DROP_OFF = "DROP_OFF";
        public static final String PICK_UP = "PICK_UP";
        public static final String IS_FINGER_PRINTS_SUCCESS = "IS_FINGER_PRINTS_SUCCESS";
        public static final String IS_BIOMETRIC_VERIFIED = "IS_FINGER_PRINTS_SUCCESS";

        public static final String POSITION_DELIVERY_SCHEDULE = "POSITION_DELIVERY_SCHEDULE";
        public static final String IS_CANCELED_TRIP = "isCancelledTrip";
        public static final String IS_CANCELED_TRIP_BY_ADMIN = "isCanceledByAdmin";
        public static final String INACTIVE_PUSH_DATA = "INACTIVE_PUSH_DATA";
    }

    public static class Broadcast {
        public static final String UPDATE_FOREGROUND_NOTIFICATION = "UPDATE_FOREGROUND_NOTIFICATION";
    }

    public static class Actions {
        public final static String STARTFOREGROUND_ACTION = "STARTFOREGROUND_ACTION";
        public final static String STOPFOREGROUND_ACTION = "STOPFOREGROUND_ACTION";
        public final static String ON_NOTIFICATION_CLICK = "ON_NOTIFICATION_CLICK";
        public final static String UPDATE_FOREGROUND_NOTIFICATION = "UPDATE_FOREGROUND_NOTIFICATION";
        public final static String ON_GPS_ENABLED_CHANGE = "android.location.GPS_ENABLED_CHANGE";
        public final static String ON_LOCATION_CHANGED = "android.location.PROVIDERS_CHANGED";
        public final static String ON_CONECTIVITY_CHANGED = "android.net.conn.CONNECTIVITY_CHANGE";

    }

    public static class Category {
        public final static String ON_CATEGORY_DEFAULT = "android.intent.category.DEFAULT";
    }

    public static class RequestCode {
        public final static int SCAN_FINGER_PRINTS = 123;
    }

    public static class TimeFormats {
        public final static String LICNENSE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    }

    public static class GoogleMap {
        public final static String GOOGLE_NAVIGATE_ENDPOINT = "http://maps.google.com/maps?saddr=";
        public final static String GOOGLE_DESTINATION_ENDPOINT = "&daddr=";
        public final static String GOOGLE_MAP_PACKAGE = "com.google.android.apps.maps";
        public final static String GOOGLE_MAP_ACTIVITY = "com.google.android.maps.MapsActivity";
    }

    public static class MapDetailsFragmentTypes {
        public static final String TYPE_CALL = "کال";
        public static final String TYPE_TAFSEEL = "تفصیل";
        public static final String TYPE_MUKAMAL = "مکمل";
    }

    public static class AnalyticsEvents {
        public final static String REPLACE = "_R_";
        public final static String CANCEL_TRIP = "Ride-Cancel";
        public final static String EYE_BALL = "Eyeball-";
        public final static String RIDE_FARE = EYE_BALL + REPLACE + "-Finish";
        public final static String RIDE_COMPLETE = EYE_BALL + REPLACE + "-Complete";
        public final static String ON_RECEIVE_NEW_JOB = EYE_BALL + REPLACE + "-Request";
        public final static String ON_ACCEPT = EYE_BALL + REPLACE + "-Accept";
        public final static String ON_ARRIVED = EYE_BALL + REPLACE + "-Arrived";
        public final static String ON_START = EYE_BALL + REPLACE + "-Started";
        public final static String ON_STATUS_UPDATE = EYE_BALL + "-StatusUpdate";
        public final static String ON_SIGN_UP_BTN_CLICK = "SignupButton";
        public final static String ON_SIGN_UP_MOBILE_ENTERED = "SignupMobile";
        public final static String ON_SIGN_UP_COMPLETE = "SignupComplete";
        public final static String ON_LOGIN_SUCCESS = "LoginSuccessful";
        public final static String ON_RIDE_COMPLETE = "RideComplete";


//        public final static String ON_FINISH = EYE_BALL + REPLACE  + "-Finished";//already logged against passenger
//        public final static String ON_FEEDBACK = EYE_BALL+ REPLACE   + "-Feedback";//already logged against passenger

    }

    public static final float ANDROID_OPACITY = 255;
    public static final String ON_SOCKET_CONNECTED = "ON_SOCKET_CONNECTED";
    public static final String COUNTRY_CODE_AUTOCOMPLETE = "country:pk";
    public static final String PLACES_TITLE = "places_title";
    public static final float SAVED_PLACES_RADIUS = 200f;
    public static final String SAVE_PLACE_RESULT = "SAVE_PLACE_RESULT";
    public static final int SAVE_PLACE_REQUEST_CODE = 107;
    public final static int REQUEST_CAMERA = 23;
    public final static int REQUEST_GALLERY = 22;
    public final static String UPLOAD_IMG_EXT = ".jpg";
    public final static int RESTART_LOCATION_SERVICE_DELAY = 1000;
    public final static int LOCATION_API_WAIT_ON_INACTIVE_PUSH = 15000;

    public final static String RIDE_TYPE_FOOD_DELIVERY = "FoodDelivery";

    /**
     * Constant for checking delivery status whether is successful or not - Using is FeedbackActivity
     * 0 is for kamyab
     * other than 0 is na-kamyab
     */
    public final static int KAMYAB_DELIVERY = 0;

    public final static String DRIVER_SOCKET_CLIENT_TYPE = "PARTNER_ANDROID";

    /**
     * This inner class will contain Constants for Log Tags and Error Log Messages
     */
    public static class LogTags {
        public final static String RETROFIT_ERROR = "Retrofit Error";
        public final static String TIME_OUT_ERROR = "TimeOut ";
        public final static String CONVERSION_ERROR = "ConversionError ";
        public final static String OTHER_ERROR = "Other Error ";
        // Log file Max file size before it creates a new file for logs
        public static final long LOG_FILE_MAX_SIZE = 1024 * 1024;
        //Developer email address which is used for sending logs.
        public static final String[] LOG_SEND_DEVELOPERS_EMAIL = new String[]{"adil.baig@bykea.com",
                "abdul.mannan@bykea.com", "amir.raza@bykea.com", "aftab.sikander@bykea.com"};
        public static final String LOG_SEND_SUBJECT = "Log Files";
        public static final String LOG_SEND_MESSAGE_BODY = "Latest logs attached";
        public static final String BYKEA_LOG_TAG = "BYKEA_LOG_TAG";
        public static final String BYKEA_INACTIVE_PUSH = "INACTIVE_PUSH";

    }

    /**
     * This inner class will contain Constants for Fcm Push Notification's Events
     */
    public static class FcmEvents {
        public static final String INACTIVE_PUSH = "7";
    }

    public static class Driver {
        public static String STATUS_ACTIVE = "ACTIVE";
        public static String STATUS_INACTIVE = "INACTIVE";
    }


    /**
     * This inner class will contain Constants for Time Formats
     */
    public static class TimeFormat {
        public final static String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    }

    /**
     * List of supported services name and title by our eco system.
     */
    public static class ServiceType {

        public static final String RIDE_NAME = "Ride";
        public static final String RIDE_TITLE = "Ride";

        public static final String SEND_NAME = "Send";
        public static final String SEND_TITLE = "Delivery";

        public static final String BRING_NAME = "Bring";
        public static final String BRING_TITLE = "Purchase";

        public static final String TICKETS_NAME = "Bus Ticket";
        public static final String TICKETS_TITLE = "Ticket";

        public static final String JOBS_NAME = "Jobs";
        public static final String JOBS_TITLE = "Jobs";

        public static final String CLASSIFIEDS_NAME = "Classifieds";
        public static final String CLASSIFIEDS_TITLE = "Classifieds";

        public static final String CARRY_VAN_NAME = "Carry Van";
        public static final String CARRY_VAN_TITLE = "Bachat Courier";

        public static final String ADS_NAME = "Ads";
        public static final String ADS_TITLE = "Food";

        public static final String UTILITY_BILL_NAME = "Utility Bill";
        public static final String UTILITY_BILL_TITLE = "Utility Bill";

        public static final String FOOD_DELIVERY_NAME = "FoodDelivery";
        public static final String FOOD_DELIVERY_TITLE = "Food Delivery";

    }

    /***
     * list of Connectivity signal status constant which would be used for future reference
     */
    public static class ConnectionSignalStatus {

        public static final String UNKNOWN_STATUS = "Unknown Status";
        public static final String BATTERY_LOW = "Battery Low";
        public static final String POOR_STRENGTH = "Poor Connection";
        public static final String FAIR_STRENGTH = "Fair Connection";
        public static final String GOOD_STRENGTH = "Good Connection";
        public static final String NO_CONNECTIVITY = "No Connection";

    }

    /***
     * This inner class will contain Constants for Trip Types
     */
    public static class TripTypes {

        public static final String RIDE_TYPE = "Ride";

        public static final String CLASSIFIED_TYPE = "Classifieds";

        public static final String MOVIETICKET_TYPE = "Movie Ticket";

        public static final String AIRTICKET_TYPE = "Air Ticket";

        public static final String PURCHASE_TYPE = "Purchase";
        public static final String PURCHASE_NAME = "Bring";

        public static final String DELIVERY_TYPE = "Delivery";

        public static final String DEPOSIT_TYPE = "Deposit";

        public static final String TOPUP_TYPE = "Top-Up";

        public static final String BUSTICKET_TYPE = "Bus Ticket";

        public static final String VAN_TYPE = "Carry Van";
        public static final String COURIER_TYPE = "Courier";

        public static final String INSURANCE_TYPE = "Bima";
        public static final String INSURANCE_NAME = "Insurance";

        public static final String JOBS_TYPE = "Jobs";

        public static final String FOOD_TYPE = "Food";
        public static final String FOOD_BOOKING_TYPE = "MB";

        public static final String BILL_TYPE = "Utility Bill";

        public static final String BATCH_TYPE = "batch";

    }


    /**
     * Inner class for Font Names
     */
    public static class FontNames {
        public static final String JAMEEL_NASTALEEQI = "jameel_noori_nastaleeq.ttf";
        public static final String OPEN_SANS_REQULAR = "open_sans_regular.ttf";
        public static final String OPEN_SANS_BOLD = "open_sans_semi_bold.ttf";
    }

    /***
     * Inner class for API error which holds all error use case constants
     */
    public static class ApiError {
        public static final int BUSINESS_LOGIC_ERROR = 422;
        public static final int DRIVER_NOT_REGISTER = 404;
        public static final int APP_FORCE_UPDATE = 1001;
        public static final int DRIVER_LAT_LNG_ZERO = 1002;
        public static final int DRIVER_ACCOUNT_BLOCKED = 1003;
        public static final int DRIVER_LICENSE_EXPIRED = 1004;
        public static final int DRIVER_REGION_NOT_ALLOWED = 1005;
        public static final int MULTIPLE_CANCELLATION_BLOCK = 1007;
        public static final int IMEI_NOT_REGISTERED = 1008;
        public static final int WALLET_EXCEED_THRESHOLD = 1009;
        public static final int OUT_OF_SERVICE_REGION = 1010;
        public static final int STATUS_CHANGE_DURING_RIDE = 1011;
        public static final int LOADBOARD_BOOKING_ALREADY_TAKEN = 1012;
        public static final int LOADBOARD_ALREADY_IN_TRIP = 1013;

    }


    public static final int MARKER_INCREMENT_FACTOR_DEFAULT = 20;
    public static final int MARKER_INCREMENT_FACTOR_TEN_KM = 10000;
    public static final int MARKER_INCREMENT_FACTOR_SIX_KM = 6000;
    public static final int MARKER_INCREMENT_FACTOR_FOUR_KM = 4000;
    public static final int MARKER_INCREMENT_FACTOR_TWO_KM = 2000;

}
