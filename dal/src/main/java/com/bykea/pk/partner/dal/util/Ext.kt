package com.bykea.pk.partner.dal.util

import android.widget.EditText
import org.apache.commons.lang3.StringUtils

/**
 * this method can be used to check whether any edit text has value or not
 * @param editText [FontEditText] of which text needs to be validate
 * @return [true] if contains text else [false]
 */
fun EditText.isFilledEditText(showError: String? = null): Boolean {
    if (StringUtils.isEmpty(this.text.toString().trim())) {
        if (showError == null) return false
        this.error = showError
        this.requestFocus()
        return false
    }
    return true
}