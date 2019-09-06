package com.bykea.pk.partner.models;

public class ReceivedMessageCount {
    private String tripId;
    private int conversationMessageCount;

    public ReceivedMessageCount(String tripId, int conversationMessageCount) {
        this.tripId = tripId;
        this.conversationMessageCount = conversationMessageCount;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public int getConversationMessageCount() {
        return conversationMessageCount;
    }

    public void setConversationMessageCount(int conversationMessageCount) {
        this.conversationMessageCount = conversationMessageCount;
    }
}
