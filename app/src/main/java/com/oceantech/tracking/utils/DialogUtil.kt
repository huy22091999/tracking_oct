package com.oceantech.tracking.utils

import android.content.Context
import com.example.mymockproject.view.dialog.LoadingDialog

object DialogUtil {
    /**
     * Loading dialog
     */
    private var loadingDialog : LoadingDialog? = null
    fun showLoadingDialog(context: Context){
        if(loadingDialog != null && loadingDialog!!.isShowing) return
        loadingDialog = LoadingDialog(context)
        loadingDialog?.show()
    }

    fun hideLoading(){
        loadingDialog?.dismiss()
    }
}