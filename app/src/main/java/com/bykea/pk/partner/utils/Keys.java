package com.bykea.pk.partner.utils;

public class Keys {

    /*******************
     * PREFERENCE KEYS *
     *******************/
    public static final String SETTING_DATA = "SETTING_DATA";
    public static final String DRIVER_DATA = "DRIVER_DATA";
    public static final String DRIVER_ID = "DRIVER_ID";
    public static final String USER_STATUS = "user_status";
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String PHONE_NUMBER_VERIFIED = "PHONE_NUMBER_VERIFIED";
    public static final String FCM_REGISTRATION_ID = "FCM_REGISTRATION_ID";
    public static final String AVAILABLE_STATUS = "AVAILABLE_STATUS";
    public static final String AVAILABLE_STATUS_API_CALL = "AVAILABLE_STATUS_API_CALL";
    public static final String VERIFIED_STATUS = "VERIFIED_STATUS";
    public static final String LOGIN_STATUS = "LOGIN_STATUS";
    public static final String SIGN_UP_API_CALL_CHECK = "SIGN_UP_API_CALL_CHECK";
    public static final String APP_VERSION = "APP_VERSION";
    public static final String APP_VERSION_CODE = "APP_VERSION_CODE";
    public static final String IS_ALREADY_CLEAR = "IS_ALREADY_CLEAR";
    public static final String EST_FARE = "EST_FARE";


    public static final String FULL_NAME = "full_name";
    public static final String PHONE_NUMBER = "phone";
    public static final String PIN_CODE = "pin_code";
    public static final String AGREE_CHECK = "i_agree";
    public static final String CITY = "city";
    public static final String ADDRESS = "address";
    public static final String EMAIL = "email";
    public static final String PLATE_NO = "plate_no";
    public static final String LICENSE_NO = "driver_license_number";
    public static final String EXPIRY_DATE = "license_expire";
    public static final String VEHICLE_TYPE = "vehicle_type";
    public static final String DEVICE_TYPE = "devicetype";
    public static final String REG_ID = "regid";

    public static final String EMAIL_VERIFIED = "email_verified";
    public static final String TOP_UP_PASSENGER = "top_up_passenger";
    public static final String ZENDESK_IDENTITY_SETUP_TIME = "zendesk_identity_setup_time";
    public static final String ZENDESK_SDK_READY = "zendesk_sdk_ready";


    /*IMAGE URL ALTERATION*/
    public static final String SQUARE_ROUND_IMAGE = "upload/c_thumb,r_10,g_face,h_100,w_120/e_improve,q_60/f_auto/";
    public static final String ROUND_IMAGE = "upload/c_thumb,g_face,h_200,w_200/e_improve,q_60/f_auto/";
    public static final String NORMAL_IMAGE = "upload/c_thumb,g_face/e_improve,q_60/f_auto/";


    /*CALL DATA OBJECT STRING KEY*/
    public static final String CALLDATA_OBJECT = "CALLDATA_OBJECT";
    public static final String ADVANCE_CALL_DATA_OBJECT = "ADVANCE_CALL_DATA_OBJECT";

    //region MULTI DELIVERY OBJECT STRING KEY
    public static final String MULTIDELIVERY_CALLDRIVER_OBJECT = "MULTIDELIVERY_CALLDRIVER_OBJECT";
    public static final String MULTIDELIVERY_MISSED_EVENT = "MULTIDELIVERY_MISSED_EVENT";
    public static final String MULTIDELIVERY_BATCH_COMPLETED = "MULTIDELIVERY_BATCH_TRIP_COMPLETED";
    public static final String DELIVERY_TYPE = "DELIVERY_TYPE";
    public static final String MULTIDELIVERY_COMPLETE_DATA = "MULTIDELIVERY_COMPLETE_DATA";
    public static final String MULTIDELIVERY_TRIP_ID = "MULTIDELIVERY_TRIP_ID";
    public static final String MULTIDELIVERY_COMPLETED_COUNT = "MULTIDELIVERY_COMPLETED_COUNT";
    public static final String MULTIDELIVERY_CANCELLED_BY_ADMIN = "MULTIDELIVERY_CANCELLED_BY_ADMIN";

    /*Added this to control feedback screen to go back or not*/
    public static final String MULTIDELIVERY_FEEDBACK_SCREEN = "MULTIDELIVERY_FEEDBACK_SCREEN";

    /*Chat Message Count Using Conversation ID*/
    public static final String CONVERSATION_BADGE_COUNT = "CONVERSATION_BADGE_COUNT";


    //endregion

    //Call Data
    public static final String TRIP_START_TIME = "trip_start_time";
    public static final String TRIP_ACCEPT_TIME = "trip_accept_time";
    public static final String TRIP_TOTAL_DISTANCE = "trip_total_distance";
    public static final String INCOMING_CALL = "INCOMING_CALL";
    public static final String CALL_TYPE = "call_type";
    public static final String SELECTED_COUNTRY = "selectedCountry";
    public static final String COME_FROM_SIGNUP = "FROM_SIGNUP";
    public static final String ADVANCE_CALL = "advanceCall";
    public static final String NORMAL_CALL = "normalCall";

