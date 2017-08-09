package com.bykea.pk.partner.models.data;

public class CitiesData
{
    private String _id;

    private String name;

    private String created_at;

    private String lng;

    private String city_code;

    private String lat;

    public String get_id ()
    {
        return _id;
    }

    public void set_id (String _id)
    {
        this._id = _id;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getCreated_at ()
    {
        return created_at;
    }

    public void setCreated_at (String created_at)
    {
        this.created_at = created_at;
    }

    public String getLng ()
    {
        return lng;
    }

    public void setLng (String lng)
    {
        this.lng = lng;
    }

    public String getCity_code ()
    {
        return city_code;
    }

    public void setCity_code (String city_code)
    {
        this.city_code = city_code;
    }

    public String getLat ()
    {
        return lat;
    }

    public void setLat (String lat)
    {
        this.lat = lat;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [_id = "+_id+", name = "+name+", created_at = "+created_at+", lng = "+lng+", city_code = "+city_code+", lat = "+lat+"]";
    }
}

