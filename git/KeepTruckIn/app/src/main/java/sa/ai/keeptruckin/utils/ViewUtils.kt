package sa.ai.keeptruckin.utils

import android.widget.Toast
import sa.ai.keeptruckin.KeepTruckInApp

/**
 * [author] by `Arsal Imam`
 * [created] on 5/17/2020
 *
 * utility class to manage view resources
 */
object ViewUtils {
    /**
     * will show android toast with the provided message
     *
     * [message] to show in toast
     * [length] delay to show toast
     */
    fun showToast(message: String, length: Int = Toast.LENGTH_LONG) {
        Toast.makeText(KeepTruckInApp.INSTANCE, message, length).show()
    }
}