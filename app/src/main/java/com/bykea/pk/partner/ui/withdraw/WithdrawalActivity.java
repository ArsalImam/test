package com.bykea.pk.partner.ui.withdraw;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.databinding.ActivityWithDrawalBinding;
import com.bykea.pk.partner.ui.activities.BaseActivity;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.loadboard.common.AppCompatActivityExtKt;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;


/**
 * This class will responsible to manage the complete withdrawal process
 *
 * @author Arsal Imam
 */
public class WithdrawalActivity extends BaseActivity {

    /**
     * Request code from which this activity is opening,
     * This is used to share data back to the last activity available in stack
     */
    public static final int REQ_CODE_WITH_DRAW = 12;

    /**
     * Binding object between activity and xml file, it contains all objects
     * of UI components used by activity
     */
    private ActivityWithDrawalBinding binding;

    /**
     * ViewModel object of {WithdrawalActivity} View
     */
    private WithdrawalViewModel viewModel;

    /**
     * Datasource object to populate payment methods in recyclerview
     */
    private WithdrawalPaymentMethodsAdapter adapter;

    /**
     * Confirmation dialog object
     */
    private DialogWithdrawConfirmation confirmationDialog;

    /**
     * This method is used to open withdrawal activity by using intent API mentioned by android docs.
     * For more info on intents, refers the below URL,
     *
     * @param activity context to open withdrawal activity
     * @see <a href="https://developer.android.com/reference/android/content/Intent">Intents</a>
     */
    public static void openActivity(Activity activity) {
        Intent i = new Intent(activity, WithdrawalActivity.class);
        activity.startActivityForResult(i, REQ_CODE_WITH_DRAW);
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
        binding.setViewmodel(viewModel);

        confirmationDialog = DialogWithdrawConfirmation.newInstance(this, viewModel);

        initUi();
        setupObservers();
        setupRecyclerView();
    }

    /**
     * This method is binding view model properties with view components
     * through LiveData API available in Android MVVM
     * @see <a href="https://developer.android.com/topic/libraries/architecture/livedata">LiveData</a>
     */
    private void setupObservers() {

        viewModel.getShowLoader().observe(this, it -> {
            if (it) Dialogs.INSTANCE.showLoader(WithdrawalActivity.this);
            else Dialogs.INSTANCE.dismissDialog();
        });

        viewModel.getDriverProfile().observe(this, it -> {
            binding.balanceTextview.setText(String.format("Rs. %s", Math.round(it.getWallet())));
            adapter.notifyDataSetChanged();
        });

        viewModel.getAvailablePaymentMethods().observe(this, it -> {
            adapter.notifyMethodsChanged(it);
        });

        viewModel.getErrorMessage().observe(this, it -> {
            boolean hasError = it == null;
            binding.withdrawErrorLayout.setVisibility(hasError ? View.GONE : View.VISIBLE);
            binding.withdrawError.setText(hasError ? "" : it);
        });

        viewModel.getShowConfirmationDialog().observe(this, it -> {
            if (it) {
                confirmationDialog.show();
                WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();
                lWindowParams.copyFrom(confirmationDialog.getWindow().getAttributes());

                lWindowParams.width = WindowManager.LayoutParams.FILL_PARENT;
                lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                confirmationDialog.getWindow().setAttributes(lWindowParams);
            } else confirmationDialog.dismiss();
        });

        viewModel.getIsWithdrawCompleted().observe(this, it -> {
            if (it) {
                ActivityStackManager.getInstance().startWithDrawCompleteActivity(WithdrawalActivity.this);
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    /**
     * <b>initUi</b> is responsible to apply and
     * initialize events (related with View) to UI components
     */
    private void initUi() {
        binding.balanceEdittext.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                Utils.hideKeyboard(WithdrawalActivity.this);
            }
            return false;
        });
    }

    /**
     * Setting up payment's recyclerview
     */
    private void setupRecyclerView() {
        adapter = new WithdrawalPaymentMethodsAdapter(new ArrayList<>(0),
                viewModel);
        binding.paymentsRecyclerView.setAdapter(adapter);
    }

    /**
     * Removing activity from stack, this method is calling from view's onclick event
     * @param v back button
     */
    public void finishActivity(View v) {
        finish();
    }
}