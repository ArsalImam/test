package com.bykea.pk.partner.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;


@TargetApi(23)
public class Permissions {

    public static int CAMERA_PERMISSION = 10;
    public static int LOCATION_PERMISSION = 11;
    public static int STORAGE_PERMISSION = 12;
    public static int SMS_PERMISSION = 13;
    public static int CALL_PERMISSION = 14;
    public static int ACCOUNTS_PERMISSION = 15;
    public static int MIC_PERMISSION = 16;

    public static boolean hasCameraPermissions(Context context) {
        String permission = "android.permission.CAMERA";
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public static void getCameraPermissions(Context context) {
        String[] permissions = {"android.permission.CAMERA"};
        ((Activity) context).requestPermissions(permissions, CAMERA_PERMISSION);
    }

    public static boolean hasLocationPermissions(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            String permission1 = "android.permission.ACCESS_FINE_LOCATION";
            String permission2 = "android.permission.ACCESS_COARSE_LOCATION";
            int res1 = context.checkCallingOrSelfPermission(permission1);
            int res2 = context.checkCallingOrSelfPermission(permission2);
            return (res1 == PackageManager.PERMISSION_GRANTED &&
                    res2 == PackageManager.PERMISSION_GRANTED);
        } else {
            return true;
        }
    }

    public static boolean hasCallPermission(Context context) {
        return (context.checkCallingOrSelfPermission("android.permission.CALL_PHONE") ==
                PackageManager.PERMISSION_GRANTED);
    }

    public static void getCallPermission(Context context) {
        String[] permissions = {"android.permission.CALL_PHONE"};
        ((Activity) context).requestPermissions(permissions, CALL_PERMISSION);
    }

    public static void getLocationPermissions(Activity context) {
        String[] permissions = {"android.permission.ACCESS_FINE_LOCATION"};
        context.requestPermissions(permissions, LOCATION_PERMISSION);
    }

    public static void getLocationPermissions(Fragment context) {
        String[] permissions = {"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"};
        context.requestPermissions(permissions, LOCATION_PERMISSION);
    }

    public static boolean hasContactsPermissions(Context context) {
        String permission = "android.permission.GET_ACCOUNTS";
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public static void getContactsPermissions(Context context) {
        String[] permissions = {"android.permission.GET_ACCOUNTS"};
        ((Activity) context).requestPermissions(permissions, ACCOUNTS_PERMISSION);
    }

    public static boolean hasStoragePermissions(Context context) {
        String permission = "android.permission.WRITE_EXTERNAL_STORAGE";
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public static void getStoragePermissions(Context context) {
        String[] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE"};
        ((Activity) context).requestPermissions(permissions, STORAGE_PERMISSION);
    }

    public static boolean hasMicPermission(Context context) {
        return (context.checkCallingOrSelfPermission("android.permission.RECORD_AUDIO") ==
                PackageManager.PERMISSION_GRANTED) &&
                (context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") ==
                        PackageManager.PERMISSION_GRANTED);
    }

    public static void getMicPermission(Context context) {
        String[] permissions = {"android.permission.RECORD_AUDIO",
                "android.permission.WRITE_EXTERNAL_STORAGE"};
        ((Activity) context).requestPermissions(permissions, 15);
    }

    public static void getPhotosPermissions(Context context) {
        String[] permissions = {"android.permission.CAMERA"};
        ((Activity) context).requestPermissions(permissions, STORAGE_PERMISSION);
    }

    public static boolean hasPermission(Context context, String permission) {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, permission);
    }
}