package com.bykea.pk.partner.ui.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bykea.pk.partner.ui.fragments.LoginFragment;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.NetworkChangeListener;
import com.bykea.pk.partner.utils.Permissions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.PilotData;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Utils;


public class LoginActivity extends BaseActivity {

    public static boolean isRegisterFragment = false;
    private PilotData pilotData;
    private LoginActivity mCurrentActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        mCurrentActivity = this;
        pilotData = new PilotData();
        LoginFragment mainFragment = new LoginFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in,
                R.anim.fade_out);
        fragmentTransaction.replace(R.id.containerView, mainFragment, null);
        fragmentTransaction.commit();
        ActivityStackManager.getInstance(mCurrentActivity.getApplicationContext()).restartLocationService();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (isRegisterFragment) {
            popLastTwoFragment();
        } else {
            super.onBackPressed();
        }
    }


    private void popLastTwoFragment() {
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in,
                R.anim.fade_out);
        getSupportFragmentManager().popBackStack("numberVerification", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().popBackStack("codeVerification", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public PilotData getPilotData() {
        return pilotData;
    }

}
