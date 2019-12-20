package com.bykea.pk.partner.utils;

import com.bykea.pk.partner.BuildConfig;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

public class Constants {
    public static final int DIGIT_ZERO = 0;
    public static final int DIGIT_ONE = 1;
    public static final int DIGIT_TWO = 2;
    public static final int NEGATIVE_DIGIT_ONE = -1;

    public static final String APP = "APP";
    public static final String GCM_PROJECT_NO = "764640458585";
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
    public static final String GOOGLE_CHROME_PACKAGE = "com.android.chrome";

    public final static String ON_NEW_NOTIFICATION = "checkNotification";
    public final static String ON_PERMISSIONS_GRANTED = "ON_PERMISSIONS_GRANTED";

    public static final String INVALID_REQUEST = "INVALID_REQUEST";
    public static final String OVER_QUERY_LIMIT = "OVER_QUERY_LIMIT";
    public static final String EVENT_ACTION_UPDATE_WITHDRAW = "EVENT_ACTION_UPDATE_WITHDRAW";
    public static final String DEFAULT_ADMIN_FEE = "10";
    public static final String FONT_NASTALIQ = "jameel_noori_nastaleeq.ttf";
    public static final String FONT_ROBOTO_MED = "roboto_medium.ttf";
    public static final String INTENT_TRIP_HISTORY_ID = "INTENT_TRIP_HISTORY_ID";
    public static final int MAX_RECORDS_PER_PAGE = 20;
    public static final String SORT_BY_NEWEST = "newest";

    public static final String S3_DD_ICON_URL = "https://bykea-assets.s3-us-west-2.amazonaws.com/icons/ic_driver_destination.png";
    public static final String S3_OFFLINE_RIDE_ICON_URL = "https://bykea-assets.s3-us-west-2.amazonaws.com/icons/ic_offline.png";
    public static final String S3_OFFLINE_DELIVERY_ICON_URL = "https://bykea-assets.s3-us-west-2.amazonaws.com/icons/ic_offline_delivery.png";

    private static final String GOOGLE_PLACE_SERVER_API_KEY_DEBUG = "AIzaSyDbLexawbNFi_cA3DPKtn0BJc_L3HMCpwk";
    private static final String GOOGLE_PLACE_SERVER_API_KEY_LIVE = "AIzaSyBWfX7y01M4x03xDl-yOBJ9gqEifB7HPDY";
    public static final String HOW_IT_WORKS_WEB_URL = "https://www.bykea.com/partner-videos";

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


    public static final long ON_TRIP_UPDATE_INTERVAL_IN_MILLISECONDS = 9 * 1000; //9s
    public static final long ON_TRIP_UPDATE_INTERVAL_IN_MILLISECONDS_DEFAULT = 21 * 1000; //21s
    public static final long ON_TRIP_UPDATE_INTERVAL_DIVISIBLE = 3;

    public static final float LOCATION_SMALLEST_DISPLACEMENT = 10f;

    public static final int LOCATION_RESPONSE_COUNTER_RESET = 0;
    public static final int LOCATION_RESPONSE_NOT_RECEIEVED_ALLOWED_COUNTER = 3;

    public static final int BATTERY_OPTIMIZATION_RESULT = 2000;

    public static final String RETROFIT_METHOD_POST = "post";
    public static final String RETROFIT_METHOD_GET = "get";

    public static final int TRIP_STATUS_CODE_DELIVERY = 10;
    public static final int TRIP_STATUS_CODE_RIDE = 7;
    public static final String SEPERATOR_ABOVE = "above";
    @Nullable
    public static final String BOOKING_DETAIL_VIEW_TYPE_RATING = "rating";
    @NotNull
    public static final String COMMA = ",";
    @NotNull
    public static final String NEAR_LBL = "Near ";

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

//    public static class FCMEvents {
//        public static final String MULTIDELIVER_INCOMING_CALL = "10";
//        public static final String MULTIDELIVER_CANCEL_BY_ADMIN = "11";
//    }

    public static final String FCM_EVENTS_MULTIDELIVER_INCOMING_CALL = "10";
    public static final String FCM_EVENTS_MULTIDELIVER_CANCEL_BY_ADMIN = "11";

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
        public final static String ON_TRIP_LOCATION_UPDATE_CUSTOM_INTERVAL = "ON_TRIP_LOCATION_UPDATE_CUSTOM_INTERVAL";
        public final static String IS_CALLED_FROM_LOADBOARD = "IS_CALLED_FROM_LOADBOARD";

