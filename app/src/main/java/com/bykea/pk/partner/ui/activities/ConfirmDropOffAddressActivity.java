package com.bykea.pk.partner.ui.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.PlacesResult;
import com.bykea.pk.partner.models.response.GoogleDistanceMatrixApi;
import com.bykea.pk.partner.repositories.places.IPlacesDataHandler;
import com.bykea.pk.partner.repositories.places.PlacesDataHandler;
import com.bykea.pk.partner.repositories.places.PlacesRepository;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.adapters.PlaceAutocompleteAdapter;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.CustomMapView;
import com.bykea.pk.partner.widgets.FontTextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConfirmDropOffAddressActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {

    private ConfirmDropOffAddressActivity mCurrentActivity;
    private GoogleMap mGoogleMap;
    private CustomMapView mapView;
    private boolean firstTime = true, isSearchedLoc;
    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private int requestCode = 0;
    private LatLng latlngPoint, prevNearByLatLng;
    private String mAddressName = "";
    //    private CallRepository mCallRepository;
    Bundle bundle;
    @Bind(R.id.tvFromName)
    FontTextView addressTv;
    @Bind(R.id.tvFromAddress)
    FontTextView tvFromAddress;

    @Bind(R.id.confirmBtn)
    FontTextView confirmBtn;
    @Bind(R.id.tvCities)
    Spinner tvCities;
    @Bind(R.id.tvCitiesSingle)
    FontTextView tvCitiesSingle;

//    @Bind(R.id.tvFenceError)
//    FontTextView tvFenceError;

    @Bind(R.id.tv_elaqa)
    FontTextView tv_elaqa;

    @Bind(R.id.autocomplete_places)
    AutoCompleteTextView mAutocompleteView;

//    @Bind(R.id.clearBtn)
//    ImageView clearSearchBtn;

//    @Bind(R.id.rlNoDriverFound)
//    RelativeLayout rlNoDriverFound;

//    @Bind(R.id.loaderIv)
//    ImageView loaderIv;
//    @Bind(R.id.timeTv)
//    TextView timeTv;
//    @Bind(R.id.arrowLocationIv)
//    ImageView arrowLocationIv;

    @Bind(R.id.rlFrom)
    RelativeLayout rlFrom;
    private ArrayList<PlacesResult> cities;
    private String primaryText;
    private String toolbarTitle, searchBoxTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_confirm_drop_off_address);
        ButterKnife.bind(this);
        mCurrentActivity = this;

        setInitMap(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(mCurrentActivity)
                .enableAutoManage(mCurrentActivity, 0 /* clientId */, mCurrentActivity)
                .addApi(Places.GEO_DATA_API)
                .build();
        requestCode = getIntent().getIntExtra("from", 0);
//        toolbarTitle = getIntent().getStringExtra(Constants.TOOLBAR_TITLE);
//        searchBoxTitle = getIntent().getStringExtra(Constants.SEARCHBOX_TITLE);
        confirmBtn.setClickable(false);
        setBackNavigation();
        hideToolbarLogo();
        hideToolbarTitle();
        setStatusButton("Cancel");
        tv_elaqa.setVisibility(View.VISIBLE);

        bundle = getIntent().getParcelableExtra("point");
        if (requestCode == Constants.CONFIRM_DROPOFF_REQUEST_CODE) {
            if (getIntent().hasExtra("point")) {
                latlngPoint = bundle.getParcelable("point_latlng");
                mAddressName = getIntent().getStringExtra("name");
                mAutocompleteView.setHint(searchBoxTitle);
            }
//            loaderIv.setVisibility(View.GONE);
//            arrowLocationIv.setVisibility(View.GONE);
//            timeTv.setVisibility(View.GONE);
        }
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
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setCountry("PK")
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                .build();
        LatLngBounds.Builder bounds =
                new LatLngBounds.Builder();
        bounds.include(new LatLng(city.latitude, city.longitude));
//        mAdapter = new PlaceAutocompleteAdapter(mCurrentActivity, mGoogleApiClient, bounds.build(),
//                typeFilter, city.name, isPickUpPoint);
        mAdapter = new PlaceAutocompleteAdapter(mCurrentActivity, mGoogleApiClient, bounds.build(), typeFilter, city.name);
        mAutocompleteView.setAdapter(mAdapter);
        mAutocompleteView.setDropDownWidth(getResources().getDisplayMetrics().widthPixels);

//        clearSearchBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mAutocompleteView.setText("");
//            }
//        });
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
            if (position < mAdapter.getCount()) {
                final AutocompletePrediction item = mAdapter.getItem(position);
                final String placeId = item.getPlaceId();
                primaryText = Utils.formatAddress(item.getFullText(null).toString());


                Utils.redLog("Auto", "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

           /* Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
                    Toast.LENGTH_SHORT).show();*/
                Utils.redLog("bykea", "Called getPlaceById to get Place details for " + placeId);
            }
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {

            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Utils.redLog("bykea", "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            if (places.get(0) == null) {
                return;
            }
            Place place = places.get(0);

            // Format details of the place for display and show it in a TextView.
//            tvLocation.setText("" + place.getAddress());
            String result = StringUtils.isNotBlank(primaryText) ? primaryText : Utils.formatAddress(!place.getAddress().toString().contains(place.getName().toString()) ? place.getName() + ", " + place.getAddress().toString() : place.getAddress().toString());
            PlacesResult placesResult = new PlacesResult(result, "",
                    place.getLatLng().latitude, place.getLatLng().longitude);
            updateDropOff(placesResult);
            places.release();
        }
    };

    private void updateDropOff(final PlacesResult placesResult) {
        if (mCurrentActivity != null && placesResult != null && mGoogleMap != null) {
            mCurrentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Utils.hideSoftKeyboard(mCurrentActivity, mAutocompleteView);
                    isSearchedLoc = true;
                    mAutocompleteView.setText("");
                    mAutocompleteView.clearFocus();
                    mAutocompleteView.setFocusable(false);
                    String result = placesResult.name;
                    String name = result.replace(result.substring(result.lastIndexOf(',') + 1), "").replace(",", "");
                    String city = result.substring(result.lastIndexOf(',') + 1).trim();
                    addressTv.setText(name);
                    tvFromAddress.setText(city);
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
            if (mAddressName.equalsIgnoreCase(getString(R.string.pick_up_point)) ||
                    mAddressName.equalsIgnoreCase(getString(R.string.drop_off_point))) {
                mlatitude = AppPreferences.getLatitude();
                mlogitude = AppPreferences.getLongitude();
            } else {
                if (latlngPoint != null) {
                    mlatitude = latlngPoint.latitude;
                    mlogitude = latlngPoint.longitude;
                } else {
                    mlatitude = AppPreferences.getLatitude();
                    mlogitude = AppPreferences.getLongitude();
                }
            }
            Log.e("mlattitude", String.valueOf(mlatitude));
            Log.e("logitude", String.valueOf(mlogitude));
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
            Utils.infoLog("GOOGLE MAPS", " onCameraIdle method called.......");
            Utils.infoLog("GOOGLE MAPS onCameraIdle", mGoogleMap.getCameraPosition().target + "");
            //ignore API call when user selects place from search bar
            if (!isSearchedLoc) {
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
                    if (StringUtils.isNotBlank(result)) {
                        if (result.contains(";")) {
                            result = result.replace(";", ", ");
                        }
                        String name = result.replace(result.substring(result.lastIndexOf(',') + 1), "").replace(",", "");
                        String city = result.substring(result.lastIndexOf(',') + 1).trim();
                        addressTv.setText(name);
                        tvFromAddress.setText(city);
                        confirmBtn.setClickable(true);
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
            confirmBtn.setClickable(true);
            Dialogs.INSTANCE.showToast(mCurrentActivity, "" + error);
//            stopLoadingAnimation();
        }
    };

//    private void startLoadingAnimation() {
//        RotateAnimation anim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        anim.setInterpolator(new LinearInterpolator());
//        anim.setRepeatCount(Animation.INFINITE);
//        anim.setDuration(700);
//        loaderIv.startAnimation(anim);
//    }

//    private void stopLoadingAnimation() {
//        try {
//            loaderIv.setAnimation(null);
//        } catch (Exception ex) {
//
//        }
//    }

    @OnClick({R.id.confirmBtn, R.id.autocomplete_places/*, R.id.tvCities*/})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmBtn:
                PlacesResult placesResult = new PlacesResult("DropOffAddress",
                        addressTv.getText().toString() + ", " + tvFromAddress.getText().toString(),
                        mGoogleMap.getCameraPosition().target.latitude, mGoogleMap.getCameraPosition().target.longitude);
                Intent returnIntent = new Intent();
                //PlacesResult data model implements Parcelable so we could pass object in extras
                returnIntent.putExtra(Constants.CONFIRM_DROPOFF_ADDRESS_RESULT, placesResult);
                setResult(Activity.RESULT_OK, returnIntent);
                tv_elaqa.setVisibility(View.GONE);
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
}
