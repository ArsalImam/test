package com.bykea.pk.partner.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.response.ProblemPostResponse;
import com.bykea.pk.partner.repositories.IUserDataHandler;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontEditText;
import com.bykea.pk.partner.widgets.FontTextView;
import com.bykea.pk.partner.widgets.FontUtils;

import org.apache.commons.lang3.StringUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReportPostActivity extends BaseActivity {

    private ReportPostActivity mCurrentActivity;

    @Bind(R.id.submitBtn)
    FontTextView submitBtn;

    @Bind(R.id.etEmail)
    FontEditText etEmail;

    @Bind(R.id.etDetails)
    FontEditText etDetails;

    @Bind(R.id.text_lay)
    LinearLayout text_lay;

    @Bind(R.id.editText_lay)
    LinearLayout editText_lay;

    private String reason;
    private String contactType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_post);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        mCurrentActivity = this;
        ButterKnife.bind(mCurrentActivity);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setGreenActionBarTitle("Report", "رپورٹ");
        if (getIntent() != null) {
            reason = getIntent().getStringExtra("reason");
            contactType = getIntent().getStringExtra(Constants.Extras.CONTACT_TYPE);
        }
        if (StringUtils.isNotBlank(AppPreferences.getPilotData().getEmail())) {
            etEmail.setText(AppPreferences.getPilotData().getEmail());
            etDetails.requestFocus();
        } else {
            etEmail.requestFocus();
        }
    }

    @OnClick({R.id.submitBtn})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.submitBtn:
                if (submitBtn.getText().toString().equalsIgnoreCase(getString(R.string.back_to_booking_history))) {
                    mCurrentActivity.finish();
                } else {
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
        submitBtn.setText("BACK TO REPORTS");
        submitBtn.setTypeface(FontUtils.getFonts(mCurrentActivity, "roboto_bold.ttf"));

//        mCurrentActivity.isSubmitted = true;
    }

    private boolean isValid() {
        if (StringUtils.isBlank(etEmail.getText().toString().trim())) {
            setError(etEmail, "Please enter your email");
            return false;
        }
        if (StringUtils.isNotBlank(etEmail.getText().toString().trim()) && !Utils.isValidEmail(etEmail.getText().toString().trim())) {
            setError(etEmail, "Email address is not valid");
            return false;
        }
        if (StringUtils.isBlank(etDetails.getText().toString().trim())) {
            setError(etDetails, "Please enter details");
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
                mCurrentActivity.reason,
                "",
                etEmail.getText().toString(),
                contactType,
                etDetails.getText().toString(),
                true);
    }

    private IUserDataHandler mCallBack = new UserDataHandler() {

        @Override
        public void onProblemPosted(final ProblemPostResponse response) {
            mCurrentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (response.isSuccess()) {
                        changeUI();
                    }
                    Utils.appToastDebug(mCurrentActivity, response.getMessage());
                    Dialogs.INSTANCE.dismissDialog();
                }
            });
        }
    };

}
