package com.bykea.pk.partner.models.response;

public class HeatMapUpdatedResponse {

    private Bounds bounds;

    private float opacity;

    public Bounds getBounds() {
        return bounds;
    }

    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }


    public class Bounds {
        private Ne ne;

        private Sw sw;

        public Ne getNe() {
            return ne;
        }

        public void setNe(Ne ne) {
            this.ne = ne;
        }

        public Sw getSw() {
            return sw;
        }

        public void setSw(Sw sw) {
            this.sw = sw;
        }

    }


    public class Ne {
        private double lon;

        private double lat;

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }
    }


    public class Sw {
        private double lon;

        private double lat;

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }
    }


}
