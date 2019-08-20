package com.bykea.pk.partner.ui.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.PlacesResult;
import com.bykea.pk.partner.models.data.SavedPlaces;
import com.bykea.pk.partner.models.response.GetSavedPlacesResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.activities.SelectPlaceActivity;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.adapters.SavedPlacesAdapter;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlacesSavedFragment extends Fragment {


    private SelectPlaceActivity mCurrentActivity;

    @BindView(R.id.lvRecentPlaces)
    RecyclerView mLvRecentPlaces;

    private SavedPlacesAdapter mAdapterRecentPlaces;

    public PlacesSavedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places_recent, container, false);
        ButterKnife.bind(this, view);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCurrentActivity = (SelectPlaceActivity) getActivity();
        getSavedPlaces();
    }

    private void getSavedPlaces() {
        if (!AppPreferences.isSavedPlacesAPICalled()) {
            new UserRepository().getSavedPlaces(mCurrentActivity, new UserDataHandler() {
                @Override
                public void onGetSavedPlacesResponse(GetSavedPlacesResponse response) {
                    if (mCurrentActivity != null && getView() != null) {
                        mCurrentActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateAdapter();
                            }
                        });
                    }
                }
            });
        }
    }

    public void updateAdapter() {
        if (mAdapterRecentPlaces != null) {
            ArrayList<SavedPlaces> recentPlaces = AppPreferences.getSavedPlaces();
            if (recentPlaces != null && recentPlaces.size() > 0) {
                mAdapterRecentPlaces.update(recentPlaces);
            } else {
                mAdapterRecentPlaces.clear();
            }
        } else {
            populateSavedPlaces();
        }
    }

    private void populateSavedPlaces() {
        ArrayList<SavedPlaces> recentPlaces = AppPreferences.getSavedPlaces();
        if (recentPlaces != null && recentPlaces.size() > 0) {
            mAdapterRecentPlaces = new SavedPlacesAdapter(mCurrentActivity, recentPlaces, new SavedPlacesAdapter.OnItemClickListener() {
                @Override
                public void onItemClickListener(int position) {
                    SavedPlaces savedPlace = mAdapterRecentPlaces.getItem(position);
                    if (savedPlace != null) {
                        String result = savedPlace.getAddress();
                        String name = result.replace(result.substring(result.lastIndexOf(',') + 1), "").replace(",", "") + ";" + result.substring(result.lastIndexOf(',') + 1).trim();
                        PlacesResult placesResult = new PlacesResult(name,
                                savedPlace.getAddress(), savedPlace.getLat(), savedPlace.getLng());
                        Intent returnIntent = new Intent();
                        //PlacesResult data model implements Parcelable so we could pass object in extras
                        returnIntent.putExtra(Constants.CONFIRM_DROPOFF_ADDRESS_RESULT, placesResult);
                        if (mCurrentActivity.showChangeButton()) {
                            returnIntent.putExtra(Constants.Extras.TOP_BAR, mCurrentActivity.isPickUp() ? Constants.Extras.PICK_UP : Constants.Extras.DROP_OFF);
                        }
                        mCurrentActivity.setResult(Activity.RESULT_OK, returnIntent);
                        mCurrentActivity.finish();
                    }
                }

                @Override
                public void onStarClickListener(final int position) {
                    Dialogs.INSTANCE.showAlertDialog(mCurrentActivity, "Delete Place", "Are you sure you want to delete this Saved Place?", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Dialogs.INSTANCE.dismissDialog();
                            Dialogs.INSTANCE.showLoader(mCurrentActivity);
                            new UserRepository().deleteSavedPlace(mCurrentActivity, mAdapterRecentPlaces.getItem(position), new UserDataHandler() {
                                @Override
                                public void onDeleteSavedPlaceResponse() {
                                    if (mCurrentActivity != null && getView() != null) {
                                        mCurrentActivity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Dialogs.INSTANCE.dismissDialog();
                                                mAdapterRecentPlaces.remove(position);
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
                                                Utils.appToast(errorMessage);
                                            }
                                        });
                                    }
                                }
                            });

                        }
                    });
                }
            });
            LinearLayoutManager layoutManager = new LinearLayoutManager(mCurrentActivity);
            mLvRecentPlaces.setLayoutManager(layoutManager);
            mLvRecentPlaces.setItemAnimator(new DefaultItemAnimator());
            mLvRecentPlaces.setHasFixedSize(true);
            mLvRecentPlaces.setAdapter(mAdapterRecentPlaces);
        }
    }
}
