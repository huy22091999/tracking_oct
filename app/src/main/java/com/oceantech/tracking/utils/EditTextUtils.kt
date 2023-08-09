package com.oceantech.tracking.utils

import android.app.DatePickerDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout
import com.oceantech.tracking.R
import java.util.Calendar

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

private fun chooseDate(context: Context, editText: EditText, layout: TextInputLayout){
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

    DatePickerDialog(
        context,
        { view, year, month, dayOfMonth ->
            val date = String.format("%02d-%02d-%04d", dayOfMonth, month + 1, year)
            editText.setText(date)
            layout.error = null
            layout.isErrorEnabled = false
        },
        year,
        month,
        dayOfMonth
    ).show()
}

internal fun EditText.selectDateAndCheckError(context: Context, error: String) {
    val edt = this
    val layout = this.parent.parent as TextInputLayout
    inputType = EditorInfo.TYPE_NULL
    onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
        v.hideKeyboard()
        if (hasFocus) {
            // Use date picker to choose date
            chooseDate(context, edt, layout)
            v.setOnClickListener {
                chooseDate(context, edt, layout)
            }
        } else {
            if (edt.text.isNullOrBlank()) {
                layout.error = error
            }
        }
    }
}