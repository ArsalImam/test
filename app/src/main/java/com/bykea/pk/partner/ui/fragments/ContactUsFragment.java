package com.bykea.pk.partner.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.response.ContactNumbersResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.activities.BanksAccountActivity;
import com.bykea.pk.partner.ui.activities.HomeActivity;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.Utils;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ContactUsFragment extends Fragment {

    private ContactNumbersResponse contactNumbers;
    private HomeActivity mCurrentActivity;
    private Unbinder unbinder;

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

    @OnClick({R.id.supportCall, R.id.yourComplain, R.id.supportEmail, R.id.bankAccountNumber})
    public void onClick(View view) {
        if (contactNumbers == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.supportCall:
                Utils.callingIntent(mCurrentActivity, contactNumbers.getData().getSupports().getCall());
                break;
            case R.id.yourComplain:
                ActivityStackManager.getInstance().startComplainListActivity(mCurrentActivity);
                break;
            case R.id.supportEmail:
                ActivityStackManager.getInstance().startProblemActivity(mCurrentActivity, null);
                break;
            case R.id.bankAccountNumber:
                startActivity(new Intent(mCurrentActivity, BanksAccountActivity.class));
                break;
        }
    }
}
