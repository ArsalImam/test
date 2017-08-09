package com.bykea.pk.partner.models.response;

import com.google.gson.annotations.SerializedName;

public class GetConversationIdResponse extends CommonResponse {

    @SerializedName("conversation_id")
    private String conversationId;

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
}
