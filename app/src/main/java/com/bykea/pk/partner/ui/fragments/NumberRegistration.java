package com.bykea.pk.partner.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.SignUpAddNumberResponse;
import com.bykea.pk.partner.models.data.SignUpCity;
import com.bykea.pk.partner.models.data.SignUpSettingsResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.activities.DocumentsRegistrationActivity;
import com.bykea.pk.partner.ui.activities.RegistrationActivity;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.adapters.CityDropDownAdapter;
import com.bykea.pk.partner.ui.helpers.adapters.DocumentsGridAdapter;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.NumericKeyBoardTransformationMethod;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontEditText;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NumberRegistration extends Fragment {
    @BindView(R.id.phoneNumberEt)
    FontEditText phoneNumberEt;
    @BindView(R.id.cnicEt)
    FontEditText cnicEt;
    @BindView(R.id.ytIcon)
    ImageView ytIcon;

    @BindView(R.id.ivThumbnail)
    ImageView ivThumbnail;

    @BindView(R.id.ivRight0)
    ImageView ivRight0;

    @BindView(R.id.tvCity)
    Spinner spCities;

    private RegistrationActivity mCurrentActivity;
    private ArrayList<SignUpCity> mServiceCities = new ArrayList<>();
    private SignUpCity mSelectedCity;
    private CityDropDownAdapter dataAdapter1;

    private String VIDEO_ID;
    private YouTubePlayerSupportFragment playerFragment;
    private UserRepository mUserRepository;

    public NumberRegistration() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_number_registeration, container, false);
        ButterKnife.bind(this, view);
        mCurrentActivity = (RegistrationActivity) getActivity();
        if (mCurrentActivity != null) {
            mCurrentActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUserRepository = new UserRepository();
        if (mServiceCities.size() == 0) {
            setCitiesAdapter();
        } else {
            initAdapter();
        }
        phoneNumberEt.setText(Utils.phoneNumberToShow(AppPreferences.getPhoneNumber()));
        phoneNumberEt.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        cnicEt.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        Utils.hideSoftKeyboard(this);
        mCurrentActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ivRight0.setImageDrawable(Utils.changeDrawableColor(mCurrentActivity, R.drawable.polygon, R.color.colorAccent));

    }

    private void initYouTube() {
        playerFragment = (YouTubePlayerSupportFragment) getChildFragmentManager().findFragmentById(R.id.player_fragment);
        Utils.initPlayerFragment(playerFragment, ytIcon, ivThumbnail, VIDEO_ID);

    }

    private void setCitiesAdapter() {
        SignUpSettingsResponse response = (SignUpSettingsResponse) AppPreferences.getObjectFromSharedPref(SignUpSettingsResponse.class);
        if (response != null && Utils.isTimeWithInNDay(response.getTimeStamp(), 0.5)) {
            mCallback.onSignUpSettingsResponse(response);
        } else {
            Dialogs.INSTANCE.showLoader(mCurrentActivity);
            mUserRepository.requestSignUpSettings(mCurrentActivity, mCallback);
        }
    }


    private void initAdapter() {
        initYouTube();
        spCities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, final View view, final int position, long id) {
                if (view != null) {
                    view.findViewById(R.id.titleUrduName).setVisibility(View.INVISIBLE);
                } else {
                    final ViewTreeObserver layoutObserver = spCities.getViewTreeObserver();
                    layoutObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            View selectedView = spCities.getSelectedView();
                            if (selectedView != null) {
                                selectedView.findViewById(R.id.titleUrduName).setVisibility(View.INVISIBLE);
                            }
                            spCities.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    });
                }
                mSelectedCity = mServiceCities.get(position);
                Utils.hideSoftKeyboard(NumberRegistration.this);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spCities.setAdapter(dataAdapter1);
        spCities.setSelection(Utils.getCurrentCityIndex(mServiceCities));
    }


    private SignUpSettingsResponse mApiRespinse;
    private UserDataHandler mCallback = new UserDataHandler() {

        @Override
        public void onSignUpSettingsResponse(final SignUpSettingsResponse response) {
            if (mCurrentActivity != null && getView() != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mApiRespinse = response;
                        mServiceCities.addAll(response.getCity());
                        dataAdapter1 = new CityDropDownAdapter(mCurrentActivity, mServiceCities);
                        VIDEO_ID = response.getMain_video();
                        initAdapter();
                        Dialogs.INSTANCE.dismissDialog();
                    }
                });
            }
        }

        @Override
        public void onSignUpAddNumberResponse(final SignUpAddNumberResponse response) {
            if (mCurrentActivity != null && getView() != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.INSTANCE.dismissDialog();
                        logAnalyticsEvent();
                        nextActivity(response);
                    }
                });
            }
        }

        @Override
        public void onError(int errorCode, String errorMessage) {
            if (mCurrentActivity != null && getView() != null) {
                Dialogs.INSTANCE.dismissDialog();
                Utils.appToast(errorMessage);
            }
        }
    };

    private void logAnalyticsEvent() {
        try {
            JSONObject data = new JSONObject();
            data.put("PhoneNo", phoneNumberEt.getText().toString());
            data.put("CityId", mSelectedCity.get_id());
            data.put("IMEI", Utils.getDeviceId(mCurrentActivity));
            Utils.logFacebookEvent(mCurrentActivity, Constants.AnalyticsEvents.ON_SIGN_UP_MOBILE_ENTERED, data);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidData() {
        boolean isValid = true;
        if (!Utils.isValidNumber(mCurrentActivity, phoneNumberEt)) {
            isValid = false;
        } else if (StringUtils.isBlank(cnicEt.getText().toString())) {
            cnicEt.setError("CNIC is Required.");
            cnicEt.requestFocus();
            isValid = false;
        } else if (cnicEt.getText().toString().length() < 13) {
            cnicEt.setError("Please enter a valid CNIC No.");
            cnicEt.requestFocus();
            isValid = false;
        } else if (mSelectedCity == null || mSelectedCity.get_id() == null || mSelectedCity.get_id().isEmpty()) {
            Utils.appToast(getString(R.string.please_select_city));
            spCities.requestFocus();
            isValid = false;
        }
        return isValid;
    }

    @OnClick({R.id.ytIcon, R.id.nextBtn, R.id.llIv2})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ytIcon:
                Utils.playVideo(mCurrentActivity, VIDEO_ID, ivThumbnail, ytIcon, playerFragment);
                break;
            case R.id.nextBtn:
                if (isValidData()) {
                    Dialogs.INSTANCE.showLoader(mCurrentActivity);
                    mUserRepository.requestRegisterNumber(mCurrentActivity, phoneNumberEt.getText().toString(),
                            mSelectedCity.get_id(), cnicEt.getText().toString(), mCallback);
                }
                break;
            case R.id.llIv2:
                spCities.performClick();
                break;
        }
    }


    private void nextActivity(SignUpAddNumberResponse response) {
        if (DocumentsGridAdapter.getmInstanceForNullCheck() != null) {
            DocumentsGridAdapter.getInstance().resetTheInstance();
        }
        AppPreferences.setSignUpApiCalled(true);
        Intent intent = new Intent(mCurrentActivity, DocumentsRegistrationActivity.class);
        intent.putExtra(Constants.Extras.CNIC, cnicEt.getText().toString());
        intent.putExtra(Constants.Extras.PHONE_NUMBER, phoneNumberEt.getText().toString());
        intent.putExtra(Constants.Extras.SELECTED_ITEM, mSelectedCity);
        intent.putExtra(Constants.Extras.DRIVER_ID, response.get_id());
        intent.putExtra(Constants.Extras.IS_BIOMETRIC_VERIFIED, response.isVerification());
        intent.putExtra(Constants.Extras.SIGN_UP_IMG_BASE, mApiRespinse.getImage_base_url());
        intent.putExtra(Constants.Extras.SIGN_UP_DATA, response.getData());
        startActivity(intent);
    }
}