    /*****************************************************************
     * TRIP STATUS VALUES                         *
     *****************************************************************/
    public static final String ON_TRIP = "on_trip";
    public static final String ON_ARRIVED = "on_arrived_screen";
    public static final String ON_BEGIN_TRIP = "on_begin_trip_screen";
    public static final String ON_END_TRIP = "on_end_trip_screen";
    public static final String TRIP_STATUS = "TRIP_STATUS";

    /*****************************************************************
     * LOCATION VALUES KEYS                                         *
     *****************************************************************/
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String BEARING = "bearing";
    public static final String IS_MOCK_LOCATION = "IS_MOCK_LOCATION";
    public static final String LOCATION_DRIVER = "DRIVER-LOCATION";

    //CHATTING VALUES
    public static final String LAST_MESSAGE_ID = "last_msg_id";
    public static final String CHAT_TYPE_TEXT = "text";
    public static final String CHAT_TYPE_VOICE = "file";
    public static final String CHAT_TYPE_IMAGE = "image";
    public static final String CHAT_CONVERSATION_ID = "refId";
    public static final String CHAT_RECEIVER_ID = "RECEIVER_ID";

    /***********************************************************************************************
     * BROAST CAST RECEIVER TAGS
     **********************************************************************************************/
    //BROADCAST RECEIVER ACTIONS
    public static final String LOCATION_UPDATE_BROADCAST = "location_tracking_broadcast";
    public static final String UNAUTHORIZED_BROADCAST = "UNAUTHORIZED_USER";
    public static final String MULTIDELIVERY_ERROR_BORADCAST = "MULTIDELIVERY_ERROR_BORADCAST";
    public static final String CONNECTION_BROADCAST = "CONNECTION_BROADCAST";
    public static final String LOCATION_NOT_UPDATE_BROADCAST = "location_not_update_broadcast";

    public static final String INACTIVE_PUSH = "INACTIVE-PUSH";
    public static final String INACTIVE_FENCE = "INACTIVE-FENCE";
    public static final String ETA_IN_BG_UPDATED = "ETA_IN_BG_UPDATED";
    public static final String ACTIVE_FENCE = "ACTIVE_FENCE";

    /*JOB ACTIVITY CANCEL JOB LISTENER*/
    public static final String BROADCAST_CANCEL_RIDE = "BROADCAST_CANCEL_RIDE";
    public static final String BROADCAST_CANCEL_BATCH = "BROADCAST_CANCEL_BATCH";
    public static final String BROADCAST_DROP_OFF_UPDATED = "BROADCAST_DROP_OFF_UPDATED";
    public static final String TRIP_DATA_UPDATED = "TRIP_DATA_UPDATED";
    public static final String BROADCAST_BATCH_UPDATED = "BROADCAST_BATCH_UPDATED";
    public static final String BROADCAST_CANCEL_BY_ADMIN = "BROADCAST_CANCEL_BY_ADMIN";
    public static final String BROADCAST_COMPLETE_BY_ADMIN = "BROADCAST_COMPLETE_BY_ADMIN";
    public static final String BROADCAST_MESSAGE_RECEIVE = "BROADCAST_MESSAGE_RECEIVE_DRIVER";
    public static final String ADMIN_MSG = "ADMIN_MSG";
    public static final String ADMIN_MSG_READ = "ADMIN_MSG_READ";
    public static final String JOB_ACTIVITY_FOREGROUND = "JOB_ACTIVITY_FOREGROUND";
    public static final String MULTIDELIVERY_JOB_ACTIVITY_FOREGROUND = "MULTIDELIVERY_JOB_ACTIVITY_FOREGROUND";
    public static final String MULTIDELIVERY_DISTANCE_MATRIX_CALLED_REQUIRED = "MULTIDELIVERY_DISTANCE_MATRIX_CALLED_REQUIRED";
    public static final String DROP_OFF_UPDATE_REQUIRED = "DROP_OFF_UPDATE_REQUIRED";
    public static final String CHAT_ACTIVITY_FOREGROUND = "CHAT_ACTIVITY_FOREGROUND";
    public static final String HOME_ACTIVITY_FOREGROUND = "HOME_ACTIVITY_FOREGROUND";
    public static final String CALLING_ACTIVITY_FOREGROUND = "CALLING_ACTIVITY_FOREGROUND";
    public static final String LATITUDE_LAST_UPDATED = "LATITUDE_LAST_UPDATED";
    public static final String LONGITUDE_LAST_UPDATED = "LONGITUDE_LAST_UPDATED";
    public static final String TIME_LAST_UPDATED = "TIME_LAST_UPDATED";
    public static final String LOCATION_ACCURACY = "LOCATION_ACCURACY";
    public static final String SERVER_TIME_DIFFERENCE = "SERVER_TIME_DIFFERENCE";
    public static final String LOCATION_EMIT_TIME = "LOCATION_EMIT_TIME";
    public static final String IS_OUT_OF_FENCE = "IS_OUT_OF_FENCE";
    public static final String IS_WALLET_AMOUNT_INCREASED = "IS_WALLET_AMOUNT_INCREASED";
    public static final String IS_PROFILE_UPDATED = "IS_PROFILE_UPDATED";
    public static final String IS_GEO_CODER_API_KEY_REQUIRED = "IS_GEO_CODER_API_KEY_REQUIRED";
    public static final String API_KEY_CHECK_TIME_GEO_CODER = "API_KEY_CHECK_TIME_GEO_CODER";

