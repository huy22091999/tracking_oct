package com.oceantech.tracking.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.oceantech.tracking.R


private lateinit var networkCallback: NetworkCallback

/**
 * Register callback for network connectivity change
 */
@RequiresApi(Build.VERSION_CODES.O)
internal fun Fragment.registerNetworkReceiver(
    handleAction: () -> Unit
){
    val context = requireContext()
    val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    networkCallback = object: NetworkCallback(){
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            handleAction()
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            showToast(context, getString(R.string.check_network))
        }

        override fun onUnavailable() {
            super.onUnavailable()
            showToast(context, getString(R.string.network_requirement))

        }

    }
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    connectivityManager.requestNetwork(networkRequest, networkCallback, 100)
}

internal fun Fragment.unregisterNetworkReceiver(){
    val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    connectivityManager.unregisterNetworkCallback(networkCallback)
}

