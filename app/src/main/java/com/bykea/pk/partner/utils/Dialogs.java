package com.bykea.pk.partner.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bykea.pk.partner.BuildConfig;
import com.bykea.pk.partner.DriverApp;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.dal.source.remote.data.Invoice;
import com.bykea.pk.partner.ui.activities.BaseActivity;
import com.bykea.pk.partner.ui.common.LastAdapter;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.IntegerCallBack;
import com.bykea.pk.partner.ui.helpers.StringCallBack;
import com.bykea.pk.partner.ui.helpers.adapters.CancelReasonDialogAdapter;
import com.bykea.pk.partner.ui.helpers.adapters.WeekAdapter;
import com.bykea.pk.partner.widgets.AutoFitFontTextView;
import com.bykea.pk.partner.widgets.FontButton;
import com.bykea.pk.partner.widgets.FontEditText;
import com.bykea.pk.partner.widgets.FontTextView;
import com.bykea.pk.partner.widgets.FontUtils;
import com.bykea.pk.partner.widgets.Fonts;
import com.google.android.gms.common.util.Strings;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.bykea.pk.partner.utils.Constants.COLON;
import static com.bykea.pk.partner.utils.Constants.DIGIT_FIVE;
import static com.bykea.pk.partner.utils.Constants.DIGIT_ONE;
import static com.bykea.pk.partner.utils.Constants.DIGIT_ZERO;
import static com.bykea.pk.partner.utils.Constants.DOT;

//import com.thefinestartist.Base;

public enum Dialogs {
    INSTANCE;

    private Dialog mDialog;
    private Dialog mAdminNotifiationDialog;

    public void showToast(String message) {
        Utils.appToast(message);
    }

    public void showTempToast(String message) {
        Utils.appToastDebug(message);
    }

    public void dismissDialog() {
        dismissDialog(mDialog);
    }

