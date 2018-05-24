package com.bykea.pk.partner.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.SignUpCity;
import com.bykea.pk.partner.models.data.SignUpUserData;
import com.bykea.pk.partner.models.response.BiometricApiResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.adapters.DocumentsGridAdapter;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class JsBankFingerSelectionActivity extends BaseActivity {
    private String DRIVER_ID;
    private String CNIC;
    private String BASE_IMG_URL;
    private SignUpCity mSelectedCity;
    private String VIDEO_ID;
    private SignUpUserData signUpData;//todo save insta
    private JsBankFingerSelectionActivity mCurrentActivity;
    private String financeNumber;

    @BindView(R.id.ivFingerSelection)
    ImageView ivFingerSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_js_bank_finger_selection);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        ButterKnife.bind(this);
        mCurrentActivity = this;
        setTitleCustomToolbarWithUrduHideBackBtn("Bank Account", "بینک اکاؤنٹ");
        if (getIntent() != null && getIntent().getExtras() != null) {
            mSelectedCity = getIntent().getExtras().getParcelable(Constants.Extras.SELECTED_ITEM);
            signUpData = getIntent().getExtras().getParcelable(Constants.Extras.SIGN_UP_DATA);
            DRIVER_ID = getIntent().getExtras().getString(Constants.Extras.DRIVER_ID);
            CNIC = getIntent().getExtras().getString(Constants.Extras.CNIC);
        }
    }

    @OnClick({R.id.rightThumb, R.id.rightIndexFinger, R.id.rightMiddleFinger, R.id.rightRingFinger, R.id.rightLittleFinger, R.id.leftThumb, R.id.leftIndexFinger, R.id.leftMiddleFinger, R.id.leftRingFinger, R.id.leftLittleFinger,})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rightThumb:
                callScanActivity(1);
                break;
            case R.id.rightIndexFinger:
                callScanActivity(2);
                break;
            case R.id.rightMiddleFinger:
                callScanActivity(3);
                break;
            case R.id.rightRingFinger:
                callScanActivity(4);
                break;
            case R.id.rightLittleFinger:
                callScanActivity(5);
                break;
            case R.id.leftThumb:
                callScanActivity(6);
                break;
            case R.id.leftIndexFinger:
                callScanActivity(7);
                break;
            case R.id.leftMiddleFinger:
                callScanActivity(8);
                break;
            case R.id.leftRingFinger:
                callScanActivity(9);
                break;
            case R.id.leftLittleFinger:
                callScanActivity(10);
                break;
        }
    }

    private void callScanActivity(int index) {
        Intent intent = new Intent(mCurrentActivity, ScannFingerPrintsActivity.class);
        intent.putExtra(Constants.Extras.CNIC, CNIC);
        intent.putExtra(Constants.Extras.SELECTED_INDEX, index);
        startActivityForResult(intent, Constants.RequestCode.SCAN_FINGER_PRINTS);
    }

    private boolean isVerified;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RequestCode.SCAN_FINGER_PRINTS && data != null) {
            if (resultCode == RESULT_OK) {
                Dialogs.INSTANCE.showLoader(mCurrentActivity);
                isVerified = data.getBooleanExtra(Constants.Extras.IS_FINGER_PRINTS_SUCCESS, false);
                new UserRepository().postBiometricVerification(mCurrentActivity, DRIVER_ID, isVerified, mCallback);
            }
        }
    }

    private void showVerificationDialog() {
        Dialogs.INSTANCE.showVerificationDialog(mCurrentActivity, isVerified, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentActivity.finish();
                ActivityStackManager.getInstance().startLoginActivity(mCurrentActivity);
                if (DocumentsGridAdapter.getmInstanceForNullCheck() != null) {
                    DocumentsGridAdapter.getInstance().resetTheInstance();
                }
            }
        });
    }


    private UserDataHandler mCallback = new UserDataHandler() {

        @Override
        public void onBiometricApiResponse(BiometricApiResponse response) {
            if (mCurrentActivity != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.INSTANCE.dismissDialog();
                        showVerificationDialog();
                    }
                });
            }
        }

        @Override
        public void onError(int errorCode, String errorMessage) {
            if (mCurrentActivity != null) {
                Dialogs.INSTANCE.dismissDialog();
                Utils.appToast(mCurrentActivity, errorMessage);
            }
        }
    };
}