        public static final String RIDE_CREATE_DATA = "RIDE_CREATE_DATA";

        public static final String FROM = "from";
        public static final String FLOW_FOR = "FLOW_FOR";
        public static final String OFFLINE_RIDE = "OFFLINE_RIDE";
    }

    public static class Broadcast {
        public static final String UPDATE_FOREGROUND_NOTIFICATION = "UPDATE_FOREGROUND_NOTIFICATION";
        public static final String UPDATE_LOADBOARD_BOOKINGS_REQUEST = "UPDATE_LOADBOARD_BOOKINGS_REQUEST";
        public static final String CHAT_MESSAGE_RECEIVED = "CHAT_MESSAGE_RECEIVED";
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
        public final static String TRANSIT_MODE_BIKE = "&mode=motorbike";
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
        public final static String ON_PARTNER_LOCATION_UPDATE = "Partner-Pulse";

        public final static String ON_LB_SWIPE_UP = EYE_BALL + "LoadBoard-Swipe-Up";
        public final static String ON_LB_REFRESH = EYE_BALL + "LoadBoard-Refreshed";
        public final static String ON_LB_BACK_SWIPE_DOWN = EYE_BALL + "LoadBoard-Swipe-Down";

        public final static String ON_LB_BOOKING_DETAIL = EYE_BALL + "LoadBoard-Booking-Detail";
        public final static String ON_LB_BACK_FROM_BOOKING_DETAIL = EYE_BALL + "LoadBoard-Back-To-List";
        public final static String ON_LB_PICKUP_DIRECTION = EYE_BALL + "LoadBoard-Direction-Pick";
        public final static String ON_LB_DROPOFF_DIRECTION = EYE_BALL + "LoadBoard-Direction-Drop";
        public final static String ON_LB_BOOKING_ACCEPT = EYE_BALL + "LoadBoard-Booking-Accept";

        public final static String ON_CHAT_TEMPLATE_TAPPED = "Chat-Template";

        public final static String ON_CALL_BUTTON_CLICK = "Call-button-Click";
        public final static String ON_CALL_BUTTON_CLICK_MOBILE = "Call-button-Click-Mobile";
        public final static String ON_CALL_BUTTON_CLICK_WHATSAPP = "Call-button-Click-Whatsapp";
//        public final static String ON_LB_TAKEN = EYE_BALL + "LoadBoard-Taken";

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
    public final static int HANDLER_POST_DELAY_LOAD_BOARD = 2000;
    public final static int MAX_LIMIT_LOAD_BOARD = 10;

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
     * Service Codes
     */
    public static class ServiceCode {
        public static final int SEND = 21;
        public static final int SEND_COD = 22;
        public static final int RIDE = 23;
        public static final int OFFLINE_RIDE = 24;
        public static final int MART = 25;
        public static final int MOBILE_TOP_UP = 27;
        public static final int MOBILE_WALLET = 28;
        public static final int BANK_TRANSFER = 29;
        public static final int UTILITY = 30;
        public static final int OFFLINE_DELIVERY = 31;
    }

    /**
     * List of supported services name and title by our eco system.
     */
    @Deprecated
    public static class ServiceType {

        public static final String RIDE_NAME = "Ride";
        public static final String RIDE_TITLE = "Ride";

        public static final String SEND_NAME = "Send";
        public static final String SEND_TITLE = "Delivery";
        public static final String OFFLINE_RIDE_STRING = "Offline Ride";

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
        public static final String GOODS_TYPE = "Goods";
        public static final String COURIER_TYPE = "Courier";

        public static final String INSURANCE_TYPE = "Bima";
        public static final String INSURANCE_NAME = "Insurance";

        public static final String JOBS_TYPE = "Jobs";

        public static final String FOOD_TYPE = "Food";
        public static final String FOOD_BOOKING_TYPE = "MB";

        public static final String BILL_TYPE = "Utility Bill";

        public static final String BATCH_TYPE = "batch";
        public static final String SAWARI = "Sawari";
        public static final String OFFLINE_RIDE = "Offline Ride";

    }

    public class BookingFetchingStates {
        public static final String END = "end";
        public static final String START = "start";
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
        public static final int ERROR_MSG_CODE = 1050;
    }

    /**
     * Inner class for Screen redirection from navigation drawer
     */
    public static class ScreenRedirections {
        public static final int PROFILE_SCREEN = 0;
        public static final int HOME_SCREEN = 1;
        public static final int OFFLINE_RIDES = 2;
        public static final int TRIP_HISTORY_SCREEN = 3;
        public static final int WALLET_SCREEN = 4;
        public static final int HOW_IT_WORKS_SCREEN = 5;
        public static final int CONTACT_US_SCREEN = 6;
        public static final int LOGOUT = 7;

        public static final String PROFILE_SCREEN_S = "PROFILE_SCREEN";
        public static final String HOME_SCREEN_S = "Home";
        public static final String OFFLINE_RIDES_S = "Offline Rides";
        public static final String TRIP_HISTORY_SCREEN_S = "Booking History";
        public static final String WALLET_SCREEN_S = "Wallet";
        public static final String HOW_IT_WORKS_SCREEN_S = "How it works";
        public static final String CONTACT_US_SCREEN_S = "Contact Us";
        public static final String LOGOUT_S = "LOGOUT";
    }

    public static final int MARKER_INCREMENT_FACTOR_DEFAULT = 20;
    public static final int MARKER_INCREMENT_FACTOR_TEN_KM = 10000;
    public static final int MARKER_INCREMENT_FACTOR_SIX_KM = 6000;
    public static final int MARKER_INCREMENT_FACTOR_FOUR_KM = 4000;
    public static final int MARKER_INCREMENT_FACTOR_TWO_KM = 2000;


    public static final String FILE_EXT = ".aac";
    public static final String AUDIO_TEMP_FILE_NAME = "tempFile";

    //Amazon credentials
    public static class Amazon {
        public static String IDENTITY_POOL_ID = "eu-west-1:19881c67-5a16-442f-a8fd-1ef78c5e5ef9";
        public static String BUCKET_NAME = "loadboard";
    }

    /**
     * Firebase Analytics Configuration
     * Event Configurations Limits
     */
    public static class FirebaseAnalyticsConfigLimits {
        public static final int EVENT_NAME_LENGTH = 40;
        public static final int EVENT_PARAMETER_KEY_LENGTH = 40;
        public static final int EVENT_MAX_STRING_VALUES = 10;
        public static final int EVENT_MAX_NUMERIC_VALUES = 40;
    }

    /**
     * Zendesk SDK Configurations
     */
    public static class ZendeskConfigurations {
        //OLD KEYS
        /*public static String SUBDOMAIN_URL = "https://bykea-help.zendesk.com";
        public static String APPLICATION_ID = "fb44d30b787144a79589dd8e89080daa46458dbff84d92ef";
        public static String OAUTH_CLIENT_ID = "mobile_sdk_client_67c82b4799db889e3113";*/

        //PROD KEYS
        public static String SUBDOMAIN_URL = "https://bykea.zendesk.com";
        public static String APPLICATION_ID = "192495b9f94219fd3b1476c480c34170d003e1918df41599";
        public static String OAUTH_CLIENT_ID = "mobile_sdk_client_84be9aa0fb3f3d5c5c2b";

        public static long ZENDESK_SETTING_IDENTITY_MAX_TIME = 10 * 1000;//2.5 Minutes
        public static long ZENDESK_SETTING_IDENTITY_INTERVAL_TIME = 100;//0.1 Second

    }

    /**
     * Zendesk Custom Fields
     */
    public static class ZendeskCustomFields {
        public static long Assignee = 360020675574L;
        public static long Booking_ID = 360023253253L;
        public static long Booking_Type = 360023253273L;
        public static long Cancellation_Reason = 360023253733L;
        public static long Cancelled_by = 360023230954L;
        public static long COD_Amount = 360023253373L;
        public static long Customer_Name = 360023230174L;
        public static long Customer_Number = 360023253053L;
        public static long Customer_Penalty_Amount = 360023231274L;
        public static long Description = 360020675474L;
        public static long Distance_to_Pickup = 360023254053L;
        public static long Group = 360020675554L;
        public static long Last_Trip_Status = 360023230914L;
        public static long Parcel_Value = 360023253293L;
        public static long Partner_Email = 360023230334L;
        public static long Partner_Name = 360023253073L;
        public static long Partner_Number = 360023253093L;
        public static long Partner_Penalty_Amount = 360023231434L;
        public static long Priority = 360020675534L;
        public static long Problem_Topic_Selected = 360023230514L;
        public static long Received_Amount = 360023230934L;
        public static long Receivers_Name = 360023230674L;
        public static long Receivers_Number = 360023253353L;
        public static long Status = 360020675494L;
        public static long Subject = 360020675454L;
        public static long Trip_Distance = 360023253553L;
        public static long Trip_End_Address = 360023253573L;
        public static long Trip_Fare = 360023230694L;
        public static long Trip_Start_Address = 360023230894L;
        public static long Trip_Time = 360023230854L;
        public static long Type = 360020675514L;
        public static long Wait_Time = 360023230974L;
        public static long Wallet_Deduction = 360023253753L;
    }

    public static class ZendeskTicketStatus {
        public static String New = "New";
        public static String Open = "Open";
        public static String Pending = "Pending";
        public static String Solved = "Solved";
    }

    public static final String INTENT_TRIP_HISTORY_DATA = "TRIP_HISTORY_DATA";
    public final static String REQUIRED_DATE_FORMAT = "dd MMM, hh:mm a";

    public static String WHATSAPP_URI_PREFIX = "https://wa.me/";

    public static class ApplicationsPackageName {
        public static String WHATSAPP_PACKAGE = "com.whatsapp";
    }

    public static final String TRANSALATION_SEPERATOR = "///";

    //ON PAGE FINISHED IS TAKING TIME, SO USE THIS. IN WHICH USER CAN GET SOMEHOW SOMETHING IS START TO APPEARS
    public final static long DIMISS_DIALOG_WEBVIEW_LOADING = 3 * 1000;

    public final static int PARTNER_TOP_UP_NEGATIVE_LIMIT_FALLBACK = 50;
    public final static int PARTNER_TOP_UP_POSITIVE_LIMIT_FALLBACK = 500;
    public final static int AMOUNT_LIMIT = 35000;
    public static final int BYKEA_CASH_MAX_AMOUNT = 7700;

    public final static int MAP_AUTO_ZOOM_IN_OUT_DELAY = 5 * 60 * 1000; /*Five Minutes*/
    public final static String MOBILE_COUNTRY_STANDARD = "92";
    public final static String MOBILE_TEL_URI = "tel:";
    public final static int MAX_LENGTH_CNIC = 13;
    public final static int MAX_LENGTH_IBAN = 24;
    public final static String BYKEA_SUPPORT_HELPLINE = "02138654444";
    public final static String BYKEA_SUPPORT_CONTACT_NUMBER = "03111111700";
    public final static String SEPERATOR = "/";

    public final static int DIRECTION_API_MIX_THRESHOLD_METERS = 45; //meters
    public final static int DIRECTION_API_MIX_THRESHOLD_METERS_FOR_MULTIDELIVERY = 150; //meters
    public final static int DISTANCE_MATRIX_API_CALL_THRESHOLD_TIME = 8; //80 seconds
    public final static int DISTANCE_MATRIX_API_CALL_START_STATE_THRESHOLD_TIME = 30; //300 seconds
    public final static int DISTANCE_MATRIX_API_MULTIDELIVERY_THRESHOLD_COUNT = 5; //5x2=100 seconds
    public final static int DIRECTION_API_TIME_IN_MILLISECONDS = 60000; //60 seconds
    public final static int DIRECTION_API_TIME_IN_MILLISECONDS_MULTIDELIVERY = 90000; //90 seconds

    public static final long SET_SCALE_ANIMATION_DURATION = 500;
    public static final int SET_SCALE_ANIMATION_REPEAT_COUNT = 7;
    public static final float SET_SCALE_ANIMATION_FROM_X = 1.0f;
    public static final float SET_SCALE_ANIMATION_FROM_Y = 1.0f;
    public static final float SET_SCALE_ANIMATION_TO_X = 0.7f;
    public static final float SET_SCALE_ANIMATION_TO_Y = 0.7f;
    public static final float SET_SCALE_ANIMATION_PIVOT_X = 0.5f;
    public static final float SET_SCALE_ANIMATION_PIVOT_Y = 0.5f;
    public static final long SET_SCALE_DELAY = 2000;
    public static final long SET_SCALE_DELAY_ZERO = 0;
    public static final String ANDROID_RESOURCE_URI = "android.resource://";
}
