package com.bykea.pk.partner.utils;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;


import com.bykea.pk.partner.BuildConfig;
import com.bykea.pk.partner.DriverApp;
import com.bykea.pk.partner.models.data.PilotData;
import com.bykea.pk.partner.models.data.PlacesResult;
import com.bykea.pk.partner.models.data.SettingsData;
import com.bykea.pk.partner.models.data.SignUpCity;
import com.bykea.pk.partner.models.data.SignUpSettingsResponse;
import com.bykea.pk.partner.models.data.VehicleListData;
import com.bykea.pk.partner.models.response.GeocoderApi;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.ui.activities.BaseActivity;
import com.bykea.pk.partner.ui.fragments.HomeFragment;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.StringCallBack;
import com.bykea.pk.partner.ui.helpers.webview.FinestWebViewBuilder;
import com.bykea.pk.partner.widgets.FontEditText;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.ui.activities.HomeActivity;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.onesignal.OneSignal;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;


public class Utils {


    public static void redLog(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.e(tag + " : ", message);
        }
    }

    public static void appToastDebug(Context context, String message) {
        if (BuildConfig.DEBUG) {
            try {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void getImageFromGallery(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        activity.startActivityForResult(intent, Constants.PICK_IMAGE_REQUEST);
    }

    public static LinearLayoutManager newLLM(Activity activity) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        return linearLayoutManager;
    }

    public static void appToast(Context context, String message) {
        try {
            if (StringUtils.isNotBlank(message)) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getFormattedDate(String dateStr, String inFormat, String outFormat) {

        SimpleDateFormat inFormatter = new SimpleDateFormat(inFormat);
        SimpleDateFormat outFormatter = new SimpleDateFormat(outFormat);

        Date date = null;
        try {
            date = inFormatter.parse(dateStr);
            redLog(" Formatted Date", new SimpleDateFormat(outFormat).format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outFormatter.format(date);

    }

    public static String getFormattedDateUTC(String dateStr, String inFormat, String outFormat) {

        SimpleDateFormat inFormatter = new SimpleDateFormat(inFormat);
        SimpleDateFormat outFormatter = new SimpleDateFormat(outFormat);

        Date date = null;
        try {
            inFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            date = inFormatter.parse(dateStr);
            redLog(" Formatted Date", new SimpleDateFormat(outFormat).format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outFormatter.format(date);

    }

    public static boolean isLicenceExpired(String dateString) {
        SimpleDateFormat inFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date date = null;
        try {
            inFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            date = inFormatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date != null && date.getTime() < System.currentTimeMillis();
    }

    public static String getIsoDate() {
        SimpleDateFormat inFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        inFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return inFormatter.format(new Date());
    }

    public static String getIsoDate(long date) {
        SimpleDateFormat inFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        inFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return inFormatter.format(calendar.getTime());
    }

    public static String getTimeDifference(String dateStr, String format) {
        String time = dateStr;
        Date fromDate;
        long fromMillies, currentMillies;

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            fromDate = simpleDateFormat.parse(time);

            fromMillies = fromDate.getTime();
            currentMillies = System.currentTimeMillis();

            long hour = 1000 * 60 * 60;
            long day = 1000 * 60 * 60 * 24;
            long week = 1000 * 60 * 60 * 24 * 7;

            if (currentMillies - fromMillies < week) {
                if (currentMillies - fromMillies < day) {
                    /*if (currentMillies - fromMillies < hour) {
                        return ((currentMillies - fromMillies) / (1000 * 60)) + "min ago";
                    } else {
                        return ((currentMillies - fromMillies) / hour) + "hr ago";
                    }*/
                    return new SimpleDateFormat("hh:mm a").format(fromDate);
                } else {
                    return new SimpleDateFormat("EEE hh:mm a").format(fromDate);
                }
            } else {
                return new SimpleDateFormat("dd MMM hh:mm a").format(fromDate);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String getFormattedDate(String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(System.currentTimeMillis());
    }

    public static String getFormattedDate(String format, long milisec) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(milisec);
    }

    public static void logout(Context context) {
        clearData(context);
        HomeActivity.visibleFragmentNumber = 0;
        ActivityStackManager.getInstance().startLoginActivity(context);
        ((Activity) context).finish();
    }

    private static void clearData(Context context) {
//        Utils.resetMixPanel(context, false);
        FirebaseAnalytics.getInstance(context).resetAnalyticsData();
        String regId = AppPreferences.getRegId();
        double currentLat = AppPreferences.getLatitude();
        double currentLng = AppPreferences.getLongitude();
        SettingsData settingsData = AppPreferences.getSettings();
        SignUpSettingsResponse signUpSettingsResponse = (SignUpSettingsResponse) AppPreferences.getObjectFromSharedPref(SignUpSettingsResponse.class);

        AppPreferences.clear();

        if (signUpSettingsResponse != null) {
            AppPreferences.setObjectToSharedPref(signUpSettingsResponse);
        }
        if (settingsData != null) {
            settingsData.getSettings().setPartner_signup_url(StringUtils.EMPTY);
            AppPreferences.saveSettingsData(settingsData);
            if (settingsData.getSettings().getCih_range() != null) {
                AppPreferences.setCashInHandsRange(settingsData.getSettings().getCih_range());
            }
        }
        AppPreferences.setRegId(regId);
        AppPreferences.saveLocation(currentLat, currentLng);
//        WebIO.getInstance().clearConnectionData();
    }

    public static String formatDecimalPlaces(String value) {
        if (StringUtils.isBlank(value) || value.equalsIgnoreCase("0") || value.equalsIgnoreCase("0.0")
                || value.equalsIgnoreCase("0.00"))
            return "0";
        else {
            return String.format(Locale.ENGLISH, "%.1f", Double.parseDouble(value));
        }
    }

    public static String formatDecimalPlaces(String value, int decimalValue) {

        String decimalPlace = "%.0f";
        switch (decimalValue) {
            case 0:
                decimalPlace = "%.0f";
                break;
            case 1:
                decimalPlace = "%.1f";
                break;
            case 2:
                decimalPlace = "%.2f";
                break;
        }

        if (StringUtils.isBlank(value) || value.equalsIgnoreCase("0") || value.equalsIgnoreCase("0.0")
                || value.equalsIgnoreCase("0.00"))
            return "0";
        else {
            return String.format(Locale.ENGLISH, decimalPlace, Double.parseDouble(value));
        }
    }

    public static String formatETA(String value) {
        if (StringUtils.isBlank(value) || value.equalsIgnoreCase("0") || value.equalsIgnoreCase("0.0")
                || value.equalsIgnoreCase("0.00"))
            return "0";
        else {
            return "" + ((int) Double.parseDouble(value));
        }
    }

    public static String getImageLink(String link) {
        return "http://res.cloudinary.com/bykea/image/upload/" + link;
    }

    public static String getFileLink(String name) {
        return ApiTags.BASE_SERVER_URL + "/files/" + name;
    }

    public static RequestBody convertFileToRequestBody(File file) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), file);
    }

    public static RequestBody convertStringToRequestBody(String str) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), str);
    }


    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float px = dp * ((float) displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return Math.round(px);
    }

    public static int convertDipToPixels(Context context, float dips) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dips, r.getDisplayMetrics());
    }

    public static float sp2px(Resources resources, float sp) {
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    public static void formatMap(GoogleMap map) {
        if (null == map) return;
//        map.setMyLocationEnabled(false);
        map.setBuildingsEnabled(false);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setCompassEnabled(false);
//        map.setTrafficEnabled(true);
    }

    /*public static int getMapIcon(String vehicleType) {
        switch (vehicleType) {
            case "ride":
            case "Both":
                return R.drawable.ic_driver_pin;
            case "parcel":
                return R.drawable.ic_driver_pin;
            default:
                return 0;
        }
    }*/

    public static int getMapIcon(String type) {
        switch (AppPreferences.getTripStatus()) {
            case TripStatus.ON_ARRIVED_TRIP:
                if (StringUtils.containsIgnoreCase(type, "van")) {
                    return R.drawable.ic_van_location;
                } else {
                    return R.drawable.map_pin_with_passenger;
                }
            case TripStatus.ON_ACCEPT_CALL:
                if (StringUtils.containsIgnoreCase(type, "van")) {
                    return R.drawable.ic_van_location;
                } else {
                    return R.drawable.map_pin_without_passenger;
                }
            case TripStatus.ON_START_TRIP:
                if (StringUtils.containsIgnoreCase(type, "van")) {
                    return R.drawable.ic_van_location;
                } else {
                    return R.drawable.map_pin_with_passenger;
                }
            default:
                if (StringUtils.containsIgnoreCase(type, "van")) {
                    return R.drawable.ic_van_location;
                } else {
                    return R.drawable.map_pin_without_passenger;
                }
        }
    }

    public static boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static void setFullScreen(Context context) {

        // If the Android version is lower than Jellybean, use this call to hide
        // the status bar.
        if (Build.VERSION.SDK_INT <= 14) {
            ((AppCompatActivity) context).getWindow()
                    .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = ((AppCompatActivity) context).getWindow().getDecorView();
            // Hide both the navigation bar and the status bar.
            // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
            // a general rule, you should design your app to hide the status bar whenever you
            // hide the navigation bar.
            int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    public static void unlockScreen(Context context) {
        Window window = ((AppCompatActivity) context).getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    public static void setCallIncomingStateWithoutRestartingService() {
        AppPreferences.setIsOnTrip(false);
        AppPreferences.setTripStatus(TripStatus.ON_FREE);
        AppPreferences.setIncomingCall(true);
        AppPreferences.clearTrackingData();
    }

    public static void setCallIncomingState() {
        AppPreferences.setIsOnTrip(false);
        AppPreferences.setTripStatus(TripStatus.ON_FREE);
        AppPreferences.setIncomingCall(true);
        AppPreferences.clearTrackingData();
        ActivityStackManager.getInstance().restartLocationService(DriverApp.getContext());
    }

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void callingIntent(Context context, String number) {

        try {
            if (StringUtils.isBlank(number)) {
                number = StringUtils.EMPTY;
            }
            Intent callingIntent = new Intent(Intent.ACTION_VIEW);
            callingIntent.setData(Uri.parse("tel:" + number));
            ((Activity) context).startActivity(callingIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void contactViaEmail(Context context, String email) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        i.putExtra(Intent.EXTRA_SUBJECT, "Bykea Feedback");
        i.putExtra(Intent.EXTRA_TEXT, "");
        try {
            context.startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void keepScreenOn(Context context) {
        ((Activity) context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public static void hideSoftKeyboard(Context context, View v) {
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static void hideSoftKeyboard(android.support.v4.app.Fragment fragment) {
        try {
            final InputMethodManager imm = (InputMethodManager) fragment.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && fragment.getView() != null) {
                imm.hideSoftInputFromWindow(fragment.getView().getWindowToken(), 0);
            }
        } catch (Exception ignored) {
        }

    }

    public static void hideKeyboard(Activity context) {
        View view = context.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public static SSLContext getSSLContext(Context context) {
        SSLContext sslContext = null;
        try {
            // loading CAs from an InputStream
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream cert = context.getResources().openRawResource(R.raw.star_bykea_net);
            Certificate ca;
            try {
                ca = cf.generateCertificate(cert);
            } finally {
                cert.close();
            }

            // creating a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // creating a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // creating an SSLSocketFactory that uses our TrustManager
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sslContext;
    }


    public static void shareWithWhatsApp(Context context, String promo) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, promo);
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");
        try {
            context.startActivity(sendIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "WhatsApp is not installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void sendSms(Context context, String number) {
        Uri uri = Uri.parse("smsto:" + number);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", "");
        context.startActivity(intent);
    }

    public static void contactViaWhatsApp(Context context, String number) {

        Uri uri = Uri.parse("smsto:" + number);
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO, uri);
        sendIntent.putExtra("sms_body", "");
        sendIntent.putExtra("chat", true);
        sendIntent.setPackage("com.whatsapp");
        try {
            context.startActivity(sendIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "WhatsApp is not installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void checkGooglePlayServicesVersion(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
        }
    }

    public static String getTotalRAM(Context context) {

        int version = android.os.Build.VERSION.SDK_INT;
        if (version >= 16) {
            ActivityManager actManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
            actManager.getMemoryInfo(memInfo);

            double availableMemory = memInfo.availMem / 1048576L;
            double totalMemory = memInfo.totalMem / 1048576L;
            return Math.round(availableMemory) + "MB available of " + Math.round(totalMemory) + " MB";
        } else {
            return StringUtils.EMPTY;
        }

    }

    public static String getAndroidVersion() {

        return "" + android.os.Build.VERSION.SDK_INT;

    }


    public static String getBatteryPercentage(Context context) {
        Intent batteryIntent = context.getApplicationContext().registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int rawlevel = batteryIntent != null ? batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : 0;
        double scale = batteryIntent != null ? batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : 0;
        double level = -1;
        if (rawlevel >= 0 && scale > 0) {
            level = (rawlevel * 100) / scale;
        }
        return Math.round(level) + " %";
    }

    public static double getBatteryPercentageDouble(Context context) {
        Intent batteryIntent = context.getApplicationContext().registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int rawlevel = batteryIntent != null ? batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : 0;
        double scale = batteryIntent != null ? batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : 0;
        double level = -1;
        if (rawlevel >= 0 && scale > 0) {
            level = (rawlevel * 100) / scale;
        }
        return Math.round(level);
    }

    public static String getDeviceId(Context context) {
        String identifier = StringUtils.EMPTY;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (StringUtils.isNotBlank(tm.getDeviceId(1))) {
                    identifier = tm.getDeviceId(1) + ",";
                }
            } else {
                try {
                    Class<?> telephonyClass = Class.forName(tm.getClass().getName());
                    Class<?>[] parameter = new Class[1];
                    parameter[0] = int.class;
                    Method getFirstMethod = telephonyClass.getMethod("getDeviceId", parameter);
                    Object[] obParameter = new Object[1];
                    obParameter[0] = 1;
                    String second = (String) getFirstMethod.invoke(tm, obParameter);
                    Utils.redLog("SimData", "Second :" + second);
                    if (StringUtils.isNotBlank(second)) {
                        identifier = second + ",";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        identifier = StringUtils.isNotBlank(identifier) && identifier.contains(tm.getDeviceId()) ? tm.getDeviceId() : identifier + tm.getDeviceId();
        if (StringUtils.isBlank(identifier)) {
            identifier = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return "" + identifier;
    }

    public static String getSignalStrength(Context context) {
        return Connectivity.getConnectionStrength(context);
    }


    public static String getVersion(Context context) {

        String currentVersion = StringUtils.EMPTY;
        PackageManager pm = context.getPackageManager();
        PackageInfo pInfo = null;
        try {
            pInfo = pm.getPackageInfo(context.getPackageName(), 0);
            currentVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }
        return currentVersion;
    }

    public static void onUnauthorized(final BaseActivity mCurrentActivity) {
        if (mCurrentActivity != null) {
            clearData(mCurrentActivity);
            mCurrentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Dialogs.INSTANCE.showAlertDialogNotSingleton(mCurrentActivity, new StringCallBack() {
                        @Override
                        public void onCallBack(String msg) {
                            ActivityStackManager.getInstance().startLoginActivity(mCurrentActivity);
                            mCurrentActivity.finish();
                        }
                    }, null, "UnAuthorized", "Session Expired. Please Log in again.");
                }
            });
        }
    }

    public static void onUnauthorizedMockLocation(final BaseActivity mCurrentActivity) {
        if (mCurrentActivity != null) {
            clearData(mCurrentActivity);
            mCurrentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Dialogs.INSTANCE.showAlertDialogNotSingleton(mCurrentActivity, new StringCallBack() {
                        @Override
                        public void onCallBack(String msg) {
                            ActivityStackManager.getInstance().startLoginActivity(mCurrentActivity);
                            mCurrentActivity.finish();
                        }
                    }, null, "UnAuthorized", "We strictly discourage usage of Fake GPS, please disable this and login again. Thank you! ");
                }
            });
        }
    }

    /*
    * This will sync Device Time and Server Time. We know that Device time can be changed easily so
    * we will calculate time difference between server and device
    * */
    public static void saveServerTimeDifference(long serverTime) {
        long currentTime = System.currentTimeMillis();
        AppPreferences.setServerTimeDifference(
                (((AppPreferences.getLocationEmitTime() - serverTime) + (currentTime - serverTime)
                        - (currentTime - AppPreferences.getLocationEmitTime())) / 2));

    }

    public static boolean isNotDelayed(long sentTime) {
        long diff = (System.currentTimeMillis() - AppPreferences.getServerTimeDifference())
                - sentTime;
        Utils.redLog("Time Diff Call", "" + diff);
        AppPreferences.setTripDelay(diff);
        return diff <= 6000;
    }

    public static boolean isCancelAfter5Min() {
        long diff = (System.currentTimeMillis() - (AppPreferences.getServerTimeDifference() + AppPreferences.getCallData().getSentTime()));
        Utils.redLog("Time Diff Call", "" + diff);
        long cancel_time = 5;
        if (AppPreferences.getSettings() != null && AppPreferences.getSettings().getSettings() != null &&
                StringUtils.isNotBlank(AppPreferences.getSettings().getSettings().getCancel_time())) {
            cancel_time = Long.parseLong(AppPreferences.getSettings().getSettings().getCancel_time());
        }
        return diff >= cancel_time * 60000;
    }

    public static boolean isCancelAfterXMin() {
        long diff = (System.currentTimeMillis() - (AppPreferences.getServerTimeDifference() + AppPreferences.getCallData().getSentTime()));
        Utils.redLog("Time Diff Call", "" + diff);
        long cancel_time = 5;
        if (AppPreferences.getSettings() != null && AppPreferences.getSettings().getSettings() != null &&
                StringUtils.isNotBlank(AppPreferences.getSettings().getSettings().getCancel_time())) {
            cancel_time = Long.parseLong(AppPreferences.getSettings().getSettings().getCancel_time());
        }
        return diff >= cancel_time * 60000;
    }

    /*
        public static ArrayList<PlacesResult> getCities() {
            PlacesResult rawalpindi = new PlacesResult("Rawalpindi", StringUtils.EMPTY, Constants.rwpLat, Constants.rwpLng);
            PlacesResult lahore = new PlacesResult("Lahore", StringUtils.EMPTY, Constants.lhrLat, Constants.lhrLng);
            PlacesResult karachi = new PlacesResult("Karachi", StringUtils.EMPTY, Constants.khiLat, Constants.khiLng);
            ArrayList<PlacesResult> cities = new ArrayList<>();
            cities.add(rawalpindi);
            cities.add(lahore);
            cities.add(karachi);
            return cities;
        }*/
    public static ArrayList<PlacesResult> getCities() {
        ArrayList<PlacesResult> availableCities = AppPreferences.getAvailableCities();
        if (availableCities.size() > 0) {
            return availableCities;
        } else {
            PlacesResult rawalpindi = new PlacesResult("Rawalpindi", StringUtils.EMPTY, Constants.rwpLat, Constants.rwpLng);
            PlacesResult lahore = new PlacesResult("Lahore", StringUtils.EMPTY, Constants.lhrLat, Constants.lhrLng);
            PlacesResult karachi = new PlacesResult("Karachi", StringUtils.EMPTY, Constants.khiLat, Constants.khiLng);
            availableCities.add(karachi);
            availableCities.add(lahore);
            availableCities.add(rawalpindi);
            return availableCities;
        }
    }

    public static int getCurrentCityIndex() {
        LatLng currentLatLng = new LatLng(AppPreferences.getLatitude(), AppPreferences.getLongitude());
        ArrayList<PlacesResult> cities = getCities();
        float shortestDistance = 0;
        int index = 0;
        for (int i = 0; i < cities.size(); i++) {
            float distance = Utils.calculateDistance(currentLatLng.latitude, currentLatLng.longitude, cities.get(i).latitude, cities.get(i).longitude);
            if (shortestDistance == 0 || distance < shortestDistance) {
                shortestDistance = distance;
                index = i;
            }
        }
        return index;
    }

    public static int getCurrentCityIndex(ArrayList<SignUpCity> cities) {
        LatLng currentLatLng = new LatLng(AppPreferences.getLatitude(), AppPreferences.getLongitude());
        float shortestDistance = 0;
        int index = 0;
        for (int i = 0; i < cities.size(); i++) {
            float distance = Utils.calculateDistance(currentLatLng.latitude, currentLatLng.longitude, cities.get(i).getGps().get(0), cities.get(i).getGps().get(1));
            if (shortestDistance == 0 || distance < shortestDistance) {
                shortestDistance = distance;
                index = i;
            }
        }
        return index;
    }


    public static float calculateDistance(double newLat, double newLon, double prevLat, double prevLon) {
        Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        newLocation.setLatitude(newLat);
        newLocation.setLongitude(newLon);

        Location prevLocation = new Location(LocationManager.GPS_PROVIDER);
        prevLocation.setLatitude(prevLat);
        prevLocation.setLongitude(prevLon);
        return newLocation.distanceTo(prevLocation);
    }

    public static String getLocationAddress(String lat, String lng, Activity activity) {

        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(Double.valueOf(lat), Double.valueOf(lng), 1);
            return addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String calculateDistanceInKm(double newLat, double newLon, double prevLat, double prevLon) {
        return "" + Math.round(((calculateDistance(newLat,
                newLon, prevLat,
                prevLon)) / 1000) * 10.0) / 10.0;
    }

    public static String upto2decimalPlaces(double value) {
        return "" + Math.round(value * 100.0) / 100.0;
    }


    public static boolean isValidNumber(Context context, FontEditText view) {
        String number = view.getText().toString();
        if (StringUtils.isBlank(number)) {
            view.setError(context.getString(R.string.error_phone_number_1));
            view.requestFocus();
            return false;
        } else if (!number.startsWith("03")) {
            view.setError(context.getString(R.string.error_phone_number_1));
            view.requestFocus();
            return false;
        } else if (number.length() < 11) {
            view.setError(context.getString(R.string.error_phone_number_1));
            view.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    public static boolean isValidNumber(FontEditText view) {
        String number = view.getText().toString();
        if (StringUtils.isBlank(number)) {
            return false;
        } else if (!number.startsWith("03")) {
            return false;
        } else if (number.length() < 11) {
            return false;
        } else {
            return true;
        }
    }


    public static String phoneNumberForServer(String number) {
        if (StringUtils.isNotBlank(number) && number.length() > 1) {
            return "92" + number.substring(1);
        } else {
            return StringUtils.EMPTY;
        }
    }

    public static String phoneNumberToShow(String phone) {
        if (StringUtils.isNotBlank(phone)) {
            return "0" + phone.substring(2);
        } else {
            return StringUtils.EMPTY;
        }
    }

    public static String formatAddress(String place) {
        return StringUtils.isNotBlank(place) ? place.replace(", Pakistan", "").replace(", Punjab", "")
                .replace(", Sindh", "").replace(", Islamabad Capital Territory", "").replace(", Islamic Republic of Pakistan", "") : StringUtils.EMPTY;
    }

    /*
    * Returns API key for Google GeoCoder API if required.
    * Will return Empty String if there's no error in Last
    * Request while using API without any Key.
    * */
    public static String getApiKeyForGeoCoder() {
        return AppPreferences.isGeoCoderApiKeyRequired() ? Constants.GOOGLE_PLACE_SERVER_API_KEY : StringUtils.EMPTY;
    }

    /*
    * Returns API key for Google Directions API if required.
    * Will return Empty String if there's no error in Last
    * Request while using API without any Key.
    * */
    public static String getApiKeyForDirections(Context context) {
        if (AppPreferences.isDirectionsApiKeyRequired()) {
            if (isDirectionsApiKeyCheckRequired()) {
                AppPreferences.setDirectionsApiKeyRequired(false);
                return StringUtils.EMPTY;
            } else {
                return Constants.GOOGLE_PLACE_SERVER_API_KEY;
            }
        } else {
            return StringUtils.EMPTY;
        }
    }

    /*
    * Returns true if Last API call was more than 1 min ago
    * */
    public static boolean isDirectionApiCallRequired() {
        return (System.currentTimeMillis() - AppPreferences.getLastDirectionsApiCallTime()) >= 30000;
    }

    public static boolean isStatsApiCallRequired() {
        return AppPreferences.isStatsApiCallRequired() ||
                ((System.currentTimeMillis() - AppPreferences.getLastStatsApiCallTime()) >= (60000 * 4 * 60)); //4 hours
    }

    public static String getTripDistance() {
        return "" + (Math.round((AppPreferences.getDistanceCoveredInMeters() / 1000) * 10.0) / 10.0);
    }

    public static String getTripTime() {
        long diff = System.currentTimeMillis() - AppPreferences.getStartTripTime();
        return diff > 0 ? "" + (1 + (int) TimeUnit.MILLISECONDS.toMinutes(diff)) : "N/A";
    }

    public static boolean isAppVersionCheckRequired() {
        long lastApiCallTime = AppPreferences.getVersionCheckTime();
        return lastApiCallTime == 0 || (System.currentTimeMillis() - lastApiCallTime) >= Constants.MILISEC_IN_HALF_DAY;
    }

    public static long getTimeInMiles(String dateString) {
        SimpleDateFormat inFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date date = null;
        try {
            inFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            date = inFormatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date != null ? date.getTime() : 0;
    }

    public static boolean isGpsEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static void loadImgPicasso(Context context, ImageView imageView, int placeHolder, String link) {
        Picasso.get().load(link)
                .fit().centerInside()
                .placeholder(placeHolder)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        redLog("Picasso", "onSuccess");
                    }

                    @Override
                    public void onError(Exception e) {
                        redLog("Picasso", "onError");
                    }
                });
    }

    public static void loadImgPicasso(Context context, ImageView imageView, String link) {
        if (StringUtils.isNotBlank(link)) {
            Picasso.get().load(link)
                    .fit().centerInside()
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            redLog("Picasso", "onSuccess");
                        }

                        @Override
                        public void onError(Exception e) {
                            redLog("Picasso", "onError");
                        }
                    });
        }
    }

    public static boolean isMockLocation(Location location, Context context) {
        boolean isMock;
        if (android.os.Build.VERSION.SDK_INT >= 18) {
            isMock = location.isFromMockProvider();
        } else {
            isMock = !Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0")
                    && areThereMockPermissionApps(context);
        }
        if (BuildConfig.DEBUG) {
            isMock = false;
        }
        return isMock;
    }


    public static void phoneCall(Activity activity, String phone) {

        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        activity.startActivity(intent);

    }

    public static boolean areThereMockPermissionApps(Context context) {
        int count = 0;

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages =
                pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName,
                        PackageManager.GET_PERMISSIONS);

                // Get Permissions
                String[] requestedPermissions = packageInfo.requestedPermissions;

                if (requestedPermissions != null) {
                    for (String requestedPermission : requestedPermissions) {
                        if (requestedPermission
                                .equals("android.permission.ACCESS_MOCK_LOCATION")
                                && !applicationInfo.packageName.equals(context.getPackageName())
                                && !applicationInfo.packageName.contains("camera")) {
                            count++;
                            break;
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Utils.redLog("Got exception ", e.getMessage());
            }
        }

        if (count > 0)
            return true;
        return false;
    }


    /*
    * - if same location coordinates then don't consider these lat lng
    * - if distance is less than 6 meter then don't consider these lat lng to avoid coordinate fluctuation
    * - Check if its time difference w.r.t last coordinate is
    * greater than minimum time a bike should take to cover that distance if that bike is traveling
    * at max 80KM/H to avoid bad/fake coordinates
    * */
    public static boolean isValidLocation(double newLat, double newLon, double prevLat, double prevLon) {
        boolean shouldConsiderLatLng = newLat != prevLat && newLon != prevLon;
        if (shouldConsiderLatLng) {
            float distance = calculateDistance(newLat, newLon, prevLat, prevLon);
            if (distance > 6) {
                long timeDifference = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - AppPreferences.getPrevDistanceTime());
                long minTime = (long) ((distance) / (80 * 1000) * 3600);
                if (timeDifference > minTime) {
                    AppPreferences.setDistanceCoveredInMeters(distance);
                    return true;
                } else {
                    return false;
                }
            } else {
                AppPreferences.setPrevDistanceTime();
                return false;
            }
        } else {
            AppPreferences.setPrevDistanceTime();
            return false;
        }
    }

    public static boolean isValidLocation(/*double newLat, double newLon, double prevLat, double prevLon, */float distance) {
//        boolean shouldConsiderLatLng = newLat != prevLat && newLon != prevLon;
//        if (shouldConsiderLatLng) {
//            if (distance > 6) {
        long timeDifference = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - AppPreferences.getPrevDistanceTime());
        long minTime = (long) ((distance) / (80 * 1000) * 3600);
        Utils.redLog("isValidLocation", "minTime ->" + minTime + " : timeDiff ->" + timeDifference);
        if (timeDifference > minTime) {
            AppPreferences.setDistanceCoveredInMeters(distance);
            return true;
        } else {
            return false;
        }
//            } else {
//                AppPreferences.setPrevDistanceTime(DriverApp.getContext());
//                return false;
//            }
//        } else {
//            AppPreferences.setPrevDistanceTime(DriverApp.getContext());
//            return false;
//        }
    }

    public static double calculateDistance(List<LatLng> mRouteLatLng) {
        double distance = 0d;
        LatLng lastLatLng = null;
        for (LatLng latLng : mRouteLatLng) {
            if (lastLatLng != null) {
                distance = distance + calculateDistance(latLng.latitude, latLng.longitude, lastLatLng.latitude, lastLatLng.longitude);
            }
            lastLatLng = latLng;
        }
        return (Math.round((distance / 1000) * 10.0) / 10.0);

    }

    public static boolean isGeoCoderApiKeyCheckRequired() {
        long lastApiCallTime = AppPreferences.getGeoCoderApiKeyCheckTime();
        return lastApiCallTime == 0 || (System.currentTimeMillis() - lastApiCallTime) >= Constants.MILLI_SEC_IN_1_AND_HALF_DAYS;
    }

    private static boolean isDirectionsApiKeyCheckRequired() {
        long lastApiCallTime = AppPreferences.getDirectionsApiKeyCheckTime();
        return lastApiCallTime == 0 || (System.currentTimeMillis() - lastApiCallTime) >= Constants.MILLI_SEC_IN_1_AND_HALF_DAYS;
    }

    public static boolean isGetCitiesApiCallRequired() {
        if (AppPreferences.getAvailableCities().size() == 0) {
            return true;
        } else if (AppPreferences.getCitiesApiCallTime() == 0) {
            return true;
        } else if ((System.currentTimeMillis() - AppPreferences.getCitiesApiCallTime()) >= Constants.MILISEC_IN_DAY) {
            return true;
        } else {
            return false;
        }
    }

    public static String getCommaFormattedAmount(long amount) {
        return NumberFormat.getNumberInstance(Locale.US).format(amount) + " ";

    }

    public static String getCommaFormattedAmount(String amount) {
        if (StringUtils.isNotBlank(amount) && amount.matches(Constants.REG_EX_DIGIT)) {
            return NumberFormat.getNumberInstance(Locale.US).format(Long.parseLong(amount)) + " ";
        } else {
            return StringUtils.EMPTY;
        }

    }

    public static String getUTCCurrentDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(System.currentTimeMillis());
    }

    public static String getUTCDate(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(time);
    }

    public static void setOneSignalTag(String KEY, String VALUE) {
        OneSignal.sendTag(KEY, VALUE);
    }


    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }


    public static void setOneSignalPlayerId() {
        if (StringUtils.isBlank(AppPreferences.getOneSignalPlayerId())) {
            OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
                @Override
                public void idsAvailable(String userId, String registrationId) {
                    if (StringUtils.isNotBlank(userId)) {
                        Utils.redLog("OneSignal P_ID", userId);
                        AppPreferences.setOneSignalPlayerId(userId);
                    }
                }
            });
        }
    }

    public static String getCloudinaryLink(String icon) {
//        return "http://res.cloudinary.com/bykea/image/upload/w_" + getDimension(context) + ",h_" + getDimension(context) + ",c_scale/" + icon;
        return "http://res.cloudinary.com/bykea/image/upload/" + icon;
    }

    private static String getDimension(Context context) {
        String dimension = "100";
        switch (context.getResources().getDisplayMetrics().densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                dimension = "36";
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                dimension = "48";
                break;
            case DisplayMetrics.DENSITY_HIGH:
                dimension = "72";
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                dimension = "96";
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                dimension = "144";
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                dimension = "192";
                break;
        }
        return dimension;
    }


    public static void logFireBaseEvent(Context context, String userId, String EVENT, JSONObject data) {
        EVENT = EVENT.toLowerCase().replace("-", "_").replace(" ", "_");
        if (EVENT.length() > 40) {
            EVENT = EVENT.substring(EVENT.length() - 40, EVENT.length());
        }
        int count = 0;
        Bundle bundle = new Bundle();
        Iterator iterator = data.keys();
        while (iterator.hasNext()) {
            //Firebase can have max 10 TEXT properties
            if (count == 10) {
                break;
            }
            count++;
            String key = (String) iterator.next();
            String value = null;
            try {
                value = data.getString(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            key = key.toLowerCase().replace("-", "_").replace(" ", "_");
            bundle.putString(key, value);
        }
        FirebaseAnalytics.getInstance(context).setUserId(userId);
        FirebaseAnalytics.getInstance(context).logEvent(EVENT, bundle);
    }

    public static void startCustomWebViewActivity(AppCompatActivity context, String link, String title) {
        if (Connectivity.isConnected(context)) {
            if (StringUtils.isNotBlank(link)) {
                new FinestWebViewBuilder.Builder(context).showIconMenu(false).showUrl(false)
                        .toolbarScrollFlags(0)
                        .toolbarColor(ContextCompat.getColor(context, R.color.white))
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                        .showIconForward(false).showIconBack(false)
                        .updateTitleFromHtml(false)
                        .showSwipeRefreshLayout(false)
                        .webViewSupportZoom(true)
                        .webViewBuiltInZoomControls(true)
                        .titleDefault(StringUtils.capitalize(title))
                        .show(link);
            }
        } else {
            appToast(context, context.getResources().getString(R.string.internet_error));
        }
    }

    public static int getDaysInBetween(long newerDate, long olderDate) {
        return Math.round((newerDate - olderDate)
                / (1000 * 60 * 60 * 24));
    }

    public static String getFormattedNumber(int number) {
        return NumberFormat.getNumberInstance(Locale.US).format(number) + "";
    }

    public static void openLinkInBrowser(String link, Context context) {
        if (StringUtils.isNotBlank(link)) {
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                context.startActivity(browserIntent);
            } catch (Exception ignored) {
            }
        }
    }


    private static void setMixPanelPeople(MixpanelAPI mMixpanel) {
        final MixpanelAPI.People people = mMixpanel.getPeople();

// Update the basic data in the user's People Analytics record.
// Unlike events, People Analytics always stores the most recent value
// provided.
        PilotData user = AppPreferences.getPilotData();
        if (user.getFullName().contains(" ") && user.getFullName().split(" ").length > 1) {
            people.setOnce("$first_name", user.getFullName().split(" ")[0]);
            people.setOnce("$last_name", user.getFullName().split(" ")[1]);
        } else {
            people.setOnce("$first_name", user.getFullName());
        }
        if (StringUtils.isNotBlank(user.getEmail())) {
            people.setOnce("$email", user.getEmail());
        }
        people.setOnce("$phone", user.getPhonePlusSign());
    }

    public static void resetMixPanel(Context context, boolean isFromSplash) {
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(context, Constants.MIX_PANEL_API_KEY);
        if (isFromSplash) {
            if (!mixpanel.getDistinctId().equalsIgnoreCase(AppPreferences.getLastMixPanelDistId())) {
                mixpanel.reset();
                AppPreferences.setLastMixPanelDistId(mixpanel.getDistinctId());
            }
        } else {
            mixpanel.reset();
        }
    }

    public static void setMixPanelUserId(Context context) {
        if (AppPreferences.isLoggedIn()) {
            MixpanelAPI mixpanel = MixpanelAPI.getInstance(context, Constants.MIX_PANEL_API_KEY);
            if (StringUtils.isBlank(mixpanel.getDistinctId())
                    || !mixpanel.getDistinctId().equalsIgnoreCase(AppPreferences.getPilotData().getId())) {
//                mixpanel.alias(AppPreferences.getUser().get_id(), null);
                mixpanel.identify(AppPreferences.getPilotData().getId());
                mixpanel.getPeople().identify(AppPreferences.getPilotData().getId());
                mixpanel.getPeople().initPushHandling(Constants.GCM_PROJECT_NO);
                setMixPanelPeople(mixpanel);
                AppPreferences.setLastMixPanelDistId(mixpanel.getDistinctId());
            }
            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
            mFirebaseAnalytics.setUserId(AppPreferences.getPilotData().getId());
            mFirebaseAnalytics.setUserProperty("name", AppPreferences.getPilotData().getFullName());
            mFirebaseAnalytics.setUserProperty("phone", AppPreferences.getPilotData().getPhonePlusSign());
        }
    }

    public static MixpanelAPI getMixPanelInstance(Context context) {
        return MixpanelAPI.getInstance(context, Constants.MIX_PANEL_API_KEY);
    }


    /*
    *  Flush Mixpanel Event in onDestroy()
    * */
    public static void logEvent(Context context, String userID, String EVENT, JSONObject data) {
        MixpanelAPI mixpanelAPI = MixpanelAPI.getInstance(context, Constants.MIX_PANEL_API_KEY);
        mixpanelAPI.identify(userID);
        mixpanelAPI.getPeople().identify(userID);
        mixpanelAPI.track(EVENT, data);

        logFireBaseEvent(context, userID, EVENT, data);
    }

    public static void flushMixPanelEvent(Context context) {
        MixpanelAPI.getInstance(context, Constants.MIX_PANEL_API_KEY).flush();
    }

    public static void animateHeight(final View v, int duration, int targetHeight) {
        int prevHeight = v.getHeight();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    public static String getCurrentLocation() {
        String currentLocaiton;
        if (AppPreferences.getLatitude() != 0 && AppPreferences.getLongitude() != 0 && AppPreferences.getLatitude() != 0.0 && AppPreferences.getLongitude() != 0.0) {
            currentLocaiton = AppPreferences.getLatitude() + "," + AppPreferences.getLongitude();
        } else {
            currentLocaiton = StringUtils.EMPTY;
        }
        return currentLocaiton;
    }


    public static void deleteFile(File file) {
        try {
            file.delete();
        } catch (Exception ignored) {
        }
    }

    public static void clearDirectory(File file) {
        try {
            if (file.isDirectory()) {
                String[] children = file.list();
                for (String aChildren : children) {
                    new File(file, aChildren).delete();
                }
            }
        } catch (Exception ignored) {
        }
    }

    public static void unbindDrawables(View view) {
        if (view != null) {
            if (view.getBackground() != null) {
                view.getBackground().setCallback(null);
            }
            if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    unbindDrawables(((ViewGroup) view).getChildAt(i));
                }
                ((ViewGroup) view).removeAllViews();
            }
        }
    }

    public static boolean isDeliveryService(String callType) {
        return StringUtils.containsIgnoreCase(callType, "Send")
                || StringUtils.containsIgnoreCase(callType, "Delivery");
    }

    public static boolean isRideService(String callType) {
        return StringUtils.containsIgnoreCase(callType, "Ride");
    }

    public static boolean isCourierService(String callType) {
        return StringUtils.containsIgnoreCase(callType, "Courier");
    }

    public static boolean isPurchaseService(String callType) {
        return StringUtils.containsIgnoreCase(callType, "Bring")
                || StringUtils.containsIgnoreCase(callType, "Purchase");
    }

    public static boolean isValidTopUpAmount(String amount, boolean isCourierType) {
        boolean valid = true;
        if (isCourierType) {
            if (StringUtils.isNotBlank(amount) && AppPreferences.getSettings() != null
                    && StringUtils.isNotBlank(AppPreferences.getSettings().getSettings().getVan_partner_topup_limit())
                    && Integer.parseInt(amount) > Integer.parseInt(AppPreferences.getSettings().getSettings().getVan_partner_topup_limit())) {
                valid = false;
            }
        } else {
            if (StringUtils.isNotBlank(amount) && AppPreferences.getSettings() != null
                    && StringUtils.isNotBlank(AppPreferences.getSettings().getSettings().getPartner_topup_limit())
                    && Integer.parseInt(amount) > Integer.parseInt(AppPreferences.getSettings().getSettings().getPartner_topup_limit())) {
                valid = false;
            }
        }
        return valid;
    }

    public static boolean isSkipDropOff(NormalCallData callData) {
        if (StringUtils.isNotBlank(callData.getEndAddress())) {
            return false;
        }
        return true;
    }


    public static class AudioTime implements Serializable {
        private String mFormat = "%02d:%02d:%02d";
        private int mHour = 0;
        private int mMinute = 0;
        private int mSecond = 0;

        public AudioTime() {

        }

        public AudioTime(long seconds) {
            setTimeInSecond(seconds);
        }

        /**
         * get time in the format of "HH:MM:SS"
         *
         * @return
         */
        public String getTime() {

            return String.format(mFormat, mHour, mMinute, mSecond);
        }

        public void setTimeInSecond(long seconds) {
            mSecond = (int) (seconds % 60);
            long m = seconds / 60;
            mMinute = (int) (m % 60);
            mHour = (int) (m / 60);

        }

        public void add(int seconds) {
            mSecond += seconds;
            if (mSecond >= 60) {
                mSecond %= 60;
                mMinute++;

                if (mMinute >= 60) {
                    mMinute %= 60;
                    mHour++;
                }
            }
        }

    }

    public static Integer getServiceIcon(NormalCallData callData) {
        String callType = callData.getCallType().replace(" ", StringUtils.EMPTY).toLowerCase();
        switch (callType) {
            case "parcel":
            case "send":
            case "delivery":
                return R.drawable.bhejdo;
            case "bring":
            case "purchase":
                return R.drawable.lay_ao;
            case "ride":
                return R.drawable.ride;
            case "top-up":
                return R.drawable.top_up;
            case "utilitybill":
                return R.drawable.utility_bill;
            case "deposit":
                return R.drawable.jama_karo;
            case "carryvan":
                return R.drawable.carry_van;
            case "courier":
                return R.drawable.courier;
            default:
                return R.drawable.ride;
        }
    }


    public static boolean isFcmIdUpdateRequired(boolean isLoggedIn) {
        boolean required = false;
        if (isLoggedIn && StringUtils.isNotBlank(AppPreferences.getRegId())
                && AppPreferences.getPilotData() != null && !AppPreferences.getRegId().equalsIgnoreCase(AppPreferences.getPilotData().getReg_id())) {
            required = true;
        }
        return required;
    }

    public static void watchYoutubeVideo(Context context, String id) {
        try {
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + id));
            context.startActivity(webIntent);
        }
    }

    public static String getServiceIcon(String serviceName) {
        String icon = StringUtils.EMPTY;
        ArrayList<VehicleListData> mList =
                AppPreferences.getSettings().getRegion_services();
        if (mList != null && mList.size() > 0) {
            for (VehicleListData data : mList) {
                if (data.getName().equalsIgnoreCase(serviceName)) {
                    icon = data.getIcon();
                    break;
                }
            }
        }
        return icon;
    }

    public static Drawable changeDrawableColor(Context context, int d, int color) {
        Drawable wrappedDrawable = DrawableCompat.wrap(ContextCompat.getDrawable(context, d).mutate());
        DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(context, color));
        return wrappedDrawable;
    }

    public static boolean isConnected(Context context, boolean showToast) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        if (null != networkInfo && networkInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            if (showToast) {
                appToast(context, context.getString(R.string.error_internet_connectivity));
            }
            return false;
        }
    }

    public static void addRecentPlace(PlacesResult placesResult) {
        String recentResult = placesResult.address;
        int lastIndex = recentResult.lastIndexOf(',');
        String name = lastIndex > 0 && lastIndex < recentResult.length() ? recentResult.substring(0, lastIndex) : recentResult;
        PlacesResult placesResult1 = new PlacesResult(name, recentResult, placesResult.latitude, placesResult.longitude);
        AppPreferences.setRecentPlaces(placesResult1);
    }

    public static boolean isTimeWithInNDay(long time, double n) {
        return (System.currentTimeMillis() - time) < (n * Constants.MILISEC_IN_DAY);
    }


    public static void scrollToBottom(final ScrollView mainScrollView) {
        scrollThisToBottom(mainScrollView);
        mainScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                mainScrollView.getWindowVisibleDisplayFrame(r);
                int screenHeight = mainScrollView.getRootView().getHeight();
                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;
                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened
                    scrollThisToBottom(mainScrollView);
                }
            }
        });
    }

    public static void scrollToBottom(final NestedScrollView mainScrollView) {
//        if (mainScrollView.getChildAt(0).getBottom() > (mainScrollView.getHeight() + mainScrollView.getScrollY())) {
        scrollThisToBottom(mainScrollView);
        mainScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                mainScrollView.getWindowVisibleDisplayFrame(r);
                int screenHeight = mainScrollView.getRootView().getHeight();
                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;
                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened
                    scrollThisToBottom(mainScrollView);
                }
            }
        });
