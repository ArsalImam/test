package com.bykea.pk.partner.ui.loadboard.common

/**
 * Various extension functions for AppCompatActivity.
 */

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders


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