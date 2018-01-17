package com.bykea.pk.partner.utils;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;


import com.bykea.pk.partner.BuildConfig;
import com.bykea.pk.partner.models.data.PlacesResult;
import com.bykea.pk.partner.ui.activities.BaseActivity;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.StringCallBack;
import com.bykea.pk.partner.ui.helpers.webview.FinestWebViewBuilder;
import com.bykea.pk.partner.widgets.FontEditText;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.security.KeyStore;
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
        clearData();
        HomeActivity.visibleFragmentNumber = 0;
        ActivityStackManager.getInstance().startLoginActivity(context);
        ((Activity) context).finish();
    }

    private static void clearData() {
        String regId = AppPreferences.getRegId();
        double currentLat = AppPreferences.getLatitude();
        double currentLng = AppPreferences.getLongitude();
        AppPreferences.clear();
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
        Utils.redLog("Image Url", "http://res.cloudinary.com/bykea/image/upload/" + Keys.NORMAL_IMAGE + link);
        return "http://res.cloudinary.com/bykea/image/" + Keys.NORMAL_IMAGE + link;
    }

    public static String getFileLink(String name) {
        Utils.redLog("File Url", "http://52.36.93.28:3000/files/" + name);
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

    public static void setCallIncomingState() {
        AppPreferences.setIsOnTrip(false);
        AppPreferences.setTripStatus(TripStatus.ON_FREE);
        AppPreferences.setIncomingCall(true);
    }

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
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
            clearData();
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
            clearData();
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


    public static float calculateDistance(double newLat, double newLon, double prevLat, double prevLon) {
        Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        newLocation.setLatitude(newLat);
        newLocation.setLongitude(newLon);

        Location prevLocation = new Location(LocationManager.GPS_PROVIDER);
        prevLocation.setLatitude(prevLat);
        prevLocation.setLongitude(prevLon);
        return newLocation.distanceTo(prevLocation);
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
        Picasso.with(context).load(link)
                .fit().centerInside()
                .placeholder(placeHolder)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        redLog("Picasso", "onSuccess");
                    }

                    @Override
                    public void onError() {
                        redLog("Picasso", "onError");
                    }
                });
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

    public static String getCloudinaryLink(String icon, Context context) {
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
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            context.startActivity(browserIntent);
        }
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

}
