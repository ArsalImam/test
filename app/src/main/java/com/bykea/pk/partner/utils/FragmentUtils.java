package com.bykea.pk.partner.utils;



import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

/***
 * Fragment Utility Class.
 */
public class FragmentUtils {
    public static boolean sDisableFragmentAnimations = false;

    /**
     * Push fragment into container
     *
     * @param activity       required to get fragment manager
     * @param containerId    id of container where fragment will be added
     * @param fragment       fragment to add
     * @param bundle         data for fragment
     * @param tag            name of fragment to add
     * @param addToBackStack true if you want to add in back stack
     * @param animate        fragment
     */
    public static void pushFragment(AppCompatActivity activity, int containerId, Fragment fragment,
                                    Bundle bundle, String tag,
                                    boolean addToBackStack, boolean animate) {

        try {
            if (fragment == null) {
                return;
            }

            if (bundle != null) {
                fragment.setArguments(bundle);
            }

            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();

            if (animate) {
                //used for animation.
                //ft.setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left,
                // R.animator.slide_in_left, R.animator.slide_out_right);
            }

            if (addToBackStack) {
                ft.addToBackStack(null);
            }

            if (!fragment.isAdded()) {
                ft.replace(containerId, fragment, tag).commit();
            }

        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Remove added fragment from specific container id
     *
     * @param activity    to get Fragment Manager
     * @param containerId of frame layout
     */
    public static void removeFragmentFromContainer(AppCompatActivity activity, int containerId) {
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        if (ft != null) {
            ft.remove(activity.getSupportFragmentManager().findFragmentById(containerId));
            ft.commit();
        }
    }

    /**
     * Removes all fragments from backstack
     *
     * @param activity required to get fragment manager
     */
    public static void removeAllFragmentsFromBackStack(AppCompatActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentUtils.sDisableFragmentAnimations = true;
        if (fragmentManager != null) {
            int count = fragmentManager.getBackStackEntryCount();
            for (int i = count; i >= 0; i--) {
                fragmentManager.popBackStackImmediate(null,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);

                // fragmentManager.popBackStack();
            }
        }
    }

    /**
     * Checks if specific fragment is in back stack
     *
     * @param activity required to get fragment manager
     * @param tag      of fragment to find
     * @return
     */
    public static boolean isFragmentInStack(AppCompatActivity activity, String tag) {
        boolean inStack = false;

        FragmentManager fragmentManager = activity.getSupportFragmentManager();

        if (fragmentManager != null) {
            Fragment fragment = fragmentManager.findFragmentByTag(tag);
            if (fragment != null) {
                inStack = true;
            }

        }

        return inStack;
    }

    /***
     * Find the fragment from the tag.
     *
     * @param activity an AppCompatActivity.
     * @param tag a unique key for storing fragment in the fragment manager.
     *
     * @return The fragment.
     */
    public static Fragment getFragmentByTag(AppCompatActivity activity, String tag) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        if (fragmentManager != null) {
            Fragment fragment = fragmentManager.findFragmentByTag(tag);
            if (fragment != null)
                return fragment;
        }
        return null;
    }


    /***
     * Remove the fragment
     *
     * @param activity an AppCompatActivity.
     * @param tag a unique key for storing fragment in the fragment manager.
     */
    public static void removeFragmentByTag(AppCompatActivity activity, String tag) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        if (fragmentManager != null) {
            Fragment fragment = fragmentManager.findFragmentByTag(tag);
            if (fragment != null)
                fragmentManager.beginTransaction().remove(fragment).commit();
        }

    }
}