//        }
    }

    public static void scrollToTop(final ScrollView mainScrollView) {
        scrollThisToTop(mainScrollView);
        mainScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                mainScrollView.getWindowVisibleDisplayFrame(r);
                int screenHeight = mainScrollView.getRootView().getHeight();
                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;
                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened
                    scrollThisToTop(mainScrollView);
                }
                mainScrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private static void scrollThisToBottom(final ScrollView mainScrollView) {
        mainScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mainScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 600);
    }

    private static void scrollThisToBottom(final NestedScrollView mainScrollView) {
        mainScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mainScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 600);
    }

    private static void scrollThisToTop(final ScrollView mainScrollView) {
        mainScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mainScrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        }, 600);
    }

    public static boolean hasLocationCoordinates() {
        if (AppPreferences.getLatitude() == 0d || AppPreferences.getLongitude() == 0d) {
            return false;
        } else {
            return true;
        }
    }

    public static void loadImgURL(ImageView imageView, String link) {
        if (StringUtils.isNotBlank(link)) {
            Picasso.get().load(link)
                    .fit().centerInside()
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Utils.redLog("loadImgURL", "onSuccess");
                        }

                        @Override
                        public void onError(Exception e) {
                            Utils.redLog("loadImgURL", "onError");
                        }
                    });

        }
    }


    public static void fadeOutAndHideImage(final ImageView img) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(250);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                img.setVisibility(View.GONE);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }
        });

        img.startAnimation(fadeOut);
    }


    public static void playVideo(final BaseActivity context, final String VIDEO_ID, ImageView ivThumbnail, ImageView ytIcon, YouTubePlayerSupportFragment playerFragment) {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            // to handle app crash caused by some bug in YouTube App. https://stackoverflow.com/questions/48674311/exception-java-lang-noclassdeffounderror-pim
            //TODO: Remove it when crash is resolved in latest YouTube App
            watchYoutubeVideo(context, VIDEO_ID);
            return;
        }
        fadeOutAndHideImage(ivThumbnail);
        fadeOutAndHideImage(ytIcon);
        if (playerFragment.getView() != null) {
            playerFragment.getView().setVisibility(View.VISIBLE);
        }
        playerFragment.initialize(context.getString(R.string.google_api_client_key), new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                if (!wasRestored) {
                    player.setShowFullscreenButton(false);
                    // loadVideo() will auto play video
                    // Use cueVideo() method, if you don't want to play it automatically
                    player.loadVideo(VIDEO_ID);

                    player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
                if (errorReason.isUserRecoverableError()) {
                    errorReason.getErrorDialog(context, 1).show();
                } else {
                    String errorMessage = errorReason.toString();
                    Utils.appToast(context, errorMessage);
                }
            }
        });
    }

    public static void initPlayerFragment(final YouTubePlayerSupportFragment playerFragment, ImageView ytIcon, final ImageView ivThumbnail, final String VIDEO_ID) {
        if (playerFragment.getView() != null) {
            playerFragment.getView().setVisibility(View.INVISIBLE);
        }
        ytIcon.setVisibility(View.VISIBLE);

        if (playerFragment.getView() != null) {
            final ViewTreeObserver layoutObserver = playerFragment.getView().getViewTreeObserver();
            layoutObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (playerFragment.getView() != null) {
                        playerFragment.getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        ivThumbnail.getLayoutParams().height = playerFragment.getView().getHeight();
                        ivThumbnail.requestLayout();
                        Utils.loadImgURL(ivThumbnail, "https://img.youtube.com/vi/" + VIDEO_ID + "/0.jpg");
                        playerFragment.getView().setVisibility(View.INVISIBLE);
                    }
                }
            });

        }
    }

    public static void playVideo(final BaseActivity context, final String VIDEO_ID, ImageView ivThumbnail, ImageView ytIcon, YouTubePlayerFragment playerFragment) {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            // to handle app crash caused by some bug in YouTube App. https://stackoverflow.com/questions/48674311/exception-java-lang-noclassdeffounderror-pim
            //TODO: Remove it when crash is resolved in latest YouTube App
            watchYoutubeVideo(context, VIDEO_ID);
            return;
        }
        fadeOutAndHideImage(ivThumbnail);
        fadeOutAndHideImage(ytIcon);
        if (playerFragment.getView() != null) {
            playerFragment.getView().setVisibility(View.VISIBLE);
        }
        playerFragment.initialize(context.getString(R.string.google_api_client_key), new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                if (!wasRestored) {
                    player.setShowFullscreenButton(false);
                    // loadVideo() will auto play video
                    // Use cueVideo() method, if you don't want to play it automatically
                    player.loadVideo(VIDEO_ID);

                    player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
                if (errorReason.isUserRecoverableError()) {
                    errorReason.getErrorDialog(context, 1).show();
                } else {
                    String errorMessage = errorReason.toString();
                    Utils.appToast(context, errorMessage);
                }
            }
        });
    }

    public static void initPlayerFragment(final YouTubePlayerFragment playerFragment, ImageView ytIcon, final ImageView ivThumbnail, final String VIDEO_ID) {
        if (playerFragment.getView() != null) {
            playerFragment.getView().setVisibility(View.INVISIBLE);
        }
        ytIcon.setVisibility(View.VISIBLE);

        if (playerFragment.getView() != null) {
            final ViewTreeObserver layoutObserver = playerFragment.getView().getViewTreeObserver();
            layoutObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (playerFragment.getView() != null) {
                        playerFragment.getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        ivThumbnail.getLayoutParams().height = playerFragment.getView().getHeight();
                        ivThumbnail.requestLayout();
                        Utils.loadImgURL(ivThumbnail, "https://img.youtube.com/vi/" + VIDEO_ID + "/0.jpg");
                        playerFragment.getView().setVisibility(View.INVISIBLE);
                    }
                }
            });

        }
    }


    public static void startCameraByIntent(Fragment act) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        act.startActivityForResult(intent, Constants.REQUEST_CAMERA);
    }


    public static void startGalleryByIntent(Fragment fragment) {
        Intent intent1 = new Intent();
        intent1.setAction(Intent.ACTION_GET_CONTENT);
        intent1.addCategory(Intent.CATEGORY_OPENABLE);
        intent1.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        intent1.setType("image/*");
        fragment.startActivityForResult(intent1, Constants.REQUEST_GALLERY);
    }


    public static Uri startCameraByIntent(Activity act, File photoFile) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Uri photoURI = null;
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(act.getPackageManager()) != null) {
            // Create the File where the photo should go
            // Continue only if the File was successfully created

            if (photoFile != null) {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                    photoURI = Uri.fromFile(photoFile);
                } else {
                    photoURI = FileProvider.getUriForFile(act,
                            act.getApplicationContext().getPackageName() + ".fileprovider",
                            photoFile);
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                act.startActivityForResult(takePictureIntent, Constants.REQUEST_CAMERA);
            }
        }
        return photoURI;
    }


    public static void startGalleryByIntent(Activity fragment) {
        Intent intent1 = new Intent();
        intent1.setAction(Intent.ACTION_GET_CONTENT);
        intent1.addCategory(Intent.CATEGORY_OPENABLE);
        intent1.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        intent1.setType("image/*");
        fragment.startActivityForResult(intent1, Constants.REQUEST_GALLERY);
    }

    public static int getOrientation(Activity activity, Uri photoUri) {
        /* it's on the external media. */
        int value;

        Cursor cursor = activity.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION},
                null, null, null);

        if (cursor.getCount() == 0) {
            return -1;
        }
        cursor.moveToFirst();
        value = cursor.getInt(0);
        cursor.close();
        return value;
    }

    public static long getFolderSize(File f) {
        long size = 0;
        if (f.isDirectory()) {
            for (File file : f.listFiles()) {
                size += getFolderSize(file);
            }
        } else {
            size = f.length();
        }
        return size;
    }

    public static String getChannelID() {
        String chanelId = "FG_NOTI_BYKEA_P";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) DriverApp.getContext().getSystemService(Context.NOTIFICATION_SERVICE);

            String channelId = "bykea_p_channel_id";
            CharSequence channelName = "Bykea Partner Notification Channel";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
            chanelId = notificationChannel.getId();
        }
        return chanelId;
    }

    public static void printHashKey(Context pContext) {
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Utils.redLog("fbKeyHash", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Utils.redLog("fbKeyHash", "printHashKey()" + e.getMessage());
        } catch (Exception e) {
            Utils.redLog("fbKeyHash", "printHashKey()" + e.getMessage());
        }
    }


    public static void logFacebookEvent(Context context, String EVENT, JSONObject data) {
        int count = 0;
        Bundle bundle = new Bundle();
        Iterator iterator = data.keys();
        while (iterator.hasNext()) {
            if (count == 10) {
                break;
            }
            count++;
            String key = (String) iterator.next();
            String value = null;
            try {
                value = data.getString(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            key = key.toLowerCase().replace("-", "_").replace(" ", "_");
            bundle.putString(key, value);
        }
        //Facebook Events
        AppEventsLogger.newLogger(context).logEvent(EVENT, bundle);
        FirebaseAnalytics.getInstance(context).logEvent(EVENT, bundle);
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        return StringUtils.capitalize(manufacturer);
    }

    public static String getDeviceModel() {
        String model = Build.MODEL;
        return StringUtils.capitalize(model);
    }

    public static boolean isInactiveCheckRequired() {
        long diff = (System.currentTimeMillis() - AppPreferences.getInactiveCheckTime());
        return !(diff <= 15000);
    }


    public static void updateTripData(NormalCallData callData) {
        if (callData.getData() != null) {
            if (callData.getData().isTripDetailsAdded()) {
                NormalCallData currentcallData = AppPreferences.getCallData();
                currentcallData.setEndAddress(callData.getData().getEndAddress());
                currentcallData.setEndLat(callData.getData().getEndLat());
                currentcallData.setEndLng(callData.getData().getEndLng());
                currentcallData.setStatus(callData.getStatus());

                currentcallData.setComplete_address(callData.getData().getComplete_address());
                currentcallData.setRec_no(callData.getData().getRec_no());
                currentcallData.setRecName(callData.getData().getRecName());
                currentcallData.setAmount_parcel_value(callData.getData().getAmount_parcel_value());
                currentcallData.setCodAmount(callData.getData().getCodAmountNotFormatted());
                currentcallData.setOrder_no(callData.getData().getOrder_no());
                currentcallData.setCod(callData.getData().isWalletDeposit());
                currentcallData.setWalletDeposit(callData.getData().isWalletDeposit());
                currentcallData.setReturnRun(callData.getData().isReturnRun());

                AppPreferences.setCallData(currentcallData);
                Intent intent = new Intent(Keys.TRIP_DATA_UPDATED);
                intent.putExtra("action", Keys.TRIP_DATA_UPDATED);
                EventBus.getDefault().post(intent);
            } else if (StringUtils.isNotBlank(callData.getData().getEndAddress()) &&
                    !callData.getData().getEndAddress().equalsIgnoreCase(AppPreferences.getCallData().getEndAddress())) {
                NormalCallData currentcallData = AppPreferences.getCallData();
                currentcallData.setEndAddress(callData.getData().getEndAddress());
                currentcallData.setEndLat(callData.getData().getEndLat());
                currentcallData.setEndLng(callData.getData().getEndLng());
                currentcallData.setStatus(callData.getData().getStatus());
                AppPreferences.setCallData(currentcallData);
                Intent intent = new Intent(Keys.BROADCAST_DROP_OFF_UPDATED);
                intent.putExtra("action", Keys.BROADCAST_DROP_OFF_UPDATED);
                EventBus.getDefault().post(intent);
            }
        }
    }

    public static boolean canSendLocation() {
        return AppPreferences.isLoggedIn()
                && (AppPreferences.getAvailableStatus() ||
                AppPreferences.isOutOfFence() || AppPreferences.isOnTrip());
    }

    /**
     * This method validates URLs
     *
     * @return true when url format is correct and false when it is incorrect
     */
    public static boolean isValidUrl(String url) {
        return Patterns.WEB_URL.matcher(url).matches();
    }

    /**
     * This method plays an audio sound for 8 secs when cancel notification is displayed
     *
     * @param context calling context
     */
    public static void playCancelNotificationSound(Context context) {
        final MediaPlayer mediaPlayer = MediaPlayer
                .create(context, R.raw.one);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.stop();
            }
        }, 8000);//millisec.
    }

}
