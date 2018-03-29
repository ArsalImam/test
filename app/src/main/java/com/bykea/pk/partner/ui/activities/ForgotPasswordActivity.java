package com.bykea.pk.partner.ui.activities;


import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

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

public class ForgotPasswordActivity extends BaseActivity {


    @Bind(R.id.backBtn)
    ImageView backBtn;
    @Bind(R.id.phoneNumberEt)
    FontEditText phoneNumberEt;
    @Bind(R.id.sendBtn)
    Button sendBtn;


    @Bind(R.id.mainScrollView)
    ScrollView mainScrollView;
    private UserRepository repository;
    private ForgotPasswordActivity mCurrentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        mCurrentActivity = this;
        ButterKnife.bind(this);
        repository = new UserRepository();
        phoneNumberEt.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        if (StringUtils.isNotBlank(AppPreferences.getPhoneNumber())) {
            phoneNumberEt.setText(Utils.phoneNumberToShow(AppPreferences.getPhoneNumber()));
        }

        phoneNumberEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    Utils.hideSoftKeyboard(mCurrentActivity, phoneNumberEt);
                    return true;
                }
                return false;
            }
        });
        Utils.scrollToBottom(mainScrollView);
    }


    @OnClick({R.id.backBtn, R.id.sendBtn, R.id.phoneNumberEt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                mCurrentActivity.onBackPressed();
                break;
            case R.id.sendBtn:
                if (Connectivity.isConnectedFast(mCurrentActivity)) {
                    if (Utils.isValidNumber(mCurrentActivity, phoneNumberEt)) {
                        Dialogs.INSTANCE.showLoader(mCurrentActivity);
                        repository.requestForgotPassword(mCurrentActivity, handler,
                                Utils.phoneNumberForServer(phoneNumberEt.getText().toString()));
                    }
                } else {
                    Dialogs.INSTANCE.showToast(mCurrentActivity, "Please check your internet connection.");
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
            if (mCurrentActivity != null) {
                Dialogs.INSTANCE.dismissDialog();
                if (commonResponse.isSuccess()) {
                    Dialogs.INSTANCE.showSuccessDialogForgotPassword(mCurrentActivity, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Dialogs.INSTANCE.dismissDialog();
                            mCurrentActivity.finish();
                        }
                    });
                } else {
                    Dialogs.INSTANCE.showError(mCurrentActivity, sendBtn, commonResponse.getMessage());
                }
            }
        }

        @Override
        public void onError(int errorCode, String errorMessage) {
            Dialogs.INSTANCE.dismissDialog();
            Dialogs.INSTANCE.showError(mCurrentActivity, sendBtn, errorMessage);
        }
    };
}
