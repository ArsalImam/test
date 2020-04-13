package com.bykea.pk.partner.dal.util

const val LATITUDE = "latitude"
const val LONGITUDE = "longitude"
const val SEPERATOR = "/"
val BUILD_VARIANT_LOCAL_FLAVOR = "local"

const val DRIVER_ID = "DRIVER_ID"
const val ACCESS_TOKEN = "ACCESS_TOKEN"
const val EMPTY_STRING = ""
const val SERVICE_CODE_SEND = 21

val OTP_SMS = "sms"
val OTP_CALL = "call"

val OFFLINE_RIDE = "Offline Ride"
val INTERNAL_SERVER_ERROR = 500
val ERROR_PLEASE_TRY_AGAIN = "Something went wrong. Please try again later."

val SUB_CODE_1051 = 1051
val SUB_CODE_1052 = 1052
val SUB_CODE_1053 = 1053
val SUB_CODE_1054 = 1054
val SUB_CODE_1055 = 1055
val SUB_CODE_1028 = 1028
val SUB_CODE_1009 = 1009
val SUB_CODE_1019 = 1019

val SUB_CODE_1052_MSG = "Invalid phone number"
val SUB_CODE_1054_MSG = "Driver has an active trip"
val SUB_CODE_1055_MSG = "Customer has an active trip"

val USER_TYPE_DRIVER = "d"
val MESSAGE_TYPE = "Reason"
val LANG_TYPE = "ur"

val COMPLAIN_WRONGE_FARE_CALCULATION = "139"

val BOOKING_CURRENT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
val BOOKING_REQUIRED_DATE_FORMAT = "HH:mm:ss MM-dd-yyyy"
val BOOKING_LIST_REQUIRED_DATE_FORMAT = "dd MMM, hh:mm a"
val BOOKING_ID_TO_REPLACE = ":id"

val LABEL_NOT_AVAILABLE = "N/A"
val DIGIT_ZERO = 0


object RequestParams {
    const val STATE: String = "state"
    const val TYPE: String = "type"
}

object AvailableTripStatus {
    val STATUS_COMPLETED: String = "completed"
    val STATUS_MISSED: String = "missed"
    val STATUS_CANCELLED: String = "cancelled"
    val STATUS_FEEDBACK: String = "feedback"
    val STATUS_FINISH: String = "finished"
}

object CancelByStatus {
    val CANCEL_BY_ADMIN: String = "cancel_by_admin"
    val CANCEL_BY_PARTNER: String = "cancel_by_partner"
    val CANCEL_BY_CUSTOMER: String = "cancel_by_customer"
}

object RolesByName {
    val CANCEL_BY_ADMIN: String = "Admin"
    val CANCEL_BY_PARTNER: String = "Partner"
    val CANCEL_BY_CUSTOMER: String = "Customer"
}
