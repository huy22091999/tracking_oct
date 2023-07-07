package com.oceantech.tracking.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.widget.Toast
import com.oceantech.tracking.R

class NetworkBroadcast:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo

        if (activeNetwork != null && activeNetwork.isConnected) {
            Toast.makeText(context, context.getString(R.string.network_connected), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, context.getString(R.string.network_disconnected), Toast.LENGTH_SHORT).show()
        }
    }
}