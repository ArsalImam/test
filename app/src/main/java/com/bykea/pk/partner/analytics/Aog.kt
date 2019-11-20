package com.bykea.pk.partner.analytics

import com.bykea.pk.partner.DriverApp
import com.bykea.pk.partner.dal.source.socket.payload.JobCall
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.utils.Constants
import com.bykea.pk.partner.utils.Utils
import org.json.JSONException
import org.json.JSONObject

/**
 * Analytics logging common class to post to different repos like mix-panel and firebase
 *
 * @author Yousuf Sohail
 */
object Aog {

    /**
     * Log Job call data to mix panel and firebase
     *
     * @param data Job call data
     * @param isOnAccept is post accepted
     */
    fun onJobCallAndJobAccept(data: JobCall, isOnAccept: Boolean, secondsEclipsed: Int) {
        try {
            val json = JSONObject()
            json.put("PassengerID", data.customer_id)
            json.put("DriverID", AppPreferences.getPilotData().id)
            json.put("TripID", data.trip_id)
            json.put("TripNo", data.booking_no)
            json.put("PickUpLocation", "${data.pickup.lat},${data.pickup.lng}")
            json.put("timestamp", Utils.getIsoDate())
            if (data.dropoff != null) {
                json.put("DropOffLocation", "${data.dropoff!!.lat},${data.dropoff!!.lng}")
            }
            json.put("ETA", "" + data.pickup.duration)
            json.put("EstimatedDistance", AppPreferences.getEstimatedDistance())
            json.put("CurrentLocation", Utils.getCurrentLocation())
            json.put("DriverName", AppPreferences.getPilotData().fullName)
            json.put("SignUpCity", AppPreferences.getPilotData().city.name)

            if (isOnAccept) {
                json.put("AcceptSeconds", secondsEclipsed)
                Utils.logEvent(DriverApp.getContext(), data.customer_id, Constants.AnalyticsEvents.ON_ACCEPT, json)
            } else {
                Utils.logEvent(DriverApp.getContext(), data.customer_id, Constants.AnalyticsEvents.ON_RECEIVE_NEW_JOB, json)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}