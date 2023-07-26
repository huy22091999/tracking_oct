package com.oceantech.tracking.utils

import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.annotation.RequiresApi
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

// Check empty or not when clicking submit button
internal fun EditText.emptyOrText(error: String): String {
    val layout = this.parent.parent as TextInputLayout

    checkTextChanged(error, layout)

    return when (val text = text.toString()) {
        "" -> {
            layout.error = error
            ""
        }

        else -> {
            text
        }
    }
}

// Check empty or not when user has just unclicked edittext
internal fun EditText.checkEmpty(error: String) {
    val layout = this.parent.parent as TextInputLayout

    checkTextChanged(error, layout)

    setOnFocusChangeListener { v, hasFocus ->
        if (!hasFocus) {
            if (text.isNullOrBlank()) {
                layout.isErrorEnabled = false //Use this to refresh layout when not having error anymore
                layout.error = error
            }
        }
    }
}

//Check text changed. If there is nothing in edit text, show error
private fun EditText.checkTextChanged(error: String, layout: TextInputLayout) {
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s != null) {
                if (s.isNotEmpty()) {
                    layout.isErrorEnabled = false
                    layout.error = null
                } else {
                    layout.error = error
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {

        }

    })
}