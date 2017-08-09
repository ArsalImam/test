package com.bykea.pk.partner.models.response;


import com.bykea.pk.partner.models.data.PilotData;

public class RegisterResponse extends CommonResponse {


    private PilotData data;

    public PilotData getUser() {
        return data;
    }

}
