package com.bykea.pk.partner.ui.helpers;

import android.content.Context;

import com.bykea.pk.partner.models.data.DropOffMarker;
import com.bykea.pk.partner.utils.Utils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.ClusterRenderer;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.Set;

/**
 * Marker Cluster Renderer is a subclass of {@linkplain DefaultClusterRenderer},
 * It is used for rendering cluster manager
 *
 * @param <T> The type of {@linkplain ClusterItem}
 */
public class MarkerClusterRenderer<T extends ClusterItem> extends DefaultClusterRenderer<T> {

    Context context;

    /**
     * Constructor
     *
     * @param context Holding the reference of an activity.
     * @param map The google map instance.
     * @param clusterManager The object of {@linkplain ClusterManager<T>} of type T
     */
    public MarkerClusterRenderer(Context context, GoogleMap map, ClusterManager<T> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<T> cluster) {
        return cluster.getSize() > 1;
    }

    @Override
    protected void onBeforeClusterItemRendered(T item, MarkerOptions markerOptions) {
        DropOffMarker marker = (DropOffMarker) item;
        markerOptions.icon(Utils.getDropOffBitmapDiscriptor(context,
                                String.valueOf(marker.getNumber())))
                        .position(marker.getPosition());
    }
}
