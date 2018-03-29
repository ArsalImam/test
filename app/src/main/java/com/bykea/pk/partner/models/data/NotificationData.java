package com.bykea.pk.partner.models.data;

import com.bykea.pk.partner.models.response.CommonResponse;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.StringUtils;

public class NotificationData {
    @SerializedName("available")
    private boolean isActive;

    private String message;
    private String title;
    private String imageLink;
    private String launchUrl;
    private String showActionButton;
    private String type;

    public boolean isActive() {
        return isActive;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return StringUtils.isNotBlank(title) ? title : "Notification";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageLink() {
        return StringUtils.isNotBlank(imageLink) ? imageLink : StringUtils.EMPTY;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getLaunchUrl() {
        return launchUrl;
    }

    public void setLaunchUrl(String launchUrl) {
        this.launchUrl = launchUrl;
    }

    public String getShowActionButton() {
        return showActionButton;
    }

    public void setShowActionButton(String showActionButton) {
        this.showActionButton = showActionButton;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
