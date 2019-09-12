package com.bykea.pk.partner.dal.util;

public class HTTPStatus {

    public static final int OK = 200;
    public static final int CREAOTED = 201;
    public static final int BAD_REQUEST = 400;
    public static final int NOT_FOUND = 404;
    public static final int UNAUTHORIZED = 401;
    public static final int CONFLICT = 409;
    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int FENCE_ERROR = 800;
    public static final int INACTIVE_DUE_TO_WALLET_AMOUNT = 801;
    public static final int FENCE_SUCCESS = 808; //for local check
    public static final int CANCELLATION_RIDE_BLOCK = 900;
}