package com.bykea.pk.partner.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.appcompat.app.AppCompatDelegate;
import android.view.View;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.utils.Constants;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScannFingerPrintsActivity extends BaseActivity {
    private String CNIC;
    private ScannFingerPrintsActivity mCurrentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_finger_prints);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        ButterKnife.bind(this);
        mCurrentActivity = this;
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        //initNadraActivity();
    }

    private void initNadraActivity() {

        //initializeNadraActivity(this, CNIC, selectedFinger);
    }



    private void returnResults(boolean success) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(Constants.Extras.IS_FINGER_PRINTS_SUCCESS, success);
        mCurrentActivity.setResult(Activity.RESULT_OK, returnIntent);
        mCurrentActivity.finish();
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
