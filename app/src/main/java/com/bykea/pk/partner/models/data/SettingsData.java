package com.bykea.pk.partner.models.data;

import java.io.Serializable;
import java.util.ArrayList;

public class SettingsData implements Serializable {


    private Settings settings;

    private Predefine_messages predefine_messages;

    public Settings getSettings ()
    {
        return settings;
    }

    public void setSettings (Settings settings)
    {
        this.settings = settings;
    }

    public Predefine_messages getPredefine_messages ()
    {
        return predefine_messages;
    }

    public void setPredefine_messages (Predefine_messages predefine_messages)
    {
        this.predefine_messages = predefine_messages;
    }
}
