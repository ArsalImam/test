package com.bykea.pk.partner.utils;

import com.bykea.pk.partner.BuildConfig;

public class ApiTags {

    //live test
    //ALi
//    private static final String BASE_SERVER_URL_DEBUG = "http://172.16.0.58:3001";  //local

    //Shehryar server
      //private static final String BASE_SERVER_URL_DEBUG = "http://192.168.0.122:3000";

    //staging
     private static final String BASE_SERVER_URL_DEBUG = "https://staging.bykea.net:3000";

      //private static final String BASE_SERVER_URL_DEBUG = "http://192.168.0.148:3055";
      //private static final String BASE_SERVER_URL_DEBUG = "https://staging.bykea.net:3001";
//    private static final String BASE_SERVER_URL_DEBUG = "https://staging.bykea.net:3002";
    //live test
//    private static final String BASE_SERVER_URL_DEBUG = "https://secure.bykea.net:3001";
//    private static final String BASE_SERVER_URL_DEBUG = "https://secure.bykea.net:3000";
//    private static final String BASE_SERVER_URL_DEBUG = "https://secure.bykea.net:3001";


    private static final String BASE_SERVER_URL_LIVE = "https://secure.bykea.net:3000";

    /*
    * change url only for debug builds, never update BASE_SERVER_URL_LIVE
    * */
    public static final String BASE_SERVER_URL = BuildConfig.DEBUG ?
            BASE_SERVER_URL_DEBUG : BASE_SERVER_URL_LIVE;

    //Bykea 2 (For Heat Map)
    public static final String BASE_SERVER_URL_2 = "http://34.210.28.53:8081";

    //    public static final String BASE_SERVER_URL_SIGN_UP = "http://54.189.207.7:5050";
    public static final String BASE_SERVER_URL_SIGN_UP_X_API = "2151d3fd63118e4f7bcef56e8b488b079cbc8052afdb45252febdaa2b9868416";
    public static final String SIGN_UP_SETTINGS = "/online_reg/setting";
    public static final String SIGN_UP_ADD_NUMBER = "/online_reg/add";
    public static final String SIGN_UP_UPLOAD_DOCUMENT = "/online_reg/image";
    public static final String SIGN_UP_COMPLETE = "/online_reg/complete";
    public static final String SIGN_UP_OPTIONAL_DATA = "/online_reg/optional";
    public static final String SIGN_UP_BIOMETRIC_VERIFICATION = "/online_reg/biometric";


    public static final String GOOGLE_API_BASE_URL = "https://maps.googleapis.com/";
    public static final String PLACES_GEOCODER_EXT_URL = "maps/api/geocode/json";
    public static final String EXTENDED_URL_GOOGLE_PLACE_AUTOCOMPLETE_API = "maps/api/place/autocomplete/json";
    public static final String EXTENDED_URL_GOOGLE_PLACE_DETAILS_API = "maps/api/place/details/json";
    public static final String HEAT_MAP_2 = "/getdata/HOUR/CITY_NAME";
    public static final String HEAT_MAP_2_X_API = "6E46E61CB1458A9F32C02CE5F056A557CC801DC31A43ABF39F7BCA9ED6FFAFD2";

    public static final String USER_LOGIN_API = "/api/v1/driver/login";
    public static final String PHONE_NUMBER_VERIFICATION_API = "/api/v1/users/sendPhonecode";
    public static final String CODE_VERIFICATION_API = "/api/v1/users/verfiyPincode";
    public static final String FORGOT_PASSWORD_API = "/api/v1/driver/forgotPassword";
    public static final String REGISTER_USER_API = "/api/v1/driver/register";
    public static final String CHECK_RUNNING_TRIP = "/api/v1/getdriverrunningtrip";
    public static final String UPDATE_PROFILE_API = "/api/v1/driver/updateDriverProfile";
    public static final String GET_PROFILE_API = "/api/v1/driver/getProfile";
    public static final String GET_WALLET_LIST = "/api/v1/users/getWallets";
    public static final String GET_BANK_ACCOUNT_LIST = "/api/v1/driver/bankAccounts";
    public static final String GET_BANK_ACCOUNT_DETAILS = "/api/v1/driver/bankFranchises";
    public static final String GET_CONTACTS_NUMBERS = "/api/v1/contanctUs";
    public static final String GET_SERVICE_TYPE_API = "/api/v1/categories";
    public static final String GET_HISTORY_LIST = "/api/v1/users/getTripHistory";
    public static final String GET_MISSED_TRIPS_HISTORY_LIST = "/api/v1/users/getMissedCallHistory";
    public static final String GET_SETTINGS = "/api/v1/common/settings";
    public static final String CHANGE_PIN = "/api/v1/common/changePin";
    public static final String GET_CITIES = "/api/v1/admin/cities";
    public static final String SET_DRIVER_DROP_OFF = "/api/v1/driver/setDropoff";


