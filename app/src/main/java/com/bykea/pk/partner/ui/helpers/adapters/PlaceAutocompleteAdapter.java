package com.bykea.pk.partner.ui.helpers.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.utils.Utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class PlaceAutocompleteAdapter
        extends ArrayAdapter<AutocompletePrediction> implements Filterable {

    private static final String TAG = "PlaceAutocompleteAdapter";
    private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);
    /**
     * Current results returned by this adapter.
     */
    private ArrayList<AutocompletePrediction> mResultList;
    private String city;
    private ArrayList<String> distances = new ArrayList<>();

    /**
     * Handles autocomplete requests.
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * The bounds used for Places Geo Data autocomplete API requests.
     */
    private LatLngBounds mBounds;

    /**
     * The autocomplete filter used to restrict queries to a specific set of place types.
     */
    private AutocompleteFilter mPlaceFilter;

    /**
     * Initializes with a resource for text rows and autocomplete query bounds.
     *
     * @see ArrayAdapter#ArrayAdapter(Context, int)
     */
    public PlaceAutocompleteAdapter(Context context, GoogleApiClient googleApiClient,
                                    LatLngBounds bounds, AutocompleteFilter filter) {
        super(context, -1, android.R.id.text1);
        mGoogleApiClient = googleApiClient;
        mBounds = bounds;
        mPlaceFilter = filter;
    }

    public PlaceAutocompleteAdapter(Context context, GoogleApiClient googleApiClient,
                                    LatLngBounds bounds, AutocompleteFilter filter, String city) {
        super(context, -1, android.R.id.text1);
        mGoogleApiClient = googleApiClient;
        mBounds = bounds;
        mPlaceFilter = filter;
        this.city = city;

    }

    /**
     * Sets the bounds for all subsequent queries.
     */
    public void setBounds(LatLngBounds bounds) {
        mBounds = bounds;
    }

    /**
     * Returns the number of results received in the last autocomplete query.
     */
    @Override
    public int getCount() {
        return mResultList != null ? mResultList.size() : 0;
    }

    /**
     * Returns an item from the last autocomplete query.
     */
    @Override
    public AutocompletePrediction getItem(int position) {
        return mResultList.get(position);
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
      /*  View row = super.getView(position, convertView, parent);

        // Sets the primary and secondary text for a row.
        // Note that getPrimaryText() and getSecondaryText() return a CharSequence that may contain
        // styling based on the given CharacterStyle.

        AutocompletePrediction item = getItem(position);

        TextView textView1 = (TextView) row.findViewById(android.R.id.text1);
        TextView textView2 = (TextView) row.findViewById(android.R.id.text2);
        textView1.setText(item.getPrimaryText(STYLE_BOLD));
        textView2.setText(item.getSecondaryText(STYLE_BOLD));
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/roboto_light.ttf");
        textView1.setTypeface(typeface);
        textView2.setTypeface(typeface);
        textView1.setTextSize(14);
        textView2.setTextSize(14);

        return row;*/

        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_places, null);
        ((TextView) convertView.findViewById(R.id.placeNameTv)).setText(getItem(position).getPrimaryText(null));
        ((TextView) convertView.findViewById(R.id.placeAddressTv)).setText(Utils.formatAddress(getItem(position).getFullText(null).toString()));
        ((TextView) convertView.findViewById(R.id.placeDistTv)).setText(distances.size() > position ? distances.get(position) + " km" : "N/A");
        return convertView;
    }

    /**
     * Returns the filter for the current set of autocomplete results.
     */
    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    // Query the autocomplete API for the (constraint) search string.
                    ArrayList<AutocompletePrediction> resultList = getAutocomplete(constraint);
                    if (resultList != null) {
                        // The API successfully returned results.
                        if (StringUtils.isNotBlank(city)) {
                            Iterator<AutocompletePrediction> it = resultList.iterator();
                            while (it.hasNext()) {
                                AutocompletePrediction item = it.next();
                                if (city.equalsIgnoreCase("Rawalpindi")) {
                                    if (!item.getSecondaryText(null).toString().contains(city) &&
                                            !item.getSecondaryText(null).toString().contains("Islamabad")) {
                                        it.remove();
                                    }
                                } else {
                                    if (!item.getSecondaryText(null).toString().contains(city)) {
                                        it.remove();
                                    }
                                }
                            }
                        }
                        if (resultList.size() > 0) {
                            final CountDownLatch resultLatch = new CountDownLatch(1);
                            String[] placeIds = new String[resultList.size()];
                            for (int i = 0; i < resultList.size(); i++) {
                                placeIds[i] = resultList.get(i).getPlaceId();
                            }
                            distances = new ArrayList<>();
                            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                                    .getPlaceById(mGoogleApiClient, placeIds);
                            placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                                @Override
                                public void onResult(@NonNull PlaceBuffer places) {
                                    if (places.getStatus() != null && places.getStatus().isSuccess()
                                            && places.getCount() > 0) {
                                        for (Place place : places) {
                                            distances.add("" + Math.round(((Utils.calculateDistance(place.getLatLng().latitude,
                                                    place.getLatLng().longitude, AppPreferences.getLatitude(getContext()),
                                                    AppPreferences.getLongitude(getContext()))) / 1000) * 10.0) / 10.0);
                                        }
                                        places.release();
                                    }
                                    resultLatch.countDown();
                                }
                            });
                            try {
                                resultLatch.await();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            mResultList = resultList;
                            results.values = mResultList;
                            results.count = mResultList.size();
                        }
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged();
                } else {
                    // The API did not return any results, invalidate the data set.
                    notifyDataSetInvalidated();
                }
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                // Override this method to display a readable result in the AutocompleteTextView
                // when clicked.
                if (resultValue instanceof AutocompletePrediction) {
                    return ((AutocompletePrediction) resultValue).getFullText(null);
                } else {
                    return super.convertResultToString(resultValue);
                }
            }
        };
    }

    /**
     * Submits an autocomplete query to the Places Geo Data Autocomplete API.
     * Results are returned as frozen AutocompletePrediction objects, ready to be cached.
     * objects to store the Place ID and description that the API returns.
     * Returns an empty list if no results were found.
     * Returns null if the API client is not available or the query did not complete
     * successfully.
     * This method MUST be called off the main UI thread, as it will block until data is returned
     * from the API, which may include a network request.
     *
     * @param constraint Autocomplete query string
     * @return Results from the autocomplete API or null if the query was not successful.
     * @see Places#GEO_DATA_API#getAutocomplete(CharSequence)
     * @see AutocompletePrediction#freeze()
     */
    private ArrayList<AutocompletePrediction> getAutocomplete(CharSequence constraint) {
        if (mGoogleApiClient.isConnected()) {
            Utils.redLog(TAG, "Starting autocomplete query for: " + constraint);

            // Submit the query to the autocomplete API and retrieve a PendingResult that will
            // contain the results when the query completes.
            PendingResult<AutocompletePredictionBuffer> results =
                    Places.GeoDataApi
                            .getAutocompletePredictions(mGoogleApiClient, constraint.toString(),
                                    mBounds, mPlaceFilter);

            // This method should have been called off the main UI thread. Block and wait for at most 60s
            // for a result from the API.
            AutocompletePredictionBuffer autocompletePredictions = results
                    .await(60, TimeUnit.SECONDS);

            // Confirm that the query completed successfully, otherwise return null
            final Status status = autocompletePredictions.getStatus();
            if (!status.isSuccess()) {
                Utils.redLog(TAG, "Error getting autocomplete prediction API call: " + status.toString());
                autocompletePredictions.release();
                return null;
            }

            Utils.redLog(TAG, "Query completed. Received " + autocompletePredictions.getCount()
                    + " predictions.");

            // Freeze the results immutable representation that can be stored safely.
            return DataBufferUtils.freezeAndClose(autocompletePredictions);
        }
        Utils.redLog(TAG, "Google API client is not connected for autocomplete query.");
        return null;
    }


}
