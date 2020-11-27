package com.bykea.pk.partner.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.dal.source.JobsDataSource;
import com.bykea.pk.partner.dal.source.JobsRepository;
import com.bykea.pk.partner.dal.util.Injection;
import com.bykea.pk.partner.models.response.ContactNumbersResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.activities.BanksAccountActivity;
import com.bykea.pk.partner.ui.activities.HomeActivity;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.TelloTalkManager;
import com.bykea.pk.partner.utils.Utils;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ContactUsFragment extends Fragment {

    private ContactNumbersResponse contactNumbers;
    private HomeActivity mCurrentActivity;
    private Unbinder unbinder;
    private JobsRepository jobsRepository;
    private UserRepository repository;

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

        jobsRepository = Injection.INSTANCE.provideJobsRepository((mCurrentActivity));

        mCurrentActivity.setToolbarTitle("Contact Us", "رابطہ");
        mCurrentActivity.hideToolbarLogo();

        mCurrentActivity.findViewById(R.id.toolbarLine).setVisibility(View.VISIBLE);
        mCurrentActivity.findViewById(R.id.statusLayout).setVisibility(View.VISIBLE);
        mCurrentActivity.hideStatusCompletely();

        repository = new UserRepository();
    }

    private UserDataHandler handler = new UserDataHandler() {
        @Override
        public void getContactNumbers(ContactNumbersResponse response) {
            Dialogs.INSTANCE.dismissDialog();
            contactNumbers = response;
            checkContactNumberAndCall(false);
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
        switch (view.getId()) {
            case R.id.supportCall:
                checkContactNumberAndCall(true);
                break;
            case R.id.submittedComplains:
                TelloTalkManager.instance().openCorporateChat(mCurrentActivity, null, null);
                break;
            case R.id.reportComplain:
                ActivityStackManager.getInstance().startComplainDepartmentActivity(mCurrentActivity);
                break;
            case R.id.bankAccountNumber:
                startActivity(new Intent(mCurrentActivity, BanksAccountActivity.class));
                break;
        }
    }

    /**
     * Check ContactNumber object
     * if it not null, get support contact number and land to mobile calling screen
     * if null, generate call if @param generateApiCall is True
     *
     * @param generateApiCall : Call API If True
     */
    private void checkContactNumberAndCall(boolean generateApiCall) {
        if (contactNumbers != null && contactNumbers.getData() != null &&
                contactNumbers.getData().getSupports() != null && contactNumbers.getData().getSupports().getCall() != null) {
            Utils.callingIntent(mCurrentActivity, contactNumbers.getData().getSupports().getCall());
        } else if (generateApiCall) {
            Dialogs.INSTANCE.showLoader(mCurrentActivity);
            repository.requestContactNumbers(mCurrentActivity, handler);
        }
    }

    /**
     * Check Is Email Updated From Remote Data Source
     */
    private void checkIsEmailUpdatedFromRemoteDataSource() {
        Dialogs.INSTANCE.showLoader(mCurrentActivity);
        jobsRepository.checkEmailUpdate(new JobsDataSource.EmailUpdateCheckCallback() {
            @Override
            public void onSuccess(@org.jetbrains.annotations.Nullable Boolean isEmailUpdated) {
                Dialogs.INSTANCE.dismissDialog();
                if (isEmailUpdated != null && isEmailUpdated)
                    AppPreferences.setEmailVerified();
                ActivityStackManager.getInstance().startComplainListActivity(mCurrentActivity);
            }

            @Override
            public void onFail(@org.jetbrains.annotations.Nullable String message) {
                Dialogs.INSTANCE.dismissDialog();
                Utils.appToast(mCurrentActivity.getString(R.string.error_try_again));
            }
        });
    }
}