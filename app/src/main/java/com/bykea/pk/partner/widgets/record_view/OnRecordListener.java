package com.bykea.pk.partner.widgets.record_view;

/**
 * On Record callback Inteface
 * Created by Devlomi on 24/08/2017.
 *
 * @see <a href="https://github.com/3llomi/RecordView">Library Documentation</a>
 */
public interface OnRecordListener {
    /**
     * Implement this method to get callback when Audio Recording starts
     */
    void onStart();

    /**
     * Implement this method to get callback when Audio Recording is cancelled
     */
    void onCancel();

    /**
     * Implement this method to get callback when Audio Recording is Finished
     *
     * @param recordTime Total Recording time
     */
    void onFinish(long recordTime);

    /**
     * Implement this method to get callback when Audio Recording is cancelled before 1 sec
     */
    void onLessThanSecond();
}
