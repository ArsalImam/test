package com.bykea.pk.partner.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Utils;
import com.google.gson.Gson;
import com.paysyslabs.instascan.Fingers;
import com.paysyslabs.instascan.NadraActivity;
import com.paysyslabs.instascan.NadraScanListener;
import com.paysyslabs.instascan.model.PersonData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScannFingerPrintsActivity extends NadraActivity implements NadraScanListener {
    private Fingers selectedFinger = Fingers.RIGHT_INDEX;
    private String CNIC;
    private ScannFingerPrintsActivity mCurrentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_finger_prints);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        ButterKnife.bind(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mCurrentActivity = this;
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        initNadraActivity();
    }

    private void initNadraActivity() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            CNIC = getIntent().getExtras().getString(Constants.Extras.CNIC);
            int index = getIntent().getExtras().getInt(Constants.Extras.SELECTED_INDEX, 0);
            switch (index) {
                case 1:
                    selectedFinger = Fingers.RIGHT_THUMB;
                    break;
                case 2:
                    selectedFinger = Fingers.RIGHT_INDEX;
                    break;
                case 3:
                    selectedFinger = Fingers.RIGHT_MIDDLE;
                    break;
                case 4:
                    selectedFinger = Fingers.RIGHT_RING;
                    break;
                case 5:
                    selectedFinger = Fingers.RIGHT_LITTLE;
                    break;
                case 6:
                    selectedFinger = Fingers.LEFT_THUMB;
                    break;
                case 7:
                    selectedFinger = Fingers.LEFT_INDEX;
                    break;
                case 8:
                    selectedFinger = Fingers.LEFT_MIDDLE;
                    break;
                case 9:
                    selectedFinger = Fingers.LEFT_RING;
                    break;
                case 10:
                    selectedFinger = Fingers.LEFT_LITTLE;
                    break;
                default:
                    selectedFinger = Fingers.RIGHT_INDEX;
                    break;
            }

        }
        initializeNadraActivity(this, CNIC, selectedFinger);
    }

    @Override
    public String getCustomProxyURL() {
        return "https://sandbox.jsbl.com/userauth/bvs/v0/verify";
    }

    @Override
    public String getCustomCookie() {
        return "JSESSIONID=somesessionid";
    }

    @Override
    public boolean useCustomProxy() {
        return true;
    }


    @Override
    public int getScanFragmentContainer() {
        return R.id.frame_test;
    }

    @Override
    public void onRequestStarted() {
        Dialogs.INSTANCE.showLoader(mCurrentActivity);
        Utils.redLog("Success", "Image Success");
    }


    @Override
    public void onSuccessfulScan(PersonData personData) {
        Dialogs.INSTANCE.dismissDialog();
        Utils.redLog("PersonData", new Gson().toJson(personData));
        Utils.appToast(mCurrentActivity, "Fingerprint Verified Successfully");
        returnResults(true);
    }

    private void returnResults(boolean success) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(Constants.Extras.IS_FINGER_PRINTS_SUCCESS, success);
        mCurrentActivity.setResult(Activity.RESULT_OK, returnIntent);
        mCurrentActivity.finish();
    }

    @Override
    public void onInvalidFingerIndex(String s, String s1, List<Fingers> list) {
        Utils.redLog("Error", "Invalid Index");
    }

    @Override
    public void onError(String s, String s1) {
        Dialogs.INSTANCE.dismissDialog();
        Utils.redLog(s, s1);
        Utils.appToast(mCurrentActivity, s1);
//        returnResults(false);
    }

    @Override
    public void onResponseReceived() {
    }


    @Override
    public Map<String, String> getCustomAuthenticationData() {
        HashMap<String, String> authorizationData = new HashMap<>();
        authorizationData.put("client_id", "5UXFmIGC60ieKlOAhtGXJz8Ar2qrl2UG");
        authorizationData.put("client_secret", "G8I3SShOI5hJErVG");
//        authorizationData.put("client_id", "Eunm90n9J0MmjU2xk98BOXbbjyvP5D04");
//        authorizationData.put("client_secret", "f1zFDfTfcqJWaQDw");
        return authorizationData;
    }

    @OnClick({R.id.nextBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nextBtn:
                if (checkClickTime()) return;
                returnResults(false);
                break;
        }
    }
    private long mLastClickTime;

    public boolean checkClickTime() {
        long currentTime = SystemClock.elapsedRealtime();
        if (mLastClickTime != 0 && (currentTime - mLastClickTime < 1000)) {
            return true;
        }
        mLastClickTime = currentTime;
        return false;
    }

}
