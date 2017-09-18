package com.bykea.pk.partner.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ChatMessage implements Serializable {

    @SerializedName("passenger_id")
    private Sender sender;

    @SerializedName("message")
    private String message;
    @SerializedName("message_type")
    private String messageType;
    @SerializedName("created_at")
    private String time;

    @SerializedName("sender")
    private String senderId;

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String sentByDriver) {
        this.senderId = sentByDriver;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }





}