    public static final String LOGOUT_API = "/api/v1/driver/logout";
    public static final String UPLOAD_DRIVER_DOCUMENTS_API = "/api/v1/users/fileupload";
    public static final String UPLOAD_AUDIO_FILE_API = "/api/v1/chat/fileupload";
    public static final String POST_PROBLEM = "api/v1/driver/sendProblemEmail";
    public static final String UPDATE_REG_ID = "/api/v1/common/UpdateRegId";


    public static final String ADD_SAVED_PLACE = "/api/v1/users/savePlace";
    public static final String GET_SAVED_PLACES = "/api/v1/users/userPlaces";
    public static final String DELETE_SAVED_PLACE = "/api/v1/users/deletePlace";
    public static final String UPDATE_SAVED_PLACE = "/api/v1/users/updatePlace";

    public static final String GET_AREAS = "/api/v1/users/getZones";
    public static final String GET_ADDRESSES = "/api/v1/users/getZoneAreas";
    public static final String TOP_UP_PASSENGER_WALLET = "/api/v1/driver/topupToPassenger";

    //HOME SCREEN DRIVER STATS

    public static final String GET_SHAHKAR = "/api/v1/driver/driverShahkar?";
    public static final String GET_BONUS_CHART = "/api/v1/driver/driverBonusChart?";
    public static final String GET_DRIVER_PERFORMANCE = "/api/v1/driver/performance?";

    //LOAD BOARD
    public static final String GET_LOAD_BOARD = "/api/v1/driver/load-board?";


    /******************************************************************************
     * SOCKET METHODS STRINGS                                                     *
     ******************************************************************************/
    public static final String SOCKET_GET_HEATMAP_DATA = "draw-heatmap";
    public static final String SOCKET_UPDATE_DRIVER_LOC = "update-lat-lng";
    public static final String SOCKET_UPDATE_DRIVER_STATUS = "driver-update-status";
    public static final String SOCKET_FREE_PILOT = "free-when-call-end";
    public static final String SOCKET_DRIVER_FEEDBACK = "trip-driverFeedback";
    public static final String SOCKET_PASSENGER_CALL = "trip-notification";
    public static final String SOCKET_CANCEL_RIDE_DRIVER = "trip-driverCancelTrip";
    public static final String SOCKET_ACCEPT_CALL = "accept-call";
    public static final String ACK_CALL = "Send-Acknowledge";
    public static final String SOCKET_REJECT_CALL = "trip-driverRejectCall";
    public static final String SOCKET_ARRIVED = "i-am-arrived";
    public static final String SOCKET_BEGIN_TRIP = "start-trip";
    public static final String SOCKET_END_TRIP = "finish-trip";
    /*CHAT APIS*/
    public static final String SOCKET_SEND_CHAT_MESSAGE = "send-message";
    public static final String SOCKET_GET_DRIVER_STATS = "driver:trip-status";
    public static final String SOCKET_GET_CONVERSATION = "get-conversation-info";
    public static final String SOCKET_GET_CONVERSATION_ID = "get-my-conversation";
    public static final String SOCKET_RECEIVE_CHAT_MESSAGE = "chat-receiver";
    public static final String SOCKET_UPDATE_STATUS = "update-driver-status";
    public static final String UPDATE_DROP_OFF = "trip-endaddress";
    public static final String PLACES_DISTANCEMATRIX_EXT_URL = "maps/api/distancematrix/json";
}
