package com.bykea.pk.partner.ui.helpers.webview;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;

import com.bykea.pk.partner.R;
import com.thefinestartist.finestwebview.FinestWebViewActivity;

public class FinestWebViewCustomActivity extends FinestWebViewActivity {

    private FinestWebViewCustomActivity mCurrentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentActivity = this;
        close.setImageDrawable(ContextCompat.getDrawable(mCurrentActivity, R.drawable.ic_arrow_back_48px));
    }

    @Override
    public void onBackPressed() {
        if (webView.getUrl().contains("thank-you.php")) {
            close();
        } else {
            super.onBackPressed();
        }
    }
}
