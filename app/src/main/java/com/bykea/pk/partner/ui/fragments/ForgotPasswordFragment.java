package com.bykea.pk.partner.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.response.ForgotPasswordResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.NumericKeyBoardTransformationMethod;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontEditText;

import org.apache.commons.lang3.StringUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPasswordFragment extends Fragment {


    @Bind(R.id.backBtn)
    ImageView backBtn;
    @Bind(R.id.phoneNumberEt)
    FontEditText phoneNumberEt;
    @Bind(R.id.sendBtn)
    Button sendBtn;

    private UserRepository repository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new UserRepository();
        phoneNumberEt.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        if (StringUtils.isNotBlank(AppPreferences.getPhoneNumber(getActivity()))) {
            phoneNumberEt.setText(Utils.phoneNumberToShow(AppPreferences.getPhoneNumber(getActivity())));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.backBtn, R.id.sendBtn, R.id.phoneNumberEt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                getActivity().onBackPressed();
                break;
            case R.id.sendBtn:
                if (Connectivity.isConnectedFast(getActivity())) {
                    if (Utils.isValidNumber(getActivity(), phoneNumberEt)) {
                        Dialogs.INSTANCE.showLoader(getActivity());
                        repository.requestForgotPassword(getActivity(), handler,
                                Utils.phoneNumberForServer(phoneNumberEt.getText().toString()));
                    }
                } else {
                    Dialogs.INSTANCE.showToast(getActivity(), "Please check your internet connection.");
                }
                break;
            case R.id.phoneNumberEt:
                if (!phoneNumberEt.hasFocus()) {
                    phoneNumberEt.requestFocus();
                }
                break;
        }
    }

    private UserDataHandler handler = new UserDataHandler() {
        @Override
        public void onForgotPassword(ForgotPasswordResponse commonResponse) {
            if (getView() != null) {
                Dialogs.INSTANCE.dismissDialog();
                if (commonResponse.isSuccess()) {
                    Dialogs.INSTANCE.showSuccessMessage(getActivity(), sendBtn, commonResponse.getMessage());
                    getActivity().onBackPressed();
                } else {
                    Dialogs.INSTANCE.showError(getActivity(), sendBtn, commonResponse.getMessage());
                }
            }
        }

        @Override
        public void onError(int errorCode, String errorMessage) {
            Dialogs.INSTANCE.dismissDialog();
        }
    };
}
