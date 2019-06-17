package com.bykea.pk.partner.ui.helpers;

public interface StringCallBack {
    default void onCallBack(String msg){}
    default void onCallBack(String msg,String msg1){}
}