package com.bykea.pk.partner.ui.helpers.webview;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.LinearLayout;

import com.bykea.pk.partner.R;
import com.thefinestartist.finestwebview.FinestWebViewActivity;

import org.apache.commons.lang3.StringUtils;

public class FinestWebViewCustomActivity extends FinestWebViewActivity {

    private FinestWebViewCustomActivity mCurrentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentActivity = this;
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        close.setImageDrawable(ContextCompat.getDrawable(mCurrentActivity, R.drawable.ic_arrow_back_48px));
    }

    @Override
    public void onBackPressed() {
        if (StringUtils.isNotBlank(webView.getUrl()) && webView.getUrl().contains("thank-you.php")) {
            close();
        } else {
            super.onBackPressed();
        }
    }
}
