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
    private GoogleApiClient mGoogleApiClient;
    private LoginActivity mCurrentActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        mCurrentActivity = this;
        buildGoogleApiClient();
        pilotData = new PilotData();
        LoginFragment mainFragment = new LoginFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in,
                R.anim.fade_out);
        fragmentTransaction.replace(R.id.containerView, mainFragment, null);
        fragmentTransaction.commit();
        ActivityStackManager.getInstance(mCurrentActivity.getApplicationContext()).restartLocationService();
        getLocation();
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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (isRegisterFragment) {
            popLastTwoFragment();
        } else {
            super.onBackPressed();
        }

    }


    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            Location location = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
            if (null != location) {
                pilotData.setLat(location.getLatitude() + "");
                pilotData.setLng(location.getLongitude() + "");
                AppPreferences.saveLocationFromLogin(mCurrentActivity, new LatLng(location.getLatitude(),
                        location.getLongitude()), "", location.getAccuracy(), Utils.isMockLocation(location, mCurrentActivity));
                Utils.infoLog("Current Device Location ", location.toString());
            } else {
                Utils.infoLog("Current Device Location ", "No Location Found.");
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(myConnectionCallbacks)
                .addOnConnectionFailedListener(myConnectionFailedListener)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    private GoogleApiClient.OnConnectionFailedListener myConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            mGoogleApiClient.connect();
        }
    };

    private GoogleApiClient.ConnectionCallbacks myConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            getLocation();
        }

        @Override
        public void onConnectionSuspended(int i) {
            mGoogleApiClient.connect();
        }
    };


    private void popLastTwoFragment() {
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in,
                R.anim.fade_out);
        getSupportFragmentManager().popBackStack("numberVerification", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().popBackStack("codeVerification", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public PilotData getPilotData() {
        return pilotData;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
