package com.bykea.pk.partner.utils;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class NetworkChangeListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        if (Connectivity.isConnectedFast(context)) {
            if (null != progressDialog)
                progressDialog.dismiss();
        } else {
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle("Internet Connectivity");
            progressDialog.setMessage("Checking for internet connection..");
            progressDialog.show();
        }

    }
}
