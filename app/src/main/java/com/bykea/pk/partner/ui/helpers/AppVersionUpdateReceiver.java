package com.bykea.pk.partner.ui.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bykea.pk.partner.models.response.UpdateAppVersionResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.utils.Utils;

/**
 * Broadcast Receiver to listen for App Update which listen to
 * {@link Intent#ACTION_MY_PACKAGE_REPLACED}
 */
public class AppVersionUpdateReceiver extends BroadcastReceiver {

    private final String TAG = AppVersionUpdateReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Utils.redLog(TAG, Intent.ACTION_MY_PACKAGE_REPLACED);
        if (Intent.ACTION_MY_PACKAGE_REPLACED.equalsIgnoreCase(intent.getAction())
                && AppPreferences.isLoggedIn()
                && !Utils.getVersion().equalsIgnoreCase(AppPreferences.getAppVersion())) {
            Utils.redLog(TAG, "Calling API for App version update");
            new UserRepository().updateAppVersion(new UserDataHandler() {
                @Override
                public void onUpdateAppVersionResponse(UpdateAppVersionResponse response) {
                    if (response.isSuccess()) {
                        AppPreferences.setAppVersion(Utils.getVersion());
                    }
                }
            });
        }
    }

}
