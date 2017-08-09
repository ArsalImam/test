package com.bykea.pk.partner.ui.helpers;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.bykea.pk.partner.DriverApp;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import org.apache.commons.lang3.StringUtils;


public class AdvertisingIdTask extends AsyncTask<Void, Void, String> {
    @Override
    protected String doInBackground(Void... params) {
        String adId = StringUtils.EMPTY;
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(DriverApp.getContext()) == ConnectionResult.SUCCESS) {
            for (int i = 0; i < 5; i++) {
                adId = getAdvStringId();
                if (StringUtils.isNotBlank(adId)) {
                    break;
                }
            }
        }
        return adId;
    }

    @Nullable
    private String getAdvStringId() {
        AdvertisingIdClient.Info idInfo = null;
        try {
            idInfo = AdvertisingIdClient.getAdvertisingIdInfo(DriverApp.getContext());
        } catch (GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String advertId = null;
        try {
            advertId = idInfo != null ? idInfo.getId() : StringUtils.EMPTY;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return advertId;
    }

    @Override
    protected void onPostExecute(String advertId) {
        if (StringUtils.isNotBlank(advertId)) {
            AppPreferences.setADID(DriverApp.getContext(), advertId);
        }
    }
}
