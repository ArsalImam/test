package com.bykea.pk.partner.models.data;

import java.util.ArrayList;

public class Predefine_messages {

    private ArrayList<String> cancel;

    private String[] reasons;

    public ArrayList<String> getCancel() {
        return cancel;
    }

    public void setCancel(ArrayList<String> cancel) {
        this.cancel = cancel;
    }

    public String[] getReasons() {
        return reasons;
    }

    public void setReasons(String[] reasons) {
        this.reasons = reasons;
    }
}