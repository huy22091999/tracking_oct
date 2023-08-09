package com.oceantech.tracking.utils

import android.content.Context
import android.graphics.Rect
import android.os.Parcel
import android.os.Parcelable
import android.text.InputType
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.appcompat.R
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout

class ExposedDropdownMenu : MaterialAutoCompleteTextView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun getFreezesText(): Boolean {
        return false
    }

    init {
        inputType = InputType.TYPE_NULL
    }

    override fun onSaveInstanceState(): Parcelable? {
        val parcelable = super.onSaveInstanceState()
        if (TextUtils.isEmpty(text)) {
            return parcelable
        }
        val customSavedState = CustomSavedState(parcelable)
        customSavedState.text = text.toString()
        return customSavedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is CustomSavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        setText(state.text, false)
        super.onRestoreInstanceState(state.superState)
    }

    private class CustomSavedState(superState: Parcelable?) : BaseSavedState(superState) {
        var text: String? = null

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeString(text)
        }
    }

    override fun enoughToFilter(): Boolean {
        return false
    }

}


internal fun setUpDropdownMenu(
    dropdownMenu: ExposedDropdownMenu,
    options: List<String>,
    context: Context,
    layout: TextInputLayout,
    error: String
) {
    val adapter = ArrayAdapter(
        context,
        R.layout.support_simple_spinner_dropdown_item,
        options
    )
    dropdownMenu.apply {
        setAdapter(adapter)
        setOnItemClickListener { _, _, position, _ ->
            setText(adapter.getItem(position))
            layout.error = null
            layout.isErrorEnabled = false
        }
        setOnFocusChangeListener { _, hasFocus ->
            hideKeyboard()
            if (hasFocus) {
                showDropDown()
                setOnClickListener {
                    showDropDown()
                }
            } else {
                if (dropdownMenu.text.isNullOrBlank()) {
                    layout.error = error
                }
            }
        }
    }

}