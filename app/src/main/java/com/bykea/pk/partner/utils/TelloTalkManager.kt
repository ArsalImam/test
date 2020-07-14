package com.bykea.pk.partner.utils

import android.app.Activity
import com.bykea.pk.partner.R
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.tilismtech.tellotalksdk.managers.TelloApiClient

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
                .accessKey(Constants.ACCESS_KEY_TELLO_TALK)
                .projectToken(Constants.PROJECT_TOKEN_TELLO_TALK)
                .notificationIcon(R.drawable.ic_stat_onesignal_default)
        telloApiClient = builder.build()
    }

    /**
     * this will configure tello fcm client
     */
    fun setupFcm() {
        telloApiClient.let {
            if (it.isLoggedIn) it.updateFcmToken(AppPreferences.getRegId())
        }
    }

    /**
     * this will perform login to tello talk instance
     *
     * [callback] this will return controll from tello to our app
     */
    fun performLogin(callback: (isLoggedIn: Boolean) -> Unit) {
        telloApiClient.let {
            it.login { isLoggedInUser ->
                if (isLoggedInUser) callback(true)
                else {
                    val user = AppPreferences.getPilotData()
                    it.registerUser(user.id, user.fullName, user.phoneNo) { success ->
                        callback(success)
                    }
                }
            }
        }
    }

    /**
     * this method will be used to open co-operate chat for CSR
     *
     * [activity] context of the activity from which it will open
     */
    fun openCorporateChat(activity: Activity?) {
        telloApiClient.openCorporateChat(activity)
    }

    /**
     * this will awake tello's client on fcm received for fcm
     */
    fun onMessageReceived() {
        if (telloApiClient.isLoggedIn)
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