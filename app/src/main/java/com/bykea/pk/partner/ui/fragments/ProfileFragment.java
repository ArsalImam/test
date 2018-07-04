package com.bykea.pk.partner.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bykea.pk.partner.BuildConfig;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.PersonalInfoData;
import com.bykea.pk.partner.models.data.PilotData;
import com.bykea.pk.partner.models.response.GetProfileResponse;
import com.bykea.pk.partner.repositories.IUserDataHandler;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.activities.PostBankAccountActivity;
import com.bykea.pk.partner.ui.activities.BaseActivity;
import com.bykea.pk.partner.ui.activities.ChangePinActivity;
import com.bykea.pk.partner.ui.activities.HomeActivity;
import com.bykea.pk.partner.ui.activities.LicenseActivity;
import com.bykea.pk.partner.ui.activities.MotorbikeActivity;
import com.bykea.pk.partner.ui.activities.PersonalActivity;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.ApiTags;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {


    @BindView(R.id.loader)
    ProgressBar loader;

    @BindView(R.id.driverImage)
    CircleImageView driverImage;
    @BindView(R.id.driverNameTv)
    FontTextView driverNameTv;
    @BindView(R.id.driverAddressTv)
    FontTextView driverAddressTv;
    @BindView(R.id.driverCityTv)
    FontTextView driverCityTv;
    @BindView(R.id.driverLatLngTv)
    FontTextView driverLatLngTv;
    @BindView(R.id.personalInfoTv)
    FontTextView personalInfoTv;
    @BindView(R.id.pinCodeTv)
    FontTextView pinCodeTv;
    @BindView(R.id.licenseInfoTv)
    FontTextView licenseInfoTv;
    @BindView(R.id.motorbikeInfoTv)
    FontTextView motorbikeInfoTv;
    @BindView(R.id.tvVersion)
    FontTextView tvVersion;
    @BindView(R.id.bankAccDetailsTv)
    FontTextView bankAccDetailsTv;
    @BindView(R.id.llTop)
    LinearLayout llTop;
    @BindView(R.id.ivHomePin)
    ImageView ivHomePin;

    private UserRepository repository;
    private HomeActivity mCurrentActivity;
    private PersonalInfoData mPersonalInfo;
    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() != null) {
            ((BaseActivity) getActivity()).setToolbarTitle("Settings", "سیٹنگز");
        }
        ((BaseActivity) getActivity()).hideToolbarLogo();
        repository = new UserRepository();
        mCurrentActivity = (HomeActivity) getActivity();
        mCurrentActivity.findViewById(R.id.toolbarLine).setVisibility(View.VISIBLE);
        mCurrentActivity.hideStatusCompletely();
        setInfoUI();
        getProfileData();
    }

    private void setInfoUI() {
        PilotData user = AppPreferences.getPilotData();
//        driverNameTv.setText(user.getFullName());
//        driverAddressTv.setText(user.getAddress());
//        driverCityTv.setText(user.getCity());
        //TODO Update Home Address When coordinates available in API (Also check visibility in xml)
//        driverLatLngTv.setText(user.getLat() + "," + user.getLng());

        if (StringUtils.isNotBlank(AppPreferences.getPilotData().getPilotImage())) {
           /* Picasso.get().load(Utils.getImageLink(AppPreferences.getPilotData(mCurrentActivity).getPilotImage()))
                    .fit().centerInside()
                    .placeholder(R.drawable.profile_pic)
                    .into(driverImage);*/
            Utils.loadImgPicasso(mCurrentActivity, driverImage, R.drawable.profile_pic,
                    Utils.getImageLink(AppPreferences.getPilotData().getPilotImage()));
        }
        String appVersion = "v " + Utils.getVersion(mCurrentActivity);
        if (BuildConfig.DEBUG) {
            if (ApiTags.BASE_SERVER_URL.contains("staging")) {
                appVersion = appVersion + " - Staging URLs " + ApiTags.BASE_SERVER_URL;
            } else {
                appVersion = appVersion + " - Live URLs " + ApiTags.BASE_SERVER_URL;
            }
        }
        tvVersion.setText(appVersion);
    }


    @Override
    public void onDestroyView() {
        mCurrentActivity.hideUrduTitle();
        super.onDestroyView();
        unbinder.unbind();
    }

    private void getProfileData() {
        loader.setVisibility(View.VISIBLE);
        Dialogs.INSTANCE.showLoader(mCurrentActivity);
        repository.getProfileData(mCurrentActivity, callbackHandler);
    }

    @OnClick({R.id.personalInfoTv, R.id.termsTv, R.id.pinCodeTv, R.id.licenseInfoTv, R.id.motorbikeInfoTv, R.id.bankAccDetailsTv})
    public void onClick(View view) {
        if (mPersonalInfo != null) {
            switch (view.getId()) {
                case R.id.personalInfoTv:
                    Intent intent = new Intent(mCurrentActivity, PersonalActivity.class);
                    intent.putExtra(Constants.SETTINGS_DATA_EXTRAS, mPersonalInfo);
                    startActivity(intent);
                    break;
                case R.id.pinCodeTv:
                    startActivity(new Intent(getActivity(), ChangePinActivity.class));
                    break;
                case R.id.licenseInfoTv:
                    Intent intent2 = new Intent(mCurrentActivity, LicenseActivity.class);
                    intent2.putExtra(Constants.SETTINGS_DATA_EXTRAS, mPersonalInfo);
                    startActivity(intent2);
                    break;
                case R.id.motorbikeInfoTv:
                    Intent intent3 = new Intent(mCurrentActivity, MotorbikeActivity.class);
                    intent3.putExtra(Constants.SETTINGS_DATA_EXTRAS, mPersonalInfo);
                    startActivity(intent3);
                    break;
                case R.id.bankAccDetailsTv:
                    Intent intent4 = new Intent(mCurrentActivity, PostBankAccountActivity.class);
                    intent4.putExtra(Constants.SETTINGS_DATA_EXTRAS, mPersonalInfo);
                    startActivity(intent4);
                    break;
                case R.id.termsTv:
                    if (AppPreferences.getSettings() != null) {
                        Utils.startCustomWebViewActivity(mCurrentActivity,
                                AppPreferences.getSettings().getSettings().getTerms(), "Terms of Services");
                    }
                    break;
            }
        }
    }


    private IUserDataHandler callbackHandler = new UserDataHandler() {

        @Override
        public void onGetProfileResponse(final GetProfileResponse response) {
            mCurrentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (getView() != null) {
                        Dialogs.INSTANCE.dismissDialog();
                        loader.setVisibility(View.GONE);
                        llTop.setVisibility(View.VISIBLE);
                        if (response.isSuccess()) {
                            enableViews(true);
                            mPersonalInfo = response.getData();
                            PilotData data = AppPreferences.getPilotData();
                            data.setFullName(mPersonalInfo.getFull_name());
                            data.setPilotImage(mPersonalInfo.getImg_id());
                            data.setLicenseExpiry(mPersonalInfo.getLicense_expire());
                            mCurrentActivity.setPilotData(data);
                            AppPreferences.setPilotData(data);
                            AppPreferences.setProfileUpdated(true);
                            driverNameTv.setText(mPersonalInfo.getFull_name());
                            driverAddressTv.setText(mPersonalInfo.getAddress());
                            driverCityTv.setText(mPersonalInfo.getCity());
                            if (StringUtils.isNotBlank(mPersonalInfo.getHomeLat())
                                    && StringUtils.isNotBlank(mPersonalInfo.getHomeLng())) {
                                driverLatLngTv.setVisibility(View.VISIBLE);
                                ivHomePin.setVisibility(View.VISIBLE);
                                driverLatLngTv.setText(mPersonalInfo.getHomeLat() + "," + mPersonalInfo.getHomeLng());
                            }
                        } else {
                            enableViews(false);
                            if (response.getCode() == HTTPStatus.UNAUTHORIZED) {
                                Utils.onUnauthorized(mCurrentActivity);
                            } else {
                                Dialogs.INSTANCE.showToast(mCurrentActivity, response.getMessage());
                            }
                        }
                    }
                }
            });
        }

        @Override
        public void onError(final int errorCode, final String errorMessage) {
            if (mCurrentActivity != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (getView() != null) {
                            Dialogs.INSTANCE.dismissDialog();
                            loader.setVisibility(View.GONE);
                            if (errorCode == HTTPStatus.UNAUTHORIZED) {
                                Utils.onUnauthorized(mCurrentActivity);
                            } else {
                                Dialogs.INSTANCE.showToast(mCurrentActivity, errorMessage);
                            }
                        }
                    }
                });
            }

        }
    };

    private void enableViews(boolean enabled) {
        personalInfoTv.setEnabled(enabled);
        pinCodeTv.setEnabled(enabled);
        licenseInfoTv.setEnabled(enabled);
        motorbikeInfoTv.setEnabled(enabled);
        bankAccDetailsTv.setEnabled(enabled);
    }

}
