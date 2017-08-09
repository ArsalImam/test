package com.bykea.pk.partner.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ChatMessage implements Serializable {

    @SerializedName("passenger_id")
    private Sender sender;
    @SerializedName("driver_id")
    private Receiver receiver;

    @SerializedName("_id")
    private String messageId;
    @SerializedName("message")
    private String message;
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

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public void setReceiver(Receiver receiver) {
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




}
