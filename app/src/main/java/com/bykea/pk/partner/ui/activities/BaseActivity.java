package com.bykea.pk.partner.ui.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bykea.pk.partner.Notifications;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.NotificationData;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
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
    private Toolbar mToolbar;
    private FontTextView mTitleTv, status;
    private FrameLayout frameLayout;
    private TextView mEditBtn;
    private ImageView mLogo, rightIv;
    private BaseActivity mCurrentActivity;
    private final EventBus mEventBus = EventBus.getDefault();
    private boolean isScreenInFront;
    private ProgressDialog progressDialog;
    private Dialog notificationDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentActivity = this;
        mEventBus.register(mCurrentActivity);
        progressDialog = new ProgressDialog(mCurrentActivity);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.internet_error));
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

    @Override
    protected void onResume() {
        super.onResume();
        isScreenInFront = true;
        checkNotification();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Keys.UNAUTHORIZED_BROADCAST);
        intentFilter.addAction(Keys.MOCK_LOCATION);
        registerReceiver(myReceiver, intentFilter);
        if (!(mCurrentActivity instanceof JobActivity)) {
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

    public void checkGps() {
        if (!Utils.isGpsEnable(mCurrentActivity)) {
            Dialogs.INSTANCE.showLocationSettings(mCurrentActivity, Permissions.LOCATION_PERMISSION);
        } else {
            Dialogs.INSTANCE.dismissDialog();
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
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            frameLayout = (FrameLayout) mToolbar.findViewById(R.id.frameLayout);
            mLogo = (ImageView) mToolbar.findViewById(R.id.logo);
            mTitleTv = (FontTextView) mToolbar.findViewById(R.id.title);
            status = (FontTextView) mToolbar.findViewById(R.id.status);
            rightIv = (ImageView) mToolbar.findViewById(R.id.rightIv);
        }

        setSupportActionBar(mToolbar);
    }

    public Toolbar getToolbar() {
        if (null == mToolbar) {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            frameLayout = (FrameLayout) mToolbar.findViewById(R.id.frameLayout);
            mLogo = (ImageView) mToolbar.findViewById(R.id.logo);
            mTitleTv = (FontTextView) mToolbar.findViewById(R.id.title);
            status = (FontTextView) mToolbar.findViewById(R.id.status);
            rightIv = (ImageView) mToolbar.findViewById(R.id.rightIv);
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
        if (myReceiver != null) {
            unregisterReceiver(myReceiver);
        }
        if (networkChangeListener != null && !(mCurrentActivity instanceof JobActivity)) {
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

}
