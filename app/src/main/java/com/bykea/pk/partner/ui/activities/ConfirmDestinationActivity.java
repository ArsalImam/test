package com.bykea.pk.partner.ui.activities;

import android.content.Intent;
import android.os.Bundle;

import com.bykea.pk.partner.utils.Constants;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.response.GeocoderApi;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;

import org.apache.commons.lang3.StringUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConfirmDestinationActivity extends BaseActivity {

    @Bind(R.id.addressTv)
    FontTextView addressTv;
    @Bind(R.id.confirmBtn)
    FontTextView confirmBtn;

    private ConfirmDestinationActivity mCurrentActivity;
    private GoogleMap mGoogleMap;
    private MapView mapView;

    private boolean firstTime = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_destination);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        ButterKnife.bind(this);
        mCurrentActivity = this;

        setBackNavigation();
        setToolbarTitle("Confirm Drop Off");
        hideToolbarLogo();

        setInitMap(savedInstanceState);

    }

    private OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;
            Utils.formatMap(mGoogleMap);
            getIntentData();

        }


    };


    private GoogleMap.OnCameraIdleListener onCameraIdleListener = new GoogleMap.OnCameraIdleListener() {
        @Override
        public void onCameraIdle() {
            if (!firstTime)
                reverseGeoCoding(mGoogleMap.getCameraPosition().target.latitude,
                        mGoogleMap.getCameraPosition().target.longitude);
            firstTime = false;
        }
    };

    private UserDataHandler handler = new UserDataHandler() {

        @Override
        public void onReverseGeocode(final GeocoderApi geocoderApiResponse) {
            if (mCurrentActivity != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String add = StringUtils.EMPTY;
                        if (geocoderApiResponse != null
                                && geocoderApiResponse.getStatus().equalsIgnoreCase(Constants.STATUS_CODE_OK)
                                && geocoderApiResponse.getResults().length > 0) {
                            String address = StringUtils.EMPTY;
                            String subLocality = StringUtils.EMPTY;
//                        String postalCode = StringUtils.EMPTY;
                            String cityName = StringUtils.EMPTY;
                            String streetNumber = StringUtils.EMPTY;
                            GeocoderApi.Address_components[] address_componentses = geocoderApiResponse.getResults()[0].getAddress_components();
                            for (GeocoderApi.Address_components addressComponent : address_componentses) {
                                String[] types = addressComponent.getTypes();
                                for (String type : types) {
                                    if (type.equalsIgnoreCase(Constants.GEOCODE_RESULT_TYPE_CITY)) {
                                        cityName = addressComponent.getLong_name();
                                    }
                                    if (type.equalsIgnoreCase(Constants.GEOCODE_RESULT_TYPE_STREET_NUMBER)) {
                                        streetNumber = addressComponent.getLong_name();
                                    }
                                    if (type.equalsIgnoreCase(Constants.GEOCODE_RESULT_TYPE_ADDRESS) || type.equalsIgnoreCase(Constants.GEOCODE_RESULT_TYPE_ADDRESS_1)) {
                                        address = addressComponent.getLong_name();
                                    }
                                    if (type.equalsIgnoreCase(Constants.GEOCODE_RESULT_TYPE_ADDRESS_SUB_LOCALITY)) {
                                        subLocality = addressComponent.getLong_name();
                                    }
                                    if (StringUtils.isNotBlank(cityName) && StringUtils.isNotBlank(address) && StringUtils.isNotBlank(subLocality)) {
                                        break;
                                    }
                                }
                                if (StringUtils.isNotBlank(cityName) && StringUtils.isNotBlank(address) && StringUtils.isNotBlank(subLocality)) {
                                    break;
                                }
                            }
                            if (StringUtils.isNotBlank(subLocality)) {
                                if (StringUtils.isNotBlank(address)) {
                                    address = address + " " + subLocality;
                                } else {
                                    address = subLocality;
                                }
                            }
                            if (StringUtils.isNotBlank(address)) {
                                add = address;
                            }

                            if (StringUtils.isNotBlank(address) && StringUtils.isNotBlank(cityName)) {
                                add = address + ", " + cityName;
                            }
                            if (StringUtils.isNotBlank(add)) {
                                addressTv.setText(add);
                            } else {
                                AppPreferences.setGeoCoderApiKeyRequired(true);
                            }
                        } else {
                            AppPreferences.setGeoCoderApiKeyRequired(true);
                        }

                    }
                });
            }

        }

        @Override
        public void onError(int errorCode, String errorMessage) {
            AppPreferences.setGeoCoderApiKeyRequired(true);
            Dialogs.INSTANCE.showError(mCurrentActivity, confirmBtn, errorMessage);
        }
    };


    private void getIntentData() {
        if (null != getIntent() && null != getIntent().getExtras()) {
            addressTv.setText(getIntent().getStringExtra("address"));
            AppPreferences.setDropOffData(getIntent().getStringExtra("address"),
                    getIntent().getDoubleExtra("lat", 0.0), getIntent().getDoubleExtra("lng", 0.0));
            setLocation(getIntent().getDoubleExtra("lat", 0.0), getIntent().getDoubleExtra("lng", 0.0));
        } else {
            addressTv.setText(getResources().getString(R.string.loading));
            firstTime = false;
            setLocation(AppPreferences.getLatitude(), AppPreferences.getLongitude());
        }
    }

    private void setLocation(double lat, double lng) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 16.0f);
        mGoogleMap.moveCamera(cameraUpdate);
        mGoogleMap.setOnCameraIdleListener(onCameraIdleListener);
    }

    private void reverseGeoCoding(double targetLat, double targetLng) {
        UserRepository repository = new UserRepository();
        repository.requestReverseGeocoding(mCurrentActivity, handler, targetLat + "," + targetLng,
                Utils.getApiKeyForGeoCoder());

    }

    private void setInitMap(Bundle savedInstanceState) {
        mapView = (MapView) findViewById(R.id.confirmMapFragment);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapView.getMapAsync(mapReadyCallback);
    }

    @OnClick(R.id.confirmBtn)
    public void onClick() {
        if (StringUtils.isNotBlank(addressTv.getText().toString())
                && !addressTv.getText().toString().equalsIgnoreCase(getResources().getString(R.string.loading))) {
            AppPreferences.setDropOffData(addressTv.getText().toString(),
                    mGoogleMap.getCameraPosition().target.latitude,
                    mGoogleMap.getCameraPosition().target.longitude);
            Intent intent = new Intent();
        /*intent.putExtra("address", addressTv.getText().toString());
        intent.putExtra("lat", mGoogleMap.getCameraPosition().target.latitude);
        intent.putExtra("lng", mGoogleMap.getCameraPosition().target.longitude);*/
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        AppPreferences.setDropOffData(StringUtils.EMPTY, 0.0, 0.0);
        super.onBackPressed();
    }

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
