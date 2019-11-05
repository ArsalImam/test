package com.bykea.pk.partner.communication;

public interface IResponseCallback {
    void onResponse(Object object);
    void onError(int errorCode, String error);
}
