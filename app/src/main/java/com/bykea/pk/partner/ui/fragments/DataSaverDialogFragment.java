package com.bykea.pk.partner.ui.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.bykea.pk.partner.BuildConfig;
import com.bykea.pk.partner.R;

/**
 * A Dialog Fragment to be shown when user has Background data restricted in phone settings. This tells
 * user to go to Android settings and remove this restriction.
 *
 * @author Yousuf Sohail
 */
public class DataSaverDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setTitle(R.string.dialog_title_bg_data_off)
                .setView(R.layout.dialog_error_background_data_restricted)
                .setPositiveButton(R.string.go_to_settings, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));

                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
