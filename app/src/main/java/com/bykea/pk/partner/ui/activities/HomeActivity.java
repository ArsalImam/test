package com.bykea.pk.partner.ui.activities;

import android.content.Intent;
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
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.fragments.HomeFragment;
import com.bykea.pk.partner.ui.helpers.adapters.NavDrawerAdapter;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Permissions;
import com.bykea.pk.partner.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeActivity extends BaseActivity {

    private HomeActivity mCurrentActivity;
    public String navTitles[];
    public String navIcons[];
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
    private boolean isDialogShown, isSettingsApiFirstTimeCalled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        mCurrentActivity = this;
        ButterKnife.bind(this);
        pilotData = AppPreferences.getPilotData();
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
//        Utils.setMixPanelUserId(mCurrentActivity);

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
                ActivityStackManager.getInstance().startLocationService(mCurrentActivity);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
         /*SETTING SERVICE CONTEXT WITH ACTIVITY TO SEND BROADCASTS*/
//        LocationService.setContext(HomeActivity.this);
//        WebIORequestHandler.getInstance().setContext(mCurrentActivity);
        AppPreferences.setProfileUpdated(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(Gravity.START); //CLOSE Nav Drawer!
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
        Dialogs.INSTANCE.dismissDialog();
    }

    private void initViews() {
        Utils.keepScreenOn(mCurrentActivity);

        visibleFragmentNumber = 1;//FOR NAVIGATION DRAWER FRAGMENT

        //Setup Titles and Icons of Navigation Drawer
        navTitles = getResources().getStringArray(R.array.navDrawerItems);
        navIcons = getResources().getStringArray(R.array.navDrawerIcons);

        recyclerViewAdapter = new NavDrawerAdapter(navTitles, navIcons, mCurrentActivity);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (AppPreferences.getSettings() == null) {
            Dialogs.INSTANCE.showLoader(mCurrentActivity);
            isSettingsApiFirstTimeCalled = true;
        }
        new UserRepository().requestSettings(mCurrentActivity, new UserDataHandler() {
            @Override
            public void onGetSettingsResponse(boolean isUpdated) {
                if (isSettingsApiFirstTimeCalled) {
                    isSettingsApiFirstTimeCalled = false;
                    Dialogs.INSTANCE.dismissDialog();
                }
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.containerView);
                if (currentFragment instanceof HomeFragment) {
                    ((HomeFragment) currentFragment).getCurrentVersion();
                    if (isUpdated) {
                        ((HomeFragment) currentFragment).initRangeBar();
                    }
                }
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

    void setupDrawerToggle() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, getToolbar(),
                R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Utils.hideKeyboard(mCurrentActivity);
                recyclerViewAdapter.notifyDataSetChanged();
//                recyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };

        drawerLayout.addDrawerListener(drawerToggle);
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        drawerToggle.syncState();

    }

    public PilotData getPilotData() {
        return pilotData;
    }

    public void setPilotData(PilotData data) {
        pilotData = data;
    }


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
