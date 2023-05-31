package com.oceantech.tracking.ui.home.request_location

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.LocationManager
import android.net.ConnectivityManager
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.oceantech.tracking.R

class NimpeBroadCastReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!=null&&intent.action==ConnectivityManager.CONNECTIVITY_ACTION)
        {
            val connMgr = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            var isWifiConn= false
            var isMobileConn = false
            connMgr.allNetworks.forEach { network ->
                connMgr.getNetworkInfo(network).apply {
                    if (this?.type == ConnectivityManager.TYPE_WIFI) {
                        isWifiConn = isWifiConn or isConnected
                    }
                    if (this?.type == ConnectivityManager.TYPE_MOBILE) {
                        isMobileConn = isMobileConn or isConnected
                    }
                }
            }
            if(!isMobileConn&&!isWifiConn)
            {
                AlertDialog.Builder(context)
                    .setMessage(R.string.need_network)
                    .setNegativeButton(R.string.close, null)
                    .show()
            }

        }
        else{
            val lm: LocationManager = context?.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
            var gps_enabled = false
            var network_enabled = false

            try {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
            } catch (ex: Exception) {
            }

            try {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            } catch (ex: Exception) {
            }
            if (!gps_enabled && !network_enabled) {
                // notify user
                AlertDialog.Builder(context)
                    .setMessage(R.string.gps_network_not_enabled)
                    .setPositiveButton(R.string.open_location_settings,
                        DialogInterface.OnClickListener { _, _ ->
                            context.startActivity(
                                Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS
                                )
                            )
                        })
                    .setNegativeButton(R.string.Cancel, null)
                    .show()
            }
        }
    }
}