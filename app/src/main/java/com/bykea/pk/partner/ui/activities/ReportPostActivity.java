package com.bykea.pk.partner.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontEditText;
import com.bykea.pk.partner.widgets.FontTextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_post);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        mCurrentActivity = this;
        ButterKnife.bind(mCurrentActivity);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setGreenActionbarTitle("Report","رپورٹ");
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
//        mCurrentActivity.isSubmitted = true;
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
        changeUI();
//        Dialogs.INSTANCE.showLoader(mCurrentActivity);
//        new UserRepository().postProblem(mCurrentActivity, mCallBack,mCurrentActivity.selectedReason,
//                mCurrentActivity.tripId,etEmail.getText().toString(),etDetails.getText().toString());
    }


}
