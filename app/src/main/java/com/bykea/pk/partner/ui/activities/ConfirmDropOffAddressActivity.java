package com.bykea.pk.partner.ui.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.NearByResults;
import com.bykea.pk.partner.models.data.PlacesResult;
import com.bykea.pk.partner.models.data.Predictions;
import com.bykea.pk.partner.models.response.GoogleDistanceMatrixApi;
import com.bykea.pk.partner.models.response.PlaceDetailsResponse;
import com.bykea.pk.partner.repositories.places.IPlacesDataHandler;
import com.bykea.pk.partner.repositories.places.PlacesDataHandler;
import com.bykea.pk.partner.repositories.places.PlacesRepository;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.adapters.PlaceAutocompleteAdapter;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.AutoFitFontTextView;
import com.bykea.pk.partner.widgets.CustomMapView;
import com.bykea.pk.partner.widgets.FontTextView;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConfirmDropOffAddressActivity extends BaseActivity {

    private ConfirmDropOffAddressActivity mCurrentActivity;
    private GoogleMap mGoogleMap;
    private CustomMapView mapView;
    private boolean isSearchedLoc;
    private PlaceAutocompleteAdapter mAdapter;
    private int requestCode = 0;
    private LatLng latlngPoint, prevNearByLatLng;
    private String mAddressName = "";
    //    private CallRepository mCallRepository;
    Bundle bundle;
    @BindView(R.id.tvFromName)
    AutoFitFontTextView addressTv;
    @BindView(R.id.tvFromAddress)
    FontTextView tvFromAddress;

    @BindView(R.id.confirmBtn)
    FontTextView confirmBtn;
    @BindView(R.id.tvCities)
    Spinner tvCities;
    @BindView(R.id.tvCitiesSingle)
    FontTextView tvCitiesSingle;
    @BindView(R.id.loader)
    ProgressBar loader;


    @BindView(R.id.autocomplete_places)
    AutoCompleteTextView mAutocompleteView;


    @BindView(R.id.rlFrom)
    RelativeLayout rlFrom;
    private ArrayList<PlacesResult> cities;
    private String primaryText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_confirm_drop_off_address);
        ButterKnife.bind(this);
        mCurrentActivity = this;

        setInitMap(savedInstanceState);
        setSearchAdapter();
    }


    private void setSearchAdapter() {
        mAutocompleteView.setText("");
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
        mAutocompleteView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (mCurrentActivity != null) {
                    if (hasFocus) {
                        mCurrentActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    } else {
                        Utils.hideSoftKeyboard(mCurrentActivity, mAutocompleteView);
                    }
                }
            }
        });
        mAutocompleteView.setFocusable(false);
        Utils.hideSoftKeyboard(mCurrentActivity, mAutocompleteView);
        mAutocompleteView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mAutocompleteView.setFocusable(true);
                mAutocompleteView.setFocusableInTouchMode(true);
                return false;
            }
        });
        cities = new ArrayList<>();
        cities = Utils.getCities();
        setCitiesSpinner();
        setCity(cities.get(Utils.getCurrentCityIndex()));
    }

    private void setCitiesSpinner() {
        tvCities.setVisibility(View.GONE);
        tvCitiesSingle.setVisibility(View.VISIBLE);
        tvCitiesSingle.setText(cities.get(Utils.getCurrentCityIndex()).name);
    }

    private void setCity(PlacesResult city) {
        mAutocompleteView.setText("");
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
        mAdapter = new PlaceAutocompleteAdapter(mCurrentActivity, city.name);
        mAutocompleteView.setAdapter(mAdapter);
        mAutocompleteView.setDropDownWidth(getResources().getDisplayMetrics().widthPixels);

    }

    private void clearAutoComplete() {
        mAutocompleteView.clearFocus();
        mAutocompleteView.setFocusable(false);
        mAutocompleteView.setText(StringUtils.EMPTY);
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            clearAutoComplete();
            if (position < mAdapter.getCount()) {
                final Predictions item = mAdapter.getItem(position);
                String placeId = StringUtils.EMPTY;
                if (item != null) {
                    placeId = item.getPlace_id();
                    primaryText = Utils.formatAddress(item.getDescription());
                }
                Utils.redLog("Auto", "Autocomplete item selected: " + primaryText);
                Utils.redLog("bykea", "Called getPlaceById to get Place details for " + placeId);
                new PlacesRepository().getPlaceDetails(placeId, mCurrentActivity, new PlacesDataHandler() {
                    @Override
                    public void onPlaceDetailsResponse(PlaceDetailsResponse response) {

                        NearByResults results = response.getResult();
                        if (results != null) {
                            String result = StringUtils.isNotBlank(primaryText) ? primaryText : Utils.formatAddress(results.getFormatted_address());
//                            mAutocompleteView.setText(result);
                            PlacesResult placesResult = new PlacesResult(result, "",
                                    results.getGeometry().getLocation().getLat(), results.getGeometry().getLocation().getLng());
                            updateDropOff(placesResult);

                        }
                    }
                });
            }
        }
    };

    private void updateDropOff(final PlacesResult placesResult) {
        if (mCurrentActivity != null && placesResult != null && mGoogleMap != null) {
            mCurrentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Utils.hideSoftKeyboard(mCurrentActivity, mAutocompleteView);
                    isSearchedLoc = true;
                    clearAutoComplete();
                    setAddress(placesResult.name);
                    mGoogleMap.getUiSettings().setScrollGesturesEnabled(false);
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(placesResult.latitude, placesResult.longitude), 16.0f), 1000, new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            mGoogleMap.getUiSettings().setScrollGesturesEnabled(true);
                        }

                        @Override
                        public void onCancel() {
                            mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
                        }
                    });
                }
            });
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
            mGoogleMap.setPadding(0, 0, 0, (int) mCurrentActivity.getResources().getDimension(R.dimen.map_padding_bottom));
            Double mlogitude;
            Double mlatitude;

            PlacesResult placesResult = null;
            if (getIntent() != null) {
                placesResult = getIntent().getParcelableExtra(Constants.Extras.DROP_OFF);
            }

            if (placesResult != null) {
                isSearchedLoc = true;
                mlatitude = placesResult.latitude;
                mlogitude = placesResult.longitude;
                setAddress(placesResult.address);

            } else {
                mlatitude = AppPreferences.getLatitude();
                mlogitude = AppPreferences.getLongitude();
            }
            setLocation(mlatitude, mlogitude);
        }
    };

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
                mAutocompleteView.setText(StringUtils.EMPTY);
                reverseGeoCoding(mGoogleMap.getCameraPosition().target.latitude,
                        mGoogleMap.getCameraPosition().target.longitude);
            } else {
                finishLoading();
                isSearchedLoc = false;
            }

        }
    };

    private void reverseGeoCoding(double targetLat, double targetLng) {
        PlacesRepository mPlacesRepository = new PlacesRepository();
        mPlacesRepository.getGoogleGeoCoder(mPlacesDataHandler, targetLat + "", "" + targetLng, mCurrentActivity);
        /*if (requestCode == Constants.CONFIRM_DROPOFF_REQUEST_CODE) {
            String origin = AppPreferences.getPickUpLoc(mCurrentActivity).latitude + "," + AppPreferences.getPickUpLoc(mCurrentActivity).longitude;
            String destination = String.valueOf(targetLat) + "," + String.valueOf(targetLng);
            mPlacesRepository.getDistanceMatrix(mPlacesDataHandler, origin, destination, mCurrentActivity);
            startLoadingAnimation();
        }*/
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
        public void onDistanceMatrixResponse(final GoogleDistanceMatrixApi response) {
//            stopLoadingAnimation();
            if (mCurrentActivity != null && response != null && response.getStatus().equals("OK")) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finishLoading();
                        if (response.getRows()[0].getElements()[0].getStatus().equals("OK")) {
                            double distance = Math.ceil(Double.parseDouble(response.getRows()[0].getElements()[0].getDistance().getValue()) / 1000);
                            int duration = (int) Math.ceil(Double.parseDouble(response.getRows()[0].getElements()[0].getDuration().getValue()) / 60);
//                            timeTv.setText(duration + "\nmin");
                        } else {
                            Utils.redLog("Elements Status", response.getRows()[0].getElements()[0].getStatus());
                        }
                    }
                });

            }
        }

        @Override
        public void onError(String error) {
            Utils.redLog("Address error", error + "");
            finishLoading();
            Dialogs.INSTANCE.showToast(mCurrentActivity, "" + error);
//            stopLoadingAnimation();
        }
    };


    private void setAddress(String result) {
        if (result.contains(",") && result.split(",").length > 1) {
            addressTv.setText(result.split(",")[0]);
            tvFromAddress.setText(result.split(",")[1]);
        } else {
            addressTv.setText(result);
            tvFromAddress.setText(result);
        }

    }

    private void finishLoading() {
//        loader.setVisibility(View.GONE);
        loader.setIndeterminate(false);
        confirmBtn.setClickable(true);
    }

    private void startLoading() {
        loader.setIndeterminate(true);
        confirmBtn.setClickable(false);
    }


    @OnClick({R.id.confirmBtn, R.id.autocomplete_places, R.id.ivBackBtn, R.id.status})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBackBtn:
            case R.id.status:
                onBackPressed();
                break;
            case R.id.confirmBtn:
                String address = addressTv.getText().toString();
                if (!address.equalsIgnoreCase(tvFromAddress.getText().toString())) {
                    address = addressTv.getText().toString() + ", " + tvFromAddress.getText().toString();
                }
                PlacesResult placesResult = new PlacesResult(address, address,
                        mGoogleMap.getCameraPosition().target.latitude, mGoogleMap.getCameraPosition().target.longitude);
                Intent returnIntent = new Intent();
                //PlacesResult data model implements Parcelable so we could pass object in extras
                returnIntent.putExtra(Constants.CONFIRM_DROPOFF_ADDRESS_RESULT, placesResult);
                setResult(Activity.RESULT_OK, returnIntent);
                mCurrentActivity.finish();
                break;
            case R.id.autocomplete_places:
                mAutocompleteView.requestFocus();
                break;
        }
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
