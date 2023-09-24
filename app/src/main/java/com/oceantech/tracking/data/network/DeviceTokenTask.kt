package com.oceantech.tracking.data.network

import android.content.Context
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.security.ProviderInstaller
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun getDeviceToken(context: Context): String? {
    return withContext(Dispatchers.IO) {
        try {
            ProviderInstaller.installIfNeeded(context)

            val task = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
            val token = task.toString()

            return@withContext token
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }
}
