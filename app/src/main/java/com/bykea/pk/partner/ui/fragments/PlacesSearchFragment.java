package com.bykea.pk.partner.ui.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.NearByResults;
import com.bykea.pk.partner.models.data.PlacesResult;
import com.bykea.pk.partner.models.data.Predictions;
import com.bykea.pk.partner.models.data.SavedPlaces;
import com.bykea.pk.partner.models.response.PlaceDetailsResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.repositories.places.IPlacesDataHandler;
import com.bykea.pk.partner.repositories.places.PlacesDataHandler;
import com.bykea.pk.partner.repositories.places.PlacesRepository;
import com.bykea.pk.partner.ui.activities.SelectPlaceActivity;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.adapters.PlaceAutocompleteAdapter;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Permissions;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.AutoFitFontTextView;
import com.bykea.pk.partner.widgets.CustomMapView;
import com.bykea.pk.partner.widgets.DownOnlyAutoCompleteTextView;
import com.bykea.pk.partner.widgets.FontTextView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlacesSearchFragment extends Fragment {

    private GoogleMap mGoogleMap;
    private CustomMapView mapView;
    private boolean firstTime = true, isSearchedLoc;
    //    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private int requestCode = 0;
    private LatLng prevNearByLatLng;
    private String mAddressName = "";
    Bundle bundle;

    //moving camera to current location's zoom and animation control values
    private int previousZoomLevel;
    private boolean isAnimating;

    @BindView(R.id.tvFromName)
    AutoFitFontTextView addressTv;
    @BindView(R.id.tvPlaceName)
    FontTextView tvPlaceName;
    @BindView(R.id.tvPlaceAddress)
    FontTextView tvPlaceAddress;

    @BindView(R.id.confirmBtn)
    FrameLayout confirmBtn;


    @BindView(R.id.autocomplete_places)
    DownOnlyAutoCompleteTextView mAutocompleteView;

    @BindView(R.id.rlNoDriverFound)
    RelativeLayout rlNoDriverFound;

    @BindView(R.id.loader)
    ProgressBar loader;

    @BindView(R.id.rlFrom)
    LinearLayout rlFrom;
    @BindView(R.id.rlDropDown)
    RelativeLayout rlDropDown;

    @BindView(R.id.rlSavePlace)
    RelativeLayout rlSavePlace;

    @BindView(R.id.ivStar)
    ImageView ivStar;

    //current location view
    @BindView(R.id.currentLocationIv)
    CardView currentLocationIv;

    private ArrayList<PlacesResult> cities;
    private String primaryText;
    private boolean isSavedPlace;
    private String placeId;


    private SelectPlaceActivity mCurrentActivity;

    private UserRepository mRepository;

    public PlacesSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places_search, container, false);
        ButterKnife.bind(this, view);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCurrentActivity = (SelectPlaceActivity) getActivity();
        mRepository = new UserRepository();
        requestCode = mCurrentActivity.getIntent().getIntExtra("from", 0);
        if (getArguments() != null && getArguments().getBoolean(Constants.Extras.HIDE_SEARCH)) {
            rlDropDown.setVisibility(View.GONE);
            rlSavePlace.setVisibility(View.VISIBLE);
            ivStar.setImageDrawable(Utils.changeDrawableColor(mCurrentActivity, R.drawable.ic_star_grey, R.color.secondaryColorLight));
        } else {
            setSearchAdapter();
        }
        Utils.hideSoftKeyboard(this);
        mapView = (CustomMapView) view.findViewById(R.id.confirmMapFragment);
        final Bundle mapViewSavedInstanceState = savedInstanceState != null ? savedInstanceState.getBundle("mapViewSaveState") : null;
        mapView.onCreate(mapViewSavedInstanceState);
        if (getArguments() == null || !getArguments().getBoolean(Constants.Extras.IS_FROM_VIEW_PAGER)) {
            setInitMap();
        }
    }

    private void updateStarColor() {
        ivStar.setImageDrawable(Utils.changeDrawableColor(mCurrentActivity, R.drawable.ic_star_grey, isSavedPlace ? R.color.yellowStar : R.color.secondaryColorLight));
    }

    private boolean isMapLoaded;

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser && !isMapLoaded) {
//        }
//    }

    public void setInitMap() {
        if (!isMapLoaded && getView() != null) {
            isMapLoaded = true;
            try {
                MapsInitializer.initialize(mCurrentActivity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mapView.getMapAsync(mapReadyCallback);
        }
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

            if (getArguments() != null && getArguments().getParcelable(Constants.Extras.SELECTED_ITEM) != null) {
                PlacesResult placesResult = getArguments().getParcelable(Constants.Extras.SELECTED_ITEM);
                if (placesResult != null) {
                    mlatitude = placesResult.latitude;
                    mlogitude = placesResult.longitude;
                    if (StringUtils.isNotBlank(placesResult.address)) {
                        isSearchedLoc = true;
                    }
                    setAddress(placesResult.address, placesResult.latitude, placesResult.longitude);
                    setLocation(mlatitude, mlogitude);
                    finishLoading();
                    callNearByCallIfRequired();
                } else {
                    moveToCurrentLocation();
                }
            } else {
                moveToCurrentLocation();
            }

            if (ActivityCompat.checkSelfPermission(mCurrentActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(mCurrentActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mGoogleMap.setMyLocationEnabled(true);
        }


    };

    private void callNearByCallIfRequired() {
        enableAllViews();
    }

    private void moveToCurrentLocation() {
        setLocation(AppPreferences.getLatitude(), AppPreferences.getLongitude());
    }

    private void setLocation(double lat, double lng) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 14.0f);
        mGoogleMap.moveCamera(cameraUpdate);
        mGoogleMap.setOnCameraIdleListener(onCameraIdleListener);
    }

    private void setAddress(String result, double lat, double lng) {
        if (StringUtils.isNotBlank(result)) {
            placeId = mCurrentActivity.isPlaceSaved(result, lat, lng);
            isSavedPlace = StringUtils.isNotBlank(placeId);
            updateStarColor();
            if (result.contains(",") && result.split(",").length > 1) {
                int lastIndex = result.lastIndexOf(',');
                addressTv.setText(result.substring(0, lastIndex));
                tvPlaceName.setText(result.substring(0, lastIndex));
                tvPlaceAddress.setText(result.substring(lastIndex + 1).trim());
            } else {
                addressTv.setText(result);
                tvPlaceName.setText(result);
                tvPlaceAddress.setText(result);
            }
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        final Bundle mapViewSaveState = new Bundle(outState);
        if (mapView != null) {
            mapView.onSaveInstanceState(mapViewSaveState);
            outState.putBundle("mapViewSaveState", mapViewSaveState);
        }
        super.onSaveInstanceState(outState);
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
                isSearchedLoc = false;
            }

        }
    };

    private void reverseGeoCoding(double targetLat, double targetLng) {
        PlacesRepository mPlacesRepository = new PlacesRepository();
        mPlacesRepository.getGoogleGeoCoder(mPlacesDataHandler, targetLat + "", "" + targetLng, mCurrentActivity);
//        mPlacesRepository.getNearbyPlaces(mPlacesDataHandler,targetLat + "," + targetLng,500,mCurrentActivity);
        /*if (requestCode == Constants.CONFIRM_DROPOFF_REQUEST_CODE) {
            String origin = AppPreferences.getPickUpLoc(mCurrentActivity).latitude + "," + AppPreferences.getPickUpLoc(mCurrentActivity).longitude;
            String destination = String.valueOf(targetLat) + "," + String.valueOf(targetLng);
            mPlacesRepository.getDistanceMatrix(mPlacesDataHandler, origin, destination, mCurrentActivity);
            startLoadingAnimation();
        }*/
    }

    private void setSearchAdapter() {
        mAutocompleteView.setText(StringUtils.EMPTY);
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
        mAutocompleteView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (mCurrentActivity != null && mAutocompleteView != null) {
                    if (hasFocus) {
                        mCurrentActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    } else {
                        Utils.hideSoftKeyboard(mCurrentActivity, mAutocompleteView);
                        mAutocompleteView.clearFocus();
                        mAutocompleteView.setFocusable(false);
                        mAutocompleteView.setFocusableInTouchMode(false);
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
        setCity(cities.get(Utils.getCurrentCityIndex()));
    }


    private void setCity(PlacesResult city) {
//        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
//                .setCountry("PK")
//                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
//                .build();
//        LatLngBounds.Builder bounds =
//                new LatLngBounds.Builder();
//        bounds.include(new LatLng(city.latitude, city.longitude));
        mAdapter = new PlaceAutocompleteAdapter(mCurrentActivity,
                city.name);
        mAutocompleteView.setAdapter(mAdapter);
        mAutocompleteView.setDropDownWidth(getResources().getDisplayMetrics().widthPixels);

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
                Predictions item = null;
                try {
                    item = mAdapter.getItem(position);
                } catch (Exception ignored) {
                }
                if (item == null) {
                    return;
                }
                String placeId;
                placeId = item.getPlace_id();
                primaryText = Utils.formatAddress(item.getDescription());
                Utils.redLog("Auto", "Autocomplete item selected: " + primaryText);
                Utils.redLog("bykea", "Called getPlaceById to get Place details for " + placeId);
                new PlacesRepository().getPlaceDetails(placeId, mCurrentActivity, new PlacesDataHandler() {
                    @Override
                    public void onPlaceDetailsResponse(PlaceDetailsResponse response) {

                        NearByResults results = response.getResult();
                        if (results != null) {
                            // Format details of the place for display and show it in a TextView.
//            tvLocation.setText("" + place.getAddress());
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
        if (mCurrentActivity != null && getView() != null && placesResult != null && mGoogleMap != null) {
            mCurrentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Utils.hideSoftKeyboard(mCurrentActivity, mAutocompleteView);
                    isSearchedLoc = true;
                    setAddress(placesResult.name, placesResult.latitude, placesResult.longitude);
                    mGoogleMap.getUiSettings().setScrollGesturesEnabled(false);
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(placesResult.latitude, placesResult.longitude), 14.0f), 1000, new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            mGoogleMap.getUiSettings().setScrollGesturesEnabled(true);
                            callNearByCallIfRequired();
                            clearAutoComplete();
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


    private void clearAutoComplete() {
        if (mAutocompleteView != null) {
            mAutocompleteView.clearFocus();
            mAutocompleteView.setFocusable(false);
            mAutocompleteView.setFocusableInTouchMode(false);
            mAutocompleteView.setText(StringUtils.EMPTY);
        }
    }


    private IPlacesDataHandler mPlacesDataHandler = new PlacesDataHandler() {


        @Override
        public void onPlacesResponse(final String response) {
            if (mCurrentActivity != null && getView() != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String result = response;
                        finishLoading();
                        if (StringUtils.isNotBlank(result)) {
                            if (result.contains(";")) {
                                result = result.replace(";", ", ");
                            }
                            setAddress(result, mGoogleMap.getCameraPosition().target.latitude, mGoogleMap.getCameraPosition().target.longitude);
                        }
                        callNearByCallIfRequired();
                    }
                });
            }
        }

        @Override
        public void onError(String error) {
            finishLoading();
            Utils.redLog("Address error", error + "");
            Utils.appToast(mCurrentActivity, "" + error);
        }
    };


    @OnClick({R.id.confirmBtn, R.id.autocomplete_places, R.id.ivStar, R.id.currentLocationIv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmBtn:
                if (!addressTv.getText().toString().equalsIgnoreCase(getString(R.string.set_pickup_location))) {
                    PlacesResult placesResult = new PlacesResult(getAddress(), getAddress(),
                            mGoogleMap.getCameraPosition().target.latitude, mGoogleMap.getCameraPosition().target.longitude);
                    Utils.addRecentPlace(placesResult);
                    Intent returnIntent = new Intent();
                    //PlacesResult data model implements Parcelable so we could pass object in extras
                    returnIntent.putExtra(Constants.CONFIRM_DROPOFF_ADDRESS_RESULT, placesResult);
                    if (mCurrentActivity.showChangeButton()) {
                        returnIntent.putExtra(Constants.Extras.TOP_BAR, mCurrentActivity.isPickUp() ? Constants.Extras.PICK_UP : Constants.Extras.DROP_OFF);
                    }
                    mCurrentActivity.setResult(Activity.RESULT_OK, returnIntent);
                }
                mCurrentActivity.finish();
                break;
            case R.id.autocomplete_places:
                mAutocompleteView.requestFocus();
                break;
            case R.id.ivStar:
                if (isSavedPlace) {
                    Dialogs.INSTANCE.showAlertDialog(mCurrentActivity, "Delete Place",
                            "Are you sure you want to delete this Saved Place?",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Dialogs.INSTANCE.dismissDialog();
                                    Dialogs.INSTANCE.showLoader(mCurrentActivity);
                                    SavedPlaces savedPlaces = new SavedPlaces();
                                    savedPlaces.setPlaceId(placeId);
                                    new UserRepository().deleteSavedPlace(mCurrentActivity, savedPlaces,
                                            new UserDataHandler() {
                                                @Override
                                                public void onDeleteSavedPlaceResponse() {
                                                    if (mCurrentActivity != null && getView() != null) {
                                                        mCurrentActivity.runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Dialogs.INSTANCE.dismissDialog();
                                                                isSavedPlace = false;
                                                                updateStarColor();
                                                                mCurrentActivity.removeSavedPlace(getAddress(),
                                                                        mGoogleMap.getCameraPosition().target.latitude,
                                                                        mGoogleMap.getCameraPosition().target.longitude);
                                                            }
                                                        });
                                                    }

                                                }

                                                @Override
                                                public void onError(int code, final String errorMessage) {
                                                    if (mCurrentActivity != null && getView() != null) {
                                                        mCurrentActivity.runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Dialogs.INSTANCE.dismissDialog();
                                                                Utils.appToast(mCurrentActivity, errorMessage);
                                                            }
                                                        });
                                                    }
                                                }
                                            });

                                }
                            });
                } else {
                    if (!addressTv.getText().toString().equalsIgnoreCase(getString(R.string.set_pickup_location))) {
                        String address = getAddress();
                        PlacesResult placesResult = new PlacesResult(address, address,
                                mGoogleMap.getCameraPosition().target.latitude, mGoogleMap.getCameraPosition().target.longitude);
                        ActivityStackManager.getInstance().startSavePlaceActivity(mCurrentActivity, placesResult);
                    }
                }
                break;

            case R.id.currentLocationIv:
                goToCurrentLocation();
                break;
        }

    }

    @NonNull
    private String getAddress() {
        String address = addressTv.getText().toString();
        return address;
    }

    @Override
    public void onResume() {
        if (mapView != null) {
            mapView.onResume();
        }
        super.onResume();
        try {
            if (mGoogleMap != null && !addressTv.getText().toString().equalsIgnoreCase(getString(R.string.set_pickup_location))) {
                placeId = mCurrentActivity.isPlaceSaved(getAddress(), mGoogleMap.getCameraPosition().target.latitude, mGoogleMap.getCameraPosition().target.longitude);
                isSavedPlace = StringUtils.isNotBlank(placeId);
                updateStarColor();
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onPause() {
        if (mapView != null) {
            mapView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mapView != null) {
            mapView.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        if (mapView != null) {
            mapView.onLowMemory();
        }
        super.onLowMemory();
    }


    private void enableAllViews() {
        rlNoDriverFound.setVisibility(View.INVISIBLE);
        rlFrom.setVisibility(View.VISIBLE);
        confirmBtn.setEnabled(true);
        confirmBtn.setBackground(ContextCompat.getDrawable(mCurrentActivity, R.drawable.button_green_square));
    }


    private void finishLoading() {
        try {
            loader.setIndeterminate(false);
            confirmBtn.setClickable(true);
        } catch (Exception ignored) {

        }

    }

    private void startLoading() {
        try {
            loader.setIndeterminate(true);
            confirmBtn.setClickable(false);
        } catch (Exception ignored) {

        }
    }
    /***
     * moving camera to current location latitude & longitude.
     */
    private void goToCurrentLocation() {
        if(Utils.isGpsEnable()){
            double lat = AppPreferences.getLatitude();
            double lng = AppPreferences.getLongitude();
            if (lat != 0.0 && lng != 0.0) {
                previousZoomLevel = 16;
                isAnimating = false;
                animateCameraTo(lat, lng);
            }
        } else {
            Dialogs.INSTANCE.showLocationSettings(mCurrentActivity, Permissions.LOCATION_PERMISSION);
        }
    }

    /***
     * Animate marker to current location.
     * @param lat a latitude of current location.
     * @param lng a longitude of current location.
     */
    private void animateCameraTo(final double lat, final double lng) {
        if (mGoogleMap != null && !isAnimating) {
            isAnimating = true;
            mGoogleMap.getUiSettings().setScrollGesturesEnabled(false);
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.
                            fromLatLngZoom(new LatLng(lat, lng), previousZoomLevel)),
                    Constants.ANIMATION_DELAY_FOR_CURRENT_POSITION,
                    new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            mGoogleMap.getUiSettings().setScrollGesturesEnabled(true);
                            isAnimating = false;
                        }

                        @Override
                        public void onCancel() {
                            mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
                            isAnimating = false;
                        }
                    });
        }
    }
}
