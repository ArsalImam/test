package com.bykea.pk.partner.ui.loadboard.list

/**
 * Listener used with data binding to process user actions of listing screen.
 *
 * @author Yousuf Sohail
 */
interface JobListActionsListener {

    /**
     * On user tap back button to collapse Loadboard bottom sheet
     *
     */
    fun onBackClicked()

    /**
     * On user taps refresh button to fetch data from remote data source
     *
     */
    fun onRefreshClicked()
}
