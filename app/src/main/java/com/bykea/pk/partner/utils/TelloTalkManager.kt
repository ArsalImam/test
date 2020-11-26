package com.bykea.pk.partner.utils

import android.app.Activity
import com.bykea.pk.partner.R
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.tilismtech.tellotalksdk.entities.DepartmentConversations
import com.tilismtech.tellotalksdk.managers.TelloApiClient
import org.apache.commons.lang3.StringUtils
import java.util.HashMap

/**
 * this is the utitlity manager class for tellotalk
 *
 * [author] ArsalImam
 */
class TelloTalkManager {
    /**
     * tello client instance
     */
    private var telloApiClient: TelloApiClient? = null

    /**
     * constructor to initialize tello sdk object
     */
    constructor() {
    }

    /**
     * this will configure tello fcm client
     */
    fun setupFcm() {
        if (telloApiClient == null) {
            build()
        }
        telloApiClient?.let {
            val registrationId = AppPreferences.getRegId()
            if (StringUtils.isNotEmpty(registrationId)) it.updateFcmToken(registrationId)
        }
    }

    /**
     * this method will logout user from device and clear's sdk data
     */
    fun logout() {
        if (telloApiClient == null) {
            build()
        }
        telloApiClient?.let { client ->
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
        if (telloApiClient == null) {
            build()
        }
        val user = AppPreferences.getPilotData()
        user.id?.let {
            telloApiClient?.registerUser(user.id, user.fullName, user.phoneNo, "Partner") { success ->
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
    fun openCorporateChat(activity: Activity?, template: String? = null, departmentConversations: DepartmentConversations? = null) {
        if (telloApiClient == null) {
            build()
        }
        telloApiClient?.openCorporateChat(activity, template, StringUtils.EMPTY, departmentConversations)
    }

    fun build() {
        if (AppPreferences.getDriverSettings() == null) return

        val builder: TelloApiClient.Builder = TelloApiClient.Builder()
                .accessKey(AppPreferences.getDriverSettings().data?.telloTalkAccessKey)
                .projectToken(AppPreferences.getDriverSettings().data?.telloTalkProjectToken)
                .notificationIcon(R.drawable.ic_stat_onesignal_default)

        telloApiClient = builder.build()
    }

    /**
     * this will awake tello's client on fcm received for fcm
     */
    fun onMessageReceived(data: Map<String, String>) {
        try {
            if (telloApiClient == null) {
                build()
            }
            telloApiClient?.onMessageNotificationReceived(data as HashMap<String, String>?)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getDepartments() = telloApiClient?.department

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
            INSTANCE ?: TelloTalkManager().also {
                INSTANCE = it
            }
        }
    }
}