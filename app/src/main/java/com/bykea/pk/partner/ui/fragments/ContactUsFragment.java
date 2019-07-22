package com.bykea.pk.partner.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.dal.source.JobRequestsDataSource;
import com.bykea.pk.partner.dal.source.JobRequestsRepository;
import com.bykea.pk.partner.dal.util.Injection;
import com.bykea.pk.partner.models.response.ContactNumbersResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.activities.BanksAccountActivity;
import com.bykea.pk.partner.ui.activities.HomeActivity;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.Utils;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ContactUsFragment extends Fragment {

    private ContactNumbersResponse contactNumbers;
    private HomeActivity mCurrentActivity;
    private Unbinder unbinder;
    private JobRequestsRepository jobRequestsRepository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCurrentActivity = (HomeActivity) getActivity();

        jobRequestsRepository = Injection.INSTANCE.provideBookingsRepository(mCurrentActivity);

        mCurrentActivity.setToolbarTitle("Contact Us", "رابطہ");
        mCurrentActivity.hideToolbarLogo();

        mCurrentActivity.findViewById(R.id.toolbarLine).setVisibility(View.VISIBLE);
        mCurrentActivity.findViewById(R.id.statusLayout).setVisibility(View.VISIBLE);
        mCurrentActivity.hideStatusCompletely();
        UserRepository repository = new UserRepository();
        Dialogs.INSTANCE.showLoader(mCurrentActivity);
        repository.requestContactNumbers(mCurrentActivity, handler);
    }

    private UserDataHandler handler = new UserDataHandler() {
        @Override
        public void getContactNumbers(ContactNumbersResponse response) {
            Dialogs.INSTANCE.dismissDialog();
            contactNumbers = response;
        }

        @Override
        public void onError(int errorCode, String errorMessage) {
            Dialogs.INSTANCE.dismissDialog();
            Dialogs.INSTANCE.showToast(errorMessage);
            if (errorCode == HTTPStatus.UNAUTHORIZED) {
                Utils.logout(mCurrentActivity);
            }

        }
    };

    @Override
    public void onDestroyView() {
        mCurrentActivity.hideUrduTitle();
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.supportCall, R.id.submittedComplains, R.id.reportComplain, R.id.bankAccountNumber})
    public void onClick(View view) {
        if (contactNumbers == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.supportCall:
                Utils.callingIntent(mCurrentActivity, contactNumbers.getData().getSupports().getCall());
                break;
            case R.id.submittedComplains: {
                if (AppPreferences.isEmailVerified()) {
                    checkStatusForZendeskSDK();
                } else {
                    checkIsEmailUpdatedFromRemoteDataSource();
                }
            }
                break;
            case R.id.reportComplain: {
                ActivityStackManager.getInstance().startComplainSubmissionActivity(mCurrentActivity, null);
            }
                break;
            case R.id.bankAccountNumber:
                startActivity(new Intent(mCurrentActivity, BanksAccountActivity.class));
                break;
        }
    }

    /**
     * Check Is Email Updated From Remote Data Source
     */
    private void checkIsEmailUpdatedFromRemoteDataSource() {
        Dialogs.INSTANCE.showLoader(mCurrentActivity);
        jobRequestsRepository.checkEmailUpdate(new JobRequestsDataSource.EmailUpdateCheckCallback() {
            @Override
            public void onSuccess(boolean isEmailUpdated) {
                Dialogs.INSTANCE.dismissDialog();
                if (isEmailUpdated) {
                    AppPreferences.isEmailVerified();
                    Utils.setZendeskIdentity();
                    checkStatusForZendeskSDK();
                } else {
                    ActivityStackManager.getInstance().startComplainListActivity(mCurrentActivity);
                }
            }

            @Override
            public void onFail(@org.jetbrains.annotations.Nullable String message) {
                Dialogs.INSTANCE.dismissDialog();
                Utils.appToast(mCurrentActivity, mCurrentActivity.getString(R.string.error_try_again));
            }
        });
    }

    /**
     * Check Status For Zendesk SDK
     * If Ready : Open Detail Fragment - To Submit Ticket
     * If Not : Open Zendesk Identity Activity
     */
    private void checkStatusForZendeskSDK() {
        if (AppPreferences.isZendeskSDKReady()) {
            ActivityStackManager.getInstance().startComplainListActivity(mCurrentActivity);
        } else {
            if (!AppPreferences.checkKeyExist(Keys.ZENDESK_IDENTITY_SETUP_TIME)) {
                //INTIALIZE ZENDESK SDK
                Utils.setZendeskIdentity();
                AppPreferences.setZendeskSDKSetupTime();
                ActivityStackManager.getInstance().startZendeskIdentityActivity(mCurrentActivity);
            } else {
                if ((new Date().getTime() - AppPreferences.getZendeskSDKSetupTime().getTime()) < Constants.ZendeskConfigurations.ZENDESK_SETTING_IDENTITY_MAX_TIME) {
                    //ZENDESK SDK NOT READY
                    ActivityStackManager.getInstance().startZendeskIdentityActivity(mCurrentActivity);
                } else {
                    //ZENDESK SDK IS READY
                    AppPreferences.setZendeskSDKReady();
                    ActivityStackManager.getInstance().startComplainListActivity(mCurrentActivity);
                }
            }
        }
    }
}
