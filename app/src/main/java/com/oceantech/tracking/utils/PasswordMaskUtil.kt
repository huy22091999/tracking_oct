package com.oceantech.tracking.utils

import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class PasswordMaskUtil private constructor() : PasswordTransformationMethod() {
    override fun getTransformation(source: CharSequence, view: View?): CharSequence {
        return PasswordCharSequence(source)
    }

    class PasswordCharSequence(private val source: CharSequence) : CharSequence {
        override val length: Int
            get() = source.length

        override fun get(index: Int): Char {
            return '*'
        }

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            return source.subSequence(startIndex, endIndex)
        }

    }

    companion object {
        private val instance = PasswordMaskUtil()
        fun getInstance(): PasswordMaskUtil {
            return instance
        }
    }

}

fun TextInputLayout.addEndIconClickListener() {
    var isPasswordVisible = false
    this.setEndIconOnClickListener {
        if (isPasswordVisible) {
            isPasswordVisible = false
            editText?.transformationMethod = PasswordMaskUtil.getInstance()
        } else {
            isPasswordVisible = true
            editText?.transformationMethod = HideReturnsTransformationMethod()
        }
        val length = editText?.text?.length
        editText?.setSelection(length ?: 0)
    }
}
