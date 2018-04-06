package com.bykea.pk.partner.ui.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.PlacesResult;
import com.bykea.pk.partner.models.data.SavedPlaces;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.activities.SelectPlaceActivity;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.adapters.RecentPlacesAdapter;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlacesRecentFragment extends Fragment {


    private SelectPlaceActivity mCurrentActivity;

    private UserRepository mRepository;

    @BindView(R.id.lvRecentPlaces)
    RecyclerView mLvRecentPlaces;

    private RecentPlacesAdapter mAdapterRecentPlaces;

    public PlacesRecentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (mAdapterRecentPlaces != null && mAdapterRecentPlaces.getItemCount() > 0) {
                for (int i = 0; i < mAdapterRecentPlaces.getItemCount(); i++) {
                    PlacesResult placesResult = mAdapterRecentPlaces.getItem(i);
                    if (!placesResult.isSaved) {
                        String placeId = mCurrentActivity.isPlaceSaved(placesResult.address, placesResult.latitude, placesResult.longitude);
                        placesResult.isSaved = StringUtils.isNotBlank(placeId);
                        placesResult.placeId = placeId;
                        if (placesResult.isSaved) {
                            mAdapterRecentPlaces.notifyItemChanged(i);
                            break;
                        }
                    }

                }
            }
        } catch (Exception ignored) {
        }
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
        mRepository = new UserRepository();
        populateRecentPlaces();
    }


    private void populateRecentPlaces() {
        ArrayList<PlacesResult> recentPlaces = AppPreferences.getRecentPlaces();
        if (recentPlaces != null && recentPlaces.size() > 0) {
            for (PlacesResult place : recentPlaces) {
                String placeId = mCurrentActivity.isPlaceSaved(place.address, place.latitude, place.longitude);
                place.isSaved = StringUtils.isNotBlank(placeId);
                place.placeId = placeId;
            }
            mAdapterRecentPlaces = new RecentPlacesAdapter(mCurrentActivity, recentPlaces, new RecentPlacesAdapter.OnItemClickListener() {
                @Override
                public void onItemClickListener(int position) {
                    PlacesResult placesResult = mAdapterRecentPlaces.getItem(position);
                    if (placesResult != null) {
                        String result = placesResult.address;
                        placesResult.name = result.replace(result.substring(result.lastIndexOf(',') + 1), "").replace(",", "") + ";" + result.substring(result.lastIndexOf(',') + 1).trim();
//                        AppPreferences.setRecentPlaces(mCurrentActivity, placesResult);
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
                    final PlacesResult placesResult = mAdapterRecentPlaces.getItem(position);
                    if (placesResult.isSaved) {
                        Dialogs.INSTANCE.showAlertDialog(mCurrentActivity, "Delete Place", "Are you sure you want to delete this Saved Place?", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Dialogs.INSTANCE.dismissDialog();
                                Dialogs.INSTANCE.showLoader(mCurrentActivity);
                                SavedPlaces savedPlaces = new SavedPlaces();
                                savedPlaces.setPlaceId(placesResult.placeId);
                                new UserRepository().deleteSavedPlace(mCurrentActivity, savedPlaces, new UserDataHandler() {
                                    @Override
                                    public void onDeleteSavedPlaceResponse() {
                                        if (mCurrentActivity != null && getView() != null) {
                                            mCurrentActivity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Dialogs.INSTANCE.dismissDialog();
                                                    mAdapterRecentPlaces.getItem(position).isSaved = false;
                                                    mAdapterRecentPlaces.notifyItemChanged(position);
                                                    mCurrentActivity.removeSavedPlace(placesResult.address, placesResult.latitude, placesResult.longitude);
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onError(int errorCode, final String errorMessage) {
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
                        ActivityStackManager.getInstance().startSavePlaceActivity(mCurrentActivity, placesResult);
                    }
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
