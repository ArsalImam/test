package com.bykea.pk.partner.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.bykea.pk.partner.Notifications;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.NotificationData;
import com.bykea.pk.partner.services.LocationService;
import com.bykea.pk.partner.ui.calling.CallingActivity;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.StringCallBack;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.Permissions;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;
import com.bykea.pk.partner.widgets.FontUtils;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


public class BaseActivity extends AppCompatActivity {
    private static final int LOCATION_DIALOG = 221;
    private static final int REQUEST_CODE = 203;
    private static final int PERMISSION_REQUEST_CODE = 1010;
    public static final String GPS_ENABLE_EVENT = "GPS_ENABLE_EVENT";
    private Toolbar mToolbar;
    private FontTextView mTitleTv, status/*, demandBtn*/;
    private ImageView mLogo, rightIv;
    private FrameLayout frameLayout_bismilla;
    private FrameLayout frameLayout_khudaHafiz;
    private BaseActivity mCurrentActivity;
    private final EventBus mEventBus = EventBus.getDefault();
    private boolean isScreenInFront;
    private ProgressDialog progressDialog;
    private Dialog notificationDialog;
    private final String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private final String PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
    private final String CALL_STATE = Manifest.permission.CALL_PHONE;
    private RelativeLayout statusLayout;

    // A reference to the service used to get location updates.
    private LocationService mService = null;

    // Tracks the bound state of the service.
    private boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mCurrentActivity = this;
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        mEventBus.register(mCurrentActivity);
        progressDialog = new ProgressDialog(mCurrentActivity);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.internet_error));
        checkPermissions(false);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        Utils.keepScreenOn(mCurrentActivity);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
        //TODO check bindservice
        /*bindService(new Intent(this, LocationService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);*/
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
            int call = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_STATE);
            if (location != PackageManager.PERMISSION_GRANTED && phoneState != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{ACCESS_FINE_LOCATION, PHONE_STATE},
                        PERMISSION_REQUEST_CODE);
            } else if (location == PackageManager.PERMISSION_GRANTED && phoneState != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{PHONE_STATE}, PERMISSION_REQUEST_CODE);
            } else if (location != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            } else if (call != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{CALL_STATE}, PERMISSION_REQUEST_CODE);
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
        } else if (requestCode == Permissions.LOCATION_PERMISSION || requestCode == LOCATION_DIALOG) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    EventBus.getDefault().post(GPS_ENABLE_EVENT);
                    ActivityStackManager.getInstance().restartLocationService(mCurrentActivity);
                    Utils.redLog("BaseActivity", "Location Enabled");
                    break;
                case Activity.RESULT_CANCELED:
                    Utils.redLog("BaseActivity", "Location Not Enabled");
                    break;
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isScreenInFront = true;
        checkNotification();
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
            if ("android.location.GPS_ENABLED_CHANGE".equalsIgnoreCase(intent.getAction()) ||
                    "android.location.PROVIDERS_CHANGED".equalsIgnoreCase(intent.getAction())) {
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

            //  Below broadcast has send to update the loadboard bookings request
            sendBroadcast(new Intent(Constants.Broadcast.UPDATE_LOADBOARD_BOOKINGS_REQUEST));
        } else {
            showProgressDialog();
        }
    }

    private boolean isLocSettingsDialogCalled;

    public void checkGps() {
        if (!Utils.isGpsEnable()) {
            isLocSettingsDialogCalled = true;
            Dialogs.INSTANCE.showLocationSettings(mCurrentActivity, Permissions.LOCATION_PERMISSION);
        } else if (isLocSettingsDialogCalled) {
            Dialogs.INSTANCE.dismissDialog();
            isLocSettingsDialogCalled = false;
        }
    }

    /***
     * Location Dialog for Enabling location via Google Setting Permission.
     */
    public void showLocationDialog() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        builder.setAlwaysShow(true);
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Utils.redLog("RideCatActivity", "All location settings are satisfied. The client can initialize");
                // All location settings are satisfied. The client can initialize
                // location requests here.
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(mCurrentActivity, LOCATION_DIALOG);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
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
        frameLayout_bismilla = mToolbar.findViewById(R.id.logo_bismilla);
        frameLayout_khudaHafiz = mToolbar.findViewById(R.id.logo_khudaHafiz);
        mTitleTv = (FontTextView) mToolbar.findViewById(R.id.title);
        status = (FontTextView) mToolbar.findViewById(R.id.status);
