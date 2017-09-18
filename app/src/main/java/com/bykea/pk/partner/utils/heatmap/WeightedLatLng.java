//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bykea.pk.partner.utils.heatmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.geometry.Point;
import com.google.maps.android.projection.SphericalMercatorProjection;
import com.google.maps.android.quadtree.PointQuadTree.Item;

public class WeightedLatLng implements Item {
    public static final double DEFAULT_INTENSITY = 1.0D;
    private static final SphericalMercatorProjection sProjection = new SphericalMercatorProjection(1.0D);
    private Point mPoint;
    private double mIntensity;

    public WeightedLatLng(LatLng latLng, double intensity) {
        this.mPoint = sProjection.toPoint(latLng);
        if(intensity >= 0.0D) {
            this.mIntensity = intensity;
        } else {
            this.mIntensity = 1.0D;
        }

    }

    public WeightedLatLng(LatLng latLng) {
        this(latLng, 1.0D);
    }

    public Point getPoint() {
        return this.mPoint;
    }

    public double getIntensity() {
        return this.mIntensity;
    }
}
