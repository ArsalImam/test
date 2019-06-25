package com.bykea.pk.partner.ui.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.bykea.pk.partner.widgets.FontTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ContactUsFragment extends Fragment {


    @BindView(R.id.supportCall)
    FontTextView supportCall;
    @BindView(R.id.supportEmail)
    FontTextView supportEmail;
    @BindView(R.id.supportWatsapp)
    FontTextView supportWatsapp;
    @BindView(R.id.financeCall)
    FontTextView financeCall;
    @BindView(R.id.financeEmail)
    FontTextView financeEmail;
    @BindView(R.id.financeWatsapp)
    FontTextView financeWatsapp;

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

    @OnClick({R.id.supportCall, R.id.supportEmail, R.id.supportWatsapp, R.id.financeCall, R.id.financeEmail, R.id.financeWatsapp, R.id.bankAccountNumber})
    public void onClick(View view) {
        if (contactNumbers == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.supportCall:
                Utils.callingIntent(mCurrentActivity, contactNumbers.getData().getSupports().getCall());
                break;
            case R.id.supportEmail:
//                Utils.contactViaEmail(mCurrentActivity, contactNumbers.getData().getSupports().getEmail());
                ActivityStackManager.getInstance().startReportActivity(mCurrentActivity, "s");
                break;
            case R.id.supportWatsapp:
                Utils.contactViaWhatsApp(mCurrentActivity, contactNumbers.getData().getSupports().getWhatsapp());
                break;
            case R.id.financeCall:
                Utils.callingIntent(mCurrentActivity, contactNumbers.getData().getFinance().getCall());
                break;
            case R.id.financeEmail:
//                Utils.contactViaEmail(mCurrentActivity, contactNumbers.getData().getFinance().getEmail());
                ActivityStackManager.getInstance().startReportActivity(mCurrentActivity, "f");
                break;
            case R.id.financeWatsapp:
                Utils.contactViaWhatsApp(mCurrentActivity, contactNumbers.getData().getFinance().getWhatsapp());
                break;
            case R.id.bankAccountNumber:
                startActivity(new Intent(mCurrentActivity, BanksAccountActivity.class));
                break;
        }
    }
}
