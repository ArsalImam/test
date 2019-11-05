package com.bykea.pk.partner.models.response;

import com.bykea.pk.partner.dal.source.remote.data.PersonalInfoData;

public class GetProfileResponse extends CommonResponse {

    private PersonalInfoData data;

    public PersonalInfoData getData() {
        return data;
    }

    public void setData(PersonalInfoData data) {
        this.data = data;
    }
}