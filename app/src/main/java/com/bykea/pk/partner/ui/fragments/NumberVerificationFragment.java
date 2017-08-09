package com.bykea.pk.partner.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bykea.pk.partner.ui.activities.LoginActivity;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.response.VerifyNumberResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.widgets.FontEditText;
import com.bykea.pk.partner.widgets.FontTextView;

import org.apache.commons.lang3.StringUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NumberVerificationFragment extends Fragment {


    @Bind(R.id.backBtn)
    ImageView backBtn;
    @Bind(R.id.topIconIv)
    ImageView topIconIv;
    @Bind(R.id.subtitleTv)
    FontTextView subtitleTv;
    @Bind(R.id.countryTv)
    FontTextView countryTv;
    @Bind(R.id.countryCodeEt)
    FontEditText countryCodeEt;
    @Bind(R.id.phoneNumberEt)
    FontEditText phoneNumberEt;
    @Bind(R.id.verifyBtn)
    FontTextView verifyBtn;

    private String mPhoneNumber, mCountryCode;
    private UserRepository repository;
    private LoginActivity mCurrentActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_number_verification, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.backBtn, R.id.verifyBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                getActivity().onBackPressed();
                break;
            case R.id.verifyBtn:
                if (validate()) {
                    Dialogs.INSTANCE.showLoader(getActivity());
                    repository.requestPhoneNumberVerification(getActivity(),
                            handler, mPhoneNumber);
                }
                break;
        }
    }

    private void init() {
        mCurrentActivity = ((LoginActivity) getActivity());
        repository = new UserRepository();
        mCountryCode = countryCodeEt.getText().toString();
        PhoneNumberFormattingTextWatcher mPhoneNumberFormattingTextWatcher = new PhoneNumberFormattingTextWatcher();
        phoneNumberEt.addTextChangedListener(mPhoneNumberFormattingTextWatcher);
    }

    private boolean validate() {
        if (StringUtils.isBlank(phoneNumberEt.getText().toString())) {
            Dialogs.INSTANCE.showToast(getActivity(), "Invalid Number");
            phoneNumberEt.setText("");
            phoneNumberEt.requestFocus();
            return false;
        }else if (!parseContact(phoneNumberEt.getText().toString(), mCountryCode.replace("+", ""))){
            Dialogs.INSTANCE.showToast(getActivity(), "Invalid Number");
            phoneNumberEt.setText("");
            phoneNumberEt.requestFocus();
            return false;
        }
        return true;
    }

    public boolean parseContact(String contact, String countrycode) {
        Phonenumber.PhoneNumber phoneNumber = null;
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        String finalNumber = null;
        String isoCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(countrycode));
        boolean isValid = false;
        PhoneNumberUtil.PhoneNumberType isMobile = null;
        try {
            phoneNumber = phoneNumberUtil.parse(contact, isoCode);
            isValid = phoneNumberUtil.isValidNumber(phoneNumber);
            isMobile = phoneNumberUtil.getNumberType(phoneNumber);

        } catch (NumberParseException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


        if (isValid
                && (PhoneNumberUtil.PhoneNumberType.MOBILE == isMobile ||
                PhoneNumberUtil.PhoneNumberType.FIXED_LINE_OR_MOBILE == isMobile)) {
            finalNumber = phoneNumberUtil.format(phoneNumber,
                    PhoneNumberUtil.PhoneNumberFormat.E164).substring(1);
            mPhoneNumber = finalNumber;
            return true;
        }

        return false;
    }

    private UserDataHandler handler = new UserDataHandler() {
        @Override
        public void onNumberVerification(VerifyNumberResponse commonResponse) {
            Dialogs.INSTANCE.dismissDialog();
            if (commonResponse.isSuccess()) {
                mCurrentActivity.getPilotData().setPhoneNo(mPhoneNumber);
                Dialogs.INSTANCE.showSuccessMessage(getActivity(), verifyBtn, commonResponse.getMessage());
                CodeVerificationFragment codeVerificationFragment = new CodeVerificationFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.fade_in,
                        R.anim.fade_out);
                fragmentTransaction.replace(R.id.containerView, codeVerificationFragment, null);
                fragmentTransaction.addToBackStack("codeVerification");
                fragmentTransaction.commit();
            } else {
                Dialogs.INSTANCE.showError(getActivity(), verifyBtn, commonResponse.getMessage());
            }
        }

        @Override
        public void onError(int errorCode, String errorMessage) {
            Dialogs.INSTANCE.dismissDialog();
            Dialogs.INSTANCE.showError(getActivity(), verifyBtn, errorMessage);

        }
    };
}
