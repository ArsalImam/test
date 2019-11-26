package com.bykea.pk.partner.utils;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bykea.pk.partner.BuildConfig;
import com.bykea.pk.partner.DriverApp;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.communication.socket.WebIO;
import com.bykea.pk.partner.databinding.DialogCallBookingBinding;
import com.bykea.pk.partner.models.data.ChatMessagesTranslated;
import com.bykea.pk.partner.models.data.MultiDeliveryCallDriverData;
import com.bykea.pk.partner.models.data.PlacesResult;
import com.bykea.pk.partner.models.data.SettingsData;
import com.bykea.pk.partner.models.data.SignUpCity;
import com.bykea.pk.partner.models.data.SignUpSettingsResponse;
import com.bykea.pk.partner.models.data.VehicleListData;
import com.bykea.pk.partner.models.response.AddressComponent;
import com.bykea.pk.partner.models.response.GeocoderApi;
import com.bykea.pk.partner.models.response.LocationResponse;
import com.bykea.pk.partner.models.response.MultipleDeliveryBookingResponse;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.models.response.Result;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.activities.BaseActivity;
import com.bykea.pk.partner.ui.activities.BookingCallListener;
import com.bykea.pk.partner.ui.activities.HomeActivity;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.StringCallBack;
import com.bykea.pk.partner.widgets.FontEditText;
import com.bykea.pk.partner.widgets.FontTextView;
import com.bykea.pk.partner.widgets.FontUtils;
import com.elvishew.xlog.XLog;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.onesignal.OneSignal;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.Format;
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

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;
import zendesk.core.JwtIdentity;
import zendesk.core.Zendesk;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.bykea.pk.partner.DriverApp.getContext;
import static com.bykea.pk.partner.dal.util.ConstKt.EMPTY_STRING;
import static com.bykea.pk.partner.utils.Constants.DIGIT_ZERO;
import static com.bykea.pk.partner.utils.Constants.GoogleMap.TRANSIT_MODE_BIKE;
import static com.bykea.pk.partner.utils.Constants.MOBILE_COUNTRY_STANDARD;
import static com.bykea.pk.partner.utils.Constants.MOBILE_TEL_URI;
import static com.bykea.pk.partner.utils.Constants.ScreenRedirections.HOME_SCREEN_S;
import static com.bykea.pk.partner.utils.Constants.ServiceCode.BANK_TRANSFER;
import static com.bykea.pk.partner.utils.Constants.ServiceCode.MART;
import static com.bykea.pk.partner.utils.Constants.ServiceCode.MOBILE_TOP_UP;
import static com.bykea.pk.partner.utils.Constants.ServiceCode.MOBILE_WALLET;
import static com.bykea.pk.partner.utils.Constants.ServiceCode.UTILITY;
import static com.bykea.pk.partner.utils.Constants.TRANSALATION_SEPERATOR;
import static com.bykea.pk.partner.utils.Constants.TripTypes.COURIER_TYPE;
import static com.bykea.pk.partner.utils.Constants.TripTypes.GOODS_TYPE;


public class Utils {

    public static final String TAG = Utils.class.getSimpleName();

    /**
     * This method handles error logs for Location Service and maintains files via XLog lib for debug builds
     *
     * @param tag     Error tag
     * @param message Error Message
     */
    public static void redLogLocation(String tag, String message) {
        if (BuildConfig.DEBUG) {
            XLog.Log.e(tag + " : ", message);
//            XLog.e(tag, message);
        }
    }

    public static void redLog(String tag, String message) {
        if (BuildConfig.DEBUG) {
            XLog.Log.e(tag + " : ", message);
        }
    }

    /**
     * This method handles error logs for Location Service and maintains files via XLog lib for debug builds
     *
     * @param tag     Error tag
     * @param message Error Message
     * @param ex      Exception object
     */
    public static void redLog(String tag, String message, Exception ex) {
        if (BuildConfig.DEBUG) {
            XLog.Log.e(tag + " : ", message, ex);
        }
    }

