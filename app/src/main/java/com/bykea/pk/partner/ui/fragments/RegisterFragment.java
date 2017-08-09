package com.bykea.pk.partner.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.activities.LoginActivity;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.widgets.FontEditText;
import com.bykea.pk.partner.widgets.FontTextView;

import org.apache.commons.lang3.StringUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {


    @Bind(R.id.backBtn)
    ImageView backBtn;
    @Bind(R.id.nameTv)
    FontEditText nameTv;
    @Bind(R.id.emailAddressTv)
    FontEditText emailAddressTv;
    @Bind(R.id.pinCodeTv)
    FontEditText pinCodeTv;
    @Bind(R.id.cityTv)
    FontEditText cityTv;
    @Bind(R.id.addressTv)
    FontEditText addressTv;
    @Bind(R.id.termsCheck)
    CheckBox termsCheck;
    @Bind(R.id.termsTv)
    TextView termsTv;
    @Bind(R.id.privacyPolicyTv)
    TextView privacyPolicyTv;
    @Bind(R.id.registerBtn)
    FontTextView registerBtn;

    private LoginActivity mCurrentActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this, view);
        LoginActivity.isRegisterFragment = true;
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        UserRepository repository = new UserRepository();
        mCurrentActivity = ((LoginActivity) getActivity());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LoginActivity.isRegisterFragment = false;
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LoginActivity.isRegisterFragment = false;
    }

    @OnClick({R.id.backBtn, R.id.registerBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                getActivity().onBackPressed();
                break;
            case R.id.registerBtn:
                if (isValid()) {
                    mCurrentActivity.getPilotData().setFullName(nameTv.getText().toString());
                    mCurrentActivity.getPilotData().setEmail(emailAddressTv.getText().toString());
                    mCurrentActivity.getPilotData().setPincode(pinCodeTv.getText().toString());
//                    mCurrentActivity.getPilotData().setCity(cityTv.getText().toString());
                    mCurrentActivity.getPilotData().setAddress(addressTv.getText().toString());
                    mCurrentActivity.getPilotData().setTermsAndConditions(termsCheck.isChecked());

                    VehicleInfoFragment vehicleInfoFragment = new VehicleInfoFragment();
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.fade_in,
                            R.anim.fade_out);
                    fragmentTransaction.replace(R.id.containerView, vehicleInfoFragment, null);
                    fragmentTransaction.addToBackStack("vehicleFragment");
                    fragmentTransaction.commit();
                    break;
                }
        }
    }

    private boolean isValid() {
        if (Connectivity.isConnectedFast(mCurrentActivity)) {
            if (StringUtils.isBlank(nameTv.getText().toString())) {
                nameTv.setError(getString(R.string.error_field_empty));
                nameTv.requestFocus();
                return false;
            } /*else if (StringUtils.isBlank(emailAddressTv.getText().toString())) {
            emailAddressTv.setError(getString(R.string.error_field_empty));
            emailAddressTv.requestFocus();
            return false;
        }*/ else if (StringUtils.isBlank(pinCodeTv.getText().toString())) {
                pinCodeTv.setError(getString(R.string.error_field_empty));
                pinCodeTv.requestFocus();
                return false;
            } else if (StringUtils.isBlank(cityTv.getText().toString())) {
                cityTv.setError(getString(R.string.error_field_empty));
                cityTv.requestFocus();
                return false;
            } else if (StringUtils.isBlank(addressTv.getText().toString())) {
                addressTv.setError(getString(R.string.error_field_empty));
                addressTv.requestFocus();
                return false;
            } else if (!termsCheck.isChecked()) {
                Dialogs.INSTANCE.showError(getActivity(), registerBtn, "check term and conditions");
                return false;
            }
            return true;
        } else {
            Dialogs.INSTANCE.showError(mCurrentActivity, registerBtn,
                    getString(R.string.error_internet_connectivity));
            return false;
        }
    }
}
