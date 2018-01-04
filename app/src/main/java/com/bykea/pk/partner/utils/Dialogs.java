package com.bykea.pk.partner.utils;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.IntegerCallBack;
import com.bykea.pk.partner.ui.helpers.StringCallBack;
import com.bykea.pk.partner.widgets.AutoFitFontTextView;
import com.bykea.pk.partner.widgets.FontButton;
import com.bykea.pk.partner.widgets.FontTextView;
import com.google.android.exoplayer.BuildConfig;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public enum Dialogs {
    INSTANCE;

    private Dialog mDialog;
    private Dialog mAdminNotifiationDialog;

    public void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public void showTempToast(Context context, String message) {
        if (BuildConfig.DEBUG) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }

    public void dismissDialog() {
        try {
            if (null != mDialog && isShowing()) {
                mDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismissAdminNotiDialog() {
        try {
            if (null != mAdminNotifiationDialog && mAdminNotifiationDialog.isShowing()) {
                mAdminNotifiationDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDialog() {
        try {
            if (null != mDialog) {
                mDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAdminNotiDialog() {
        try {
            if (null != mAdminNotifiationDialog) {
                mAdminNotifiationDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isShowing() {
        return ((null != mDialog) && mDialog.isShowing());
    }

    public void showLoader(Context context) {
        if (null != mDialog && mDialog.isShowing()) return;
        mDialog = new Dialog(context, R.style.actionSheetTheme);
        mDialog.setContentView(R.layout.loading);
        mDialog.setCancelable(false);
        showDialog();
    }

    public void showError(Context context, View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message,
                Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.color_error));

        snackbar.show();
    }

    public void showSuccessMessage(Context context, View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message,
                Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.color_success));

        snackbar.show();
    }

    public void showWarningMessage(Context context, View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message,
                Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.color_warning));

        snackbar.show();
    }

    public void showConfirmationDialog(Context context, View.OnClickListener positive,
                                       View.OnClickListener negative,
                                       String message) {
        dismissDialog();
        mDialog = new Dialog(context, R.style.actionSheetTheme);
        mDialog.setContentView(R.layout.dialog_logout);

        mDialog.findViewById(R.id.positive).setOnClickListener(positive);
        mDialog.findViewById(R.id.negative).setOnClickListener(negative);
        ((FontTextView) mDialog.findViewById(R.id.message)).setText(message);

        showDialog();
    }

    public void showAlertDialog(Context context, View.OnClickListener positive,
                                View.OnClickListener negative, String title, String message) {
        if (null == context) return;
        dismissDialog();
        mDialog = new Dialog(context, R.style.actionSheetTheme);
        mDialog.setContentView(R.layout.dialog_alert);
        if (null == negative)
            mDialog.setCancelable(true);
        else
            mDialog.setCancelable(false);

        if (negative == null) {
            mDialog.findViewById(R.id.negativeBtn).setVisibility(View.GONE);
        } else {
            mDialog.findViewById(R.id.negativeBtn).setOnClickListener(negative);
        }
        mDialog.findViewById(R.id.positiveBtn).setOnClickListener(positive);
        ((FontTextView) mDialog.findViewById(R.id.messageTv)).setText(message);
        ((FontTextView) mDialog.findViewById(R.id.titleTv)).setText(title);

        showDialog();
    }

    public void showRideStatusDialog(Context context, View.OnClickListener positive,
                                     View.OnClickListener negative, String title) {
        if (null == context) return;
        dismissDialog();
        mDialog = new Dialog(context, R.style.actionSheetTheme);
        mDialog.setContentView(R.layout.dialog_ride_status);
        mDialog.setCancelable(false);
        mDialog.findViewById(R.id.negativeBtn).setOnClickListener(negative);
        mDialog.findViewById(R.id.positiveBtn).setOnClickListener(positive);
        ((FontTextView) mDialog.findViewById(R.id.titleTv)).setText(title);
        showDialog();
    }

    public void showAlertDialogNotSingleton(Context context, final StringCallBack positive,
                                            View.OnClickListener negative, String title, String message) {
        if (null == context) return;
        dismissDialog();
        final Dialog dialog = new Dialog(context, R.style.actionSheetTheme);
        dialog.setContentView(R.layout.dialog_alert);
        dialog.setCancelable(false);
        if (negative == null) {
            dialog.findViewById(R.id.negativeBtn).setVisibility(View.GONE);
        } else {
            dialog.findViewById(R.id.negativeBtn).setOnClickListener(negative);
        }
        dialog.findViewById(R.id.positiveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                positive.onCallBack("");
            }
        });
        ((FontTextView) dialog.findViewById(R.id.messageTv)).setText(message);
        ((FontTextView) dialog.findViewById(R.id.titleTv)).setText(title);

        try {
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showCancelDialog(final Context context, final StringCallBack callBack) {
        if (null == context) return;
        dismissDialog();
        mDialog = new Dialog(context, R.style.actionSheetTheme);
        mDialog.setContentView(R.layout.cancel_job_dialog);
        final RadioGroup radioGroup = (RadioGroup) mDialog.findViewById(R.id.optionBtn);
        RadioGroup.LayoutParams rprms;
        ArrayList<String> cancelMessages = AppPreferences.getSettings().getPredefine_messages().getCancel();
        if (cancelMessages.size() > 0) {
            int id = 0;
            for (String msg : cancelMessages) {
                RadioButton radioButton = new RadioButton(context);
                radioButton.setText(msg);
//                if (id == 0) {
//                    radioButton.setChecked(true);
//                }
                radioButton.setId(id++);
                radioButton.setTextColor(ContextCompat.getColor(context, R.color.textColorPrimary));
                rprms = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                radioGroup.addView(radioButton, rprms);
            }
        }
        mDialog.findViewById(R.id.positiveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioGroup.getCheckedRadioButtonId() != -1) {
                    dismissDialog();
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    RadioButton radioButton = (RadioButton) mDialog.findViewById(selectedId);
                    callBack.onCallBack("" + radioButton.getText());
                } else {
                    Utils.appToast(context, "Please select Cancellation Reason.");
                }
            }
        });
        showDialog();
    }

    public void showConfirmArrivalDialog(Context context, boolean showTickBtn, View.OnClickListener positive) {
        if (null == context) return;
        dismissDialog();
        mDialog = new Dialog(context, R.style.actionSheetTheme);
        mDialog.setContentView(R.layout.dialog_confirm_arrival);
        AutoFitFontTextView tvMessage = (AutoFitFontTextView) mDialog.findViewById(R.id.tvMessage);
        if (showTickBtn) {
            tvMessage.setText("آپ ابھی بھی کچھ دورہیں، کیا واقعی پہنچ گئے؟");
            mDialog.findViewById(R.id.positiveBtn).setVisibility(View.VISIBLE);
            mDialog.findViewById(R.id.positiveBtn).setOnClickListener(positive);
        } else {
            tvMessage.setText("آپ ابھی بھی کچھ دورہیں");
            mDialog.findViewById(R.id.positiveBtn).setVisibility(View.GONE);
        }
        mDialog.findViewById(R.id.negativeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissDialog();
            }
        });
        showDialog();
    }

    public void showInactiveAccountDialog(final Context context, final String number, final String msg) {
        if (null == context) return;
        dismissDialog();
        mDialog = new Dialog(context, R.style.actionSheetTheme);
        mDialog.setContentView(R.layout.dialog_inactive_account);
        if (StringUtils.isNotBlank(msg)) {
            ((FontTextView) mDialog.findViewById(R.id.titleTv)).setText(msg);
        }
        mDialog.findViewById(R.id.positiveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
                Utils.callingIntent(context, number);
            }
        });
        showDialog();
    }


    public void showStatusDialog(Context context, View.OnClickListener positive,
                                 View.OnClickListener negative) {
        dismissDialog();
        mDialog = new Dialog(context, R.style.actionSheetTheme);
        mDialog.setContentView(R.layout.dialog_status);

        mDialog.findViewById(R.id.positive).setOnClickListener(positive);
        mDialog.findViewById(R.id.negative).setOnClickListener(negative);

        showDialog();
    }

    public void showDatePicker(Context context, DatePickerDialog.OnDateSetListener dateListener,
                               int year, int monthOfYear, int dayOfMonth, DialogInterface.OnCancelListener cancelListener) {
        dismissDialog();
        mDialog = new DatePickerDialog(context,
                dateListener,
                year, monthOfYear, dayOfMonth);
        mDialog.setOnCancelListener(cancelListener);
        showDialog();
    }

    public void showLocationSettings(final Context context, final int requestCode) {
        dismissDialog();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting DialogHelp Title
        alertDialog.setTitle("GPS Settings");

        // Setting DialogHelp Message
        alertDialog
                .setMessage("Turn on your location from settings.");
        alertDialog.setCancelable(false);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        ((AppCompatActivity) context).startActivityForResult(intent, requestCode);
                    }
                });

        // on pressing cancel button
        /*alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });*/

        // Showing Alert Message
        mDialog = alertDialog.create();
        showDialog();
    }

    public void showPermissionSettings(final Context context, final int requestCode, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting DialogHelp Title
        alertDialog.setTitle(title);

        // Setting DialogHelp Message
        alertDialog
                .setMessage(message);
        alertDialog.setCancelable(false);

        // On pressing Settings button
        alertDialog.setPositiveButton("Go to Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", ((AppCompatActivity) context).getPackageName(), null);
                        intent.setData(uri);
                        ((AppCompatActivity) context).startActivityForResult(intent, requestCode);
                    }
                });

        try {
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showItems(Context context, String[] items, final IntegerCallBack listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                listener.onCallBack(item);
                dismissDialog();
            }
        });
        dismissDialog();
        mDialog = builder.create();
        mDialog.setCancelable(false);
        showDialog();

    }


    public void showTopUpDialogPromo(Context context, String totalAmount) {
        dismissDialog();
        mDialog = new Dialog(context, R.style.actionSheetTheme);
        mDialog.setContentView(R.layout.top_up_dialog);

        FontButton okIv = (FontButton) mDialog.findViewById(R.id.ivPositive);
        FontTextView msg = (FontTextView) mDialog.findViewById(R.id.tvMessage);
        String msgToShow;
        msgToShow = "Rs. " + totalAmount;
        msg.setText(msgToShow);
        okIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });
        mDialog.setCancelable(false);
        showDialog();
    }

    public void showInactiveConfirmationDialog(Context context, View.OnClickListener onClick) {
        dismissDialog();
        mDialog = new Dialog(context, R.style.actionSheetTheme);
        mDialog.setContentView(R.layout.inactive_confirmation_dialog);
        FontButton noIv = (FontButton) mDialog.findViewById(R.id.negativeBtn);
        FontButton okIv = (FontButton) mDialog.findViewById(R.id.positiveBtn);
        okIv.setOnClickListener(onClick);
        noIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });
        mDialog.setCancelable(false);
        showDialog();
    }

    public void showLogoutDialog(Context context,
                                 View.OnClickListener onClickListener) {
        dismissDialog();
        mDialog = new Dialog(context, R.style.actionSheetTheme);
        mDialog.setContentView(R.layout.logout_dialog);
        FontButton okIv = (FontButton) mDialog.findViewById(R.id.ivPositive);
        FontButton cancelIv = (FontButton) mDialog.findViewById(R.id.ivNegative);

        cancelIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        okIv.setOnClickListener(onClickListener);

        showDialog();

    }

    public void showUpdateAppDialog(final Context context, String title, String message, final String link) {
        if (null == context) return;
        dismissDialog();
        mDialog = new Dialog(context, R.style.actionSheetTheme);
        mDialog.setContentView(R.layout.dialog_alert);
        mDialog.setCancelable(false);
        mDialog.findViewById(R.id.negativeBtn).setVisibility(View.GONE);

        mDialog.findViewById(R.id.positiveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(link));
                context.startActivity(i);
                dismissDialog();
            }
        });
        ((FontTextView) mDialog.findViewById(R.id.messageTv)).setText(message);
        ((FontTextView) mDialog.findViewById(R.id.titleTv)).setText(title);

        showDialog();
    }

    public void showAlertDialog(final Context context, String title, String message) {
        if (null == context) return;
        dismissDialog();
        mDialog = new Dialog(context, R.style.actionSheetTheme);
        mDialog.setContentView(R.layout.dialog_alert);
        mDialog.setCancelable(false);
        mDialog.findViewById(R.id.negativeBtn).setVisibility(View.GONE);

        mDialog.findViewById(R.id.positiveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });
        ((FontTextView) mDialog.findViewById(R.id.messageTv)).setText(message);
        ((FontTextView) mDialog.findViewById(R.id.titleTv)).setText(title);

        showDialog();
    }
}
