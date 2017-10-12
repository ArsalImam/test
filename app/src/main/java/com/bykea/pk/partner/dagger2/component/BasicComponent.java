package com.bykea.pk.partner.dagger2.component;

import android.content.SharedPreferences;
import com.bykea.pk.partner.dagger2.module.ApplicationModule;
import com.bykea.pk.partner.dagger2.module.SharedPrefModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class,SharedPrefModule.class})
public interface BasicComponent {
    SharedPreferences getSharedPref();
}
