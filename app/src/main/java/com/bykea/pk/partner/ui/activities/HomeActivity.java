package com.bykea.pk.partner.ui.activities;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bykea.pk.partner.Notifications;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.PilotData;
import com.bykea.pk.partner.models.response.UpdateAppVersionResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.fragments.DataSaverDialogFragment;
import com.bykea.pk.partner.ui.fragments.HomeFragment;
import com.bykea.pk.partner.ui.fragments.WalletFragment;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.adapters.NavDrawerAdapter;
import com.bykea.pk.partner.ui.withdraw.WithdrawalActivity;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Permissions;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends BaseActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();

    private HomeActivity mCurrentActivity;
    public String navTitles[];
    public String navIcons[];
    public String navDrawerNewText[];
    public NavDrawerAdapter recyclerViewAdapter;
    public ActionBarDrawerToggle drawerToggle;
    public static int visibleFragmentNumber = 1;
    private EventBus mBus = EventBus.getDefault();
    private PilotData pilotData;
    private UserRepository mUserRepository;

    @BindView(R.id.toolbarLine)
    View toolbarLine;
    @BindView(R.id.containerView)
    FrameLayout containerView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.drawerMainActivity)
    public DrawerLayout drawerLayout;

    @BindView(R.id.bottomSheetLoader)
    public ProgressBar bottomSheetLoader;
    @BindView(R.id.appBottomBarLayout)
    public AppBarLayout bottomSheet;

    @BindView(R.id.achaconnectionTv)
    FontTextView achaconnectionTv;

    @BindView(R.id.connectionStatusIv)
    AppCompatImageView connectionStatusIv;

    /**
     * loadboard bottom sheet in main screen when driver is active and cash user
     */
    private BottomSheetBehavior bottomSheetBehavior;

    private boolean isDialogShown, isSettingsApiFirstTimeCalled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        mCurrentActivity = this;
        ButterKnife.bind(this);
        mUserRepository = new UserRepository();
        pilotData = AppPreferences.getPilotData();
        setToolbarLogo();

        Utils.setZendeskIdentity();

        initViews();
        setupDrawerToggle();
        Utils.unlockScreen(this);
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
        Utils.disableBatteryOptimization(this, mCurrentActivity);
        Utils.clearSharedPrefIfDirty(mCurrentActivity);
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
        } else if (requestCode == WithdrawalActivity.REQ_CODE_WITH_DRAW) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.containerView);
            if (currentFragment instanceof WalletFragment) {
                ((WalletFragment) currentFragment).updateHistory();
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        /*SETTING SERVICE CONTEXT WITH ACTIVITY TO SEND BROADCASTS*/
//        LocationService.setContext(HomeActivity.this);
//        WebIORequestHandler.getInstance().setContext(mCurrentActivity);
        AppPreferences.setProfileUpdated(true);

        //we need to validate app version on every visit on home screen.
        updateAppVersionIfRequired();

        checkIfBackgroundDataAccessible();
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

    @SuppressLint("WrongConstant")
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(Gravity.START); //CLOSE Nav Drawer!
        } else {
            //close bottom sheet if is in expanded state
            if (bottomSheetBehavior != null && bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else {
                if (visibleFragmentNumber == 1) {
                    super.onBackPressed();
                } else {

                    if (visibleFragmentNumber == 3) showToolbar();
                    //Add the Very First i.e Squad Fragment to the Container
                    showHomeFragment();
                }
            }
        }
    }

    /**
     * This method loads home fragment with in HomeActivity context.
     */
    private void showHomeFragment() {
        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in,
                R.anim.fade_out);
        fragmentTransaction.replace(R.id.containerView, homeFragment, null);
        fragmentTransaction.commit();
        recyclerViewAdapter.notifyDataSetChanged();
        visibleFragmentNumber = 1;
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
        navDrawerNewText = getResources().getStringArray(R.array.navDrawerNewText);

        recyclerViewAdapter = new NavDrawerAdapter(navTitles, navIcons,navDrawerNewText, mCurrentActivity);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (AppPreferences.getSettings() == null) {
            Dialogs.INSTANCE.showLoader(mCurrentActivity);
            isSettingsApiFirstTimeCalled = true;
        }
        mUserRepository.requestSettings(mCurrentActivity, new UserDataHandler() {
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

        setupBottomSheet();
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
        if (getSupportFragmentManager() != null) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.containerView);
            if (currentFragment != null) {
                if (currentFragment instanceof HomeFragment) {
                    ((HomeFragment) currentFragment).onEvent(action);
                }
            }

        }
    }


    /**
     * This method handles new intent when activity is started with Intent.FLAG_ACTIVITY_CLEAR_TOP.
     * When Constants.Extras.NAVIGATE_TO_HOME_SCREEN is set to true with Extras while staring HomeActivity it
     * will load HomeFragment even if any other Fragment from side bar menu is already loaded.
     *
     * @see Intent#FLAG_ACTIVITY_CLEAR_TOP
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && intent.getBooleanExtra(Constants.Extras.NAVIGATE_TO_HOME_SCREEN, false)) {
            showHomeFragment();
        }
    }

    /**
     * This method compares App Version in user model with App version on User's Device and updates
     * App version on server via API if required.
     */
    private void updateAppVersionIfRequired() {
        if (!Utils.getVersion().equalsIgnoreCase(AppPreferences.getAppVersion())) {
            mUserRepository.updateAppVersion(new UserDataHandler() {
                @Override
                public void onUpdateAppVersionResponse(UpdateAppVersionResponse response) {
                    if (response.isSuccess()) {
                        AppPreferences.setAppVersion(Utils.getVersion());
                    }
                }
            });
        }
    }

    /**
     * Check the availability of cellular data access in background.
     */
    private void checkIfBackgroundDataAccessible() {
        if (!Connectivity.isBackgroundDataAccessAvailable(mCurrentActivity)) {
            DataSaverDialogFragment dialogFragment = new DataSaverDialogFragment();
            dialogFragment.setCancelable(false);
            dialogFragment.show(mCurrentActivity.getSupportFragmentManager(), TAG);
        }
    }

    /**
     * initialize loadboard listing with empty data and bottom sheet with behavior
     */
    public void setupBottomSheet() {
//    TODO:COMMENTED
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(bottomSheet, "elevation", 0.1f));
            bottomSheet.setStateListAnimator(stateListAnimator);
        }
        bottomSheet.bringToFront();
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        //    TODO:COMMENTED
//        showBottomSheetLoader();

        if (AppPreferences.getAvailableStatus()) showLoadBoardBottomSheet();
        else hideLoadBoardBottomSheet();
    }

    /**
     * Visible on main screen when active and cash user and GONE for other screens navigate from side menu
     *
     * @param visibility VISIBLE/GONE
     */
    public void toggleBottomSheetOnNavigationMenuSelection(int visibility) {
        if (bottomSheet != null)
            bottomSheet.setVisibility(visibility);
    }

    /**
     * visible loadboard
     * <p>
     * // * @param list loadboard jobs data
     */

    //    TODO:COMMENTED
    public void showLoadBoardBottomSheet() {
        if (bottomSheet != null) {
            bottomSheet.setVisibility(View.VISIBLE);
        }
    }

    /**
     * GONE loadboard bottom sheet
     */
    public void hideLoadBoardBottomSheet() {
        if (bottomSheet != null) {
            bottomSheet.setVisibility(View.GONE);
        }
    }

    /**
     * VISIBLE/GONE connections status on main screen's toolbar
     *
     * @param visibility VISIBLE/GONE
     */
    public void toggleAchaConnection(int visibility) {
        achaconnectionTv.setVisibility(visibility);
        connectionStatusIv.setVisibility(visibility);
    }

    /*
     * Update Connection Status according to Signal Strength
     * */
    public void setConnectionStatus() {
        String connectionStatus = Connectivity.getConnectionStatus(mCurrentActivity);
        switch (connectionStatus) {
            case Constants.ConnectionSignalStatus.UNKNOWN_STATUS:
                break;
            case Constants.ConnectionSignalStatus.BATTERY_LOW:
                achaconnectionTv.setTextColor(ContextCompat.getColor(mCurrentActivity, R.color.color_error));
                achaconnectionTv.setText(getString(R.string.low_battery_ur));
                connectionStatusIv.setImageResource(R.drawable.empty_battery);
                break;
            case Constants.ConnectionSignalStatus.POOR_STRENGTH:
            case Constants.ConnectionSignalStatus.FAIR_STRENGTH:
            case Constants.ConnectionSignalStatus.NO_CONNECTIVITY:

                achaconnectionTv.setTextColor(ContextCompat.getColor(mCurrentActivity, R.color.black_3a3a3a));
                achaconnectionTv.setText(getString(R.string.bura_connection_ur));
                break;
            case Constants.ConnectionSignalStatus.GOOD_STRENGTH:
                achaconnectionTv.setTextColor(ContextCompat.getColor(mCurrentActivity, R.color.black_3a3a3a));
                achaconnectionTv.setText(getString(R.string.acha_connection_ur));
                connectionStatusIv.setImageResource(R.drawable.wifi_connection_signal_symbol);
                break;
        }
    }
}
