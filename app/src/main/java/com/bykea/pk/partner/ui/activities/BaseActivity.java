package com.bykea.pk.partner.ui.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bykea.pk.partner.Notifications;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.NotificationData;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.StringCallBack;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.Permissions;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontButton;
import com.bykea.pk.partner.widgets.FontTextView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


public class BaseActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 203;
    private Toolbar mToolbar;
    private FontTextView mTitleTv, status;
    private ImageView mLogo, rightIv;
    private BaseActivity mCurrentActivity;
    private final EventBus mEventBus = EventBus.getDefault();
    private boolean isScreenInFront;
    private ProgressDialog progressDialog;
    private Dialog notificationDialog;
    private final String ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION";
    private final String PHONE_STATE = "android.permission.READ_PHONE_STATE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentActivity = this;
        mEventBus.register(mCurrentActivity);
        progressDialog = new ProgressDialog(mCurrentActivity);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.internet_error));
        checkPermissions(false);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isScreenInFront = false;
    }

    public void setGreenActionBarTitle(String english, String urdu) {
        FontTextView tvTitle = (FontTextView) findViewById(R.id.tvTitle);
        FontTextView tvTitleUrdu = (FontTextView) findViewById(R.id.tvTitleUrdu);
        ImageView ivBackBtn = (ImageView) findViewById(R.id.ivBackBtn);

        if (StringUtils.isEmpty(urdu)) {
            tvTitleUrdu.setVisibility(View.GONE);
        } else {
            tvTitleUrdu.setText(urdu);
        }
        tvTitle.setText(english);
        ivBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private boolean checkPermissions(boolean restartLocationService) {
        boolean hasPermission = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int location = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
            int phoneState = ContextCompat.checkSelfPermission(getApplicationContext(), PHONE_STATE);
            if (location != PackageManager.PERMISSION_GRANTED && phoneState != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{ACCESS_FINE_LOCATION, PHONE_STATE}, 1010);
            } else if (location == PackageManager.PERMISSION_GRANTED && phoneState != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{PHONE_STATE}, 1010);
            } else if (location != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{ACCESS_FINE_LOCATION}, 1010);
            } else {
                hasPermission = true;
            }
        } else {
            hasPermission = true;
        }
        if (hasPermission) {
            if (restartLocationService) {
                Utils.redLog("BaseActivity", "restartLocationService");
                ActivityStackManager.getInstance().restartLocationService(mCurrentActivity);
            }
            if (mCurrentActivity instanceof SplashActivity) {
                mEventBus.post(Constants.ON_PERMISSIONS_GRANTED);
            }
        }
        return hasPermission;
    }


    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull String[] permissions, @NonNull final int[] grantResults) {
        if (mCurrentActivity != null) {
            mCurrentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (requestCode) {
                        case 1010:
                            if (grantResults.length > 1) {
                                if (grantResults[0] != PackageManager.PERMISSION_GRANTED
                                        || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                                    onPermissionResult();
                                } else {
                                    checkPermissions(true);
                                }
                            } else if (grantResults.length > 0) {
                                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                                    onPermissionResult();
                                } else {
                                    checkPermissions(true);
                                }
                            }
                            break;
                    }
                }
            });
        }
    }


    private void onPermissionResult() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION) && shouldShowRequestPermissionRationale(PHONE_STATE)) {
                Dialogs.INSTANCE.showAlertDialogNotSingleton(mCurrentActivity,
                        new StringCallBack() {
                            @Override
                            public void onCallBack(String msg) {
                                checkPermissions(true);
                            }
                        }, null, "Share Your Location"
                        , getString(R.string.permissions_location));
            } else {
                Dialogs.INSTANCE.showPermissionSettings(mCurrentActivity,
                        1010, "Permissions Required",
                        "For the best Bykea experience, please enable Permission-> Location & Phone in the application settings.");
            }
        }
    }

    public boolean checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (!Settings.canDrawOverlays(this)) {
            Utils.redLog("BaseActivity", "canDrawOverlays false");
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE);
            return false;
        } else {
            Utils.redLog("BaseActivity", "canDrawOverlays true");
            checkPermissions(true);
            return true;
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1010) {
            checkPermissions(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isScreenInFront = true;
        checkNotification();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Keys.UNAUTHORIZED_BROADCAST);
        intentFilter.addAction(Keys.MOCK_LOCATION);
        registerReceiver(myReceiver, intentFilter);
        if (!(mCurrentActivity instanceof BookingActivity)) {
            IntentFilter intentFilterNetwork = new IntentFilter();
            intentFilterNetwork.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            intentFilterNetwork.addAction("android.location.GPS_ENABLED_CHANGE");
            intentFilterNetwork.addAction("android.location.PROVIDERS_CHANGED");
            registerReceiver(networkChangeListener, intentFilterNetwork);
            checkGps();
            checkConnectivity(mCurrentActivity);
        }
    }

    private BroadcastReceiver networkChangeListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("android.location.GPS_ENABLED_CHANGE") ||
                    intent.getAction().equalsIgnoreCase("android.location.PROVIDERS_CHANGED")) {
                checkGps();
            } else {
                checkConnectivity(context);
            }
            mEventBus.post(Keys.CONNECTION_BROADCAST);
        }
    };

    public void checkConnectivity(Context context) {
        if (Connectivity.isConnectedFast(context)) {
            dismissProgressDialog();
        } else {
            showProgressDialog();
        }
    }

    private boolean isLocSettingsDialogCalled;

    public void checkGps() {
        if (!Utils.isGpsEnable(mCurrentActivity)) {
            isLocSettingsDialogCalled = true;
            Dialogs.INSTANCE.showLocationSettings(mCurrentActivity, Permissions.LOCATION_PERMISSION);
        } else if (isLocSettingsDialogCalled) {
            Dialogs.INSTANCE.dismissDialog();
            isLocSettingsDialogCalled = false;
        }
    }


    private void dismissProgressDialog() {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProgressDialog() {
        try {
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkNotification() {
        if (mCurrentActivity != null) {
            mCurrentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (StringUtils.isNotBlank(AppPreferences.getAdminMsg()) && isScreenInFront
                            && !(mCurrentActivity instanceof CallingActivity) && !(mCurrentActivity instanceof SplashActivity)) {
                        try {
                            NotificationData notificationData = new Gson().fromJson(AppPreferences.getAdminMsg(), NotificationData.class);
                            if (StringUtils.isNotBlank(notificationData.getImageLink())) {
                                showImageNotification(notificationData);
                            } else if (!(mCurrentActivity instanceof SplashActivity)) {
                                showMessageNotification(notificationData);
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
            });
        }
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mCurrentActivity != null && intent != null) {
                        if (intent.getAction().equalsIgnoreCase(Keys.UNAUTHORIZED_BROADCAST)) {
                            Utils.onUnauthorized(BaseActivity.this);
                        } else if (intent.getAction().equalsIgnoreCase(Keys.MOCK_LOCATION)) {
                            Utils.onUnauthorizedMockLocation(BaseActivity.this);
//                            Dialogs.INSTANCE.showToast(mCurrentActivity, "Please disable Mock/Fake Location Providers.");
                        }
                    }
                }
            });
        }
    };

    //setting contentLayout view to activity
    public void setContentView(int activity_main) {
        super.setContentView(activity_main);
    }

    public void setToolbar() {
        if (null == mToolbar) {
            initToolbar();
        }
        setSupportActionBar(mToolbar);
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mLogo = (ImageView) mToolbar.findViewById(R.id.logo);
        mTitleTv = (FontTextView) mToolbar.findViewById(R.id.title);
        status = (FontTextView) mToolbar.findViewById(R.id.status);
        rightIv = (ImageView) mToolbar.findViewById(R.id.rightIv);
    }

    public Toolbar getToolbar() {
        if (null == mToolbar) {
            initToolbar();
        }
        return mToolbar;
    }

    public void setBackNavigation() {
        getToolbar().setNavigationIcon(R.drawable.ic_arrow_back_48px);
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void setStatusButton(String title) {
        if (null == mToolbar) getToolbar();
        status.setVisibility(View.VISIBLE);
        status.setText(title);
        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void setToolbarLogo() {
        if (null == mToolbar) getToolbar();
        mLogo.setVisibility(View.VISIBLE);
//        getToolbar().setLogo(R.drawable.top_logo);
    }

    public void hideToolbarLogo() {
        if (null == mToolbar) getToolbar();
        mLogo.setVisibility(View.GONE);
//        getToolbar().setLogo(null);
    }

    public void hideStatusCompletely() {
        status.setVisibility(View.GONE);
    }

    public void hideToolbarBackNav() {
        getToolbar().setNavigationIcon(null);
        getToolbar().setNavigationOnClickListener(null);
    }

    public void setToolbarTitle(String toolbarTitle) {
        if (null == mToolbar) getToolbar();
        mTitleTv.setVisibility(View.VISIBLE);
        mTitleTv.setText(toolbarTitle);
    }

    public void hideToolbarTitle() {
        if (null == mToolbar) getToolbar();
        mTitleTv.setVisibility(View.INVISIBLE);
        mTitleTv.setText("");
    }

    public void showWalletIcon(final View.OnClickListener onClick) {
        if (null != rightIv) {
            rightIv.setVisibility(View.VISIBLE);
            rightIv.setImageDrawable(ContextCompat.getDrawable(mCurrentActivity, R.drawable.wallet));
            rightIv.setPadding(5, 5, 5, 5);
            rightIv.setOnClickListener(onClick);
        }
    }

    public void showMissedCallIcon(final View.OnClickListener onClick) {
        if (null != rightIv) {
            rightIv.setVisibility(View.VISIBLE);
            rightIv.setImageDrawable(ContextCompat.getDrawable(mCurrentActivity, R.drawable.miss_call_icon));
            rightIv.setPadding(5, 5, 5, 5);
            rightIv.setOnClickListener(onClick);
        }
    }

    public void hideWalletIcon() {
        if (null != rightIv) {
            rightIv.setVisibility(View.GONE);
        }
    }

    public void hideMissedCallIcon() {
        if (null != rightIv) {
            rightIv.setVisibility(View.GONE);
        }
    }


    public void setTitleSize(int dimenId) {
        mTitleTv.setTextSize(14);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {


        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.flushMixPanelEvent(mCurrentActivity);
        if (myReceiver != null) {
            unregisterReceiver(myReceiver);
        }
        if (networkChangeListener != null && !(mCurrentActivity instanceof BookingActivity)) {
            unregisterReceiver(networkChangeListener);
        }
        dismissProgressDialog();
        if (mEventBus != null) {
            mEventBus.unregister(mCurrentActivity);
        }
    }

    @Subscribe
    public void onEvent(final String action) {
        if (action.equalsIgnoreCase(Constants.ON_NEW_NOTIFICATION)) {
            checkNotification();
        }
    }


    private void showMessageNotification(final NotificationData notificationData) {
        dismissNotificationDialog();
        notificationDialog = new Dialog(mCurrentActivity, R.style.actionSheetTheme);
        notificationDialog.setContentView(R.layout.admin_notification_dialog);
        FontTextView msg = (FontTextView) notificationDialog.findViewById(R.id.tvMessage);
        FontTextView title = (FontTextView) notificationDialog.findViewById(R.id.title);
        msg.setMovementMethod(new ScrollingMovementMethod());
        msg.setText(notificationData.getMessage());
        title.setText(notificationData.getTitle());
        FontButton okIv = (FontButton) notificationDialog.findViewById(R.id.ivPositive);
        if (StringUtils.isNotBlank(notificationData.getLaunchUrl())) {
            okIv.setText("VIEW DETAILS");
        }
        okIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNotificationOkClick(notificationData);
            }
        });
        notificationDialog.setCancelable(false);
        showNotificationDialog();
    }

    private void showImageNotification(final NotificationData notificationData) {
        dismissNotificationDialog();
        notificationDialog = new Dialog(mCurrentActivity, R.style.actionSheetTheme1);
        notificationDialog.setContentView(R.layout.admin_notification_dialog_image);
        final ImageView imageView = (ImageView) notificationDialog.findViewById(R.id.ivNotification);
        FontButton okIv = (FontButton) notificationDialog.findViewById(R.id.ivPositive);
        if (StringUtils.isNotBlank(notificationData.getLaunchUrl())) {
            okIv.setText("VIEW DETAILS");
        }
        okIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNotificationOkClick(notificationData);
            }
        });
        notificationDialog.setCancelable(false);
        final Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                imageView.setImageBitmap(bitmap);
                if (!(mCurrentActivity instanceof SplashActivity)) {
                    showNotificationDialog();
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Utils.redLog("Error", "onBitmapFailed");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        imageView.setTag(target);
        Picasso.with(mCurrentActivity).load(notificationData.getImageLink()).into(target);
    }

    private void onNotificationOkClick(NotificationData notificationData) {
        Utils.openLinkInBrowser(notificationData.getLaunchUrl(), mCurrentActivity);
        AppPreferences.setAdminMsg(null);
        dismissNotificationDialog();
    }

    private void showNotificationDialog() {
        try {
            notificationDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Notifications.removeAllNotifications(mCurrentActivity);
    }

    private void dismissNotificationDialog() {
        try {
            if (notificationDialog != null) {
                notificationDialog.dismiss();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            super.onBackPressed();
        } catch (IllegalStateException ignored) {

        }
    }
}
