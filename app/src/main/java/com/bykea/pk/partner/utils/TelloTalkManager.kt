package com.bykea.pk.partner.utils

import android.app.Activity
import com.bykea.pk.partner.R
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.tilismtech.tellotalksdk.managers.TelloApiClient

class TelloTalkManager {
    private var telloApiClient: TelloApiClient

    constructor() {
        val builder: TelloApiClient.Builder = TelloApiClient.Builder()
                .accessKey(Constants.ACCESS_KEY_TELLO_TALK)
                .projectToken(Constants.PROJECT_TOKEN_TELLO_TALK)
                .notificationIcon(R.drawable.ic_stat_onesignal_default)
        telloApiClient = builder.build()
    }

    fun setupFcm() {
        telloApiClient.let {
            if (it.isLoggedIn) it.updateFcmToken(AppPreferences.getRegId())
        }
    }

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

    fun openCorporateChat(activity: Activity?) {
        telloApiClient.openCorporateChat(activity)
    }

    fun onMessageReceived() {
        if (telloApiClient.isLoggedIn)
            telloApiClient.onMessageNotificationReceived()
    }

    companion object {
        private var INSTANCE: TelloTalkManager? = null

        @JvmStatic
        fun instance(): TelloTalkManager = INSTANCE ?: synchronized(TelloTalkManager::class.java) {
            INSTANCE ?: TelloTalkManager()
        }
    }
}