package com.bykea.pk.partner.services;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;

public class OneSignalNotificationExtenderService extends NotificationExtenderService {

    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {

        // Return true to stop the notification from displaying.
        return receivedResult.isAppInFocus;
    }
}