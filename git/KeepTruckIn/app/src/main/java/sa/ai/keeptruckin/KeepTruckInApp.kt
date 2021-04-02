package sa.ai.keeptruckin

import android.app.Application
import androidx.multidex.MultiDexApplication
import dagger.hilt.android.HiltAndroidApp

/**
 * [author] by `Arsal Imam`
 * [created] on 5/17/2020
 *
 * singleton application class for the app
 */
@HiltAndroidApp
class KeepTruckInApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

    }

    companion object {
        /**
         * instance object to use as context
         */
        lateinit var INSTANCE: Application
    }
}