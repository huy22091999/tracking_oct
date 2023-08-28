package com.oceantech.tracking.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.oceantech.tracking.R
import com.oceantech.tracking.databinding.DialogSuccesBinding
import com.oceantech.tracking.databinding.DialogTimesheetBinding

public fun showDialogSuccess(context: Context, message: String?) {
    var dialog = Dialog(context)
    var bindingDialog = DialogSuccesBinding.inflate(dialog.layoutInflater)
    dialog.setContentView(bindingDialog.root)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.show()

    Glide.with(dialog.context).load(R.raw.success).diskCacheStrategy(DiskCacheStrategy.ALL).into(bindingDialog.image);
    bindingDialog.tvMessage.text = message ?: context.getString(R.string.success)
}

public fun showDialogFailed(context: Context, message: String?) {
    var dialog = Dialog(context)
    var bindingDialog = DialogSuccesBinding.inflate(dialog.layoutInflater)
    dialog.setContentView(bindingDialog.root)
    dialog.show()
    bindingDialog.tvMessage.text = message ?: context.getString(R.string.success)
}