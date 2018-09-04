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
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bykea.pk.partner.BuildConfig;
import com.bykea.pk.partner.DriverApp;
import com.bykea.pk.partner.R;
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

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
        try {
            if (null != mDialog) {
                mDialog.show();
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
        FontTextView messageTv = ((FontTextView) mDialog.findViewById(R.id.messageTv));
        messageTv.setTypeface(FontUtils.getFonts("jameel_noori_nastaleeq.ttf"));
        messageTv.setText(message);
        messageTv.setTextSize(context.getResources().getDimension(R.dimen._7sdp));
        ((FontTextView) mDialog.findViewById(R.id.titleTv)).setText(title);

        showDialog();
    }


    public void showAlertDialogWithTickCross(Context context, View.OnClickListener positive,
                                             View.OnClickListener negative, String title, String message) {
        if (null == context) return;
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


    public void showAlertDialog(Context context, String title, String message, View.OnClickListener onClick) {
        dismissDialog();
        mDialog = new Dialog(context, R.style.actionSheetTheme);
        mDialog.setContentView(R.layout.dialog_alert);

        FontButton okIv = (FontButton) mDialog.findViewById(R.id.positiveBtn);
        FontButton cancelIv = (FontButton) mDialog.findViewById(R.id.negativeBtn);
        cancelIv.setText("CANCEL");
        okIv.setText("OK");

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

    public void showCallPassengerDialog(Context context, View.OnClickListener btnSender,
                                        View.OnClickListener btnRecipient) {
        if (null == context) return;
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
        if (null == context) return;
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
                    Utils.appToast(context, "Please select Cancellation Reason.");
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

    public void showTopUpDialog(final Context context, final boolean isCourierType, final StringCallBack callBack) {
        if (null == context) return;
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
                if (Utils.isValidTopUpAmount(receivedAmountEt.getText().toString(), isCourierType)) {
                    dismissDialog();
                    callBack.onCallBack(receivedAmountEt.getText().toString());
                } else {
                    String msg = "Amount can't be more than " + AppPreferences.getSettings().getSettings().getPartner_topup_limit();
                    if (isCourierType) {
                        msg = "Amount can't be more than " + AppPreferences.getSettings().getSettings().getVan_partner_topup_limit();
                    }
                    receivedAmountEt.setError(msg);
                }
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
        mDialog = new Dialog(context, R.style.actionSheetThemeFullScreen);
        mDialog.setContentView(R.layout.logout_dialog);
        ImageView okIv = mDialog.findViewById(R.id.ivPositive);
        ImageView cancelIv = mDialog.findViewById(R.id.ivNegative);

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
        mDialog.setContentView(R.layout.dialog_alert_update_app);
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


    public void showSuccessDialogForgotPassword(Context context, View.OnClickListener onClick) {
        dismissDialog();
        mDialog = new Dialog(context, R.style.actionSheetTheme);
        mDialog.setContentView(R.layout.forgot_password_success_dialog);
        FontButton okIv = (FontButton) mDialog.findViewById(R.id.ivPositive);
        okIv.setOnClickListener(onClick);
        mDialog.setCancelable(false);
        showDialog();
    }

    /**
     * This methods shows a pop up dialog when partner has successfully signed up
     *
     * @param context Calling context
     * @param phoneNo Registered Phone No.
     * @param onClick callback to handle positive button's click
     */
    public void showSignUpSuccessDialog(Context context, String phoneNo, View.OnClickListener onClick) {
        if (null == context) return;
        dismissDialog();
        mDialog = new Dialog(context, R.style.actionSheetThemeTimer);
        mDialog.setContentView(R.layout.signup_success_dialog);
        ((FontTextView)mDialog.findViewById(R.id.tvTrainingLinkMsg)).setText(context.getString(R.string.register_tarining_link_msg, phoneNo));
        mDialog.setCancelable(false);
        mDialog.findViewById(R.id.nextBtn).setOnClickListener(onClick);
        showDialog();
    }

    public void showVerificationDialog(Context context, boolean success, View.OnClickListener onClick) {
        if (null == context) return;
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

    /**
     * This method shows a dialog to enter base url for local builds
     *
     * @param activity    calling activity
     * @param dataHandler call back handler
     */
    public void showInputAlert(final Activity activity, final StringCallBack dataHandler) {
        try {

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
                                Utils.appToast(activity, "enter your ip");
                            } else if (!Utils.isValidUrl(input.getText().toString())) {
                                Utils.appToast(activity, "enter valid url");
                            } else {
                                dataHandler.onCallBack(input.getText().toString());
                                alertDialog.dismiss();
                            }
                        }
                    });
                }
            });
            alertDialog.show();
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
     * @param negative OnClickListener for callback when Negative button is pressed
     * @param positive OnClickListener for callback when Positive button is pressed
     */
    public void showAlertDialogUrduWithTickCross(Context context, String message,
                                                 View.OnClickListener negative, View.OnClickListener positive) {
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
        showDialog();
    }

    /**
     * This method creates a dialog to show cancel notification
     *
     * @param context Calling context
     * @param message notification message to show
     * @param onClick Callback to notify that OK/Positive button is clicked
     */
    public void showCancelNotification(Context context, String message, final StringCallBack onClick) {
        if (null == context) return;
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
