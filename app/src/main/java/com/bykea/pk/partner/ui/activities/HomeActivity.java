package com.bykea.pk.partner.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.bykea.pk.partner.Notifications;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.communication.socket.WebIORequestHandler;
import com.bykea.pk.partner.models.data.PilotData;
import com.bykea.pk.partner.models.response.SettingsResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.services.LocationService;
import com.bykea.pk.partner.ui.fragments.HomeFragment;
import com.bykea.pk.partner.ui.helpers.adapters.NavDrawerAdapter;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.NetworkChangeListener;
import com.bykea.pk.partner.utils.Permissions;
import com.bykea.pk.partner.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeActivity extends BaseActivity {

    private HomeActivity mCurrentActivity;
    public String navTitles[];
    public TypedArray navIcons;
    public NavDrawerAdapter recyclerViewAdapter;
    public ActionBarDrawerToggle drawerToggle;
    public static int visibleFragmentNumber = 1;
    private EventBus mBus = EventBus.getDefault();
    private PilotData pilotData;

    @Bind(R.id.toolbarLine)
    View toolbarLine;
    @Bind(R.id.containerView)
    FrameLayout containerView;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.drawerMainActivity)
    public DrawerLayout drawerLayout;
    ProgressDialog progressDialog;
    private boolean isDialogShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        mCurrentActivity = this;
        ButterKnife.bind(this);
        pilotData = AppPreferences.getPilotData(mCurrentActivity);
        setToolbarLogo();
        initViews();
        setupDrawerToggle();
        //Add the Very First i.e Squad Fragment to the Container
        HomeFragment mainFragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.containerView, mainFragment, null);
        fragmentTransaction.commit();

        if (!Permissions.hasLocationPermissions(mCurrentActivity)) {
            Permissions.getLocationPermissions(mCurrentActivity);
        }

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            Dialogs.INSTANCE.showLocationSettings(mCurrentActivity, Permissions.LOCATION_PERMISSION);

        Notifications.clearNotifications(mCurrentActivity);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Permissions.LOCATION_PERMISSION) {
            LocationManager locationManager;
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                Dialogs.INSTANCE.showLocationSettings(mCurrentActivity, Permissions.LOCATION_PERMISSION);
            else {
                ActivityStackManager.getInstance(mCurrentActivity).startLocationService();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.location.GPS_ENABLED_CHANGE");
        intentFilter.addAction("android.location.PROVIDERS_CHANGED");
        registerReceiver(networkChangeListener, intentFilter);
         /*SETTING SERVICE CONTEXT WITH ACTIVITY TO SEND BROADCASTS*/
        LocationService.setContext(HomeActivity.this);
        ActivityStackManager.activities = 1;
        WebIORequestHandler.getInstance().setContext(mCurrentActivity);
        AppPreferences.setProfileUpdated(mCurrentActivity, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkChangeListener);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(Gravity.LEFT); //CLOSE Nav Drawer!
        } else {
            if (visibleFragmentNumber == 1) {
                super.onBackPressed();
            } else {

                if (visibleFragmentNumber == 3) showToolbar();
                //Add the Very First i.e Squad Fragment to the Container
                HomeFragment homeFragment = new HomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.fade_in,
                        R.anim.fade_out);
                fragmentTransaction.replace(R.id.containerView, homeFragment, null);
                fragmentTransaction.commit();
                recyclerViewAdapter.notifyDataSetChanged();
                visibleFragmentNumber = 1;

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
        Dialogs.INSTANCE.dismissDialog();
    }

    private void initViews() {

        progressDialog = new ProgressDialog(mCurrentActivity);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.internet_error));
        Utils.keepScreenOn(mCurrentActivity);

        visibleFragmentNumber = 1;//FOR NAVIGATION DRAWER FRAGMENT

        //Setup Titles and Icons of Navigation Drawer
        navTitles = getResources().getStringArray(R.array.navDrawerItems);
        navIcons = getResources().obtainTypedArray(R.array.navDrawerIcons);

        recyclerViewAdapter = new NavDrawerAdapter(navTitles, navIcons, mCurrentActivity);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        UserRepository repository = new UserRepository();
        repository.requestSettings(mCurrentActivity, new UserDataHandler() {
            @Override
            public void onGetSettingsResponse(SettingsResponse settingsResponse) {

            }
        });


    }

    public void hideToolbar() {
        getToolbar().setVisibility(View.GONE);
        toolbarLine.setVisibility(View.GONE);
    }

    public void showToolbar() {
        getToolbar().setVisibility(View.VISIBLE);
        toolbarLine.setVisibility(View.VISIBLE);
    }

    public void resetNavigation() {
        recyclerViewAdapter.notifyDataSetChanged();
    }

    private void dismissProgressDialog() {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProgressDialog() {
        try {
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setupDrawerToggle() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, getToolbar(),
                R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
//                recyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                recyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                Utils.hideKeyboard(mCurrentActivity);
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        drawerToggle.syncState();

    }

    public PilotData getPilotData() {
        return pilotData;
    }

    public void setPilotData(PilotData data) {
        pilotData = data;
    }

    private NetworkChangeListener networkChangeListener = new NetworkChangeListener() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("android.location.GPS_ENABLED_CHANGE") ||
                    intent.getAction().equalsIgnoreCase("android.location.PROVIDERS_CHANGED")) {
                if (!Utils.isGpsEnable(mCurrentActivity)) {
                    Dialogs.INSTANCE.showLocationSettings(mCurrentActivity, Permissions.LOCATION_PERMISSION);
                } else {
                    Dialogs.INSTANCE.dismissDialog();
                }
            } else {
                if (Connectivity.isConnectedFast(context)) {
                    dismissProgressDialog();
                } else {
                    showProgressDialog();
                }
            }
            mBus.post(Keys.CONNECTION_BROADCAST);
        }
    };


    public boolean isDialogShown() {
        return isDialogShown;
    }

    public void setDialogShown(boolean dialogShown) {
        isDialogShown = dialogShown;
    }

    @Override
    public void onEvent(String action) {
        super.onEvent(action);
        Fragment currentFragment = mCurrentActivity.getSupportFragmentManager().findFragmentById(R.id.containerView);
        if (currentFragment instanceof HomeFragment) {
            ((HomeFragment) currentFragment).onEvent(action);
        }
    }
}
