package com.bykea.pk.partner.models.data;

import java.io.Serializable;

public class VehicleListData implements Serializable {

    private String icon;
    private String name;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
