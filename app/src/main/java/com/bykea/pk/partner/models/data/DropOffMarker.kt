package com.bykea.pk.partner.models.data

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.view.DefaultClusterRenderer


/**
 * Drop Down Marker Data Class
 *
 * @param position The [LatLng] coordinates.
 * @param number The number that will be display on marker.
 */
data class DropOffMarker(private val position: LatLng, var number: Int) : ClusterItem {

    override fun getPosition(): LatLng {
        return position
    }

    override fun getTitle(): String? {
        return null
    }

    override fun getSnippet(): String? {
        return null
    }


}
