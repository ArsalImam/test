package com.bykea.pk.partner.models.data;

import java.util.ArrayList;

public class Predefine_messages {

    private ArrayList<String> cancel;

    private String[] reasons;
    private String[] contact_reason;

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

    public String[] getContact_reason() {
        return contact_reason;
    }

    public void setContact_reason(String[] contact_reason) {
        this.contact_reason = contact_reason;
    }
}