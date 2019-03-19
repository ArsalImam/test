package com.bykea.pk.partner.ui.helpers;

/**
 * Generic callback
 * @param <T> Data Type
 */
public interface Callback<T> {
    void invoke(T obj);
}