    public void dismissDialog(Dialog dialog) {
        try {
            if (null != dialog && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismissAdminNotiDialog() {
        try {
            if (null != mAdminNotifiationDialog && mAdminNotifiationDialog.isShowing()) {
                mAdminNotifiationDialog.dismiss();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDialog() {
        showDialog(mDialog);
    }

    private void showDialog(Dialog dialog) {
        try {
            if (!isShowing()) {
                dialog.show();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
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

    /***
     * Show Dialog to user for Invalid OTP code entry.
     *
     * @param context Calling context
     */
    public void showInvalidCodeDialog(Context context) {
        if (context instanceof AppCompatActivity && !((AppCompatActivity) context).isFinishing()) {
            dismissDialog();
            mDialog = new Dialog(context, R.style.actionSheetTheme);
            mDialog.setContentView(R.layout.invalid_code_dialog);
            FontButton okIv = mDialog.findViewById(R.id.ivPositive);
            okIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissDialog();
                }
            });
            mDialog.setCancelable(false);
            showDialog();
        }
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
        if (context instanceof AppCompatActivity && !((AppCompatActivity) context).isFinishing()) {
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
            FontTextView messageTv = ((FontTextView) mDialog.findViewById(R.id.messageTv));
            messageTv.setTypeface(FontUtils.getFonts(Constants.FontNames.JAMEEL_NASTALEEQI));
            messageTv.setText(message);
            messageTv.setTextSize(context.getResources().getDimension(R.dimen._7sdp));
            ((FontTextView) mDialog.findViewById(R.id.titleTv)).setText(title);

            showDialog();
        }
    }


    public void showAlertDialogWithTickCross(Context context, View.OnClickListener positive,
                                             View.OnClickListener negative, String title, String message) {
        if (context instanceof AppCompatActivity && !((AppCompatActivity) context).isFinishing()) {
            dismissDialog();
            mDialog = new Dialog(context, R.style.actionSheetTheme);
            mDialog.setContentView(R.layout.dialog_alert_tick_cross);
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
            FontTextView messageTv = mDialog.findViewById(R.id.messageTv);
            messageTv.setTypeface(FontUtils.getFonts("jameel_noori_nastaleeq.ttf"));
            messageTv.setText(message);
            messageTv.setTextSize(context.getResources().getDimension(R.dimen._7sdp));
            if (StringUtils.isNotBlank(title)) {
                ((FontTextView) mDialog.findViewById(R.id.titleTv)).setText(title);
            } else {
                mDialog.findViewById(R.id.titleTv).setVisibility(View.GONE);
            }

            showDialog();
        }
    }

    /***
     * Shows battery dialog
     * @param context Calling context
     * @param title  Title which needs to be displayed.
     * @param message Message which needs to be displayed.
     * @param onClick Click listener
     */
    public void showAlertDialogForBattery(Context context,
                                          String title,
                                          String message,
                                          View.OnClickListener onClick) {

        if (context instanceof AppCompatActivity && !((AppCompatActivity) context).isFinishing()) {
            dismissDialog();
            mDialog = new Dialog(context, R.style.actionSheetTheme);
            mDialog.setContentView(R.layout.dialog_alert);
            mDialog.setCancelable(false);
            mDialog.findViewById(R.id.negativeBtn).setVisibility(View.GONE);

            mDialog.findViewById(R.id.positiveBtn).setOnClickListener(onClick);
            ((FontTextView) mDialog.findViewById(R.id.messageTv)).setText(message);
            ((FontTextView) mDialog.findViewById(R.id.titleTv)).setText(title);

            showDialog();
        }

    }

    public void showAlertDialog(Context context, String title, String message, View.OnClickListener onClick) {
        if (context instanceof AppCompatActivity && !((AppCompatActivity) context).isFinishing()) {
            dismissDialog();
            mDialog = new Dialog(context, R.style.actionSheetTheme);
            mDialog.setContentView(R.layout.dialog_alert);

            FontTextView okIv = (FontTextView) mDialog.findViewById(R.id.positiveBtn);
            FontTextView cancelIv = (FontTextView) mDialog.findViewById(R.id.negativeBtn);
            cancelIv.setText(context.getString(R.string.button_text_cancel));
            okIv.setText(context.getString(R.string.button_text_ok));

            cancelIv.setAllCaps(true);
            okIv.setAllCaps(true);

            FontTextView titleTv = (FontTextView) mDialog.findViewById(R.id.titleTv);
            FontTextView msg = (FontTextView) mDialog.findViewById(R.id.messageTv);

            titleTv.setText(title);
            msg.setText(message);
            okIv.setOnClickListener(onClick);
            cancelIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissDialog();
                }
            });
            mDialog.setCancelable(false);
            showDialog();
        }
    }

    public void showCallPassengerDialog(Context context, View.OnClickListener btnSender,
                                        View.OnClickListener btnRecipient) {
        if (context instanceof AppCompatActivity && !((AppCompatActivity) context).isFinishing()) {
            dismissDialog();
            mDialog = new Dialog(context, R.style.actionSheetTheme);
            mDialog.setContentView(R.layout.call_passenger_dialog);

            mDialog.findViewById(R.id.ivSender).setOnClickListener(btnSender);
            mDialog.findViewById(R.id.ivRecipient).setOnClickListener(btnRecipient);
            mDialog.findViewById(R.id.ivBackBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissDialog();
                }
            });

            showDialog();
        }
    }

    public void showRideStatusDialog(Context context, View.OnClickListener positive,
                                     View.OnClickListener negative, String title) {
        if (context instanceof AppCompatActivity && !((AppCompatActivity) context).isFinishing()) {
            dismissDialog();
            mDialog = new Dialog(context, R.style.actionSheetTheme);
            mDialog.setContentView(R.layout.dialog_ride_status);
            mDialog.setCancelable(false);
            mDialog.findViewById(R.id.negativeBtn).setOnClickListener(negative);
            mDialog.findViewById(R.id.positiveBtn).setOnClickListener(positive);
            ((FontTextView) mDialog.findViewById(R.id.titleTv)).setText(title);
            showDialog();
        }
    }

    public void showAlertDialogNotSingleton(Context context, final StringCallBack positive,
                                            View.OnClickListener negative, String title, String message) {
        if (context instanceof AppCompatActivity && !((AppCompatActivity) context).isFinishing()) {
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
    }

    public void showAlert(final List<String> list, final TextView tv, final Activity mCurrentActivity) {
        dismissDialog();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mCurrentActivity);
        final LayoutInflater li = LayoutInflater.from(mCurrentActivity);
        View promptsView = li.inflate(R.layout.dropdown_weeks_layout, null);
        alertDialogBuilder.setView(promptsView);

        mDialog = alertDialogBuilder.create();

        final ListView lv = promptsView
                .findViewById(R.id.lv);

        final WeekAdapter arrayAdapter = new WeekAdapter(mCurrentActivity, R.layout.week_item, list);
        lv.setAdapter(arrayAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    if (list.get(position).equalsIgnoreCase(mCurrentActivity.getString(R.string.current_week))) {
                        setCalenderCurrentWeek(tv); //week start from friday to thursday
                    } else {
                        setlastWeek(tv); //week start from friday
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //tv.setText(list.get(position));
                mDialog.dismiss();
            }
        });

        mDialog.show();
        mDialog.setCanceledOnTouchOutside(false);
    }

