package com.bykea.pk.partner.models.data;

import com.google.gson.annotations.SerializedName;

public class ServiceTypeData {
    @SerializedName("_id")
    private String id;
    private String name;
    private String icon;
    @SerializedName("is_deleted")
    private boolean deleted;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
