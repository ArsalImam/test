package com.bykea.pk.partner.ui.withdraw.detail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.databinding.ActivityWithDrawalBinding;
import com.bykea.pk.partner.ui.activities.BaseActivity;
import com.bykea.pk.partner.ui.loadboard.common.AppCompatActivityExtKt;
import com.bykea.pk.partner.utils.Dialogs;

import java.util.ArrayList;


/**
 * This class will responsible to manage the complete withdrawal process
 *
 * @author Arsal Imam
 */
public class WithdrawalActivity extends BaseActivity {

    private ActivityWithDrawalBinding binding;
    private WithdrawalViewModel viewModel;
    private WithdrawalPaymentMethodsAdapter adapter;

    /**
     * This method is used to open withdrawal activity by using intent API mentioned by android docs.
     * For more info on intents, refers the below URL,
     *
     * @param activity context to open withdrawal activity
     * @see <a href="https://developer.android.com/reference/android/content/Intent">Intents</a>
     */
    public static void openActivity(Activity activity) {
        Intent i = new Intent(activity, WithdrawalActivity.class);
        activity.startActivity(i);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This will calls on every new initialization of this activity,
     * It can be used for any initializations or on start executions
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_with_drawal);

        viewModel = AppCompatActivityExtKt.obtainViewModel(this, WithdrawalViewModel.class);
        viewModel.setApplicationContext(getApplicationContext());
        binding.setViewmodel(viewModel);

        viewModel._showLoader.observe(this, it -> {
            if (it) Dialogs.INSTANCE.showLoader(WithdrawalActivity.this);
            else Dialogs.INSTANCE.dismissDialog();
        });
        viewModel.getAvailablePaymentMethods().observe(this, it -> {
            adapter.notifyMethodsChanged(it);
        });
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        adapter = new WithdrawalPaymentMethodsAdapter(new ArrayList<>(0),
                viewModel);
        binding.paymentsRecyclerView.setAdapter(adapter);
    }

    public void finishActivity(View v) {
        finish();
    }
}