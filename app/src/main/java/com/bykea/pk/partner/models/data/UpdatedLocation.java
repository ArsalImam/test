package com.bykea.pk.partner.models.data;

public class UpdatedLocation {
    private String _id;

    private String status;

    private String inCall;

    private String lng;

    private String token_id;

    private String lat;

    public String get_id ()
    {
        return _id;
    }

    public void set_id (String _id)
    {
        this._id = _id;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    public String getInCall ()
    {
        return inCall;
    }

    public void setInCall (String inCall)
    {
        this.inCall = inCall;
    }

    public String getLng ()
    {
        return lng;
    }

    public void setLng (String lng)
    {
        this.lng = lng;
    }

    public String getToken_id ()
    {
        return token_id;
    }

    public void setToken_id (String token_id)
    {
        this.token_id = token_id;
    }

    public String getLat ()
    {
        return lat;
    }

    public void setLat (String lat)
    {
        this.lat = lat;
    }
}