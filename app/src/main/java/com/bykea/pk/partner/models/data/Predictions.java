package com.bykea.pk.partner.models.data;

public class Predictions {
    private String id;

    private String place_id;
    private String description;

    private Structured_formatting structured_formatting;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public Structured_formatting getStructured_formatting() {
        return structured_formatting;
    }

    public void setStructured_formatting(Structured_formatting structured_formatting) {
        this.structured_formatting = structured_formatting;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}