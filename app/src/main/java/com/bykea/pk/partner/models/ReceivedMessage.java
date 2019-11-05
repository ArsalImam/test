package com.bykea.pk.partner.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ReceivedMessage implements Serializable {

    ReceivedMessage data;

    public ReceivedMessage getData() {
        return data;
    }

    public void setData(ReceivedMessage data) {
        this.data = data;
    }

    @SerializedName("trip_id")
    private String tripId;
    @SerializedName("trip_no")
    private String tripNo;

    @SerializedName("full_name")
    private String fullName;


    @SerializedName("passenger_id")
    private String sender;
    @SerializedName("driver_id")
    private String receiver;

    @SerializedName("_id")
    private String messageId;
    @SerializedName("message")
    private String message;

    @SerializedName("batch_id")
    private String batchID;

    @SerializedName("message_type")
    private String messageType;
    @SerializedName("created_at")
    private String time;
    private String status;
    @SerializedName("conversation_id")
    private String conversationId;

    private String textInfo;
    private boolean isSent;

    @SerializedName("sender")
    private String senderId;

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String sentByDriver) {
        this.senderId = sentByDriver;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    public String getTextInfo() {
        return textInfo;
    }

    public void setTextInfo(String textInfo) {
        this.textInfo = textInfo;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getBatchID() {
        return batchID;
    }

    public void setBatchID(String batchID) {
        this.batchID = batchID;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }
}