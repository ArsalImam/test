package com.bykea.pk.partner.models.data;

public class Place {

    public String id;

    public String name;

    public String vicinity;

    public Geometry geometry;

    public static class Geometry
    {
        public Location location;
    }

    public static class Location
    {

        public double lat;

        public double lng;
    }

}