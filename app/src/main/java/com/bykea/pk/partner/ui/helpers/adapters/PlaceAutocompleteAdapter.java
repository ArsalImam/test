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

import com.bykea.pk.partner.communication.rest.RestRequestHandler;
import com.bykea.pk.partner.models.data.Predictions;
import com.bykea.pk.partner.models.response.PlaceAutoCompleteResponse;
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
        extends ArrayAdapter<Predictions> implements Filterable {

    private ArrayList<Predictions> mResultList;
    private String city;
    private RestRequestHandler mRestRequestHandler = new RestRequestHandler();



    public PlaceAutocompleteAdapter(Context context, String city) {
        super(context, -1, android.R.id.text1);
        this.city = city;
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
    public Predictions getItem(int position) {
        return mResultList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_places, null);
        if (mResultList != null && position < mResultList.size()) {
            ((TextView) convertView.findViewById(R.id.placeNameTv)).setText(mResultList.get(position).getStructured_formatting().getMain_text());
            ((TextView) convertView.findViewById(R.id.placeAddressTv)).setText(Utils.formatAddress(mResultList.get(position).getDescription()));
//            ((TextView) convertView.findViewById(R.id.placeDistTv)).setText(distances.size() > position ? distances.get(position) + " km" : "N/A");
        } else {
            ((TextView) convertView.findViewById(R.id.placeNameTv)).setText(StringUtils.EMPTY);
            ((TextView) convertView.findViewById(R.id.placeAddressTv)).setText(StringUtils.EMPTY);
//            ((TextView) convertView.findViewById(R.id.placeDistTv)).setText(StringUtils.EMPTY);
        }
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

                    if (constraint.length() == 4 || constraint.length() == 6 || constraint.length() >= 8) {
                        // Query the autocomplete API for the (constraint) search string.
//                        ArrayList<AutocompletePrediction> resultList = getAutocomplete(constraint);
                        PlaceAutoCompleteResponse result = mRestRequestHandler.autocomplete(constraint.toString());
                        ArrayList<Predictions> resultList = result.getPredictions();
                        if (resultList != null) {
                            // The API successfully returned results.
                            if (StringUtils.isNotBlank(city)) {
                                Iterator<Predictions> it = resultList.iterator();
                                while (it.hasNext()) {
                                    Predictions item = it.next();
                                    if (city.equalsIgnoreCase("Rawalpindi")) {
                                        if (!item.getStructured_formatting().getSecondary_text().contains(city) &&
                                                !item.getStructured_formatting().getSecondary_text().contains("Islamabad")) {
                                            it.remove();
                                        }
                                    } else {
                                        if (!item.getStructured_formatting().getSecondary_text().contains(city)) {
                                            it.remove();
                                        }
                                    }
                                }
                            }
                            if (resultList.size() > 0) {
                                //details api starts from here
                               /* final CountDownLatch resultLatch = new CountDownLatch(1);
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
                                                        place.getLatLng().longitude, AppPreferences.getLatitude(),
                                                        AppPreferences.getLongitude())) / 1000) * 10.0) / 10.0);
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
                                }*/

                                mResultList = resultList;
                                results.values = mResultList;
                                results.count = mResultList.size();
                            } else {
                                if (mResultList != null && mResultList.size() > 0) {
                                    results.values = mResultList;
                                    results.count = mResultList.size();
                                }
                            }
                        }
                    } else if (mResultList != null && mResultList.size() > 0) {
                        results.values = mResultList;
                        results.count = mResultList.size();
                    }
                } else if (mResultList != null) {
                    mResultList.clear();
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

}
