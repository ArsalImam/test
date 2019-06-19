package com.bykea.pk.partner.ui.helpers;

/**
 * TWO CALLBACKS
 * SET TO DEFAULT AS INTERFACE DO NOT ENFORCE TO IMPLEMENT ALL THE METHODS
 * ONLY IMPLEMENT THE REQUIRED ONE
 */

public interface StringCallBack {
    default void onCallBack(String msg){}
    default void onCallBack(String msg,String msg1){}
}