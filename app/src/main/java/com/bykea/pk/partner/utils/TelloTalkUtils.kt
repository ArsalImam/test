package com.bykea.pk.partner.utils

class TelloTalkUtils {
    fun initialize() {
        val builder: TelloApiClient.Builder = TelloApiClient.Builder()
                .accessKey("accessKey")
                .projectToken("projectToken")
                .notificationIcon("Drawable Resource for notification Small Icon")
        telloApiClient = builder.build()
    }
}