package com.bykea.pk.partner.utils;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bykea.pk.partner.R;


public class Dialogs_new extends Dialog {

    private LinearLayout dialogLayout;
    private Dialog dialog;
    private Context mContext;


    public Dialogs_new(Context context) {
        super(context, R.style.actionSheetTheme);
        this.mContext = context;
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater li = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = null;
        view = li.inflate(R.layout.profile_action_sheet_new, null);
//        height=Utils.dpToPx(mContext,500);
        setContentView(view);
        dialogLayout = (LinearLayout) findViewById(R.id.llDialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = getWindow();
        lp.copyFrom(window.getAttributes());
//This makes the dialog take up the full width
        DisplayMetrics dm = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        lp.width = dm.widthPixels;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        getWindow().getAttributes().windowAnimations = R.style.DialogSlideAnim;
        getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
//        getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        getWindow().getAttributes().windowAnimations = R.style.DialogSlideAnim;
//        WindowManager.LayoutParam

    }

    public void showProgressDialog() {
        dialogDismiss();
        dialog = new ProgressDialog(mContext);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    public void dialogDismiss() {

        if (dialog != null) {
            dialog.dismiss();
        }

    }

    public boolean isShowing() {

        return (dialog != null) && dialog.isShowing();

    }

    public void makeToast(String text) {
        Utils.appToast(text);
    }

    public void showReportDialog(OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Flags");
        builder.setCancelable(false);
//		builder.setItems(Constants.items,listener);
        AlertDialog alert = builder.create();
        alert.show();
    }


    public void showDatePicker(Context context, OnDateSetListener dateListener,
                               int year, int monthOfYear, int dayOfMonth, OnCancelListener cancelListener) {
        dialogDismiss();
        dialog = new DatePickerDialog(context,
                dateListener,
                year, monthOfYear, dayOfMonth);


        dialog.setOnCancelListener(cancelListener);
        dialog.show();
    }

    public void showActionSheet(View.OnClickListener takeListener, View.OnClickListener fromFileListener,
                                View.OnClickListener cancelListener,
                                OnCancelListener dialogCancelListener) {

        TextView take_button = (TextView) findViewById(R.id.profile_take_picture);
        TextView fromFile = (TextView) findViewById(R.id.profile_from_file);
        TextView cancel = (TextView) findViewById(R.id.profile_cancel);
        take_button.setOnClickListener(takeListener);
        fromFile.setOnClickListener(fromFileListener);
        cancel.setOnClickListener(cancelListener);
        setOnCancelListener(dialogCancelListener);
        show();
    }

    private void expand() {
        TranslateAnimation animate = new TranslateAnimation(0, 0, 500, 0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        dialogLayout.startAnimation(animate);
        dialogLayout.setVisibility(View.VISIBLE);
    }

    private void collapse() {
        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, dialogLayout.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dialogLayout.setVisibility(View.GONE);
                dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        dialogLayout.setAnimation(animate);
        dialogLayout.startAnimation(animate);
    }

    @Override
    public void show() {
        expand();
        super.show();
    }

    @Override
    public void onBackPressed() {
        collapse();
    }


}
