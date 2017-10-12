package com.bykea.pk.partner.dagger2.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.bykea.pk.partner.DriverApp;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SharedPrefModule {

    @Singleton
    @Provides
    Context provideContext(){
        return DriverApp.getApplication();
    }

    @Singleton
    @Provides
    SharedPreferences provideSharedPreferences(Context mContext){
        return PreferenceManager.getDefaultSharedPreferences(mContext);
    }
}
