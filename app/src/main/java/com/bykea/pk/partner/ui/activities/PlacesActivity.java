package com.bykea.pk.partner.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bykea.pk.partner.ui.helpers.adapters.PlaceAutocompleteAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.GeoApiContext;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.PlacesResult;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class PlacesActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {

    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private AutoCompleteTextView mAutocompleteView;
    private ImageView clearSearchBtn;
    private Place place = null;
    private String primaryText;
    private String secondaryText;
    private FontTextView tvCities;
    private PlacesActivity mCurrentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        mCurrentActivity = this;

        GeoApiContext mGeoContext = new GeoApiContext().setApiKey(getString(R.string.google_api_serverkey));
        mGoogleApiClient = new GoogleApiClient.Builder(mCurrentActivity)
                .enableAutoManage(mCurrentActivity, 0 /* clientId */, mCurrentActivity)
                .addApi(Places.GEO_DATA_API)
                .build();

        initViews();
        setListeners();
    }

    private void setListeners() {
        tvCities.setOnClickListener(onClick);
    }

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tvCities:
                    //Remove comments to Allow different cities options if required
                    /*String[] citiesArray = new String[cities.size()];
                    int i = 0;
                    for (PlacesResult city : cities) {
                        citiesArray[i] = city.name;
                        i++;
                    }
                    Dialogs.INSTANCE.showItems(mCurrentActivity, citiesArray, new IntegerCallBack() {
                        @Override
                        public void onCallBack(int position) {
                            setCity(cities.get(position));
                        }
                    });*/
                    break;
            }
        }
    };

    private void initViews() {

        setToolbarTitle("Drop Off");
        hideToolbarLogo();
        setStatusButton("Cancel");
        AppPreferences.setDropOffData(mCurrentActivity, StringUtils.EMPTY, 0.0, 0.0);
        setBackNavigation();

        ArrayList<PlacesResult> cities = new ArrayList<>();
        cities = Utils.getCities(mCurrentActivity);


        clearSearchBtn = (ImageView) findViewById(R.id.clearBtn);
        mAutocompleteView = (AutoCompleteTextView)
                findViewById(R.id.autocomplete_places);
        tvCities = (FontTextView) findViewById(R.id.tvCities);
        setCity(cities.get(Utils.getCurrentCityIndex(mCurrentActivity)));

    }


    private void setCity(PlacesResult city) {
        mAutocompleteView.setText("");
        tvCities.setText(city.name);
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                .build();
        LatLngBounds.Builder bounds =
                new LatLngBounds.Builder();
        bounds.include(new LatLng(city.latitude, city.longitude));
        mAdapter = new PlaceAutocompleteAdapter(mCurrentActivity, mGoogleApiClient, bounds.build(),
                typeFilter, city.name);
        mAutocompleteView.setAdapter(mAdapter);
        mAutocompleteView.setDropDownWidth(getResources().getDisplayMetrics().widthPixels);

        clearSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAutocompleteView.setText("");
            }
        });
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();

    }

    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see GeoDataApi#getPlaceById(GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

            if (mCurrentActivity != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (position < mAdapter.getCount()) {
                            final AutocompletePrediction item = mAdapter.getItem(position);
                            final String placeId = item.getPlaceId();
                            primaryText = Utils.formatAddress(item.getFullText(null).toString());
                            mAutocompleteView.setText(primaryText);
                            Utils.redLog(Constants.APP_NAME, "Autocomplete item selected: " + primaryText);

                            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                                    .getPlaceById(mGoogleApiClient, placeId);
                            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

                            Utils.redLog(Constants.APP_NAME, "Called getPlaceById to get Place details for " + placeId);
                        }
                    }
                });
            }
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(final PlaceBuffer places) {
            if (mCurrentActivity != null && places != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!places.getStatus().isSuccess()) {
                            // Request did not complete successfully
                            Utils.redLog(Constants.APP_NAME, "Place query did not complete. Error: " + places.getStatus().toString());
                            places.release();
                            return;
                        }
                        // Get the Place object from the buffer.
                        if (places.get(0) == null) {
                            return;
                        }
                        place = places.get(0);
                        Utils.redLog(Constants.APP_NAME, "Place details received: " + place.toString());
                        Intent intent = new Intent(PlacesActivity.this, ConfirmDestinationActivity.class);
                        intent.putExtra("address", primaryText /*+ " " + place.getAddress()*/);
                        intent.putExtra("lat", place.getLatLng().latitude);
                        intent.putExtra("lng", place.getLatLng().longitude);
                        startActivity(intent);
                        finish();

                    }
                });
            }


        }
    };
}
