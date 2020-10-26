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
-keep class com.bykea.pk.partner.utils.audio.** { *; }
-keep class com.bykea.pk.partner.dal.** { *; }
-keep class java.io.** { *; }
-keep class id.zelory.** { *; }
-keep class io.reactivex.** { *; }
-keep class com.wang.** { *; }
-keep class com.onesignal.** { *; }
-keep class android.support.** { *; }
-keep class org.opencv.R
-keep class org.opencv.R$styleable
-keep class org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-keep interface android.support.** { *; }
-dontwarn butterknife.internal.**
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault
-dontwarn org.opencv.R
-dontwarn org.opencv.R$styleable
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
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

# Class names are needed in reflection
-keepnames class com.amazonaws.**
-keepnames class com.amazon.**
# Request handlers defined in request.handlers
-keep class com.amazonaws.services.**.*Handler
# The following are referenced but aren't required to run
-dontwarn com.fasterxml.jackson.**
-dontwarn org.apache.commons.logging.**
# Android 6.0 release removes support for the Apache HTTP client
-dontwarn org.apache.http.**
# The SDK has several references of Apache HTTP client
-dontwarn com.amazonaws.http.**
-dontwarn com.amazonaws.metrics.**

##PROGUARD RULES FOR ZENDESK

# Sdk
-keep public interface com.zendesk.** { *; }
-keep public class com.zendesk.** { *; }
-dontwarn java.awt.**
# Appcompat and support
-dontwarn android.app.Notification
# Gson
-keep interface com.google.gson.** { *; }
-keep class com.google.gson.** { *; }
# Retrofit
-keep class com.google.inject.** { *; }
-keep class org.apache.http.** { *; }
-keep class org.apache.james.mime4j.** { *; }
-keep class javax.inject.** { *; }
-keep class retrofit.** { *; }
-keep interface retrofit.** { *; }
-dontwarn rx.**
-dontwarn com.google.appengine.api.urlfetch.**
-dontwarn okio.**

-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
#Picasso
-dontwarn com.squareup.okhttp.**
