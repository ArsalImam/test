package com.bykea.pk.partner.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.utils.Utils;

public class BootReceiver  extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Utils.redLog("Boot Receiver", "TRUE");
        ActivityStackManager.getInstance().startLocationService(context);
    }
}