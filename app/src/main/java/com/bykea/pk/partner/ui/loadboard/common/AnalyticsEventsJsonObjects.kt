package com.bykea.pk.partner.ui.loadboard.common

import com.bykea.pk.partner.dal.JobRequest
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.utils.Constants
import com.bykea.pk.partner.utils.Utils
import org.json.JSONObject

object AnalyticsEventsJsonObjects {
    fun getEventLoadBoardJson(eventLog: String, jobRequest: JobRequest? = null): JSONObject {
        return JSONObject().apply {
            put("current_location", Utils.getCurrentLocation())
            put("driver_id", AppPreferences.getPilotData().id)
            put("driver_name", AppPreferences.getPilotData().fullName)
            put("signup_city", AppPreferences.getPilotData().city.name)
            put("timestamp", Utils.getIsoDate())

            //  Constants.AnalyticsEvents.ON_LB_SWIPE_UP -> put("bookings_count", listCount)

            when (eventLog) {
                Constants.AnalyticsEvents.ON_LB_REFRESH -> put("is_cash", AppPreferences.getIsCash())

                Constants.AnalyticsEvents.ON_LB_BOOKING_DETAIL -> {
                    put("trip_id", jobRequest?.id)
                    put("trip_no", jobRequest?.booking_no)
                    put("cod_amount", jobRequest?.cod_value)
                    put("dropoff_location", "${jobRequest?.dropoff?.lat.toString()},${jobRequest?.dropoff?.lng.toString()}")
                    put("dropoff_distance", AppPreferences.getEstimatedDistance())
                    put("fare", jobRequest?.fare_est)
                    put("pickup_location", "${jobRequest?.pickup?.lat.toString()},${jobRequest?.pickup?.lng.toString()}")
                    put("pickup_eta", jobRequest?.pickup?.duration)
                    put("trip_id", jobRequest?.trip_id)
                    put("type", jobRequest?.trip_id)
                    put("voice_message", jobRequest?.voice_note.isNullOrEmpty())
                }

                Constants.AnalyticsEvents.ON_LB_BACK_FROM_BOOKING_DETAIL -> {
                    put("trip_id", jobRequest?.trip_id)
                    put("dropoff_location", "${jobRequest?.dropoff?.lat.toString()},${jobRequest?.dropoff?.lng.toString()}")
                }

                Constants.AnalyticsEvents.ON_LB_BOOKING_ACCEPT -> {
                    put("dropoff_location", "${jobRequest?.dropoff?.lat.toString()},${jobRequest?.dropoff?.lng.toString()}")
                    put("pickup_location", "${jobRequest?.pickup?.lat.toString()},${jobRequest?.pickup?.lng.toString()}")
                    put("trip_id", jobRequest?.id)
                    put("trip_no", jobRequest?.booking_no)
                    put("type", jobRequest?.trip_type)
                }

                Constants.AnalyticsEvents.ON_LB_PICKUP_DIRECTION -> {
                    put("trip_id", jobRequest?.id)
                    put("trip_no", jobRequest?.booking_no)
                    put("sender_name", jobRequest?.sender?.name)
                    put("sender_phone", jobRequest?.sender?.phone)
                    put("sender_address", jobRequest?.sender?.address)
                }

                Constants.AnalyticsEvents.ON_LB_DROPOFF_DIRECTION -> {
                    put("trip_id", jobRequest?.id)
                    put("trip_no", jobRequest?.booking_no)
                    put("receiver_name", jobRequest?.receiver?.name)
                    put("receiver_phone", jobRequest?.receiver?.phone)
                    put("receiver_address", jobRequest?.receiver?.address)
                    put("dropoff_location", "${jobRequest?.dropoff?.lat.toString()},${jobRequest?.dropoff?.lng.toString()}")
                    put("dropoff_distance", jobRequest?.dropoff?.distance)
                    put("dropoff_eta", jobRequest?.dropoff?.duration)
                }
            }
        }
    }
}