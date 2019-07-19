package com.bykea.pk.partner.ui.loadboard.common

import com.bykea.pk.partner.dal.JobRequest
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.utils.Constants
import com.bykea.pk.partner.utils.Utils
import org.json.JSONObject

/**
 * Firebase Analytics Events = JSON Objects Creation
 */
object AnalyticsEventsJsonObjects {
    /**
     * @param eventLog : Firebase Event Name
     * @param jobRequest : Loadboard Booking Detail Object
     */
    fun getEventLoadBoardJson(eventLog: String, jobRequest: JobRequest? = null, bookingCount: Int? = null): JSONObject {
        return JSONObject().apply {
            put("current_location", Utils.getCurrentLocation())
            put("driver_id", AppPreferences.getPilotData().id)
            put("driver_name", AppPreferences.getPilotData().fullName)
            put("signup_city", AppPreferences.getPilotData().city.name)
            put("timestamp", Utils.getIsoDate())

            when (eventLog) {
                Constants.AnalyticsEvents.ON_LB_REFRESH -> put("is_cash", AppPreferences.getIsCash())

                Constants.AnalyticsEvents.ON_LB_SWIPE_UP,
                Constants.AnalyticsEvents.ON_LB_BACK_SWIPE_DOWN -> put("bookings_count", bookingCount)

                Constants.AnalyticsEvents.ON_LB_BOOKING_DETAIL -> {
                    put("cod_amount", jobRequest?.cod_value)
                    put("dropoff_distance", (jobRequest?.dropoff?.distance)?.div(1000))
                    put("delivery_type", jobRequest?.trip_type)
                    put("dropoff_lat", jobRequest?.dropoff?.lat)
                    put("dropoff_lng", jobRequest?.dropoff?.lng)
                    put("fare", jobRequest?.fare_est)
                    put("trip_id", jobRequest?.trip_id)
                    put("trip_no", jobRequest?.booking_no)
                    put("pickup_eta", jobRequest?.pickup?.duration?.div(60))
                    put("pickup_lat", jobRequest?.pickup?.lat)
                    put("pickup_lng", jobRequest?.pickup?.lng)
                    put("voice_message", jobRequest?.voice_note.isNullOrEmpty())
                }

                Constants.AnalyticsEvents.ON_LB_BACK_FROM_BOOKING_DETAIL -> {
                    put("delivery_type", jobRequest?.trip_type)
                    put("dropoff_lat", jobRequest?.dropoff?.lat)
                    put("dropoff_lng", jobRequest?.dropoff?.lng)
                    put("trip_id", jobRequest?.trip_id)
                    put("trip_no", jobRequest?.booking_no)
                }

                Constants.AnalyticsEvents.ON_LB_BOOKING_ACCEPT -> {
                    put("delivery_type", jobRequest?.trip_type)
                    put("dropoff_lat", jobRequest?.dropoff?.lat)
                    put("dropoff_lng", jobRequest?.dropoff?.lng)
                    put("pickup_lat", jobRequest?.pickup?.lat)
                    put("pickup_lng", jobRequest?.pickup?.lng)
                    put("trip_id", jobRequest?.id)
                    put("trip_no", jobRequest?.booking_no)
                }

                Constants.AnalyticsEvents.ON_LB_PICKUP_DIRECTION -> {
                    put("delivery_type", jobRequest?.trip_type)
                    put("sender_name", jobRequest?.sender?.name)
                    put("sender_phone", jobRequest?.sender?.phone)
                    put("sender_address", jobRequest?.sender?.address)
                    put("trip_id", jobRequest?.id)
                    put("trip_no", jobRequest?.booking_no)
                }

                Constants.AnalyticsEvents.ON_LB_DROPOFF_DIRECTION -> {
                    put("delivery_type", jobRequest?.trip_type)
                    put("dropoff_eta", jobRequest?.dropoff?.duration)
                    put("dropoff_distance", (jobRequest?.dropoff?.distance)?.div(1000))
                    put("dropoff_lat", jobRequest?.dropoff?.lat)
                    put("dropoff_lng", jobRequest?.dropoff?.lng)
                    put("receiver_name", jobRequest?.receiver?.name)
                    put("receiver_phone", jobRequest?.receiver?.phone)
                    put("receiver_address", jobRequest?.receiver?.address)
                    put("trip_id", jobRequest?.id)
                    put("trip_no", jobRequest?.booking_no)
                }
            }
        }
    }
}