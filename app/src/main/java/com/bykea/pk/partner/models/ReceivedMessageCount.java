package com.bykea.pk.partner.models;

import com.google.gson.annotations.SerializedName;

public class ReceivedMessageCount {
    private String conversationId;
    private int conversationMessageCount;

    public ReceivedMessageCount(String conversationId, int conversationMessageCount) {
        this.conversationId = conversationId;
        this.conversationMessageCount = conversationMessageCount;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public int getConversationMessageCount() {
        return conversationMessageCount;
    }

    public void setConversationMessageCount(int conversationMessageCount) {
        this.conversationMessageCount = conversationMessageCount;
    }
}
