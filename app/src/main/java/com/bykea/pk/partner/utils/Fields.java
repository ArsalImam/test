package com.bykea.pk.partner.utils;


public class Fields {

    public static final String id = "_id";
    public static final String tokenId = "token_id";
    public static final String USER_TYPE = "user_type";

    public static class AcceptCall{
        public static final String PHONE_NUMBER = "phone";

    }

    public static class Login {
        public static final String PHONE_NUMBER = "phone";
        public static final String PIN_CODE = "pincode";
        public static final String DEVICE_TYPE = "devicetype";
        public static final String USER_STATUS = "user_status";
        public static final String REG_ID = "regid";
        public static final String LAT = "lat";
        public static final String LNG = "lng";
        public static final String IMEI_NUMBER = "imei";
        public static final String APP_VERSION = "app_version";
        public static final String ADID = "advertising_id";
        public static final String ONE_SIGNAL_PLAYER_ID = "one_signal_p_id";
    }

    public static class NumberVerification {
        public static final String PHONE_NUMBER = "phone";
        public static final String USER_TYPE = "user_type";
    }

    public static class OtpVerification {
        public static final String PHONE_NUMBER = "phone";
        public static final String PHONE_CODE = "phoneCode";
        public static final String USER_TYPE = "user_type";
    }

    public static class Register {
        public static final String USER_STATUS = "user_status";
        public static final String FULL_NAME = "full_name";
        public static final String PHONE_NUMBER = "phone";
        public static final String PIN_CODE = "pin_code";
        public static final String AGREE_CHECK = "i_agree";
        public static final String CITY = "city";
        public static final String ADDRESS = "address";
        public static final String EMAIL = "email";
        public static final String PLATE_NO = "plate_no";
        public static final String LICENSE_NO = "driver_license_number";
        public static final String LICENSE_NO_IMAGE = "dirver_license_image_id";
        public static final String PILOT_IMAGE = "img_id";
        public static final String EXPIRY_DATE = "license_expire";
        public static final String VEHICLE_TYPE = "vehicle_type";
        public static final String DEVICE_TYPE = "device_type";
        public static final String CNIC = "cnic";
        public static final String REG_ID = "reg_id";
        public static final String REGISTER_LAT = "current_lat";
        public static final String REGISTER_LNG = "current_lng";

    }

    public static class Logout {
        public static final String id = "_id";
        public static final String tokenId = "token_id";
    }

    public static class Forgot {
        public static final String PHONE_NUMBER = "phone";
    }

    public static class StatusUpdate {
        public static final String id = "_id";
        public static final String tokenId = "token_id";
        public static final String isAvailable = "is_available";
    }


}
