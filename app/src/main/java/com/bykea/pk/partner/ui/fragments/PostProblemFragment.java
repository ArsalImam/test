package com.bykea.pk.partner.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.response.ProblemPostResponse;
import com.bykea.pk.partner.repositories.IUserDataHandler;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.activities.ProblemActivity;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontButton;
import com.bykea.pk.partner.widgets.FontEditText;

import org.apache.commons.lang3.StringUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostProblemFragment extends Fragment {


    @Bind(R.id.submitBtn)
    FontButton submitBtn;

    @Bind(R.id.etEmail)
    FontEditText etEmail;

    @Bind(R.id.etDetails)
    FontEditText etDetails;

    @Bind(R.id.text_lay)
    LinearLayout text_lay;

    @Bind(R.id.editText_lay)
    LinearLayout editText_lay;

    private ProblemActivity mCurrentActivity;


    public PostProblemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_problem, container, false);
        ButterKnife.bind(this, view);
        setRetainInstance(true);
        mCurrentActivity = (ProblemActivity) getActivity();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if(StringUtils.isNotBlank(AppPreferences.getPilotData().getEmail())) {
            etEmail.setText(AppPreferences.getPilotData().getEmail());
            etDetails.requestFocus();
        }else{
            etEmail.requestFocus();
        }
    }

    @OnClick({R.id.submitBtn})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.submitBtn:
                if(submitBtn.getText().toString().equalsIgnoreCase(getString(R.string.back_to_booking_history))){
                    mCurrentActivity.finish();
                }else {
                    if (isValid()) {
                        submitProblem();
                    }
                }
                break;
        }
    }

    private void changeUI() {
        mCurrentActivity.findViewById(R.id.ivBackBtn).setVisibility(View.GONE);
        editText_lay.setVisibility(View.GONE);
        text_lay.setVisibility(View.VISIBLE);
        submitBtn.setText(getString(R.string.back_to_booking_history));
        mCurrentActivity.isSubmitted = true;
    }

    private boolean isValid() {
        if (StringUtils.isBlank(etEmail.getText().toString().trim())) {
            setError(etEmail, "Please Enter Email");
            return false;
        }
        if (StringUtils.isNotBlank(etEmail.getText().toString().trim()) && !Utils.isValidEmail(etEmail.getText().toString().trim())) {
            setError(etEmail, "Email address is not valid");
            return false;
        }
        if (StringUtils.isBlank(etDetails.getText().toString().trim())) {
            setError(etDetails, "Please Enter Some Details");
            return false;
        }
        return true;
    }

    private void setError(FontEditText editText, String message) {
        editText.setError(message);
        editText.requestFocus();
    }

    private void submitProblem() {
        Dialogs.INSTANCE.showLoader(mCurrentActivity);
        new UserRepository().postProblem(mCurrentActivity,
                mCallBack,
                mCurrentActivity.selectedReason,
                mCurrentActivity.tripId,
                etEmail.getText().toString(),
                "",
                etDetails.getText().toString(),
                false);
        }

    private IUserDataHandler mCallBack = new UserDataHandler(){

        @Override
        public void onProblemPosted(final ProblemPostResponse response) {
            mCurrentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(response.isSuccess()){
                        changeUI();
                    }
                    Utils.appToastDebug(mCurrentActivity,response.getMessage());
                    Dialogs.INSTANCE.dismissDialog();
                }
            });
        }
    };

}