//        demandBtn = (FontTextView) mToolbar.findViewById(R.id.demandBtn);
        statusLayout = (RelativeLayout) mToolbar.findViewById(R.id.statusLayout);
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

    public void setDemandButtonForBismilla(String title, View.OnClickListener listener) {
        if (null == mToolbar) getToolbar();
        statusLayout.setVisibility(View.GONE);
//        demandBtn.setVisibility(View.VISIBLE);
//        demandBtn.setText(title);
//
//        demandBtn.setOnClickListener(listener);


    }

    public void setToolbarLogo() {
        if (null == mToolbar) getToolbar();
        mLogo.setVisibility(View.VISIBLE);
        frameLayout_bismilla.setVisibility(View.GONE);
        frameLayout_khudaHafiz.setVisibility(View.GONE);
//        getToolbar().setLogo(R.drawable.top_logo);
    }

    /***
     * Display Driver Active button layout
     */
    public void showBismillah() {
        frameLayout_khudaHafiz.setVisibility(View.GONE);
        frameLayout_bismilla.setVisibility(View.VISIBLE);
    }

    /***
     * Display Driver In-Active button layout.
     */
    public void showKhudaHafiz() {
        frameLayout_khudaHafiz.setVisibility(View.VISIBLE);
        frameLayout_bismilla.setVisibility(View.GONE);
    }

    public void setToolbarLogoBismilla(View.OnClickListener listener) {
        if (null == mToolbar) getToolbar();
        frameLayout_khudaHafiz.setVisibility(View.GONE);
        frameLayout_bismilla.setVisibility(View.VISIBLE);
        mTitleTv.setVisibility(View.GONE);

        frameLayout_bismilla.setOnClickListener(listener);

//        getToolbar().setLogo(R.drawable.top_logo);
    }

    public void setToolbarLogoKhudaHafiz(View.OnClickListener listener) {
        if (null == mToolbar) getToolbar();
        frameLayout_khudaHafiz.setVisibility(View.VISIBLE);
        frameLayout_bismilla.setVisibility(View.GONE);
        mTitleTv.setVisibility(View.GONE);

        frameLayout_khudaHafiz.setOnClickListener(listener);

//        getToolbar().setLogo(R.drawable.top_logo);
    }

    public void hideToolbarLogo() {
        if (null == mToolbar) getToolbar();
        mLogo.setVisibility(View.GONE);
        frameLayout_bismilla.setVisibility(View.GONE);
        frameLayout_khudaHafiz.setVisibility(View.GONE);
//        getToolbar().setLogo(null);
    }

    public void hideStatusCompletely() {
        status.setVisibility(View.GONE);
//        demandBtn.setVisibility(View.GONE);
    }

    /***
     * Make Demand button invisible on UI just to take space on our Toolbar to make Title Align
     */
    public void makeDemandSpaceAvailableOnUI() {
//        demandBtn = mToolbar.findViewById(R.id.demandBtn);
//        if (demandBtn != null) {
//            demandBtn.setVisibility(View.INVISIBLE);
//        }
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

    public void setToolbarTitle(String toolbarTitle, String urduText) {
        if (null == mToolbar) getToolbar();
        mTitleTv.setVisibility(View.VISIBLE);
        mTitleTv.setText(toolbarTitle);
        FontTextView urduTextView = mToolbar.findViewById(R.id.tvTitleUrdu);
        urduTextView.setVisibility(View.VISIBLE);
        urduTextView.setText(urduText);
    }

    /***
     * Hide status layout on toolbar
     */
    public void hideStatusLayout() {
        RelativeLayout statusLayout = findViewById(R.id.statusLayout);
        if (statusLayout != null) {
            statusLayout.setVisibility(View.GONE);
        }
    }

    /***
     * Show status layout on toolbar
     */
    public void showStatusLayout() {
        RelativeLayout statusLayout = findViewById(R.id.statusLayout);
        if (statusLayout != null) {
            statusLayout.setVisibility(View.VISIBLE);
        }
    }

    /***
     * Hide Urdu title from toolbar
     */
    public void hideUrduTitle() {
        findViewById(R.id.tvTitleUrdu).setVisibility(View.GONE);
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
            mCurrentActivity.findViewById(R.id.statusLayout).setVisibility(View.VISIBLE);
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
        if (Constants.ON_NEW_NOTIFICATION.equalsIgnoreCase(action)) {
            checkNotification();
        } else if (Keys.UNAUTHORIZED_BROADCAST.equalsIgnoreCase(action)) {
            Utils.onUnauthorized(mCurrentActivity);
        } else if (Keys.MOCK_LOCATION.equalsIgnoreCase(action)) {
            Utils.onUnauthorizedMockLocation(mCurrentActivity);
        } else if (Keys.MULTIDELIVERY_ERROR_BORADCAST.equalsIgnoreCase(action)) {
            //MULTI DELIVERY EVENT ERROR HANDLING
            Utils.appToast(
                    mCurrentActivity.getString(R.string.error_try_again));
        } else if (action.equalsIgnoreCase(Keys.MULTIDELIVERY_BATCH_COMPLETED )) {
            Utils.multiDeliveryFreeDriverOnBatchComplete();
            ActivityStackManager
                    .getInstance()
                    .startHomeActivity(true, mCurrentActivity);
            finish();
        } else if (action.equalsIgnoreCase(Keys.MULTIDELIVERY_CANCELLED_BY_ADMIN )) {
            Utils.setCallIncomingState();
            AppPreferences.setAvailableStatus(true);
            ActivityStackManager.getInstance().startHomeActivityFromCancelTrip(true, mCurrentActivity);
            finish();
        }
    }


    private void showMessageNotification(final NotificationData notificationData) {
        dismissNotificationDialog();
        notificationDialog = new Dialog(mCurrentActivity, R.style.actionSheetTheme);
        notificationDialog.setContentView(R.layout.admin_notification_dialog);
        FontTextView msg = (FontTextView) notificationDialog.findViewById(R.id.tvMessage);
        FontTextView title = (FontTextView) notificationDialog.findViewById(R.id.title);
        msg.setText(notificationData.getMessage());
        msg.setMovementMethod(new ScrollingMovementMethod());
        title.setText(notificationData.getTitle());
        ImageView ivCross = (ImageView) notificationDialog.findViewById(R.id.ivCross);
        ivCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppPreferences.setAdminMsg(null);
                dismissNotificationDialog();
            }
        });
        FontTextView okIv = (FontTextView) notificationDialog.findViewById(R.id.ivPositive);
        boolean isUrduNotification = StringUtils.isNotBlank(notificationData.getType()) &&
                notificationData.getType().equalsIgnoreCase("urdu");
        if (isUrduNotification) {
            msg.setTypeface(FontUtils.getFonts("jameel_noori_nastaleeq.ttf"));
            title.setTypeface(FontUtils.getFonts("jameel_noori_nastaleeq.ttf"));
            okIv.setTypeface(FontUtils.getFonts("jameel_noori_nastaleeq.ttf"));
        }
        setActionButton(notificationData, okIv);
        notificationDialog.setCancelable(false);
        showNotificationDialog();
    }

    private void showImageNotification(final NotificationData notificationData) {
        dismissNotificationDialog();
        notificationDialog = new Dialog(mCurrentActivity, R.style.actionSheetTheme1);
        notificationDialog.setContentView(R.layout.admin_notification_dialog_image);
        final ImageView imageView = (ImageView) notificationDialog.findViewById(R.id.ivNotification);
        ImageView ivCross = (ImageView) notificationDialog.findViewById(R.id.ivCross);
        ivCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppPreferences.setAdminMsg(null);
                dismissNotificationDialog();
            }
        });
        FontTextView okIv = (FontTextView) notificationDialog.findViewById(R.id.ivPositive);
        boolean isUrduNotification = StringUtils.isNotBlank(notificationData.getType()) &&
                notificationData.getType().equalsIgnoreCase("urdu");
        if (isUrduNotification) {
            okIv.setTypeface(FontUtils.getFonts("jameel_noori_nastaleeq.ttf"));
        }
        setActionButton(notificationData, okIv);
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
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Utils.redLog("Error", "onBitmapFailed");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        imageView.setTag(target);
        Picasso.get().load(notificationData.getImageLink()).into(target);
    }


    private void setActionButton(final NotificationData notificationData, FontTextView okIv) {
        if (StringUtils.isNotBlank(notificationData.getShowActionButton())) {
            okIv.setText(notificationData.getShowActionButton());
            okIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onNotificationOkClick(notificationData);
                }
            });
        } else {
            okIv.setVisibility(View.GONE);
        }
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


    void setTitleCustomToolbarUrdu(String urdu) {
        FontTextView tvTitleUrdu = (FontTextView) findViewById(R.id.tvTitleUrdu);
        FontTextView tvTitle = (FontTextView) findViewById(R.id.tvTitle);

        tvTitle.setVisibility(View.GONE);
        tvTitleUrdu.setVisibility(View.VISIBLE);
        tvTitleUrdu.setText(urdu);
        setBackPress(findViewById(R.id.ivBackBtn));
    }


    public void setTitleCustomToolbarWithUrdu(String title, String name_urdu) {
        final FontTextView ivTitle = findViewById(R.id.tvTitle);
        final FontTextView tvTitleUrdu = findViewById(R.id.tvTitleUrdu);
        tvTitleUrdu.setVisibility(View.VISIBLE);
        ivTitle.setText(title);
        tvTitleUrdu.setVisibility(View.GONE);
        if (StringUtils.isNotBlank(name_urdu)) {
            tvTitleUrdu.setVisibility(View.VISIBLE);
            tvTitleUrdu.setText(name_urdu);
        }

        setBackPress(findViewById(R.id.ivBackBtn));
    }

    public void setTitleCustomToolbarWithUrduHideBackBtn(String title, String name_urdu) {
        final FontTextView ivTitle = (FontTextView) findViewById(R.id.tvTitle);
        final FontTextView tvTitleUrdu = (FontTextView) findViewById(R.id.tvTitleUrdu);
        tvTitleUrdu.setVisibility(View.VISIBLE);
        ivTitle.setText(title);
        if (StringUtils.isNotBlank(name_urdu)) {
            tvTitleUrdu.setText(name_urdu);
        }
        findViewById(R.id.ivBackBtn).setVisibility(View.INVISIBLE);
    }

    private void setBackPress(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private long mLastClickTime;

    public boolean checkClickTime() {
        long currentTime = SystemClock.elapsedRealtime();
        if (mLastClickTime != 0 && (currentTime - mLastClickTime < 1000)) {
            return true;
        }
        mLastClickTime = currentTime;
        return false;
    }


    // Monitors the state of the connection to the service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            String name = componentName.getClassName();
            if (name.endsWith("LocationTrackingService")) {
                LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
//                TrackingService.LocalBinder binder = (TrackingService.LocalBinder) service;
                mService = binder.getService();
                mBound = true;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };

}
