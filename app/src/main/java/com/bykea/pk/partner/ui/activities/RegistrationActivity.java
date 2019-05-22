package com.bykea.pk.partner.ui.activities;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.ui.fragments.NumberRegistration;

public class RegistrationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_places_area);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        addFragment(new NumberRegistration());
    }

    public void addFragment(Fragment fragment) {
        fragment.setRetainInstance(true);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment, fragment.getClass().getName()).commit();
    }

    public void replaceFragment(Fragment fragment) {
        fragment.setRetainInstance(true);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment, fragment.getClass().getName())
                .addToBackStack(fragment.getClass().getName()).commitAllowingStateLoss();
    }
}