    public static void setlastWeek(TextView tv) {
        try {
            DateFormat df = new SimpleDateFormat("d MMM");
            String startDate = "", endDate = "";
            Calendar calendar = Calendar.getInstance();
            Log.v("Current Week", String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR)));
            // get the starting and ending date
            // Set the calendar to friday of the current week


            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
                calendar.add(Calendar.DATE, -7);
            } else if (calendar.get(Calendar.DAY_OF_WEEK) > Calendar.FRIDAY) {
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
            } else {
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                System.out.println("Current week = " + Calendar.DAY_OF_WEEK);
                calendar.add(Calendar.DATE, -14);
            }

            // Print dates of the current week starting on friday
            startDate = df.format(calendar.getTime());
            calendar.add(Calendar.DATE, 6);
            endDate = df.format(calendar.getTime());

            System.out.println("Start Date = " + startDate);
            System.out.println("End Date = " + endDate);

            tv.setText(startDate + " - " + endDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setCalenderCurrentWeek(TextView tv) {
        try {
            String startDate = "", endDate = "";
            Calendar calendar = Calendar.getInstance();
            DateFormat df = new SimpleDateFormat("d MMM");
            Log.v("Current Week", String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR)));
            // get the starting and ending date
            // Set the calendar to friday of the current week

            if (calendar.get(Calendar.DAY_OF_WEEK) > Calendar.FRIDAY) {
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
            } else {
                if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY) {
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                    System.out.println("Current week = " + Calendar.DAY_OF_WEEK);
                    calendar.add(Calendar.DATE, -7);
                }

            }

            // Print dates of the current week starting on Friday
            startDate = df.format(calendar.getTime());
            calendar.add(Calendar.DATE, 6);
            endDate = df.format(calendar.getTime());

            System.out.println("Start Date = " + startDate);
            System.out.println("End Date = " + endDate);

            tv.setText(startDate + " - " + endDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showCancelDialog(final Context context, final StringCallBack callBack) {
        if (context instanceof AppCompatActivity && !((AppCompatActivity) context).isFinishing()) {
            dismissDialog();
            mDialog = new Dialog(context, R.style.actionSheetThemeFullScreen);
            mDialog.setContentView(R.layout.cancel_job_dialog);
            final ArrayList<String> cancelMessages = AppPreferences.getSettings().getPredefine_messages().getCancel();
            final RecyclerView recyclerView = mDialog.findViewById(R.id.rvItems);
            final CancelReasonDialogAdapter adapter = new CancelReasonDialogAdapter(context, cancelMessages);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setAdapter(adapter);
            mDialog.findViewById(R.id.ivPositive).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (adapter.getSelectedIndex() != 999) {
                        dismissDialog();
                        callBack.onCallBack("" + cancelMessages.get(adapter.getSelectedIndex()));
                    } else {
                        Utils.appToast(context.getString(R.string.cancel_reason));
                    }
                }
            });
            mDialog.findViewById(R.id.ivNegative).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissDialog();
                }
            });
            showDialog();
        }
    }

    public void showConfirmArrivalDialog(Context context, boolean showTickBtn, View.OnClickListener positive) {
        if (context instanceof AppCompatActivity && !((AppCompatActivity) context).isFinishing()) {
            dismissDialog();
            mDialog = new Dialog(context, R.style.actionSheetTheme);
            mDialog.setContentView(R.layout.dialog_confirm_arrival);
            AutoFitFontTextView tvMessage = (AutoFitFontTextView) mDialog.findViewById(R.id.tvMessage);
            if (showTickBtn) {
                tvMessage.setText(context.getString(R.string.aap_abhi_door_hain_kiya_pohnch_gye));
                mDialog.findViewById(R.id.positiveBtn).setVisibility(View.VISIBLE);
                mDialog.findViewById(R.id.positiveBtn).setOnClickListener(positive);
            } else {
                tvMessage.setText(context.getString(R.string.aap_abhi_door_hain));
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
    }

    /***
     * Show Driver license expire Account deactivate message
     * @param context Calling context
     * @param number Support Helpline number
     */
    public void showInactiveAccountDialog(final Context context, final String number) {
        if (context instanceof AppCompatActivity && !((AppCompatActivity) context).isFinishing()) {
            dismissDialog();
            mDialog = new Dialog(context, R.style.actionSheetTheme);
            mDialog.setContentView(R.layout.dialog_inactive_account);
            mDialog.findViewById(R.id.positiveBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissDialog();
                    Utils.callingIntent(context, number);
                }
            });
            showDialog();
        }
    }

    /***
     * Show Region Out error message with support number
     * @param context Calling Context
     * @param number Support number
     * @param msg Message which needs to be displayed.
     */
    public void showRegionOutErrorDialog(final Context context,
                                         final String number,
                                         final String msg) {
        if (context instanceof AppCompatActivity && !((AppCompatActivity) context).isFinishing()) {
            dismissDialog();
            Dialog mDialog = new Dialog(context, R.style.actionSheetTheme);
            mDialog.setContentView(R.layout.dialog_region_out);
            if (StringUtils.isNotBlank(msg)) {
                ((AutoFitFontTextView) mDialog.findViewById(R.id.messageTv)).setText(msg);
            }
            mDialog.findViewById(R.id.positiveBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissDialog();
                    Utils.callingIntent(context, number);
                }
            });
            showDialog(mDialog);
        }
    }

    /***
     * Show IMEI not registered error message which takes user to report submit screen.
     * @param context Calling Context
     * @param msg Message which needs to be displayed.
     * @param positive report submit click listener.
     */
    public void showImeiRegistrationErrorDialog(final Context context,
                                                final SpannableStringBuilder msg,
                                                final View.OnClickListener positive) {
        if (context instanceof AppCompatActivity && !((AppCompatActivity) context).isFinishing()) {
            dismissDialog();
            mDialog = new Dialog(context, R.style.actionSheetTheme);
            mDialog.setContentView(R.layout.dialog_imei_not_registerd);
            if (StringUtils.isNotBlank(msg)) {
                ((FontTextView) mDialog.findViewById(R.id.messageTv)).setText(msg);
            }
            mDialog.findViewById(R.id.positiveBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissDialog();
                    positive.onClick(v);
                }
            });
            showDialog();
        }
    }


    public void showTopUpDialog(final Context context, final boolean isCourierType, final StringCallBack callBack) {
        if (context instanceof AppCompatActivity && !((AppCompatActivity) context).isFinishing()) {
            dismissDialog();
            mDialog = new Dialog(context, R.style.actionSheetTheme);
            mDialog.setContentView(R.layout.top_up_add_dialog);
            final FontEditText receivedAmountEt = (FontEditText) mDialog.findViewById(R.id.receivedAmountEt);
            receivedAmountEt.setTransformationMethod(new NumericKeyBoardTransformationMethod());
            mDialog.findViewById(R.id.ivBackBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismissDialog();
                }
            });
            mDialog.findViewById(R.id.ivPositive).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (StringUtils.isNotBlank(receivedAmountEt.getText().toString()) &&
                            Integer.valueOf(receivedAmountEt.getText().toString()) == DIGIT_ZERO) {
                        receivedAmountEt.setError(DriverApp.getContext().getString(R.string.enter_correct_amount));
                    } else if (Utils.isValidTopUpAmount(receivedAmountEt.getText().toString(), isCourierType)) {
                        dismissDialog();
                        callBack.onCallBack(receivedAmountEt.getText().toString());
                    } else {
                        String amount = AppPreferences.getSettings().getSettings().getPartner_topup_limit();
                        if (isCourierType)
                            amount = AppPreferences.getSettings().getSettings().getVan_partner_topup_limit();

                        receivedAmountEt.setError(DriverApp.getContext().getString(R.string.amount_cannot_greater, amount));
                    }
                }
            });
            showDialog();
        }
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
        if (context instanceof AppCompatActivity && !((AppCompatActivity) context).isFinishing()) {
            dismissDialog();
            Dialog dialog = new Dialog(context, R.style.actionSheetTheme);
            dialog.setContentView(R.layout.enable_gps_dialog);

            ImageView okIv = dialog.findViewById(R.id.ivPositive);

            okIv.setOnClickListener(v -> {
                if (context instanceof BaseActivity) {
                    dismissDialog(dialog);
                    ((BaseActivity) context).showLocationDialog();
                } else {
                    dismissDialog(dialog);
                    Intent intent = new Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    ((Activity) context).startActivityForResult(intent, Constants.REQUEST_CODE_GPS_AND_LOCATION);
                }
            });
            showDialog(dialog);
        }
    }

    public void showPermissionSettings(final Context context, final int requestCode, String title, String message) {
        if (context instanceof AppCompatActivity && !((AppCompatActivity) context).isFinishing()) {
            try {
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

                alertDialog.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
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

    /**
     * will show change image dialog
     *
     * @param context               of the activity
     * @param uri                   local file uri
     * @param positiveClickListener on change image click listener
     * @param negativeClickListener on tick click listener
     */
    public void showChangeImageDialog(Context context, File uri, View.OnClickListener positiveClickListener, View.OnClickListener negativeClickListener) {
        showChangeImageDialog(context, uri, null, positiveClickListener, negativeClickListener);
    }

    /**
     * will show change image dialog
     *
     * @param context               of the activity
     * @param uri                   global image url
     * @param positiveClickListener on change image click listener
     * @param negativeClickListener on tick click listener
     */
    public void showChangeImageDialog(Context context, String uri, View.OnClickListener positiveClickListener, View.OnClickListener negativeClickListener) {
        showChangeImageDialog(context, null, uri, positiveClickListener, negativeClickListener);
    }


    /**
     * will show change image dialog
     *
     * @param context               of the activity
     * @param url                   global image url
     * @param uri                   local file uri
     * @param positiveClickListener on change image click listener
     * @param negativeClickListener on tick click listener
     */
    public void showChangeImageDialog(Context context, File uri, String url, View.OnClickListener positiveClickListener, View.OnClickListener negativeClickListener) {
        if (context instanceof AppCompatActivity && !((AppCompatActivity) context).isFinishing()) {
            dismissDialog();
            mDialog = new Dialog(context, R.style.actionSheetThemeFullScreen);
            mDialog.setContentView(R.layout.dialog_change_image);
            ImageView ivPicture = mDialog.findViewById(R.id.ivPicture);
            ImageView okIv = mDialog.findViewById(R.id.ivPositive);
            View cancelIv = mDialog.findViewById(R.id.llChangeImage);
            if (uri != null)
                ivPicture.setImageURI(Uri.fromFile(uri));
            else
                Picasso.get().load(Uri.parse(url)).into(ivPicture);

            if (negativeClickListener == null)
                cancelIv.setVisibility(View.GONE);
            else
                cancelIv.setOnClickListener(negativeClickListener);
            if (positiveClickListener == null)
                okIv.setOnClickListener(v -> mDialog.dismiss());
            else
                okIv.setOnClickListener(positiveClickListener);
            showDialog();
        }
    }

    /**
     * This method shows a pop up dialog with Urdu text and Tick/Cross as Positive/Negative Button
     * Positive button will have Red background and negative will have green/colorAccent.
     *
     * @param context               Calling Context
     * @param msg                   Message to show in String
     * @param positiveClickListener Callback to notify that OK/Positive button is clicked
     * @param negativeClickListener Callback to notify that Cancel/Negative button is clicked
     */
    public void showNegativeAlertDialog(Context context, String msg,
                                        View.OnClickListener positiveClickListener, View.OnClickListener negativeClickListener) {
        if (context instanceof AppCompatActivity && !((AppCompatActivity) context).isFinishing()) {
            dismissDialog();
            mDialog = new Dialog(context, R.style.actionSheetThemeFullScreen);
            mDialog.setContentView(R.layout.dialog_neg_alert_ur_tick_cross);
            ImageView okIv = mDialog.findViewById(R.id.ivPositive);
            ImageView cancelIv = mDialog.findViewById(R.id.ivNegative);
            FontTextView tvMsg = mDialog.findViewById(R.id.tvMsg);
            tvMsg.setText(msg);

            if (negativeClickListener == null)
                cancelIv.setOnClickListener(v -> mDialog.dismiss());
            else
                cancelIv.setOnClickListener(negativeClickListener);

            okIv.setOnClickListener(positiveClickListener);

            showDialog();
        }

    }

    /**
     * This method shows a pop up dialog with Urdu text and Tick as Positive Button
     * Positive button will have Red background. This is show for demand screen
     *
     * @param context         Calling Context
     * @param msg             Message to show in String
     * @param onClickListener Callback to notify that OK/Positive button is clicked
     */
    public void showNegativeAlertDialogForDemand(Context context,
                                                 String msg,
                                                 final View.OnClickListener onClickListener) {
        if (context instanceof AppCompatActivity && !((AppCompatActivity) context).isFinishing()) {
            dismissDialog();
            mDialog = new Dialog(context, R.style.actionSheetThemeFullScreen);
            mDialog.setContentView(R.layout.dialog_neg_alert_ur_tick_cross);
            ImageView okIv = mDialog.findViewById(R.id.ivPositive);
            ImageView cancelIv = mDialog.findViewById(R.id.ivNegative);
            FontTextView tvMsg = mDialog.findViewById(R.id.tvMsg);
            tvMsg.setText(msg);
            if (cancelIv != null) {
                cancelIv.setVisibility(View.GONE);
            }


            okIv.setBackground(ContextCompat.getDrawable(context, R.drawable.button_green_square));
            okIv.setOnClickListener(onClickListener);

            showDialog();
        }
    }

    public void showUpdateAppDialog(final Context context, String title, String message, final String link) {
        if (context instanceof AppCompatActivity && !((AppCompatActivity) context).isFinishing()) {
            dismissDialog();
            mDialog = new Dialog(context, R.style.actionSheetTheme);
            mDialog.setContentView(R.layout.dialog_alert_update_app);
            mDialog.setCancelable(false);
            //mDialog.findViewById(R.id.negativeBtn).setVisibility(View.GONE);

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
    }

    public void showAlertDialog(final Context context, String title, String message) {
        if (context instanceof AppCompatActivity && !((AppCompatActivity) context).isFinishing()) {
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

    /**
     * This methods shows a pop up dialog when partner has successfully signed up
     *
     * @param context Calling context
     * @param phoneNo Registered Phone No.
     * @param onClick callback to handle positive button's click
     */
    public void showSignUpSuccessDialog(Context context, String phoneNo, View.OnClickListener onClick) {
        if (context instanceof AppCompatActivity && !((AppCompatActivity) context).isFinishing()) {
            dismissDialog();
            mDialog = new Dialog(context, R.style.actionSheetThemeTimer);
            mDialog.setContentView(R.layout.signup_success_dialog);
            ((FontTextView) mDialog.findViewById(R.id.tvTrainingLinkMsg)).setText(context.getString(R.string.register_tarining_link_msg, phoneNo));
            mDialog.setCancelable(false);
            mDialog.findViewById(R.id.nextBtn).setOnClickListener(onClick);
            showDialog();
        }
    }

    public void showVerificationDialog(Context context, boolean success, View.OnClickListener onClick) {
        if (context instanceof AppCompatActivity && !((AppCompatActivity) context).isFinishing()) {
            dismissDialog();
            mDialog = new Dialog(context, R.style.actionSheetThemeTimer);
            if (success) {
                mDialog.setContentView(R.layout.verification_success_dialog);
            } else {
                mDialog.setContentView(R.layout.verification_unsuccessful_dialog);
            }
            mDialog.setCancelable(false);
            mDialog.findViewById(R.id.nextBtn).setOnClickListener(onClick);
            showDialog();
        }
    }

    /**
     * This method shows a dialog to enter base url for local builds
     *
     * @param activity    calling activity
     * @param dataHandler call back handler
     */
    public void showInputAlert(final Activity activity, final StringCallBack dataHandler) {
        try {
            if (activity instanceof AppCompatActivity && !activity.isFinishing()) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                alertDialogBuilder.setTitle("Enter Your IP");

                final EditText input = new EditText(DriverApp.getContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                input.setTextColor(activity.getResources().getColor(R.color.black));
                input.setText(BuildConfig.FLAVOR_URL);
                alertDialogBuilder.setView(input);

                alertDialogBuilder.setPositiveButton("OK", null);

                alertDialogBuilder.setCancelable(false);

                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {

                        Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                if (input.getText().length() == 0) {
                                    Utils.appToast("enter your ip");
                                } else if (!Utils.isValidUrl(input.getText().toString())) {
                                    Utils.appToast("enter valid url");
                                } else {
                                    dataHandler.onCallBack(input.getText().toString());
                                    alertDialog.dismiss();
                                }
                            }
                        });
                    }
                });
                alertDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * This method shows a pop up dialog with Urdu text and Tick/Cross as Positive/Negative Button
     * It will hide cross button when OnClickListener for negative button is null
     *
     * @param context  Calling Context
     * @param message  Message to display
     * @param textSize Size of message textview (to use defualt text size i.e 22sdp pass 0)
     * @param negative OnClickListener for callback when Negative button is pressed
     * @param positive OnClickListener for callback when Positive button is pressed
     */
    public void showAlertDialogUrduWithTickCross(Context context, String message, float textSize,
                                                 View.OnClickListener negative, View.OnClickListener positive) {
        if (context instanceof AppCompatActivity && !((AppCompatActivity) context).isFinishing()) {
            dismissDialog();
            mDialog = new Dialog(context, R.style.actionSheetThemeFullScreen);
            mDialog.setContentView(R.layout.dialog_alert_tick_cross_urdu);

            if (negative == null) {
                mDialog.setCancelable(true);
                mDialog.findViewById(R.id.negativeBtn).setVisibility(View.GONE);
            } else {
                mDialog.setCancelable(false);
                mDialog.findViewById(R.id.negativeBtn).setOnClickListener(negative);
            }

            mDialog.findViewById(R.id.positiveBtn).setOnClickListener(positive);
            FontTextView messageTv = mDialog.findViewById(R.id.messageTv);
            messageTv.setText(message);
            if (textSize > 0f) {
                messageTv.setTextSize(textSize);
            }
            showDialog();
        }
    }

    /**
     * This method creates a dialog to show cancel notification
     *
     * @param context Calling context
     * @param message notification message to show
     * @param onClick Callback to notify that OK/Positive button is clicked
     */
    public void showCancelNotification(Context context, String message, final StringCallBack onClick) {
        if (context instanceof AppCompatActivity && !((AppCompatActivity) context).isFinishing()) {
            dismissDialog();
            final Dialog dialog = new Dialog(context, R.style.actionSheetThemeFullScreen);
            dialog.setContentView(R.layout.dialog_cancel_notification);
            dialog.setCancelable(false);
            dialog.findViewById(R.id.positiveBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    onClick.onCallBack(StringUtils.EMPTY);
                }
            });
            ((FontTextView) dialog.findViewById(R.id.messageTv)).setText(message);
            dialog.show();
        }
    }

    public void showReturnRunInvoice(Context context, ArrayList<Invoice> invoices, View.OnClickListener onClick) {
        if (context instanceof AppCompatActivity && !((AppCompatActivity) context).isFinishing()) {
            dismissDialog();
            mDialog = new Dialog(context, R.style.actionSheetTheme);
            mDialog.setContentView(R.layout.dialog_return_run_invoice);
            LastAdapter<Invoice> adapter = new LastAdapter<>(R.layout.adapter_booking_detail_invoice, item -> {

            });
            RecyclerView mDialogRecyclerView = mDialog.findViewById(R.id.invoiceRecyclerView);
            mDialogRecyclerView.setAdapter(adapter);

            adapter.setItems(invoices);
            mDialog.findViewById(R.id.ivPositive).setOnClickListener(v -> {
                dismissDialog();
                if (onClick != null)
                    onClick.onClick(v);
            });
            mDialog.setCancelable(false);

            showDialog();
        }
    }


    /**
     * GENERIC DIALOG
     *
     * @param mTitle    ? View.VISIBLE : View.GONE
     * @param mMesssage ? View.VISIBLE : View.GONE
     * @param onClick   ? If Want To Perform Another Operation, Implement Callback Else Send Null (As Dialog Will Already Being
     *                  Dismiss By OnClick
     */
    public void showAlertDialogTick(Context context, String mTitle, String mMesssage, View.OnClickListener onClick) {
        if (context instanceof AppCompatActivity && !((AppCompatActivity) context).isFinishing()) {
            dismissDialog();
            mDialog = new Dialog(context, R.style.actionSheetTheme);
            mDialog.setContentView(R.layout.booking_already_taken);

            FontTextView mDialogTitle, mDialogMessage;

            mDialogTitle = mDialog.findViewById(R.id.dialogTitle);
            mDialogMessage = mDialog.findViewById(R.id.dialogMessage);

            if (Strings.isEmptyOrWhitespace(mTitle)) {
                mDialogTitle.setVisibility(View.GONE);
            } else {
                mDialogTitle.setText(mTitle);
                mDialogTitle.setVisibility(View.VISIBLE);
            }

            if (Strings.isEmptyOrWhitespace(mMesssage)) {
                mDialogMessage.setVisibility(View.GONE);
            } else {
                mDialogMessage.setText(mMesssage);
                mDialogMessage.setVisibility(View.VISIBLE);
            }

            mDialog.findViewById(R.id.ivPositive).setOnClickListener(v -> {
                dismissDialog();
                if (onClick != null)
                    onClick.onClick(v);
            });

            mDialog.setCancelable(false);

            showDialog();
        }
    }


    /**
     * Dialog Called From Splash Activity
     * Enter Testing IP and LoadBoard IP
     *
     * @param activity    : calling activity
     * @param dataHandler : Use for the callback of strings.
     */
    public void showAlertDialogForURL(final Activity activity, final StringCallBack dataHandler) {
        try {
            if (activity instanceof AppCompatActivity && !activity.isFinishing()) {
                dismissDialog();
                mDialog = new Dialog(activity, R.style.actionSheetTheme);
                mDialog.setContentView(R.layout.dialog_service_host_url);

                EditText mEditTextIP, mEditTextLoadBoardIP;

                mEditTextIP = mDialog.findViewById(R.id.editTextIP);
                mEditTextLoadBoardIP = mDialog.findViewById(R.id.editTextLoadBoardIP);

                mEditTextIP.setText(BuildConfig.FLAVOR_URL);
                mEditTextLoadBoardIP.setText(com.bykea.pk.partner.dal.BuildConfig.FLAVOR_URL_LOADBOARD);

                mDialog.setOnShowListener(dialog -> mDialog.findViewById(R.id.imgViewClick).setOnClickListener(v -> {
                    if (mEditTextIP.getText().length() == 0) {
                        Utils.appToast(activity.getString(R.string.enter_your_ip));
                        return;
                    } else if (!Utils.isValidUrl(mEditTextIP.getText().toString())) {
                        Utils.appToast(activity.getString(R.string.enter_valid_ip));
                        return;
                    }

                    if (mEditTextLoadBoardIP.getText().length() == 0) {
                        Utils.appToast(activity.getString(R.string.enter_your_loadboard_ip));
                        return;
                    } else if (!Utils.isValidUrl(mEditTextLoadBoardIP.getText().toString())) {
                        Utils.appToast(activity.getString(R.string.enter_valid_loadboard_ip));
                        return;
                    }

                    mDialog.dismiss();
                    dataHandler.onCallBack(mEditTextIP.getText().toString(), mEditTextLoadBoardIP.getText().toString());
                }));

                mDialog.setCancelable(false);
                showDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param context     : Calling context
     * @param dataHandler : Use for the callback of strings.
     */
    public Dialog showTemperatureDialog(Context context, StringCallBack dataHandler) {
        if (context instanceof AppCompatActivity && !((AppCompatActivity) context).isFinishing()) {
            dismissDialog();
            Dialog mDialog = new Dialog(context, R.style.actionSheetTheme);
            mDialog.setContentView(R.layout.dialog_enter_temperature);
            try {
                mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mDialog.setCancelable(false);
            TextView titleTv = mDialog.findViewById(R.id.titleTv);
            EditText mEditTextTemperature = mDialog.findViewById(R.id.editTextTemperature);
            TextView textViewError = mDialog.findViewById(R.id.textViewError);

            Pair<Double, Double> fahrenheitMinMaxLimit = Utils.getMinMaxFahrenheitLimit();
            titleTv.setText(new SpannableStringBuilder(StringUtils.SPACE)
                    .append(FontUtils.getStyledTitle(context, context.getString(R.string.fahrenheit),
                            Fonts.Roboto_Medium.getName()))
                    .append(FontUtils.getStyledTitle(context, StringUtils.SPACE,
                            Fonts.Roboto_Medium.getName()))
                    .append(FontUtils.getStyledTitle(context, context.getString(R.string.enter_your_temperature),
                            Fonts.Jameel_Noori_Nastaleeq.getName()))
                    .append(StringUtils.SPACE));

            mEditTextTemperature.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(DIGIT_FIVE, DIGIT_ONE)});
            mEditTextTemperature.setFocusable(true);
            mEditTextTemperature.requestFocus();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mEditTextTemperature, InputMethodManager.SHOW_FORCED);

            ImageView mPositiveButton = mDialog.findViewById(R.id.positiveBtn);

            mDialog.findViewById(R.id.negativeBtn).setOnClickListener(v -> {
                mEditTextTemperature.setText(StringUtils.EMPTY);
                mDialog.dismiss();
            });

            mEditTextTemperature.addTextChangedListener(new TextWatcherUtil() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mEditTextTemperature.setError(null);
                    if (s.toString().trim().length() > DIGIT_ZERO && !s.toString().trim().equals(DOT) && Double.parseDouble(s.toString()) > fahrenheitMinMaxLimit.second) {
                        textViewError.setVisibility(View.VISIBLE);
                    } else {
                        textViewError.setVisibility(View.GONE);
                    }
                }
            });

            mDialog.setOnShowListener(dialog -> mPositiveButton.setOnClickListener(v -> {
                if (StringUtils.isEmpty(mEditTextTemperature.getText()) || mEditTextTemperature.getText().toString().trim().equals(DOT) ||
                        Double.parseDouble(mEditTextTemperature.getText().toString()) < fahrenheitMinMaxLimit.first ||
                        Double.parseDouble(mEditTextTemperature.getText().toString()) > fahrenheitMinMaxLimit.second) {
                    mEditTextTemperature.setError(DriverApp.getContext().getString(R.string.temperature_value_error,
                            String.valueOf(fahrenheitMinMaxLimit.first), String.valueOf(fahrenheitMinMaxLimit.second)));
                    mEditTextTemperature.requestFocus();
                    return;
                }
                dataHandler.onCallBack(mEditTextTemperature.getText().toString());
            }));
            return mDialog;
        }
        return null;
    }

    public void showCancelDialog(Activity activity, String message, View.OnClickListener onTickClick, View.OnClickListener onCrossClick) {
        try {
            if (activity instanceof AppCompatActivity && !activity.isFinishing()) {
                dismissDialog();
                mDialog = new Dialog(activity, R.style.actionSheetTheme);
                mDialog.setContentView(R.layout.dialog_alert_cancel);

                FontTextView textViewMessage = mDialog.findViewById(R.id.textViewMessage);
                textViewMessage.setText(message);

                ImageView negativeBtn = mDialog.findViewById(R.id.negativeBtn);
                ImageView positiveBtn = mDialog.findViewById(R.id.positiveBtn);

                if (onTickClick != null) {
                    negativeBtn.setOnClickListener(onCrossClick);
                } else {
                    negativeBtn.setOnClickListener(v -> dismissDialog());
                }

                if (onTickClick != null) {
                    positiveBtn.setOnClickListener(onTickClick);
                } else {
                    positiveBtn.setOnClickListener(v -> dismissDialog());
                }

                mDialog.setCancelable(false);
                showDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows Alert Dialog
     *
     * @param context   calling Context
     * @param titleText Title Text
     * @param message   Message Text
     * @param onClick   On Positive Button Click callback
     */
    public void showAlertDialogNew(Context context, String titleText, String message, View.OnClickListener onClick) {
        showAlertDialogNew(context, titleText, message, onClick, false);
    }

    /**
     * Shows Alert Dialog
     *
     * @param context   calling Context
     * @param titleText Title Text
     * @param message   Message Text
     * @param onClick   On Positive Button Click callback
     */
    public void showAlertDialogNew(Context context, String titleText, String message, View.OnClickListener onClick, boolean isCancellable) {
        dismissDialog();
        mDialog = new Dialog(context, R.style.actionSheetTheme);
        mDialog.setContentView(R.layout.dialog_permission);

        ImageView okIv = mDialog.findViewById(R.id.ivPositive);
        ImageView cancelIv = mDialog.findViewById(R.id.ivNegative);

        FontTextView title = mDialog.findViewById(R.id.cancelTitle);
        FontTextView msg = mDialog.findViewById(R.id.tvErrorMessage);

        title.setText(titleText);
        msg.setText(message);
        okIv.setOnClickListener(onClick);
        cancelIv.setOnClickListener(v -> dismissDialog());
        mDialog.setCancelable(isCancellable);
        showDialog();
    }

    /**
     * This method creates a dialog to show any message left amount limit
     *
     * @param context Calling context
     * @param amount  Left amount to show
     */
    public void showAmountLimitMessageDialog(Context context, int amount) {
        if (context instanceof AppCompatActivity && !((AppCompatActivity) context).isFinishing()) {
            dismissDialog();
            final Dialog dialog = new Dialog(context, R.style.actionSheetThemeFullScreen);
            dialog.setContentView(R.layout.dialog_general_message_layout);
            dialog.setCancelable(false);

            AutoFitFontTextView messageAmountTv = dialog.findViewById(R.id.messageAmountTv);

            messageAmountTv.setText(new SpannableStringBuilder(StringUtils.EMPTY)
                    .append(FontUtils.getStyledTitle(context, DriverApp.getContext().getString(R.string.remaining_limit), Constants.FontNames.JAMEEL_NASTALEEQI))
                    .append(FontUtils.getStyledTitle(context, COLON, Constants.FontNames.ROBOTO_MEDIUM))
                    .append(FontUtils.getStyledTitle(context, StringUtils.SPACE, Constants.FontNames.ROBOTO_MEDIUM))
                    .append(FontUtils.getStyledTitle(context, String.format(DriverApp.getContext().getString(R.string.amount_rs_int), amount), Constants.FontNames.ROBOTO_MEDIUM))
                    .append(StringUtils.SPACE));

            dialog.findViewById(R.id.positiveBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    /**
     * This method creates a dialog to represent passenger balance as negative
     *
     * @param context Calling context
     */
    public void showPassengerNegativeDialog(Context context) {
        if (context instanceof AppCompatActivity && !((AppCompatActivity) context).isFinishing()) {
            dismissDialog();
            final Dialog dialog = new Dialog(context, R.style.actionSheetThemeFullScreen);
            dialog.setContentView(R.layout.dialog_general_passenger_negative_balance);
            dialog.setCancelable(false);

            dialog.findViewById(R.id.positiveBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    /**
     * This method shows a pop up dialog with Urdu text and Tick as Positive Button
     *
     * @param context Calling Context
     * @param message Message to display
     */
    public void showAlertDialogUrduWithTick(Context context, String message) {
        if (context instanceof AppCompatActivity && !((AppCompatActivity) context).isFinishing()) {
            dismissDialog();
            Dialog mDialog = new Dialog(context, R.style.actionSheetThemeFullScreen);
            mDialog.setContentView(R.layout.dialog_alert_tick_cross_urdu);
            mDialog.findViewById(R.id.negativeBtn).setVisibility(View.GONE);
            mDialog.findViewById(R.id.positiveBtn).setOnClickListener(v -> dismissDialog(mDialog));
            FontTextView messageTv = mDialog.findViewById(R.id.messageTv);
            messageTv.setText(message);
            showDialog(mDialog);
        }
    }
}
