# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\C\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name details the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-dontwarn okio.**
-dontwarn okhttp3.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-dontwarn retrofit.**
-dontwarn org.joda.**
-keep class butterknife.** { *; }
-keep class com.squareup.** { *; }
-keep class org.jsoup.** { *; }
-keep class com.github.jd.** { *; }
-keep class com.bykea.pk.partner.models.** { *; }
-keep class com.instabug.** { *; }
-keep class top.oply.opuslib.** { *; }
-keep class java.io.** { *; }
-keep class com.wang.** { *; }
-keep class com.onesignal.** { *; }
-keep class android.support.** { *; }
-keep interface android.support.** { *; }
-dontwarn butterknife.internal.**
-dontwarn com.instabug.**
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault
-keep class **$$ViewBinder { *; }
# Retain generated class which implement Unbinder.
-keep public class * implements butterknife.Unbinder { public <init>(**, android.view.View); }

-renamesourcefileattribute SourceFile

-keepattributes SourceFile,LineNumberTable

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keepattributes Exceptions, Signature, InnerClasses
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
        public static final *** NULL;
}
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
        @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
        public static final ** CREATOR;
}