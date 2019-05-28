package com.bykea.pk.partner.ui.helpers.adapters;

import android.content.Context;
import android.os.Handler;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.bykea.pk.partner.models.data.Predictions;
import com.bykea.pk.partner.models.response.PlaceAutoCompleteResponse;
import com.bykea.pk.partner.repositories.places.PlacesDataHandler;
import com.bykea.pk.partner.repositories.places.PlacesRepository;
//import com.google.android.gms.location.places.AutocompletePrediction;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.utils.Utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;


public class PlaceAutocompleteAdapter
        extends ArrayAdapter<Predictions> implements Filterable {


    private ArrayList<Predictions> mResultList;
    private String city;
    private PlacesRepository mRepository = new PlacesRepository();


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
            try {
                ((TextView) convertView.findViewById(R.id.placeNameTv)).setText(mResultList.get(position).getStructured_formatting().getMain_text());
                ((TextView) convertView.findViewById(R.id.placeAddressTv)).setText(Utils.formatAddress(mResultList.get(position).getDescription()));
//            ((TextView) convertView.findViewById(R.id.placeDistTv)).setText(distances.size() > position ? distances.get(position) + " km" : "N/A");
            } catch (Exception ignored) {
            }
        } else {
            ((TextView) convertView.findViewById(R.id.placeNameTv)).setText(StringUtils.EMPTY);
            ((TextView) convertView.findViewById(R.id.placeAddressTv)).setText(StringUtils.EMPTY);
//            ((TextView) convertView.findViewById(R.id.placeDistTv)).setText(StringUtils.EMPTY);
        }
        return convertView;
    }

    private final Handler autocompleteHandler = new Handler();

    /**
     * Returns the filter for the current set of autocomplete results.
     */
    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(final CharSequence constraint) {
                final FilterResults results = new FilterResults();
                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    if (constraint.length() >= 3) {
                        autocompleteHandler.removeMessages(0);
                        autocompleteHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mRepository.getPlaceAutoComplete(getContext(), constraint.toString(), new PlacesDataHandler() {
                                    @Override
                                    public void onError(String error) {

                                    }

                                    @Override
                                    public void onPlaceAutoCompleteResponse(PlaceAutoCompleteResponse response) {
                                        ArrayList<Predictions> resultList = response.getPredictions();
                                        if (resultList != null) {
                                            // The API successfully returned results.
                                            if (resultList.size() > 0) {
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
                                        notifyDataSetChanged();
                                    }
                                });
                            }
                        }, 1000);
                    } else if (mResultList != null) {
                        autocompleteHandler.removeMessages(0);
                        mResultList.clear();
                    }
                    if (mResultList != null && mResultList.size() > 0) {
                        results.values = mResultList;
                        results.count = mResultList.size();
                    }
                } else if (mResultList != null) {
                    autocompleteHandler.removeMessages(0);
//                    mResultList.clear();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                // The API returned at least one result, update the data.
                notifyDataSetChanged();
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                // Override this method to display a readable result in the AutocompleteTextView
                // when clicked.
//                if (resultValue instanceof AutocompletePrediction) {
//                    return ((AutocompletePrediction) resultValue).getFullText(null);
//                } else {
                return super.convertResultToString(resultValue);
//                }
            }
        };
    }


}
