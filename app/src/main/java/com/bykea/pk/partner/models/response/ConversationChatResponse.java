package com.bykea.pk.partner.models.response;


import com.bykea.pk.partner.models.ChatMessage;

import java.util.ArrayList;


public class ConversationChatResponse extends CommonResponse {

    private ArrayList<ChatMessage> data;

    public ArrayList<ChatMessage> getData() {
        return data;
    }

    public void setData(ArrayList<ChatMessage> data) {
        this.data = data;
    }
}
