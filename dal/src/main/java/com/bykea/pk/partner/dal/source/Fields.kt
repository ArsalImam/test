package com.bykea.pk.partner.dal.source


object Fields {
    object OtpSend {
        const val ID = "_id"
        const val TOKEN_ID = "token_id"
        const val PHONE_NUMBER = "phone"
        const val TYPE = "type"
    }

    object FareEstimation {
        const val ID = "_id"
        const val TOKEN_ID = "token_id"
        const val START_LAT = "s_lat"
        const val START_LNG = "s_lng"
        const val END_LAT = "e_lat"
        const val END_LNG = "e_lng"
        const val DISTANCE = "distance"
        const val TIME = "time"
        const val RIDE_TYPE = "v_type"
        const val TYPE = "type"
    }

    object Login {
        const val PHONE_NUMBER = "phone"
        //public static final String PIN_CODE = "pincode";
        const val OTP_CODE = "code"
        const val DEVICE_TYPE = "devicetype"
        const val USER_STATUS = "user_status"
        const val REG_ID = "regid"
        const val LAT = "lat"
        const val LNG = "lng"
        const val IMEI_NUMBER = "imei"
        const val APP_VERSION = "app_version"
        const val ADID = "advertising_id"
        const val ONE_SIGNAL_PLAYER_ID = "one_signal_p_id"
    }

}
