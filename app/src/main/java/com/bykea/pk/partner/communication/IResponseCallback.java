package com.bykea.pk.partner.communication;

public interface IResponseCallback {
    void onResponse(Object object);
    void onSuccess();
    void onError(int errorCode, String error);
}
