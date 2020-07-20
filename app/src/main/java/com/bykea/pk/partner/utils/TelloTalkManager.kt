package com.bykea.pk.partner.utils

import android.app.Activity
import com.bykea.pk.partner.BuildConfig
import com.bykea.pk.partner.R
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.tilismtech.tellotalksdk.managers.TelloApiClient
import org.apache.commons.lang3.StringUtils

/**
 * this is the utitlity manager class for tellotalk
 *
 * [author] ArsalImam
 */
class TelloTalkManager {
    /**
     * tello client instance
     */
    private var telloApiClient: TelloApiClient

    /**
     * constructor to initialize tello sdk object
     */
    constructor() {
        val builder: TelloApiClient.Builder = TelloApiClient.Builder()
                .accessKey(BuildConfig.ACCESS_KEY_TELLO_TALK)
                .projectToken(BuildConfig.PROJECT_TOKEN_TELLO_TALK)
                .notificationIcon(R.drawable.ic_stat_onesignal_default)
        telloApiClient = builder.build()
    }

    /**
     * this will configure tello fcm client
     */
    fun setupFcm() {
        telloApiClient.let {
            val registrationId = AppPreferences.getRegId()
            if (StringUtils.isNotEmpty(registrationId)) it.updateFcmToken(registrationId)
        }
    }

    /**
     * this method will logout user from device and clear's sdk data
     */
    fun logout() {
        telloApiClient.let { client ->
            client.logOff {
                if (it) {
                    client.ClearUserData {
                        return@ClearUserData
                    }
                }
                return@logOff
            }
        }
    }

    /**
     * this will perform login to tello talk instance
     *
     * [callback] this will return controll from tello to our app
     */
    fun performLogin(callback: (isLoggedIn: Boolean) -> Unit) {
        val user = AppPreferences.getPilotData()
        user.id?.let {
            telloApiClient.registerUser(user.id, user.fullName, user.phoneNo) { success ->
                callback(success)
            }
        } ?: kotlin.run {
            callback(false)
        }
    }

    /**
     * this method will be used to open co-operate chat for CSR
     *
     * [activity] context of the activity from which it will open
     */
    fun openCorporateChat(activity: Activity?, template: String? = null) {
        telloApiClient.openCorporateChat(activity, template)
    }

    /**
     * this will awake tello's client on fcm received for fcm
     */
    fun onMessageReceived() {
        telloApiClient.onMessageNotificationReceived()
    }

    companion object {
        /**
         * singleton instance of this class
         */
        private var INSTANCE: TelloTalkManager? = null

        /**
         * synchronized method to generate singleton instance of this class
         */
        @JvmStatic
        fun instance(): TelloTalkManager = INSTANCE ?: synchronized(TelloTalkManager::class.java) {
            INSTANCE ?: TelloTalkManager()
        }
    }
}