package com.bykea.pk.partner.ui.common

/**
 * Various extension functions for AppCompatActivity.
 */

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.bykea.pk.partner.DriverApp
import com.bykea.pk.partner.R
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.utils.Constants.*
import com.bykea.pk.partner.utils.Dialogs
import org.apache.commons.lang3.StringUtils


/**
 * The `fragment` is added to the container view with id `frameId`. The operation is
 * performed by the `fragmentManager`.
 *
 * @param fragment Fragment to replace
 * @param frameId Container view id
 */
fun AppCompatActivity.replaceFragmentInActivity(fragment: Fragment, frameId: Int) {
    supportFragmentManager.transact {
        replace(frameId, fragment)
    }
}

/**
 * The `fragment` is added to the container view with tag. The operation is
 * performed by the `fragmentManager`.
 *
 * @param fragment Fragment to be added
 * @param tag Tag for Fragment Manager
 */
fun AppCompatActivity.addFragmentToActivity(fragment: Fragment, tag: String) {
    supportFragmentManager.transact {
        add(fragment, tag)
    }
}

/**
 * Extension function to obtain ViewModel from ViewModelFactory
 *
 * @param T ViewModel class
 * @param viewModelClass ViewModel class
 */
fun <T : ViewModel> AppCompatActivity.obtainViewModel(viewModelClass: Class<T>) =
        ViewModelProviders.of(this, ViewModelFactory.getInstance(application)).get(viewModelClass)

/**
 * Runs a FragmentTransaction, then calls commit().
 *
 * @param action Fragment Transaction
 */
private inline fun FragmentManager.transact(action: FragmentTransaction.() -> Unit) {
    beginTransaction().apply {
        action()
    }.commit()
}

/**
 * Extension function to navigate to google map with destination and waypoints
 *
 * @param lat Destination Latitude
 * @param lng Destination Longitude
 * @param wayPoints WayPoints (Optional)
 */
fun AppCompatActivity.gotoGoogleMapOnDesiredLocation(
        lat: String,
        lng: String,
        wayPoints: String = StringUtils.EMPTY
) {
    val link: String = if (wayPoints.isNotEmpty()) {
        "${GOOGLE_MAP_ADDRESS_WITH_WAY_POINTS}$ORIGIN${AppPreferences.getLatitude()},${AppPreferences.getLongitude()}$DESTINATION$lat,$lng$WAY_POINTS$wayPoints"
    } else {
        "$GOOGLE_MAP_ADDRESS$lat,$lng"
    }
    val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(link)
    )
    intent.setPackage(GOOGLE_MAP_PACKAGE)
    try {
        startActivity(intent)
    } catch (e: Exception) {
        try {
            val forceIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            startActivity(forceIntent)
        } catch (e: Exception) {
            Dialogs.INSTANCE.showToast(DriverApp.getContext().getString(R.string.install_google_map))
        }
    }
}