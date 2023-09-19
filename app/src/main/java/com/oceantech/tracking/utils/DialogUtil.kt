package com.oceantech.tracking.utils

import android.app.Dialog
import android.content.Context
import com.example.mymockproject.view.dialog.LoadingDialog
import com.saadahmedsoft.popupdialog.PopupDialog
import com.saadahmedsoft.popupdialog.Styles
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener


object DialogUtil {
    /**
     * Loading dialog
     */
    private var loadingDialog: LoadingDialog? = null
    fun showLoadingDialog(context: Context) {
        if (loadingDialog != null && loadingDialog!!.isShowing) return
        loadingDialog = LoadingDialog(context)
        loadingDialog?.show()
    }

    fun hideLoading() {
        loadingDialog?.dismiss()
    }

    fun showAlertDialogSuccess(context: Context, heading: String,description:String) {
        PopupDialog.getInstance(context)
            .setStyle(Styles.SUCCESS)
            .setHeading(heading)
            .setDescription(
                description
            )
            .setCancelable(false)
            .showDialog(object : OnDialogButtonClickListener() {
                override fun onDismissClicked(dialog: Dialog) {
                    super.onDismissClicked(dialog)
                }
            })
    }

    fun showAlertDialogAlert(context: Context, heading: String,description:String) {
        PopupDialog.getInstance(context)
            .setStyle(Styles.ALERT)
            .setHeading(heading)
            .setDescription(
                description
            )
            .setCancelable(false)
            .showDialog(object : OnDialogButtonClickListener() {
                override fun onDismissClicked(dialog: Dialog) {
                    super.onDismissClicked(dialog)
                }
            })
    }

}