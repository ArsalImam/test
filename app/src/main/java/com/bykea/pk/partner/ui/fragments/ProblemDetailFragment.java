package com.bykea.pk.partner.ui.fragments;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import com.zendesk.service.ErrorResponse;
import com.zendesk.service.ZendeskCallback;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import zendesk.support.CreateRequest;
import zendesk.support.CustomField;
import zendesk.support.Request;
import zendesk.support.RequestProvider;
import zendesk.support.Support;

import static com.bykea.pk.partner.ui.activities.ProblemActivity.DETAIL_SUBMITTED_FRAGMENT;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProblemDetailFragment extends Fragment {
    @BindView(R.id.submitBtn)
    FontButton submitBtn;

    @BindView(R.id.etEmail)
    FontEditText etEmail;

    @BindView(R.id.etDetails)
    FontEditText etDetails;

    @BindView(R.id.text_lay)
    LinearLayout text_lay;

    @BindView(R.id.editText_lay)
    LinearLayout editText_lay;

    private ProblemActivity mCurrentActivity;
    private RequestProvider requestProvider;

    public ProblemDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_problem_detail, container, false);
        ButterKnife.bind(this, rootView);
        setRetainInstance(true);
        mCurrentActivity = (ProblemActivity) getActivity();

        requestProvider = Support.INSTANCE.provider().requestProvider();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (StringUtils.isNotBlank(AppPreferences.getPilotData().getEmail())) {
            etEmail.setText(AppPreferences.getPilotData().getEmail());
            etDetails.requestFocus();
        } else {
            etEmail.requestFocus();
        }
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


    private IUserDataHandler mCallBack = new UserDataHandler() {

        @Override
        public void onProblemPosted(final ProblemPostResponse response) {
            mCurrentActivity.runOnUiThread(() -> {
                if (response.isSuccess()) {
//                        changeUI();
                }
                Utils.appToastDebug(mCurrentActivity, response.getMessage());
                Dialogs.INSTANCE.dismissDialog();
            });
        }
    };

    private void createRequest() {
        Dialogs.INSTANCE.showLoader(mCurrentActivity);


        requestProvider.createRequest(buildCreateRequest(), new ZendeskCallback<Request>() {
            @Override
            public void onSuccess(Request request) {
                Dialogs.INSTANCE.dismissDialog();
                Utils.appToastDebug(mCurrentActivity, "Zendesk(createRequest) - onSuccess");
//                mCurrentActivity.loadFragment(new ProblemSubmittedFragment(), DETAIL_SUBMITTED_FRAGMENT);
            }

            @Override
            public void onError(ErrorResponse errorResponse) {
                Dialogs.INSTANCE.dismissDialog();
                Utils.appToastDebug(mCurrentActivity, "Zendesk(createRequest) - onError");
            }
        });

        /*new UserRepository().postProblem(mCurrentActivity,
                mCallBack,
                mCurrentActivity.selectedReason,
                mCurrentActivity.tripId,
                etEmail.getText().toString(),
                "",
                etDetails.getText().toString(),
                false);*/
    }

    private void getAllRequests() {
        requestProvider.getAllRequests(new ZendeskCallback<List<Request>>() {
            @Override
            public void onSuccess(List<Request> requests) {
                Utils.appToastDebug(mCurrentActivity, "Zendesk(createRequest) - onSuccess");
            }

            @Override
            public void onError(ErrorResponse errorResponse) {
                Utils.appToastDebug(mCurrentActivity, "Zendesk(createRequest) - onError");
            }
        });
    }

    private CreateRequest buildCreateRequest() {
        CreateRequest createRequest = new CreateRequest();
        createRequest.setSubject("Ticket Subject");
        createRequest.setDescription("Ticket Description");
        createRequest.setCustomFields(buildCustomFields());

        return createRequest;
    }

    private List<CustomField> buildCustomFields() {
        List<CustomField> customFields = new ArrayList<>();
        return customFields;
    }

    @OnClick({R.id.submitBtn})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.submitBtn:
                if (isValid()) {
                    getAllRequests();
                }
                break;
        }
    }
}
