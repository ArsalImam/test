package com.bykea.pk.partner.ui.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.response.VerifyCodeResponse;
import com.bykea.pk.partner.models.response.VerifyNumberResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.activities.LoginActivity;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.widgets.FontEditText;
import com.bykea.pk.partner.widgets.FontTextView;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CodeVerificationFragment extends Fragment {


    @Bind(R.id.topIconIv)
    ImageView topIconIv;
    @Bind(R.id.subtitleTv)
    FontTextView subtitleTv;
    @Bind(R.id.pinCodeEt)
    FontEditText pinCodeEt;
    @Bind(R.id.verifyBtn)
    FontTextView verifyBtn;
    @Bind(R.id.resendCodeBtn)
    FontTextView resendCodeBtn;

    private UserRepository repository;
    private LoginActivity mCurrentActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_code_verification, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new UserRepository();
        mCurrentActivity = ((LoginActivity) getActivity());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    private String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{4}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
    }

    @OnClick({R.id.verifyBtn, R.id.resendCodeBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.verifyBtn:
                if (StringUtils.isNoneBlank(pinCodeEt.getText().toString())) {
                    Dialogs.INSTANCE.showLoader(mCurrentActivity);
                    repository.requestCodeAuthentication(getActivity(), handler,
                            pinCodeEt.getText().toString(),
                            mCurrentActivity.getPilotData().getPhoneNo());
                } else {
                    pinCodeEt.setError(getString(R.string.error_field_empty));
                    pinCodeEt.requestFocus();
                }

                break;
            case R.id.resendCodeBtn:
                Dialogs.INSTANCE.showLoader(getActivity());
                repository.requestPhoneNumberVerification(getActivity(),
                        handler, mCurrentActivity.getPilotData().getPhoneNo());
                break;
        }
    }

    private UserDataHandler handler = new UserDataHandler() {

        @Override
        public void onNumberVerification(VerifyNumberResponse commonResponse) {
            Dialogs.INSTANCE.dismissDialog();
            if (commonResponse.isSuccess()) {
                Dialogs.INSTANCE.showSuccessMessage(getActivity(), verifyBtn, commonResponse.getMessage());
            } else {
                Dialogs.INSTANCE.showError(getActivity(), verifyBtn, commonResponse.getMessage());
            }
        }

        @Override
        public void onCodeVerification(VerifyCodeResponse commonResponse) {
            Dialogs.INSTANCE.dismissDialog();
            if (commonResponse.isSuccess()) {
                RegisterFragment registerFragment = new RegisterFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.fade_in,
                        R.anim.fade_out);
                fragmentTransaction.replace(R.id.containerView, registerFragment, null);
                fragmentTransaction.addToBackStack("registerFragment");
                fragmentTransaction.commit();
            } else {
                Dialogs.INSTANCE.showError(getActivity(), resendCodeBtn, commonResponse.getMessage());
                mCurrentActivity.getPilotData().setPhoneNo("");
            }
        }

        @Override
        public void onError(int errorCode, String errorMessage) {
            Dialogs.INSTANCE.dismissDialog();
            Dialogs.INSTANCE.showError(getActivity(), resendCodeBtn, errorMessage);
        }
    };

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
