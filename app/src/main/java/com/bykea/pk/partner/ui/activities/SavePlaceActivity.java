package com.bykea.pk.partner.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.PlacesResult;
import com.bykea.pk.partner.models.data.SavedPlaces;
import com.bykea.pk.partner.models.response.AddSavedPlaceResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.repositories.places.IPlacesDataHandler;
import com.bykea.pk.partner.repositories.places.PlacesDataHandler;
import com.bykea.pk.partner.repositories.places.PlacesRepository;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.AutoFitFontTextView;
import com.bykea.pk.partner.widgets.CustomMapView;
import com.bykea.pk.partner.widgets.FontEditText;
import com.bykea.pk.partner.widgets.FontTextView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;


import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SavePlaceActivity extends BaseActivity {

    private SavePlaceActivity mCurrentActivity;
    private GoogleMap mGoogleMap;
    private CustomMapView mapView;
    private boolean isSearchedLoc;

    @Bind(R.id.tvFromName)
    AutoFitFontTextView addressTv;
    @Bind(R.id.tvPlaceName)
    FontTextView tvPlaceName;

    @Bind(R.id.tvFromAddress)
    FontTextView tvFromAddress;
    @Bind(R.id.tvPlaceAddress)
    FontTextView tvPlaceAddress;

    @Bind(R.id.confirmBtn)
    FrameLayout confirmBtn;

    @Bind(R.id.etEditedName)
    FontEditText etEditedName;

    @Bind(R.id.etEditedAddress)
    FontEditText etEditedAddress;

    @Bind(R.id.etMobileNumber)
    FontEditText etMobileNumber;

    @Bind(R.id.loader)
    ProgressBar loader;

    private SavedPlaces mSavedPlaceToServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_save_place);
        ButterKnife.bind(this);
        mCurrentActivity = this;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setTitleCustomToolbarUrdu("ڈائریکٹری میں شامل کریں");
        setInitMap(savedInstanceState);
    }


    private void finishLoading() {
        loader.setIndeterminate(false);
        confirmBtn.setClickable(true);
    }

    private void startLoading() {
        loader.setIndeterminate(true);
        confirmBtn.setClickable(false);
    }


    private void setAddress(String result) {


        if (result.contains(",") && result.split(",").length > 1) {
            int lastIndex = result.lastIndexOf(',');
            addressTv.setText(result.substring(0, lastIndex));
            tvPlaceName.setText(result.substring(0, lastIndex));
            tvFromAddress.setText(result.substring(lastIndex + 1).trim());
            tvPlaceAddress.setText(result.substring(lastIndex + 1).trim());
        } else {
            addressTv.setText(result);
            tvPlaceName.setText(result);
            tvFromAddress.setText(result);
            tvPlaceAddress.setText(result);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        final Bundle mapViewSaveState = new Bundle(outState);
        mapView.onSaveInstanceState(mapViewSaveState);
        outState.putBundle("mapViewSaveState", mapViewSaveState);
        super.onSaveInstanceState(outState);
    }

    private void setInitMap(Bundle savedInstanceState) {
        mapView = (CustomMapView) findViewById(R.id.confirmMapFragment);
        final Bundle mapViewSavedInstanceState = savedInstanceState != null ? savedInstanceState.getBundle("mapViewSaveState") : null;
        mapView.onCreate(mapViewSavedInstanceState);
        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapView.getMapAsync(mapReadyCallback);
    }

    private OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            if (mCurrentActivity == null) {
                return;
            }
            mGoogleMap = googleMap;
            Utils.formatMap(mGoogleMap);
            mapView.init(mGoogleMap);
//            mGoogleMap.setPadding(0, 0, 0, (int) mCurrentActivity.getResources().getDimension(R.dimen.map_padding_bottom));
            Double mlogitude;
            Double mlatitude;

            if (getIntent() != null && getIntent().getExtras() != null
                    && getIntent().getExtras().getParcelable(Constants.Extras.SELECTED_ITEM) != null) {
                PlacesResult placesResult = getIntent().getExtras().getParcelable(Constants.Extras.SELECTED_ITEM);
                if (placesResult != null) {
                    isSearchedLoc = true;
                    mlatitude = placesResult.latitude;
                    mlogitude = placesResult.longitude;
                    setAddress(placesResult.address);
                    setLocation(mlatitude, mlogitude);
                    finishLoading();
                } else {
                    moveToCurrentLocation();
                }
            } else {
                moveToCurrentLocation();
            }

        }


    };

    private void moveToCurrentLocation() {
        setLocation(AppPreferences.getLatitude(), AppPreferences.getLongitude());
    }

    private void setLocation(double lat, double lng) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 16.0f);
        mGoogleMap.moveCamera(cameraUpdate);
        mGoogleMap.setOnCameraIdleListener(onCameraIdleListener);
    }

    private GoogleMap.OnCameraIdleListener onCameraIdleListener = new GoogleMap.OnCameraIdleListener() {
        @Override
        public void onCameraIdle() {
            //ignore API call when user selects place from search bar
            if (!isSearchedLoc) {
                startLoading();
                reverseGeoCoding(mGoogleMap.getCameraPosition().target.latitude,
                        mGoogleMap.getCameraPosition().target.longitude);
            } else {
                isSearchedLoc = false;
            }

        }
    };

    private void reverseGeoCoding(double targetLat, double targetLng) {
        PlacesRepository mPlacesRepository = new PlacesRepository();
        mPlacesRepository.getGoogleGeoCoder(mPlacesDataHandler, targetLat + "", "" + targetLng, mCurrentActivity);
    }

    private IPlacesDataHandler mPlacesDataHandler = new PlacesDataHandler() {

        @Override
        public void onPlacesResponse(final String response) {
            mCurrentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String result = response;
                    finishLoading();
                    if (StringUtils.isNotBlank(result)) {
                        if (result.contains(";")) {
                            result = result.replace(";", ", ");
                        }
                        setAddress(result);
                    }
                }
            });
        }

        @Override
        public void onError(String error) {
            finishLoading();
            Utils.redLog("Address error", error + "");
            Utils.appToast(mCurrentActivity, "" + error);
        }
    };


    @OnClick({R.id.confirmBtn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmBtn:
                if (!addressTv.getText().toString().equalsIgnoreCase(getString(R.string.set_pickup_location))) {
                    Dialogs.INSTANCE.showLoader(mCurrentActivity);

                    String address = addressTv.getText().toString();
                    if (!address.equalsIgnoreCase(tvFromAddress.getText().toString())) {
                        address = addressTv.getText().toString() + ", " + tvFromAddress.getText().toString();
                    }
                    mSavedPlaceToServer = new SavedPlaces();
                    mSavedPlaceToServer.setAddress(address);
                    mSavedPlaceToServer.setLat(mGoogleMap.getCameraPosition().target.latitude);
                    mSavedPlaceToServer.setLng(mGoogleMap.getCameraPosition().target.longitude);
                    mSavedPlaceToServer.setEdited_address(StringUtils.isNotBlank(etEditedAddress.getText().toString()) ? etEditedAddress.getText().toString() : null);
                    mSavedPlaceToServer.setEdited_name(StringUtils.isNotBlank(etEditedName.getText().toString()) ? etEditedName.getText().toString() : null);
                    mSavedPlaceToServer.setPhone(StringUtils.isNotBlank(etMobileNumber.getText().toString()) ? etMobileNumber.getText().toString() : null);

                    String placeId = isPlaceSaved(mSavedPlaceToServer.getAddress(), mSavedPlaceToServer.getLat(), mSavedPlaceToServer.getLng());
                    if (StringUtils.isNotBlank(placeId)) {
                        //Place is already saved no need to call API
                        mSavedPlaceToServer.setPlaceId(placeId);
                        AppPreferences.setSavedPlace(mSavedPlaceToServer);
                        mCurrentActivity.finish();
//                        mSavedPlaceToServer.setPlaceId(placeId);
//                        new UserRepository().updateSavedPlace(mCurrentActivity, mSavedPlaceToServer, mCallBack);
                    } else {
                        new UserRepository().addSavedPlace(mCurrentActivity, mSavedPlaceToServer, mCallBack);
                    }
                }
                break;
        }

    }

    private String isPlaceSaved(String place, double lat, double lng) {
        String isSaved = StringUtils.EMPTY;
        ArrayList<SavedPlaces> savedPlaces = AppPreferences.getSavedPlaces();
        if (savedPlaces != null && savedPlaces.size() > 0) {
            for (SavedPlaces savedPlace : savedPlaces) {
                if (savedPlace.getAddress().equalsIgnoreCase(place)) {
                    float distance = Utils.calculateDistance(lat, lng, savedPlace.getLat(), savedPlace.getLng());
                    if (distance < Constants.SAVED_PLACES_RADIUS) {
                        isSaved = savedPlace.getPlaceId();
                        if (distance != 0) {
                            savedPlace.setLat(lat);
                            savedPlace.setLng(lng);
                            AppPreferences.updateSavedPlace(savedPlaces);
                        }
                        break;
                    }
                }
            }
        }
        return isSaved;
    }

    private UserDataHandler mCallBack = new UserDataHandler() {
        @Override
        public void onAddSavedPlaceResponse(final AddSavedPlaceResponse response) {
            if (mCurrentActivity != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.INSTANCE.dismissDialog();
                        mSavedPlaceToServer.setPlaceId(response.getPlaceId());
                        AppPreferences.setSavedPlace(mSavedPlaceToServer);
                        mCurrentActivity.finish();
                    }
                });
            }
        }

        @Override
        public void onError(int errorCode, final String errorMessage) {
            if (mCurrentActivity != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.INSTANCE.dismissDialog();
                        Utils.appToast(mCurrentActivity, errorMessage);
                    }
                });
            }
        }
    };

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }
}
