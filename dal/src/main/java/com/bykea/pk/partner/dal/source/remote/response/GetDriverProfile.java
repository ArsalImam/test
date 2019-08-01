package com.bykea.pk.partner.dal.source.remote.response;

import com.bykea.pk.partner.dal.source.remote.data.PersonalInfoData;

public class GetDriverProfile extends BaseResponse {

    private PersonalInfoData data;

    public PersonalInfoData getData() {
        return data;
    }

    public void setData(PersonalInfoData data) {
        this.data = data;
    }
}