    public static final String LAST_DIRECTIONS_API_CALL_TIME = "LAST_DIRECTIONS_API_CALL_TIME";
    public static final String DISTANCE_COVERED = "DISTANCE_COVERED";
    public static final String VERSION_CHECK_TIME = "VERSION_CHECK_TIME";

    public static final String LATITUDE_PREV_DISTANCE = "LATITUDE_PREV_DISTANCE";
    public static final String LONGITUDE_PREV_DISTANCE = "LONGITUDE_PREV_DISTANCE";
    public static final String TIME_PREV_DISTANCE = "TIME_PREV_DISTANCE";
    public static final String MOCK_LOCATION = "MOCK_LOCATION_BYKEA_PARTNER";
    public static final String IS_STOP_SERVICE_CALLED = "IS_STOP_SERVICE_CALLED";
    public static final String IN_TRIP_LAT_LNG_ARRAY = "IN_TRIP_LAT_LNG_ARRAY";
    public static final String IS_STATS_API_CALL_REQUIRED = "IS_STATS_API_CALL_REQUIRED";
    public static final String STATS_API_CALL_TIME = "STATS_API_CALL_TIME";
    public static final String IS_DIRECTIONS_API_KEY_REQUIRED = "IS_DIRECTIONS_API_KEY_REQUIRED";
    public static final String API_KEY_CHECK_TIME_DIRECTIONS = "API_KEY_CHECK_TIME_DIRECTIONS";

    public static final String SERVICE_CITIES = "SERVICE_CITIES";
    public static final String AVAILABLE_CITIES = "AVAILABLE_CITIES";
    public static final String AVAILABLE_CITIES_API_CALL_TIME = "AVAILABLE_CITIES_API_CALL_TIME";
    public static final String ADID = "GOOGLE_ADID";
    public static final String LAST_ACK_TRIP_ID = "LAST_ACK_TRIP_ID";
    public static final String ONE_SIGNAL_PALYER_ID = "ONE_SIGNAL_PALYER_ID";
    public static final String WALLET_ERROR = "WALLET_ERROR";
    public static final String SETTINGS_VERSION = "SETTINGS_VERSION";
    public static final String CASH_IN_HANDS = "CASH_IN_HANDS";
    public static final String LOCATION_RESPONSE_NOT_RECEIVED_COUNT = "LOCATION_RESPONSE_NOT_RECEIVED_COUNT";
    public static final String DRIVER_OFFLINE_FORCEFULLY = "DRIVER_OFFLINE_FORCEFULLY";
    public static final String CASH_IN_HANDS_RANGE = "CASH_IN_HANDS_RANGE";
    public static final String CASH_IN_HANDS_INDEX = "CASH_IN_HANDS_INDEX";
    public static final String TRIP_DELAY = "TRIP_DELAY";
    public static final String CASH = "CASH";
    public static final String BOOKING_VOICE_NOTE_URL = "BOOKING_VOICE_NOTE_URL";

    /***********************************************************************************************
     * Driver drop off tags
     **********************************************************************************************/

    public static final String API_KEY_CHECK_TIME = "API_KEY_CHECK_TIME";
    public static final String IS_API_KEY_REQUIRED = "IS_API_KEY_REQUIRED";
    public static final String DRIVER_DEST = "DRIVER_DEST";
    public static final String MIX_PANEL_DIST_ID = "MIX_PANEL_DIST_ID";
    public static final String TRACKING_DATA = "TRACKING_DATA";
    public static final String RECENT_PLACES = "RECENT_PLACES_NEW";
    public static final String SAVED_PLACES = "SAVED_PLACES_NEW";
    public static final String IS_SAVED_PLACES_API_CALLED = "IS_SAVED_PLACES_API_CALLED";
    public static final String INACTIVE_CHECK_TIME = "INACTIVE_CHECK_TIME";

    public static final String FRAGMENT_TYPE_NAME = "Type";

    public static final String BASE_URL_LOCAL = "BASE_URL_LOCAL";


    /**
     * Loadboard Pickup and Dropoff Zone data preferences tags
     */
    public static final String LOADBOARD_SELECTED_PICKUP_ZONE = "LOADBOARD_SELECTED_PICKUP_ZONE";
    public static final String LOADBOARD_SELECTED_DROPOFF_ZONE = "LOADBOARD_SELECTED_DROPOFF_ZONE";

    public static final String LAST_PARTNER_TEMPERATURE_SUBMIT = "LAST_PARTNER_TEMPERATURE_SUBMIT";
    public static final String LAST_SELECTED_MSG_POSITION = "LAST_SELECTED_MSG_POSITION";
    public static final String LIST_DELIVERY_ACTIVITY_FOREGROUND = "LIST_DELIVERY_ACTIVITY_FOREGROUND";
}
