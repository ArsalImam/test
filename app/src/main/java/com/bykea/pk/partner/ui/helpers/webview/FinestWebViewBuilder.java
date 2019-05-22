package com.bykea.pk.partner.ui.helpers.webview;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;

import com.thefinestartist.finestwebview.FinestWebView;
import com.thefinestartist.finestwebview.listeners.BroadCastManager;

public class FinestWebViewBuilder extends FinestWebView {

    public static class Builder extends FinestWebView.Builder {

        public Builder(@NonNull Activity activity) {
            super(activity);
        }

        @Override
        protected void show(String url, String data) {
            this.url = url;
            this.data = data;
            this.key = System.identityHashCode(this);

            if (!listeners.isEmpty()) new BroadCastManager(context, key, listeners);

            Intent intent = new Intent(context, FinestWebViewCustomActivity.class);
            intent.putExtra("builder", this);

            context.startActivity(intent);

            if (context instanceof Activity)
                ((Activity) context).overridePendingTransition(animationOpenEnter, animationOpenExit);
        }
    }
}
