package com.bykea.pk.partner.ui.activities;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bykea.pk.partner.Notifications;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.LoadBoardListingData;
import com.bykea.pk.partner.models.data.PilotData;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.fragments.HomeFragment;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.adapters.ActiveHomeLoadBoardListAdapter;
import com.bykea.pk.partner.ui.helpers.adapters.NavDrawerAdapter;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Permissions;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
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

    private ActiveHomeLoadBoardListAdapter mloadBoardListAdapter;
    private ArrayList<LoadBoardListingData> mlist = new ArrayList<>();


    @BindView(R.id.toolbarLine)
    View toolbarLine;
    @BindView(R.id.containerView)
    FrameLayout containerView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.activeHomeLoadBoardList)
    RecyclerView activeHomeLoadBoardList;
    @BindView(R.id.drawerMainActivity)
    public DrawerLayout drawerLayout;

    @BindView(R.id.bottomSheetToolbarDivider)
    public View bottomSheetToolbarDivider;
    @BindView(R.id.bottomSheetToolbarLayout)
    public FrameLayout bottomSheetToolbarLayout;
    @BindView(R.id.bottomSheetPickDropDivider)
    public View bottomSheetPickDropDivider;
    @BindView(R.id.bottomSheetPickDropLayout)
    public LinearLayout bottomSheetPickDropLayout;
    @BindView(R.id.bottomSheetRefreshIV)
    public AppCompatImageView bottomSheetRefreshIV;
    @BindView(R.id.bottomSheetBackIV)
    public AppCompatImageView bottomSheetBackIV;
    @BindView(R.id.bottomSheetDropTV)
    public FontTextView bottomSheetDropTV;
    @BindView(R.id.bottomSheetPickTV)
    public FontTextView bottomSheetPickTV;
    @BindView(R.id.bottomSheet)
    public AppBarLayout bottomSheet;

    @BindView(R.id.achaconnectionTv)
    TextView achaconnectionTv;

    @BindView(R.id.connectionStatusIv)
    ImageView connectionStatusIv;

    /*@BindView(R.id.achaconnectionTv1)
    TextView achaconnectionTv1;

    @BindView(R.id.connectionStatusIv1)
    ImageView connectionStatusIv1;*/
    private BottomSheetBehavior bottomSheetBehavior;

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
//        Utils.setMixPanelUserId(mCurrentActivity);
        Utils.disableBatteryOptimization(this, mCurrentActivity);

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
                showHomeFragment();
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
        Fragment currentFragment = mCurrentActivity.getSupportFragmentManager().findFragmentById(R.id.containerView);
        if (currentFragment instanceof HomeFragment) {
            ((HomeFragment) currentFragment).onEvent(action);
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

    public void setupBottomSheet(){
        mloadBoardListAdapter = new ActiveHomeLoadBoardListAdapter(this, mlist, new ActiveHomeLoadBoardListAdapter.ItemClickListener() {
            @Override
            public void onClick(LoadBoardListingData item) {
                if(bottomSheetBehavior != null && bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    Utils.appToast(mCurrentActivity, item.getPickup_zone().getUrduName());
                }
            }
        });
        activeHomeLoadBoardList.setLayoutManager(new LinearLayoutManager(this));
        activeHomeLoadBoardList.setHasFixedSize(true);
        activeHomeLoadBoardList.setAdapter(mloadBoardListAdapter);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState){
                    case BottomSheetBehavior.PEEK_HEIGHT_AUTO:
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                toggleBottomSheetToolbar(slideOffset);
            }
        });
        bottomSheetRefreshIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.appToast(v.getContext(), "Refresh");
            }
        });
        bottomSheetBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomSheetBehavior != null)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        bottomSheetPickTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.appToast(v.getContext(), "Pick");
            }
        });
        bottomSheetDropTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.appToast(v.getContext(), "Drop");
            }
        });
        hideLoadBoardBottomSheet();
    }

    public void showLoadBoardBottomSheet(ArrayList<LoadBoardListingData> list){
        if(bottomSheet != null){
            bottomSheet.setVisibility(View.VISIBLE);
            updateList(list);
        }
    }
    public void hideLoadBoardBottomSheet(){
        if(bottomSheet != null){
            bottomSheet.setVisibility(View.GONE);
            if(mlist != null)
                mlist.clear();
        }
    }
    public void updateList(ArrayList<LoadBoardListingData> list){
        if(mloadBoardListAdapter != null && mlist != null){
            mlist.clear();
            mlist.addAll(list);
            mloadBoardListAdapter.notifyDataSetChanged();
        }
    }

    private void toggleBottomSheetToolbar(float alpha){
        if(alpha > 0.7f){
            bottomSheetToolbarLayout.setVisibility(View.VISIBLE);
            bottomSheetPickDropLayout.setVisibility(View.VISIBLE);
            bottomSheetToolbarDivider.setVisibility(View.VISIBLE);
            bottomSheetPickDropDivider.setVisibility(View.VISIBLE);
            bottomSheetPickDropDivider.setAlpha(alpha);
            bottomSheetPickDropLayout.setAlpha(alpha);
            bottomSheetToolbarDivider.setAlpha(alpha);
            bottomSheetToolbarLayout.setAlpha(alpha);
        } else {
            bottomSheetToolbarLayout.setVisibility(View.GONE);
            bottomSheetPickDropLayout.setVisibility(View.GONE);
            bottomSheetToolbarDivider.setVisibility(View.GONE);
            bottomSheetPickDropDivider.setVisibility(View.GONE);
            bottomSheetPickDropDivider.setAlpha(alpha);
            bottomSheetPickDropLayout.setAlpha(alpha);
            bottomSheetToolbarDivider.setAlpha(alpha);
            bottomSheetToolbarLayout.setAlpha(alpha);
        }
    }

    public void toggleAchaConnection(int visibility){
        achaconnectionTv.setVisibility(visibility);
        connectionStatusIv.setVisibility(visibility);
    }
    /*
     * Update Connection Status according to Signal Strength
     * */
    public void setConnectionStatus() {
        String connectionStatus = Connectivity.getConnectionStatus(mCurrentActivity);

        //achaconnectionTv.setText(connectionStatus);
        //achaconnectionTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable._good_sattelite, 0, 0, 0);
        switch (connectionStatus) {
            case "Unknown Status":
                //tvConnectionStatus.setBackgroundColor(ContextCompat.getColor(mCurrentActivity, R.color.textColorSecondary));
                break;
            case "Battery Low":
                achaconnectionTv.setTextColor(ContextCompat.getColor(mCurrentActivity, R.color.color_error));
                achaconnectionTv.setText("لو بیٹری");
                connectionStatusIv.setImageResource(R.drawable.empty_battery);

//                achaconnectionTv1.setTextColor(ContextCompat.getColor(mCurrentActivity, R.color.color_error));
//                achaconnectionTv1.setText("لو بیٹری");
//                connectionStatusIv1.setImageResource(R.drawable.empty_battery);
                break;
            case "Poor Connection":
            case "Fair Connection":
            case "No Connection":

                achaconnectionTv.setTextColor(ContextCompat.getColor(mCurrentActivity, R.color.black_3a3a3a));
                achaconnectionTv.setText("برا کنکشن");
//                achaconnectionTv1.setText("برا کنکشن");
                //tvConnectionStatus.setBackgroundColor(ContextCompat.getColor(mCurrentActivity, R.color.color_fair_connection));
                break;
            case "Good Connection":
                achaconnectionTv.setTextColor(ContextCompat.getColor(mCurrentActivity, R.color.black_3a3a3a));
                achaconnectionTv.setText("اچھا کنکشن");
//                achaconnectionTv1.setText("اچھا کنکشن");
                connectionStatusIv.setImageResource(R.drawable.wifi_connection_signal_symbol);
//                connectionStatusIv1.setImageResource(R.drawable.wifi_connection_signal_symbol);
                break;
        }

//        if (connectionStatus.equalsIgnoreCase("Unknown Status")) {
//            tvConnectionStatus.setBackgroundColor(ContextCompat.getColor(mCurrentActivity, R.color.textColorSecondary));
//        } else if (connectionStatus.equalsIgnoreCase("Battery Low")) {
//            tvConnectionStatus.setBackgroundColor(ContextCompat.getColor(mCurrentActivity, R.color.color_error));
//            tvConnectionStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.low_battery_icon, 0, 0, 0);
//        } else if (connectionStatus.equalsIgnoreCase("Poor Connection") ||
//                connectionStatus.equalsIgnoreCase("Fair Connection") ||
//                connectionStatus.equalsIgnoreCase("No Connection")) {
//            tvConnectionStatus.setBackgroundColor(ContextCompat.getColor(mCurrentActivity, R.color.color_fair_connection));
//        } else if (connectionStatus.equalsIgnoreCase("Good Connection")) {
//            tvConnectionStatus.setBackgroundColor(ContextCompat.getColor(mCurrentActivity, R.color.colorPrimary));
//        }

    }
}
