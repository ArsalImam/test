package com.bykea.pk.partner.ui.activities;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import com.bykea.pk.partner.ui.fragments.LoginFragment;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.PilotData;


public class LoginActivity extends BaseActivity {

    public static boolean isRegisterFragment = false;
    private PilotData pilotData;
    private LoginActivity mCurrentActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        mCurrentActivity = this;
        pilotData = new PilotData();
        LoginFragment mainFragment = new LoginFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in,
                R.anim.fade_out);
        fragmentTransaction.replace(R.id.containerView, mainFragment, null);
        fragmentTransaction.commit();
        ActivityStackManager.getInstance().restartLocationService(mCurrentActivity);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (isRegisterFragment) {
            popLastTwoFragment();
        } else {
            super.onBackPressed();
        }
    }


    private void popLastTwoFragment() {
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in,
                R.anim.fade_out);
        getSupportFragmentManager().popBackStack("numberVerification", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().popBackStack("codeVerification", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public PilotData getPilotData() {
        return pilotData;
    }

}
