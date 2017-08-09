package com.bykea.pk.partner.models.response;

public class CommonResponse {

    private boolean success;
    private String message;
    private int code;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }


}