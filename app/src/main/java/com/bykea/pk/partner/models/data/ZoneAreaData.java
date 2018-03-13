package com.bykea.pk.partner.models.data;

import java.util.ArrayList;

public class ZoneAreaData {
    private String urdu;

    private String name;

    private ArrayList<String> loc;

    public String getUrdu() {
        return urdu;
    }

    public void setUrdu(String urdu) {
        this.urdu = urdu;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getLoc() {
        return loc;
    }

    public void setLoc(ArrayList<String> loc) {
        this.loc = loc;
    }
}
