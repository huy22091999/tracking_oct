package com.oceantech.tracking.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Constraints
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.ContentViewCallback
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.SnackbarLayout
import com.oceantech.tracking.R
import com.oceantech.tracking.databinding.DialogFailedBinding
import com.oceantech.tracking.databinding.DialogSuccesBinding
import com.oceantech.tracking.databinding.SnackBarBinding

public fun showDialogSuccess(context: Context, message: String?) {
    var dialog = Dialog(context)
    var bindingDialog = DialogSuccesBinding.inflate(dialog.layoutInflater)
    dialog.setContentView(bindingDialog.root)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.show()

    bindingDialog.tvMessage.text = message ?: context.getString(R.string.success)
    var handle = android.os.Handler()
    Thread{
        Thread.sleep(2000)
        handle.post {
            dialog.dismiss()
        }
    }.start()
}

public fun showDialogFailed(context: Context, message: String?) {
    var dialog = Dialog(context)
    var bindingDialog = DialogFailedBinding.inflate(dialog.layoutInflater)
    dialog.setContentView(bindingDialog.root)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.show()
    bindingDialog.tvMessage.text = message ?: context.getString(R.string.failed)
    var handle = android.os.Handler()
    Thread{
        Thread.sleep(2000)
        handle.post {
            dialog.dismiss()
        }
    }.start()

}

@SuppressLint("ShowToast", "ResourceAsColor")
public fun showSnackbar(view: View, message: String, btnStr: String?, backgroundColor: Int, onClick: () -> Unit){
    val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
    snackbar.view.setBackgroundColor(ContextCompat.getColor(view.context!!,backgroundColor))
    snackbar.setActionTextColor(Color.WHITE)
    snackbar.setAction(btnStr){ onClick() }
    snackbar.show()
}
