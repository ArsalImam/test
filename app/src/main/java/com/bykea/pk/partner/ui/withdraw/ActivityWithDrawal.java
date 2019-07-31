package com.bykea.pk.partner.ui.withdraw;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.ui.activities.BaseActivity;


/**
 * This class will responsible to manage the complete withdrawal process
 *
 * @author Arsal Imam
 */
public class ActivityWithDrawal extends BaseActivity {

    /**
     * This method is used to open withdrawal activity by using intent API mentioned by android docs.
     * For more info on intents, refers the below URL,
     *
     * @param activity context to open withdrawal activity
     * @see <a href="https://developer.android.com/reference/android/content/Intent">Intents</a>
     */
    public static void openActivity(Activity activity) {
        Intent i = new Intent(activity, ActivityWithDrawal.class);
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
        setContentView(R.layout.activity_with_drawal);
    }
}