    public static void appToastDebug(String message) {
        if (BuildConfig.DEBUG) {
            try {
                appToast(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get Support Helpline Number from setting or from constants
     *
     * @return Helpline number
     */
    public static String getSupportHelplineNumber() {
        String supportContact = AppPreferences.getSettings().getSettings().getBykeaSupportHelpline();
        if (StringUtils.isNotBlank(supportContact))
            return supportContact;
        else
            return Constants.BYKEA_SUPPORT_HELPLINE;
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

    /**
     * Show application toast
     *
     * @param message The message that will display in toast.
     */
    public static void appToast(final String message) {
        try {
            if (StringUtils.isNotBlank(message)) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DriverApp.getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });

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
        AppPreferences.clearLoadboardSelectedZoneData();
        HomeActivity.visibleFragmentNumber = HOME_SCREEN_S;
        //ActivityStackManager.getInstance().startLoginActivity(context);
        ActivityStackManager.getInstance().startLandingActivity(context);
        ((Activity) context).finish();
    }

    /***
     * Clear Data when user is unauthorized.
     * @param context Calling context.
     */
    public static void clearData(Context context) {
        FirebaseAnalytics.getInstance(context).resetAnalyticsData();
        String regId = AppPreferences.getRegId();
        double currentLat = AppPreferences.getLatitude();
        double currentLng = AppPreferences.getLongitude();
        SettingsData settingsData = AppPreferences.getSettings();
        SignUpSettingsResponse signUpSettingsResponse = (SignUpSettingsResponse) AppPreferences.getObjectFromSharedPref(SignUpSettingsResponse.class);

        int savedAppVersionCode = AppPreferences.getAppVersionCode();
        AppPreferences.clear();
        AppPreferences.setAppVersionCode(savedAppVersionCode);

        if (signUpSettingsResponse != null) {
            AppPreferences.setObjectToSharedPref(signUpSettingsResponse);
        }
        if (settingsData != null && settingsData.getSettings() != null) {
            settingsData.getSettings().setPartner_signup_url(StringUtils.EMPTY);
            AppPreferences.saveSettingsData(settingsData);
            if (settingsData.getSettings().getCih_range() != null) {
                AppPreferences.setCashInHandsRange(settingsData.getSettings().getCih_range());
            }
        }
        AppPreferences.setRegId(regId);
        AppPreferences.saveLocation(currentLat, currentLng);
        WebIO.getInstance().clearConnectionData();
        ActivityStackManager.getInstance().stopLocationService(context);
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

    /***
     * Navigate to google map with origin (Start) lat, lng & destination (end) lat, lng
     * to draw the direction on google map.
     *
     * @param context holding the reference of an activity.
     * @param mCallData NormalCallData object to fetch the lat lng.
     */
    public static void navigateToGoogleMap(Context context,
                                           NormalCallData mCallData) {
        try {
            if (mCallData.getStatus().equalsIgnoreCase(TripStatus.ON_ACCEPT_CALL)) {
                String startAddr = StringUtils.EMPTY;
                String endAddr = StringUtils.EMPTY;
                if (mCallData.getStatus().equalsIgnoreCase(TripStatus.ON_ACCEPT_CALL)) {
                    startAddr = getCurrentLocation();
                    endAddr = mCallData.getStartLat() + "," + mCallData.getStartLng();
                }
                String uri = Constants.GoogleMap.GOOGLE_NAVIGATE_ENDPOINT + startAddr +
                        Constants.GoogleMap.GOOGLE_DESTINATION_ENDPOINT + endAddr;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setClassName(Constants.GoogleMap.GOOGLE_MAP_PACKAGE,
                        Constants.GoogleMap.GOOGLE_MAP_ACTIVITY);
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                }
            } else {
                Uri gmmIntentUri = Uri.parse("geo:" + getCurrentLocation());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage(Constants.GoogleMap.GOOGLE_MAP_PACKAGE);
                if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(mapIntent);
                }
            }
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * Navigate to google map with origin (Start) lat, lng & destination (end) lat, lng
     * to draw the direction on google map.
     *
     * @param context holding the reference of an activity.
     * @param mCallData MultiDeliveryCallDriverData object to fetch the lat lng.
     */
    public static void navigateToGoogleMap(Context context,
                                           MultiDeliveryCallDriverData mCallData) {
        try {
            String startAddr = getCurrentLocation();
            String endAddr = mCallData.getPickup().getLat() + "," +
                    mCallData.getPickup().getLng();
            String uri = Constants.GoogleMap.GOOGLE_NAVIGATE_ENDPOINT + startAddr +
                    Constants.GoogleMap.GOOGLE_DESTINATION_ENDPOINT + endAddr;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setClassName(Constants.GoogleMap.GOOGLE_MAP_PACKAGE,
                    Constants.GoogleMap.GOOGLE_MAP_ACTIVITY);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            }
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * Navigate to google map with origin (Start) lat, lng & destination (end) lat, lng
     * to draw the direction on google map.
     *
     * @param context holding the reference of an activity.
     * @param pickLat Double, latitude
     * @param pickLng Double, longitude
     * @param dropLat Double, latitude
     * @param dropLng Double, longitude
     */
    public static void navigateToGoogleMap(Context context,
                                           double pickLat, double pickLng, double dropLat, double dropLng) {
        try {
            String startAddr = pickLat + "," + pickLng;
            String endAddr = dropLat + "," + dropLng;
            String uri = Constants.GoogleMap.GOOGLE_NAVIGATE_ENDPOINT + startAddr +
                    Constants.GoogleMap.GOOGLE_DESTINATION_ENDPOINT + endAddr + TRANSIT_MODE_BIKE;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setClassName(Constants.GoogleMap.GOOGLE_MAP_PACKAGE,
                    Constants.GoogleMap.GOOGLE_MAP_ACTIVITY);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            }
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * Navigate to google map with origin (Start) lat, lng & destination (end) lat, lng
     * to draw the direction on google map.
     *
     * @param context holding the reference of an activity.
     * @param latLng the drop off lat lng.
     */
    public static void navigateDropDownToGoogleMap(Context context,
                                                   LatLng latLng) {
        try {
            String startAddr = getCurrentLocation();
            String endAddr = latLng.latitude + "," +
                    latLng.longitude;
            String uri = Constants.GoogleMap.GOOGLE_NAVIGATE_ENDPOINT + startAddr +
                    Constants.GoogleMap.GOOGLE_DESTINATION_ENDPOINT + endAddr;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setClassName(Constants.GoogleMap.GOOGLE_MAP_PACKAGE,
                    Constants.GoogleMap.GOOGLE_MAP_ACTIVITY);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            }
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
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


    /***
     * Convert the drawable id into BitmapDescriptor.
     *
     * @param context holds the reference of an activity.
     *
     * @return The BitmapDescriptor.
     */
    public static BitmapDescriptor getBitmapDiscriptor(Context context) {
        int drawableId = R.drawable.ic_pickupmarker;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            VectorDrawable vectorDrawable = (VectorDrawable) context.getDrawable(drawableId);

            int h = (int) (vectorDrawable.getIntrinsicHeight() / 1.3);
            int w = (int) (vectorDrawable.getIntrinsicWidth() / 1.3);

            vectorDrawable.setBounds(0, 0, w, h);

            Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bm);
            vectorDrawable.draw(canvas);

            return BitmapDescriptorFactory.fromBitmap(bm);

        } else {
            Drawable drawable = ContextCompat.getDrawable(context, drawableId);

            Bitmap bmp = Bitmap.createBitmap(
                    drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    Bitmap.Config.ARGB_8888
            );
            Canvas canvas = new Canvas(bmp);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return BitmapDescriptorFactory.fromBitmap(bmp);
        }
    }

    /**
     * Create drop off marker.
     *
     * @param context Holding the reference of an activity.
     * @param number  The number of drop off.
     *                <p>
     *                By ignoring the constant, Time complexity of this function is O(n)^2
     *                because this function execute in a loop.
     * @return The bitmap for dropoff marker.
     */
    public static Bitmap createDropOffMarker(Context context, String number) {

        View marker = ((LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.drop_off_marker_layout, null);

        FontTextView txt_name = marker.findViewById(R.id.dropOffMarker);
        txt_name.setText(number);
        try {
            MultiDeliveryCallDriverData data = AppPreferences.getMultiDeliveryCallDriverData();

            List<MultipleDeliveryBookingResponse> bookingResponseList = data.getBookings();

            int index = Integer.parseInt(number) - 1;
            if (bookingResponseList.get(index).getTrip().getStatus().
                    equalsIgnoreCase(TripStatus.ON_COMPLETED_TRIP) ||
                    bookingResponseList.get(index).getTrip().getStatus().
                            equalsIgnoreCase(TripStatus.ON_FEEDBACK_TRIP)) {

                ViewCompat.setBackgroundTintList(txt_name, ContextCompat
                        .getColorStateList(context,
                                R.color.multi_delivery_dropoff_completed));

            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(
                40,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(
                marker.getMeasuredWidth(),
                marker.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }

    public static BitmapDescriptor getDropOffBitmapDiscriptor(Context context, String number) {
        Bitmap bmp = createDropOffMarker(context, number);
        return BitmapDescriptorFactory.fromBitmap(bmp);
    }

    /***
     * Fetch the bitmap from drawable resource id.
     *
     * @param context is holding a reference of an activity.
     * @param resourceID is a drawable id.
     *
     * @return the Bitmap generated from drawable id.
     */
    public static Bitmap getBitmap(Context context, int resourceID) {
        try {
            float height = context.getResources().getDimension(R.dimen.driver_marker_height);
            float width = context.getResources().getDimension(R.dimen.driver_marker_width);
            BitmapDrawable bitmapdraw = (BitmapDrawable) context.getResources().
                    getDrawable(resourceID);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, (int) width, (int) height, false);
            return smallMarker;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            ((AppCompatActivity) context).setShowWhenLocked(true);
            ((AppCompatActivity) context).setTurnScreenOn(true);
        } else {
            Window window = ((AppCompatActivity) context).getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
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

    /**
     * Check is service running in foreground service.
     *
     * @param context      The {@link Context}.
     * @param serviceClass Service class which needs to be checked for foreground service.
     */
    public static boolean serviceIsRunningInForeground(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                    Integer.MAX_VALUE)) {
                if (serviceClass.getClass().getName().equals(service.service.getClassName())) {
                    if (service.foreground) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public static void callingIntent(Context context, String number) {

        try {
            if (StringUtils.isBlank(number)) {
                number = StringUtils.EMPTY;
            } else if (number.startsWith(MOBILE_COUNTRY_STANDARD)) {
                number = phoneNumberToShow(number);
            }
            Intent callingIntent = new Intent(Intent.ACTION_VIEW);
            callingIntent.setData(Uri.parse(MOBILE_TEL_URI + number));
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
        } catch (ActivityNotFoundException ex) {
            appToast("There are no email clients installed.");
        }
    }

    public static void keepScreenOn(Context context) {
        ((Activity) context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public static void hideSoftKeyboard(Context context, View v) {
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /***
     * Hide keyboard from screen
     * @param fragment calling fragment where keyboard should be hidden.
     */
    public static void hideSoftKeyboard(Fragment fragment) {
        try {
            final InputMethodManager imm = (InputMethodManager) fragment.getActivity()
                    .getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null && fragment.getView() != null) {
                imm.hideSoftInputFromWindow(fragment.getView().getWindowToken(), 0);
            }
        } catch (Exception ignored) {
        }

    }

    public static void hideKeyboard(Activity context) {
        View view = context.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void shareWithWhatsApp(Context context, String promo) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, promo);
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");
        try {
            context.startActivity(sendIntent);
        } catch (ActivityNotFoundException ex) {
            appToastDebug("WhatsApp is not installed.");
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
        } catch (ActivityNotFoundException ex) {
            appToast("WhatsApp is not installed.");
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

        int version = Build.VERSION.SDK_INT;
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

        return "" + Build.VERSION.SDK_INT;

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

    public static String getImei(Context context) {
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


    /**
     * Get application installed version from package manager.
     *
     * @return Installed App version using {@link PackageManager}
     */
    public static String getVersion() {
        String currentVersion = StringUtils.EMPTY;
        PackageManager pm = DriverApp.getContext().getPackageManager();
        PackageInfo pInfo;
        try {
            pInfo = pm.getPackageInfo(DriverApp.getContext().getPackageName(), 0);
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
                                    //ActivityStackManager.getInstance().startLoginActivity(mCurrentActivity);
                                    ActivityStackManager.getInstance().startLandingActivity(mCurrentActivity);
                                    mCurrentActivity.finish();
                                }
                            }, null, mCurrentActivity.getString(R.string.unauthorized_title),
                            mCurrentActivity.getString(R.string.unauthorized_message_session_expired));
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
                                    //ActivityStackManager.getInstance().startLoginActivity(mCurrentActivity);
                                    ActivityStackManager.getInstance().startLandingActivity(mCurrentActivity);
                                    mCurrentActivity.finish();
                                }
                            }, null, mCurrentActivity.getString(R.string.unauthorized_title),
                            mCurrentActivity.getString(R.string.unauthorized_message_fake_gps));
                }
            });
        }
    }

    /***
     *  This will sync Device Time and Server Time.
     *  We know that Device time can be changed easily so
     *  we will calculate time difference between server and device
     * @param serverTime Latest server time in milliseconds.
     */
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

    /**
     * Invoked this method to check that the customer is
     * canceling the ride after 5 minutes of its acceptance
     *
     * <p>Calculate the difference of current time in millisecond, server time that keep on
     * updating from location update & the acceptance time.</p>
     *
     * @param acceptedTime The accepted time in millisecond.
     * @return true if calculated difference is greater than equal to
     * cancellation time otherwise false
     * @see AppPreferences#getSettings()
     */
    public static boolean isCancelAfter5Min(long acceptedTime) {
        long diff = (System.currentTimeMillis() -
                (AppPreferences.getServerTimeDifference() + acceptedTime));
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

    /*public static ArrayList<PlacesResult> getCities() {
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

    /**
     * Using GeoCode To Get Address
     *
     * @param lat      : Latitude
     * @param lng      : Longitude
     * @param activity : Calling Activity
     * @return String: address
     */
    public static String getLocationAddress(String lat, String lng, Activity activity) {
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(Double.valueOf(lat), Double.valueOf(lng), 1);
            return addresses.get(0).getAddressLine(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return StringUtils.EMPTY;
    }

    public interface LocationAddressCallback {
        void onSuccess(String reverseGeoCodeAddress);

        default void onFail() {
        }
    }

    /**
     * Get Address From Reverse Geo Coder API
     *
     * @param lat                     : Latitude
     * @param lng                     : Longtitude
     * @param activity                : Calling Activity
     * @param locationAddressCallback : Interface For Location Address CallBack
     */
    public static void getLocationAddress(String lat, String lng, Activity activity, LocationAddressCallback locationAddressCallback) {
        StringBuilder reverseGeoCodeAddress = new StringBuilder(StringUtils.EMPTY);
        UserRepository repository = new UserRepository();
        Dialogs.INSTANCE.showLoader(activity);
        repository.requestReverseGeocoding(activity, new UserDataHandler() {
            @Override
            public void onReverseGeocode(GeocoderApi geocoderApiResponse) {
                if (activity != null) {
                    if (geocoderApiResponse != null
                            && geocoderApiResponse.getStatus().equalsIgnoreCase(Constants.STATUS_CODE_OK)
                            && geocoderApiResponse.getResults().length > 0) {
                        String address = StringUtils.EMPTY;
                        String subLocality = StringUtils.EMPTY;
                        String cityName = StringUtils.EMPTY;
                        GeocoderApi.Address_components[] address_componentses = geocoderApiResponse.getResults()[0].getAddress_components();
                        for (GeocoderApi.Address_components addressComponent : address_componentses) {
                            String[] types = addressComponent.getTypes();
                            for (String type : types) {
                                if (type.equalsIgnoreCase(Constants.GEOCODE_RESULT_TYPE_CITY))
                                    cityName = addressComponent.getLong_name();
                                if (type.equalsIgnoreCase(Constants.GEOCODE_RESULT_TYPE_ADDRESS) || type.equalsIgnoreCase(Constants.GEOCODE_RESULT_TYPE_ADDRESS_1))
                                    address = addressComponent.getLong_name();
                                if (type.equalsIgnoreCase(Constants.GEOCODE_RESULT_TYPE_ADDRESS_SUB_LOCALITY))
                                    subLocality = addressComponent.getLong_name();
                                if (StringUtils.isNotBlank(cityName) && StringUtils.isNotBlank(address) && StringUtils.isNotBlank(subLocality))
                                    break;
                            }
                            if (StringUtils.isNotBlank(cityName) && StringUtils.isNotBlank(address) && StringUtils.isNotBlank(subLocality))
                                break;
                        }
                        if (StringUtils.isNotBlank(subLocality)) {
                            if (StringUtils.isNotBlank(address))
                                address = address + " " + subLocality;
                            else
                                address = subLocality;
                        }
                        if (StringUtils.isNotBlank(address))
                            reverseGeoCodeAddress.append(address);
                        if (StringUtils.isNotBlank(address) && StringUtils.isNotBlank(cityName))
                            reverseGeoCodeAddress.append(address).append(", ").append(cityName);

                        if (StringUtils.isNotBlank(reverseGeoCodeAddress.toString())) {
                            locationAddressCallback.onSuccess(reverseGeoCodeAddress.toString());
                        } else {
                            AppPreferences.setGeoCoderApiKeyRequired(true);
                            locationAddressCallback.onFail();
                        }
                    } else {
                        locationAddressCallback.onFail();
                        AppPreferences.setGeoCoderApiKeyRequired(true);
                    }
                } else {
                    locationAddressCallback.onFail();
                }
                Dialogs.INSTANCE.dismissDialog();
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                AppPreferences.setGeoCoderApiKeyRequired(true);
                locationAddressCallback.onFail();
                Dialogs.INSTANCE.dismissDialog();
            }
        }, lat + "," + lng, Utils.getApiKeyForGeoCoder());
    }

    public static String calculateDistanceInKm(double newLat, double newLon, double prevLat,
                                               double prevLon) {
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
            return MOBILE_COUNTRY_STANDARD + number.substring(1);
        } else {
            return StringUtils.EMPTY;
        }
    }

    /**
     * Convert Phone Number from 92********** to 03*********
     *
     * @param phone : Phone Number
     */
    public static String phoneNumberToShow(String phone) {
        if (StringUtils.isNotBlank(phone)) {
            if (phone.startsWith(MOBILE_COUNTRY_STANDARD))
                return "0" + phone.substring(2);
            else
                return phone;
        } else {
            return StringUtils.EMPTY;
        }
    }

    public static String formatAddress(String place) {
        return StringUtils.isNotBlank(place) ? place.replace(", Pakistan", "").replace(", Punjab", "")
                .replace(", Sindh", "").replace(", Islamabad Capital Territory", "").replace(", Islamic Republic of Pakistan", "") : StringUtils.EMPTY;
    }


    /**
     * Cook displayable address from the response of GeoCode API
     *
     * @param result GeoCode API response
     * @return Displayable Address
     */
    public static String cookAddressGeoCodeResult(Result result) {
        StringBuilder builder = new StringBuilder(EMPTY_STRING);
        boolean isFirstComp = true;
        for (AddressComponent comp : result.getAddress_components()) {
            String name = comp.getLong_name();
            List<String> types = comp.getTypes();
            if (!name.isEmpty()) {
                if (types.contains("premise") || types.contains("street_number") || types.contains("route")
                        || types.contains("sublocality_level_3") || types.contains("sublocality_level_2")
                        || types.contains("sublocality_level_1") || types.contains("locality")) {
                    builder.append(isFirstComp ? name : ", " + name);
                    isFirstComp = false;
                }
            }
        }
        return builder.toString();
    }

    /**
     * Returns API key for Google GeoCoder API if required.
     * Will return Empty String if there's no error in Last
     * Request while using API without any Key.
     *
     * @return Google place server API key
     */
    public static String getApiKeyForGeoCoder() {
        return AppPreferences.isGeoCoderApiKeyRequired() ? Constants.GOOGLE_PLACE_SERVER_API_KEY : StringUtils.EMPTY;
    }

    /***
     *  Returns API key for Google Directions API if required.
     *  Will return Empty String if there's no error in Last
     *  Request while using API without any Key.
     * @param context Calling Context.
     * @return Google place server API key
     */
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

    /***
     * Validate where we should call Direction API on screen.
     * @return Returns true if Last API call was more than 1 min ago
     */
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

    /***
     * Check that the GPS is enable or not.
     *
     * @return true if gps/location is enable otherwise returns false
     */
    public static boolean isGpsEnable() {
        LocationManager locationManager = (LocationManager) DriverApp.getContext().
                getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }

        return false;
    }

    public static void loadImgPicasso(ImageView imageView, int placeHolder, String link) {
        if (imageView != null) {
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
        if (Build.VERSION.SDK_INT >= 18) {
            isMock = location.isFromMockProvider();
        } else {
            try {
                isMock = !Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0") && areThereMockPermissionApps(context);
            } catch (Exception e) {
                Utils.redLog(TAG, e.getMessage());
                isMock = false;
            }
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


    /***
     *  Validate Location latest latitude and longitude with following set of rules.
     *  <ul>
     *      <li> if same location coordinates then don't consider these lat lng </li>
     *      <li> if distance is less than 6 meter then don't consider these
     *      lat lng to avoid coordinate fluctuation </li>
     *      <li> Check if its time difference w.r.t last coordinate is greater than minimum
     *      time a bike should take to cover that distance if that bike is traveling
     *      at max 80KM/H to avoid bad/fake coordinates </li>
     *  </ul>
     * @param newLat Latest latitude fetched.
     * @param newLon Latest longitude fetched.
     * @param prevLat Previously stored latitude.
     * @param prevLon Previously stored longitude.
     *
     * @return True if latest fetched latitude and longitude are valid, otherwise return false.
     */
    public static boolean isValidLocation(double newLat, double newLon, double prevLat,
                                          double prevLon) {
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

    public static boolean isValidLocation
            (/*double newLat, double newLon, double prevLat, double prevLon, */ float distance) {
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


    /**
     * Call To Trigger Firebase Event
     *
     * @param context : Calling Activity
     * @param userId  : AppPreference Driver Id
     * @param EVENT   : Firebase Event Name
     * @param data    : JSON Object To Parse Into Bundle
     */
    public static void logFireBaseEvent(Context context, String userId, String
            EVENT, JSONObject data) {
        EVENT = EVENT.toLowerCase().replace("-", "_").replace(" ", "_");
        if (EVENT.length() > Constants.FirebaseAnalyticsConfigLimits.EVENT_NAME_LENGTH)
            EVENT = EVENT.substring(0, EVENT.length() - Constants.FirebaseAnalyticsConfigLimits.EVENT_NAME_LENGTH);

        int stringParametersCounts = 0, numericParametersCounts = 0;
        Bundle bundle = new Bundle();
        Iterator iterator = data.keys();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            Object value;
            try {
                value = data.get(key);

                key = key.toLowerCase().replace("-", "_").replace(" ", "_");
                if (key.length() > Constants.FirebaseAnalyticsConfigLimits.EVENT_PARAMETER_KEY_LENGTH)
                    key = key.substring(0, key.length() - Constants.FirebaseAnalyticsConfigLimits.EVENT_PARAMETER_KEY_LENGTH);

                if (value instanceof String) {
                    if (stringParametersCounts == Constants.FirebaseAnalyticsConfigLimits.EVENT_MAX_STRING_VALUES)
                        continue;
                    bundle.putString(key, value.toString());
                    stringParametersCounts++;
                } else if (numericParametersCounts < Constants.FirebaseAnalyticsConfigLimits.EVENT_MAX_NUMERIC_VALUES) {
                    if (value instanceof Integer) {
                        bundle.putInt(key, ((Number) value).intValue());
                        numericParametersCounts++;
                    } else if (value instanceof Long) {
                        bundle.putLong(key, ((Number) value).longValue());
                        numericParametersCounts++;
                    } else if (value instanceof Float) {
                        bundle.putFloat(key, ((Number) value).floatValue());
                        numericParametersCounts++;
                    } else if (value instanceof Double) {
                        bundle.putDouble(key, ((Number) value).doubleValue());
                        numericParametersCounts++;
                    } else if (value instanceof Boolean) {
                        bundle.putInt(key, ((Boolean) value) ? 1 : 0);
                        numericParametersCounts++;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        FirebaseAnalytics.getInstance(context).setUserId(AppPreferences.getPilotData().getId());
        FirebaseAnalytics.getInstance(context).setUserProperty("driver_id", AppPreferences.getPilotData().getId());

        // SET PASSENGER_ID AS USER PROPERTY WHEN USER_ID IS NOT EQUALS TO DRIVER_ID
        if (StringUtils.isNotEmpty(userId) && StringUtils.isNotEmpty(AppPreferences.getPilotData().getId()) &&
                !userId.equals(AppPreferences.getPilotData().getId()))
            FirebaseAnalytics.getInstance(context).setUserProperty("passenger_id", userId);

        FirebaseAnalytics.getInstance(context).setUserProperty("cash_in_hand", String.valueOf(AppPreferences.getCashInHands()));
        FirebaseAnalytics.getInstance(context).setUserProperty("device_imei", Utils.getDeviceId(context));
        FirebaseAnalytics.getInstance(context).setUserProperty("driver_name", AppPreferences.getPilotData().getFullName());
        FirebaseAnalytics.getInstance(context).setUserProperty("is_cash", AppPreferences.getIsCash() ? "1" : "0");
        FirebaseAnalytics.getInstance(context).setUserProperty("service_type", AppPreferences.getPilotData().getService_type());
        FirebaseAnalytics.getInstance(context).setUserProperty("singup_city", AppPreferences.getPilotData().getCity().getName());
        if (AppPreferences.getDriverDestination() != null) {
            FirebaseAnalytics.getInstance(context).setUserProperty("dd_lat", String.valueOf(AppPreferences.getDriverDestination().latitude));
            FirebaseAnalytics.getInstance(context).setUserProperty("dd_lng", String.valueOf(AppPreferences.getDriverDestination().longitude));
        }

        FirebaseAnalytics.getInstance(context).logEvent(EVENT, bundle);
    }

    public static void startCustomWebViewActivity(AppCompatActivity context, String
            link, String title) {
        if (isConnected(context, true)) {
            if (StringUtils.isNotBlank(link)) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage(Constants.GOOGLE_CHROME_PACKAGE);
                if (intent.resolveActivityInfo(context.getPackageManager(), 0) != null) {
                    context.startActivity(intent);
                } else {
                    Utils.appToast(DriverApp.getContext().getString(R.string.no_browser_found_error));
                }
            }
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

    /***
     * Common method to log event on analytics services
     * @param context Calling context.
     * @param userID User id which is currently logged in.
     * @param EVENT Event name which needs to be flush.
     * @param data Json data which needs to be emitted.
     */
    public static void logEvent(Context context, String userID, String EVENT, JSONObject data) {
        logFireBaseEvent(context, userID, EVENT, data);
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

    /**
     * Checks if call type is one of the delivery types
     *
     * @param callType Call type
     * @return Either delivery service or not
     */
    public static boolean isDeliveryService(String callType) {
        return StringUtils.containsIgnoreCase(callType, "Send")
                || StringUtils.containsIgnoreCase(callType, "Delivery")
                || StringUtils.containsIgnoreCase(callType, "COD")
                || StringUtils.containsIgnoreCase(callType, "NOD");
    }

    /**
     * Checks if call type is from loadboard
     *
     * @param callType Call type
     * @return Either loadboard service or not
     */
    public static boolean isLoadboardService(String callType) {
        return StringUtils.containsIgnoreCase(callType, "COD")
                || StringUtils.containsIgnoreCase(callType, "NOD");
    }

    /**
     * Is ride for NOC(21), COD(22) or MART(22)
     *
     * @param serviceCode : Ride Service Code
     * @return
     */
    public static boolean isDescriptiveAddressRequired(Integer serviceCode) {
        return serviceCode != null
                && (serviceCode == Constants.ServiceCode.SEND
                || serviceCode == Constants.ServiceCode.SEND_COD
                || serviceCode == MART
        );
    }

    /**
     * Checks if call type is among modern services
     *
     * @param serviceCode Service Code
     * @return Either modern service or not
     */
    public static boolean isModernService(Integer serviceCode) {
        return serviceCode != null
                && (serviceCode == Constants.ServiceCode.SEND
                || serviceCode == Constants.ServiceCode.SEND_COD
                || serviceCode == Constants.ServiceCode.RIDE
                || serviceCode == Constants.ServiceCode.OFFLINE_RIDE
                || serviceCode == Constants.ServiceCode.OFFLINE_DELIVERY
                || serviceCode == MART
                || serviceCode == MOBILE_TOP_UP
                || serviceCode == MOBILE_WALLET
                || serviceCode == BANK_TRANSFER
                || serviceCode == UTILITY
        );
    }

    public static boolean isRideService(String callType) {
        return StringUtils.containsIgnoreCase(callType, "Sawari");
    }

    public static boolean isCourierService(String callType) {
        return StringUtils.containsIgnoreCase(callType, GOODS_TYPE) || StringUtils.containsIgnoreCase(callType, COURIER_TYPE);
    }

    public static boolean isPurchaseService(String callType) {
        return isPurchaseService(callType, null);
    }

    public static boolean isPurchaseService(String callType, Integer serviceCode) {
        if (serviceCode == null)
            return StringUtils.containsIgnoreCase(callType, "Bring")
                    || StringUtils.containsIgnoreCase(callType, "Purchase");
        else
            return StringUtils.containsIgnoreCase(callType, "Bring")
                    || StringUtils.containsIgnoreCase(callType, "Purchase")
                    || serviceCode == MART;
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

    /**
     * Return resource id of image for service icon on basis of call type
     *
     * @param callData Call data
     * @return Resource id of service icon image
     */
    @Deprecated
    public static Integer getServiceIcon(NormalCallData callData) {
        String callType = callData.getCallType().replace(" ", StringUtils.EMPTY).toLowerCase();
        switch (callType) {
            case "parcel":
            case "offlineparcel":
            case "send":
            case "delivery":
            case "cod":
            case "nod":
                return R.drawable.bhejdo_no_caption;
            case "bring":
            case "purchase":
            case "mart":
                return R.drawable.lay_ao_no_caption;
            case "ride":
                return R.drawable.ride_right;
            case "top-up":
                return R.drawable.top_up;
            case "utilitybill":
                return R.drawable.utility_bill;
            case "deposit":
                return R.drawable.jama_karo;
            case "carryvan":
                return R.drawable.carry_van;
            case "courier":
            case "goods":
                return R.drawable.courier_no_caption;
            case "bykeacash-mobiletopup":
            case "bykeacash-mobilewallet":
            case "bykeacash-banktransfer":
            case "bykeacash-utilitybill":
                return R.drawable.ic_pay;
            default:
                return R.drawable.ride_right;
        }
    }

    /**
     * This method compares FCM token of SP with token placed with User (PilotData) data model to indicate if we need to update FCM token on our server or not.
     *
     * @return boolean true/false
     */
    public static boolean isFcmIdUpdateRequired(boolean isLoggedIn) {
        AppPreferences.setRegId(FirebaseInstanceId.getInstance().getToken()); //on Android 8, sometimes onNewToken gets called 2 times and 2nd one is not latest(Unexpected behaviour). That's why updating SP with latest FCM Token
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

    @Deprecated
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
                appToast(context.getString(R.string.error_internet_connectivity));
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

    /**
     * Load multiple delivery image URL using {@link Picasso}
     *
     * @param imageView   The image container.
     * @param link        The image URL.
     * @param placeHolder The place holder image.
     */
    public static void loadMultipleDeliveryImageURL(ImageView imageView, String link,
                                                    int placeHolder) {

        if (imageView != null) {
            Picasso.get().load(link)
                    .placeholder(placeHolder)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {

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


    public static void playVideo(final BaseActivity context, final String VIDEO_ID, ImageView
            ivThumbnail, ImageView ytIcon, YouTubePlayerSupportFragment playerFragment) {

        if (playerFragment == null || playerFragment.getView() == null || Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
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
                    Utils.appToast(errorMessage);
                }
            }
        });
    }

    public static void initPlayerFragment(
            final YouTubePlayerSupportFragment playerFragment, ImageView ytIcon,
            final ImageView ivThumbnail, final String VIDEO_ID) {
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

    public static void playVideo(final BaseActivity context, final String VIDEO_ID, ImageView
            ivThumbnail, ImageView ytIcon, YouTubePlayerFragment playerFragment) {

        if (playerFragment == null || playerFragment.getView() == null || Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
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
                    Utils.appToast(errorMessage);
                }
            }
        });
    }

    public static void initPlayerFragment(
            final YouTubePlayerFragment playerFragment, ImageView ytIcon, final ImageView ivThumbnail,
            final String VIDEO_ID) {
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

    /**
     * This method creates separate notification channel (On Android O and above) for Trip Cancel
     * Notification which has different Sound URI.
     *
     * @return String of Channel ID
     */
    public static String getChannelIDForCancelNotifications() {
        String chanelId = "bykea_channel_id_for_cancel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) DriverApp.getContext().getSystemService(Context.NOTIFICATION_SERVICE);

            CharSequence channelName = "Bykea Partner Notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(chanelId, channelName, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);

            Uri soundUri = Uri.parse("android.resource://"
                    + DriverApp.getContext().getPackageName() + "/"
                    + R.raw.one);

            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();

            notificationChannel.setSound(soundUri, att);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
            chanelId = notificationChannel.getId();
        }
        return chanelId;
    }

    /**
     * This method creates Notification Channel for OS version >= Android o
     *
     * @return notification chanel id
     */
    public static String getChannelID() {
        String chanelId = "bykea_p_channel_id";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) DriverApp.getContext().getSystemService(Context.NOTIFICATION_SERVICE);

//            String channelId = "bykea_p_channel_id";
            CharSequence channelName = "Bykea Notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(chanelId, channelName, importance);
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


    /**
     * This method creates Separate Notification Channel for Foreground Location Service on devices
     * with OS version >= Android O
     *
     * @param context Calling context
     * @return notification chanel id
     */
    public static String getChannelIDForOnGoingNotification(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(
                    Constants.Notification.NOTIFICATION_CHANNEL_ID,
                    Constants.Notification.NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
            return notificationChannel.getId();
        }
        return Constants.Notification.NOTIFICATION_CHANNEL_ID;
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

                currentcallData.setReceiverAddress(callData.getData().getReceiverAddress());
                currentcallData.setReceiverPhone(callData.getData().getReceiverPhone());
                currentcallData.setReceiverName(callData.getData().getReceiverName());
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
     * This method disables battery optimization/doze mode for devices with OS version 6.0 or higher.
     *
     * @param context  calling context
     * @param activity calling activity
     * @return True if we are going to ask Battery optimization, else false.
     * @see Settings#ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
     */
    public static boolean disableBatteryOptimization(Context context, AppCompatActivity
            activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = context.getPackageName();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (pm != null && !pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    activity.startActivityForResult(intent, Constants.BATTERY_OPTIMIZATION_RESULT);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    /**
     * This method disables battery optimization/doze mode for devices with OS version 6.0 or higher.
     *
     * @param context  calling context
     * @param fragment Calling fragment
     * @return True if we are going to ask Battery optimization, else false.
     * @see Settings#ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
     */
    public static boolean disableBatteryOptimization(Context context,
                                                     Fragment fragment) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = context.getPackageName();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (pm != null && !pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    fragment.startActivityForResult(intent, Constants.BATTERY_OPTIMIZATION_RESULT);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    /**
     * Create email intent
     *
     * @param toEmail Receiver email address
     * @param subject Email subject
     * @param message Email message body
     * @param uri     attachment file URI
     * @return {@link Intent} intent object which we will use to invoke action
     */
    public static Intent createEmailIntentWithAttachment(final String subject,
                                                         final String message,
                                                         Uri uri,
                                                         final String... toEmail) {

        // Nothing resolves send to, so fallback to send...
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        //emailIntent.setType("plain/text");
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, toEmail);
        /*emailIntent.putExtra(Intent.EXTRA_EMAIL,
                new String[]{toEmail});*/
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
        if (uri != null) {
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        }
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        return Intent.createChooser(emailIntent, "Send mail");

    }

    /**
     * Send email to developer using email client on device.
     *
     * @param currentActivity Calling activity
     */
    public static void sendEmailToDeveloper(AppCompatActivity currentActivity) {
        File logFileDir = FileUtil.createRootDirectoryForLogs(currentActivity);
        if (logFileDir != null) {

            String fileName = String.format("%1$s-%2$s-%3$s-%4$s.zip",
                    BuildConfig.APPLICATION_ID,
                    BuildConfig.FLAVOR,
                    BuildConfig.VERSION_CODE,
                    BuildConfig.VERSION_NAME);
            boolean zipResult = FileUtil.zipFileAtPath(logFileDir.getAbsolutePath(),
                    currentActivity.getExternalCacheDir() + File.separator + fileName);
            if (zipResult) {
                Uri uri;
                File file = new File(currentActivity.getExternalCacheDir() + File.separator + fileName);
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                    uri = Uri.fromFile(file);
                } else {
                    uri = FileProvider.getUriForFile(currentActivity,
                            currentActivity.getApplicationContext().getPackageName() + ".fileprovider", file);
                }
                Intent emailIntent = Utils.createEmailIntentWithAttachment(
                        Constants.LogTags.LOG_SEND_SUBJECT,
                        Constants.LogTags.LOG_SEND_MESSAGE_BODY,
                        uri,
                        Constants.LogTags.LOG_SEND_DEVELOPERS_EMAIL);
                // Verify the intent will resolve to at least one activity
                if (emailIntent.resolveActivity(currentActivity.getPackageManager()) != null) {
                    Utils.redLogLocation(Constants.LogTags.BYKEA_LOG_TAG, "Email intent set");
                    currentActivity.startActivity(emailIntent);
                }
            }
        }
    }


    /***
     * Validate service name in our stored collection if service exist we can safely use provided icon by API.
     * @param serviceType Service name provided by API server.
     * @return Returns true if service name matches our collection otherwise return false.
     */
    public static boolean useServiceIconProvidedByAPI(String serviceType) {
        if (!TextUtils.isEmpty(serviceType)) {
            switch (serviceType) {
                case Constants.ServiceType.RIDE_NAME:
                case Constants.ServiceType.SEND_NAME:
                case Constants.ServiceType.SEND_TITLE:
                case Constants.ServiceType.BRING_NAME:
                case Constants.ServiceType.BRING_TITLE:
                case Constants.ServiceType.TICKETS_NAME:
                case Constants.ServiceType.TICKETS_TITLE:
                case Constants.ServiceType.JOBS_NAME:
                case Constants.ServiceType.CLASSIFIEDS_NAME:
                case Constants.ServiceType.CARRY_VAN_NAME:
                case Constants.ServiceType.CARRY_VAN_TITLE:
                case Constants.ServiceType.ADS_NAME:
                case Constants.ServiceType.UTILITY_BILL_NAME:
                case Constants.ServiceType.FOOD_DELIVERY_NAME:
                case Constants.ServiceType.FOOD_DELIVERY_TITLE:
                    return true;
            }
        }
        return false;
    }

    /****
     * Generate IMEI not registered error message for user.
     * @param context Calling context.
     * @return Spannable String format object
     */
    public static SpannableStringBuilder generateImeiRegistrationErrorMessage(Context context) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(FontUtils.getStyledTitle(context,
                R.string.imei_report_part_one_ur, Constants.FontNames.JAMEEL_NASTALEEQI));
        spannableStringBuilder.append(FontUtils.getStyledTitle(context,
                R.string.imei_report_part_two, Constants.FontNames.OPEN_SANS_BOLD));
        spannableStringBuilder.append(FontUtils.getStyledTitle(context,
                R.string.imei_report_part_three_ur, Constants.FontNames.JAMEEL_NASTALEEQI));
        spannableStringBuilder.append(FontUtils.getStyledTitle(context,
                R.string.imei_report_part_four_ur, Constants.FontNames.JAMEEL_NASTALEEQI));
        spannableStringBuilder.append(FontUtils.getStyledTitle(context,
                R.string.imei_report_part_two, Constants.FontNames.OPEN_SANS_BOLD));
        spannableStringBuilder.append(FontUtils.getStyledTitle(context,
                R.string.imei_report_part_five_ur, Constants.FontNames.JAMEEL_NASTALEEQI));
        return spannableStringBuilder;
    }

    /****
     * Generate GPS High Accuracy warning message for user.
     * @param context Calling context.
     * @return String object
     */
    public static String generateGpsHighAccuracyWarningMessage(Context context) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(FontUtils.getStyledTitle(context,
                R.string.gps_high_accuracy_error_ur_one, Constants.FontNames.JAMEEL_NASTALEEQI));
        spannableStringBuilder.append(FontUtils.getStyledTitle(context,
                R.string.gps_high_accuracy_error_ur_two, Constants.FontNames.OPEN_SANS_BOLD));
        spannableStringBuilder.append(FontUtils.getStyledTitle(context,
                R.string.gps_high_accuracy_error_ur_three, Constants.FontNames.JAMEEL_NASTALEEQI));
        return spannableStringBuilder.toString();
    }

    /***
     *  Check is provided activity in running task.
     *
     * @param context Calling context.
     * @param activityClass Class name which we need to find in running task.
     * @return True if class found otherwise return false.
     */
    public static boolean isActivityRunning(Context context, Class<?> activityClass) {
        try {
            ActivityManager activityManager = (ActivityManager)
                    context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> tasks = null;
            if (activityManager != null) {
                tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);
                for (ActivityManager.RunningTaskInfo task : tasks) {
                    if (activityClass.getName().equalsIgnoreCase(task.baseActivity.getClassName()))
                        return true;
                }
            }
        } catch (Exception ignored) {
        }


        return false;
    }


    /**
     * Get Application Current version from device Package manager.
     *
     * @return Application installed version number.
     */
    public static String getAppCurrentVersion() {
        Context context = DriverApp.getApplication();
        if (context != null) {
            PackageManager pm = context.getPackageManager();
            PackageInfo pInfo;
            try {
                pInfo = pm.getPackageInfo(context.getPackageName(), 0);
                return pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

    //region API error response Body parsing

    /***
     * HTTP response body converted to specified {@code type}.
     * {@code null} if there is no response
     * @param response response we received from API server.
     * @param type Concrete type class which is use to parse error response.
     * @return {@link Object } Object class which would be casted to respective class
     * parsed when we receive error body
     */
    public static <T> T parseAPIErrorResponse(Response<?> response, Class<T> type) {
        try {
            return new Gson().fromJson(response.errorBody().string(), type);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //endregion

    /***
     * Handle business logic location Failure use cases for driver Location API .
     * <ul>
     *     <li> Multiple cancellation block. </li>
     *     <li> Wallet amount exceeds threshold. </li>
     *     <li> Out of service region area block. </li>
     *     <li> Account Blocked</li>
     * </ul>
     *
     * @param eventBus {@link EventBus} object
     * @param locationResponse Latest response received from API Server
     */
    public static void handleLocationBusinessLogicErrors(EventBus eventBus,
                                                         LocationResponse locationResponse) {
        switch (locationResponse.getSubCode()) {
            case Constants.ApiError.WALLET_EXCEED_THRESHOLD:
                if (StringUtils.isNotBlank(locationResponse.getMessage())) {
                    AppPreferences.setWalletIncreasedError(locationResponse.getMessage());
                }
                AppPreferences.setWalletAmountIncreased(true);
                AppPreferences.setAvailableStatus(false);
                eventBus.post(Keys.INACTIVE_FENCE);
                break;
            case Constants.ApiError.OUT_OF_SERVICE_REGION:
                AppPreferences.setOutOfFence(true);
                AppPreferences.setAvailableStatus(false);
                eventBus.post(Keys.INACTIVE_FENCE);
                break;
            case Constants.ApiError.MULTIPLE_CANCELLATION_BLOCK:
            case Constants.ApiError.DRIVER_ACCOUNT_BLOCKED:
                AppPreferences.setAvailableStatus(false);
                eventBus.post(Keys.INACTIVE_FENCE);
                break;
        }
    }


    /**
     * This method starts Google Map API to show navigation
     *
     * @param context  Calling context
     * @param endPoint String end point lat lng coordinates
     */
    public static void startGoogleDirectionsApp(Context context, String endPoint) {

        try {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + endPoint + "&mode=d");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            context.startActivity(mapIntent);
        } catch (Exception ex) {
            Utils.appToast(context.getString(R.string.google_maps_missing_error));
        }

    }

    //region MultiDelivery Helper Methods

    /**
     * Fetch the distance in meter or kilo meter.
     *
     * <p>If the distance in meter is greater than equals to 1000 return the
     * distance in kilometer otherwise return the distance in meter</p>
     *
     * @param distanceInMeter The distance in meter.
     * @return The distance in meter or kilometer
     */
    public static String getDistance(float distanceInMeter) {
        return String.format(DriverApp.
                        getContext().
                        getString(R.string.decimal_format_one_digit),
                distanceInMeter / 1000);
    }

    /**
     * Fetch the duration in minutes
     *
     * <p>If the duration in second is greater than or equal to 60 than convert
     * it into minutes. If duration in seconds is greater than equal to 3600 convert
     * duration to hour other wise keep it in seconds</p>
     *
     * @param durationInSeconds The duration in seconds
     * @return The duration in Seconds or minutes & hours
     */
    public static int getDuration(int durationInSeconds) {
        int SECONDS_IN_MINUTES = 60;
        return durationInSeconds / SECONDS_IN_MINUTES;
    }

    /**
     * Fetch Time in MilliSeconds
     *
     * @param timeInSeconds The time in seconds.
     * @return The time in milliseconds
     */
    public static int getTimeInMilliseconds(int timeInSeconds) {
        return timeInSeconds * 1000;
    }

    public static int getTimeInPercentage(int timeInMilliSeconds, float percentage) {
        return (int) ((percentage / 100f) * timeInMilliSeconds);
    }

    /**
     * Fetch drop down lat lng list.
     *
     * @param deliveryCallDriverData The {@link MultiDeliveryCallDriverData} object.
     * @return The collection of drop down lat lng.
     */
    public static List<LatLng> getDropDownLatLngList(MultiDeliveryCallDriverData
                                                             deliveryCallDriverData) {
        List<LatLng> latLngList = new ArrayList<>();
        for (MultipleDeliveryBookingResponse response : deliveryCallDriverData.getBookings()) {
            latLngList.add(new LatLng(

                    response.getDropOff().getLat(),
                    response.getDropOff().getLng())
            );
        }

        return latLngList;
    }

    /**
     * Multi Delivery Free Driver on Batch Complete.
     */
    public static void multiDeliveryFreeDriverOnBatchComplete() {
        setCallIncomingState();
        AppPreferences.setWalletAmountIncreased(false);
        AppPreferences.setAvailableStatus(true);
    }

    /**
     * multi delivery success or fail messages
     *
     * @return list of messages
     */
    public static String[] getDeliveryMsgsList(Context context) {
        return context.getResources().getStringArray(R.array.delivery_messages);
    }

    /**
     * Bykea cash success or fail messages
     *
     * @return list of messages
     */
    public static String[] getBykeaCashJobStatusMsgList(Context context) {
        return context.getResources().getStringArray(R.array.bykea_cash_status_messages);
    }

    /**
     * Get Ride Complain Reasons List.
     *
     * @param context : Calling Activity
     * @return
     */
    public static String[] getRideComplainReasonsList(Context context) {
        return context.getResources().getStringArray(R.array.ride_complain_reasons);
    }

    //endregion


    /**
     * Clears the Local Shared Pref in case of dirt
     *
     * @param context calling activity context
     */
    public static void clearSharedPrefIfDirty(Context context) {
        int savedVersionCode = AppPreferences.getAppVersionCode();
        int currentVersionCode = BuildConfig.VERSION_CODE;
        if (savedVersionCode < currentVersionCode) {
            AppPreferences.setAppVersionCode(currentVersionCode);
            Utils.clearData(context);

        }
    }

    /**
     * Format duration in millisecond in clock like timestemp
     *
     * @param time in millisecond
     * @return
     */
    public static String formatTimeForTimer(long time) {
        Date date = new Date(time);
        Timestamp ts = new Timestamp(date.getTime());
        Format format = new SimpleDateFormat("mm:ss");
        return format.format(ts);
    }

    /**
     * Get the Android ID of current device
     *
     * @param context App Context
     * @return Android ID
     */
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Setup Zendesk Identity - For JWT Authorization
     */
    public static void setZendeskIdentity() {
        Zendesk.INSTANCE.setIdentity(new JwtIdentity(AppPreferences.getDriverId()));
    }

    /**
     * Add DD Property (True/False)
     *
     * @param jsonObject : JSONObject To Add Priority
     */
    public static void addDriverDestinationProperty(JSONObject jsonObject) {
        try {
            if (AppPreferences.getDriverDestination() != null)
                jsonObject.put("DD", true);
            else
                jsonObject.put("DD", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param context : Calling Activity
     * @param uri     : Package
     * @return If Application Is Installed Return True Else False
     */
    public static boolean isAppInstalledWithPackageName(Context context, String uri) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * Chat Messages in Urdu
     *
     * @return List Of Chat Message In Urdu
     */
    public static String[] getChatMessageInUrdu(Context context) {
        return context.getResources().getStringArray(R.array.chat_messages_urdu);
    }

    /**
     * Chat Messages in English
     *
     * @return List Of Chat Message In English
     */
    public static String[] getChatMessageInEnglish(Context context) {
        return context.getResources().getStringArray(R.array.chat_messages_english);
    }

    /**
     * @param context Calling Context
     * @return List Of ChatMessages With English Urdu Transalations
     */
    public static ArrayList<ChatMessagesTranslated> getAllChatMessageTranslated(Context context) {
        ArrayList<ChatMessagesTranslated> chatMessagesTranslateds = new ArrayList<>();
        String[] chatMessageInEnglish = Utils.getChatMessageInEnglish(context);
        String[] chatMessageInUrdu = Utils.getChatMessageInUrdu(context);

        for (int i = 0; i < chatMessageInEnglish.length; i++) {
            if (StringUtils.isNotEmpty(chatMessageInEnglish[i]) && StringUtils.isNotEmpty(chatMessageInUrdu[i]))
                chatMessagesTranslateds.add(new ChatMessagesTranslated(i + Constants.DIGIT_ONE, chatMessageInEnglish[i], chatMessageInUrdu[i]));
        }
        return chatMessagesTranslateds;
    }

    /**
     * @param chatMessageInUrdu       Text to Match
     * @param chatMessagesTranslateds List From Which To Match
     * @return If text matches return the english transalation against it.
     */
    public static Pair<Boolean, String> getTranslationIfExists(String
                                                                       chatMessageInUrdu, ArrayList<ChatMessagesTranslated> chatMessagesTranslateds) {
        for (ChatMessagesTranslated chatMessagesTranslated : chatMessagesTranslateds) {
            if (chatMessagesTranslated.getChatMessageInUrdu().contains(chatMessageInUrdu))
                return new Pair<>(true, chatMessagesTranslated.getChatMessageInEnglish());
        }
        return new Pair<>(false, StringUtils.EMPTY);
    }

    /**
     * @param chatMessagesTranslated Object Containing English and Urdu Value
     * @return
     */
    public static String getConcatenatedTransalation(ChatMessagesTranslated
                                                             chatMessagesTranslated) {
        return chatMessagesTranslated.getChatMessageInUrdu() + TRANSALATION_SEPERATOR + chatMessagesTranslated.getChatMessageInEnglish();
    }

    /**
     * Shows the soft keyboard
     */
    public static void showSoftKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }

    /**
     * Get String Value After Applying HTML
     *
     * @param html
     * @return String : After Applying HTML to String.
     */
    public static String getTextFromHTML(String html) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return String.valueOf(Html.fromHtml(html));
        } else {
            return String.valueOf(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
        }
    }

    /**
     * Set ImageView Drawable
     *
     * @param imageView : ImageView
     * @param drawable  : Drawable Id
     */
    public static void setImageDrawable(ImageView imageView, int drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.setImageDrawable(imageView.getContext().getResources().getDrawable(drawable, null));
        } else {
            imageView.setImageDrawable(imageView.getContext().getResources().getDrawable(drawable));
        }
    }

    /**
     * Remove Navigation Drawer List Item
     */
    public static void removeOrHideItemFromNavigationDrawerList(String
                                                                        itemToRemove, List<String> titlesEnglish, List<String> titlesUrdu, List<String> newLabelToShow) {
        int position = -1;
        for (int i = 0; i < titlesEnglish.size(); i++) {
            if (titlesEnglish.get(i).contains(itemToRemove)) {
                position = i;
                break;
            }
        }

        if (position != -1) {
            titlesEnglish.remove(position);
            titlesUrdu.remove(position);
            newLabelToShow.remove(position);
        }
    }

    /**
     * Generate firebase event for the synergy of calling.
     *
     * @param context   : Calling Context
     * @param callData  : Current Trip Model Deta
     * @param eventName : Firebase Event Name
     */
    public static void generateFirebaseEventForCalling(Context context, NormalCallData
            callData, String eventName) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("lat", AppPreferences.getLatitude());
            jsonObject.put("lng", AppPreferences.getLongitude());
            jsonObject.put("whatsapp_installed", Utils.isAppInstalledWithPackageName(context, Constants.ApplicationsPackageName.WHATSAPP_PACKAGE));
            if (callData != null) {
                jsonObject.put("category", callData.getServiceCode());
                jsonObject.put("booking_id", callData.getTripId());
                Utils.logEvent(context, callData.getPassId(), eventName, jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Open calling option dialog and navigate to whatsapp or phone dialer on basis of user's choice
     *
     * @param callNumber : Phone Number
     */
    public static void openCallDialog(Context context, NormalCallData callData, String
            callNumber) {
        if (StringUtils.isEmpty(callNumber)) {
            Utils.appToastDebug("Number is empty");
            return;
        }
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_call_booking, null, false);
        DialogCallBookingBinding mBinding = DialogCallBookingBinding.bind(view);
        dialog.setContentView(mBinding.getRoot());
        mBinding.setListener(new BookingCallListener() {
            @Override
            public void onCallOnPhone() {
                Utils.generateFirebaseEventForCalling(context, callData, Constants.AnalyticsEvents.ON_CALL_BUTTON_CLICK_MOBILE);
                if (callNumber.startsWith(context.getString(R.string.country_code_pk)))
                    Utils.callingIntent(context, Utils.phoneNumberToShow(callNumber));
                else
                    Utils.callingIntent(context, callNumber);
                dialog.dismiss();
            }

            @Override
            public void onCallOnWhatsapp() {
                Utils.generateFirebaseEventForCalling(context, callData, Constants.AnalyticsEvents.ON_CALL_BUTTON_CLICK_WHATSAPP);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                if (callNumber.startsWith(context.getString(R.string.country_code_pk)))
                    intent.setData(Uri.parse(String.valueOf(new StringBuilder(Constants.WHATSAPP_URI_PREFIX).append(callNumber))));
                else
                    intent.setData(Uri.parse(String.valueOf(new StringBuilder(Constants.WHATSAPP_URI_PREFIX).append(Utils.phoneNumberForServer(callNumber)))));
                context.startActivity(intent);
                dialog.dismiss();
            }
        });
        mBinding.iVCallOnMobile.setImageResource(R.drawable.ic_mobile_call);
        mBinding.iVCallOnWhatsapp.setImageResource(R.drawable.ic_whatsapp_call);
        dialog.show();
    }

    /**
     * Set Scale Animation (Zoom In and Zoom Out Animation)
     * @param view : On Which Animation Has To Perform
     */
    public static void setScaleAnimation(View view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(Constants.SET_SCALE_ANIMATION_FROM_X, Constants.SET_SCALE_ANIMATION_TO_X,
                Constants.SET_SCALE_ANIMATION_FROM_Y, Constants.SET_SCALE_ANIMATION_TO_Y,
                Animation.RELATIVE_TO_SELF, Constants.SET_SCALE_ANIMATION_PIVOT_X,
                Animation.RELATIVE_TO_SELF, Constants.SET_SCALE_ANIMATION_PIVOT_Y);
        scaleAnimation.setRepeatCount(Constants.SET_SCALE_ANIMATION_REPEAT_COUNT);
        scaleAnimation.setDuration(Constants.SET_SCALE_ANIMATION_DURATION);
        scaleAnimation.setStartOffset(Constants.SET_SCALE_DELAY);
        scaleAnimation.setRepeatMode(Animation.REVERSE);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                scaleAnimation.setStartOffset(DIGIT_ZERO);
            }
        });
        view.setAnimation(scaleAnimation);
    }
}
