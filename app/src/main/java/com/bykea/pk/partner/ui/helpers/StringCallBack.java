package com.bykea.pk.partner.ui.helpers;

/**
 * TWO CALLBACKS
 * SET TO DEFAULT AS INTERFACE DO NOT ENFORCE TO IMPLEMENT ALL THE METHODS
 * ONLY IMPLEMENT THE REQUIRED ONE
 */

public interface StringCallBack {
    /**
     * Use If Need One String Callback
     * @param msg : Callback return for the first string
     */
    default void onCallBack(String msg){}

    /**
     * Use If Need Two Strings Callback
     * @param msg : Callback return for the first string
     * @param msg1 : Callback return for the second string
     */
    default void onCallBack(String msg,String msg1){}
}