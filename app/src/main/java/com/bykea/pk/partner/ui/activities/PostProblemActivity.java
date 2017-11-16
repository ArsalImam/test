package com.bykea.pk.partner.ui.activities;

import android.os.Bundle;
import android.view.View;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontButton;
import com.bykea.pk.partner.widgets.FontEditText;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PostProblemActivity extends BaseActivity {

    private PostProblemActivity mCurrentActivity;
    private String tripId, reason;

    @Bind(R.id.submitBtn)
    FontButton submitBtn;

    @Bind(R.id.etEmail)
    FontEditText etEmail;

    @Bind(R.id.etDetails)
    FontEditText etDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_problem);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        mCurrentActivity = this;
        ButterKnife.bind(mCurrentActivity);

        tripId = getIntent().getStringExtra("TRIP_ID");
        reason = getIntent().getStringExtra("REASON");
        setBackNavigation();
        setToolbarTitle(tripId);
        hideToolbarLogo();
    }

    @OnClick({R.id.submitBtn})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.submitBtn:
                if (isValid()) {
                    submitProblem();
                }
                break;
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

    private void submitProblem() {
        //TODO : Post Problem Reason,Trip Id,email,details to server
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("reason", reason);
            jsonObject.put("tip_id", tripId);
            jsonObject.put("email", etEmail.getText().toString());
            jsonObject.put("details", etDetails.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
