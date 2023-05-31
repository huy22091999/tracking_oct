/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oceantech.tracking.ui.home.request_location

import android.app.Notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.os.*

import android.util.Base64
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.oceantech.tracking.R
import com.oceantech.tracking.data.model.Patient
import com.oceantech.tracking.data.model.Vector
import com.oceantech.tracking.data.network.RemoteDataSource
import com.oceantech.tracking.ui.MainActivity
import com.oceantech.tracking.ui.MainActivity.Companion.NOTIFICATION_CHANNEL_ID
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import okhttp3.ResponseBody
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import timber.log.Timber.d
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Service tracks location when requested and updates Activity via binding. If Activity is
 * stopped/unbinds and tracking is enabled, the service promotes itself to a foreground service to
 * insure location updates aren't interrupted.
 *
 * For apps running in the background on O+ devices, location is computed much less than previous
 * versions. Please reference documentation for details.
 */
class ForegroundOnlyLocationService : Service() {
    /*
     * Checks whether the bound activity has really gone away (foreground service with notification
     * created) or simply orientation change (no-op).
     */
    private lateinit var handlerThread: HandlerThread
    private lateinit var mHandler: Handler
    private var configurationChange = false
    private lateinit var notificationManager: NotificationManager
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: Location? = null
    private var isFirst = true
    private var isLastCheckHasDengue = false
    override fun onCreate() {
        handlerThread = HandlerThread("HandlerThreadName")
        handlerThread.start()
        mHandler = Handler(handlerThread.looper)
        isFirst = true
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(60)
            fastestInterval = TimeUnit.SECONDS.toMillis(30)
            maxWaitTime = TimeUnit.MINUTES.toMillis(2)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                currentLocation = locationResult.lastLocation
                val api = RemoteDataSource().buildApi().getAllLocation()
                api.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        val gson = Gson()
                        val data = response.body()
                        val result = data?.bytes()?.let { decrypt(it) }
                        val jsonObject = JSONTokener(result).nextValue() as JSONObject
                        val listPatientItem = jsonObject.getJSONArray("listPatientInformation")
                        val listDengueLocationItem =
                            jsonObject.getJSONArray("listDengueLocationItem")
                        var listPatient = mutableListOf<Patient>()
                        var listDengue = mutableListOf<Vector>()
                        for (i in 0 until listPatientItem.length()) {
                            val patient = gson.fromJson(
                                listPatientItem.getJSONObject(i).toString(),
                                Patient::class.java
                            )
                            listPatient.add(patient)
                        }
                        for (i in 0 until listDengueLocationItem.length()) {
                            val vector = gson.fromJson(
                                listDengueLocationItem.getJSONObject(i).toString(),
                                Vector::class.java
                            )
                            listDengue.add(vector)
                        }

                        val isHasDengue = checkDengue(currentLocation!!, listPatient, listDengue)
                        if (isFirst) {
                            isFirst = false
                            generateNotificationDengue(isHasDengue)
                        } else {
                            if (isHasDengue != isLastCheckHasDengue) {
                                generateNotificationDengue(isHasDengue)
                            }
                        }
                        isLastCheckHasDengue = isHasDengue

                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
            }
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mHandler.post {
            var notification: Notification = generateNotification()
            startForeground(NOTIFICATION_ID_FOREGROUND, notification)
            subscribeToLocationUpdates()
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        Timber.d("onBind()")
        return null
    }


    override fun onDestroy() {
        d("onDestroy()")
        handlerThread.interrupt()
        unsubscribeToLocationUpdates()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        configurationChange = true
    }

    fun checkDengue(location: Location, list1: List<Patient>, list2: List<Vector>): Boolean {
        for (item in list1) {
            var temp = Location("")
            temp.latitude = item.latitude!!
            temp.longitude = item.longitude!!

            if (location.distanceTo(temp) <= 200f)
                return true
        }
        for (item in list2) {
            var temp = Location("")
            temp.latitude = item.latitude!!
            temp.longitude = item.longitude!!
            if (location.distanceTo(temp) <= 200f)
                return true
        }
        return false
    }


    fun subscribeToLocationUpdates() {
        d("subscribeToLocationUpdates()")
        //SharedPreferenceUtil.saveLocationTrackingPref(this, true)
        // Binding to this service doesn't actually trigger onStartCommand(). That is needed to
        // ensure this Service can be promoted to a foreground service, i.e., the service needs to
        // be officially started (which we do here).
        try {
            // TODO: Step 1.5, Subscribe to location changes.
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, locationCallback, handlerThread.looper
            )
        } catch (unlikely: SecurityException) {
            //SharedPreferenceUtil.saveLocationTrackingPref(this, false)
            d("Lost location permissions. Couldn't remove updates. $unlikely")
        }
    }

    fun unsubscribeToLocationUpdates() {
        d("unsubscribeToLocationUpdates()")
        try {
            // TODO: Step 1.6, Unsubscribe to location changes.
            val removeTask = fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            removeTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    d("Location Callback removed.")
                    stopSelf()
                } else {
                    d("Failed to remove Location Callback.")
                }
            }
            // SharedPreferenceUtil.saveLocationTrackingPref(this, false)
        } catch (unlikely: SecurityException) {
            //SharedPreferenceUtil.saveLocationTrackingPref(this, true)
            d("Lost location permissions. Couldn't remove updates. $unlikely")
        }
    }

    /*
     * Generates a BIG_TEXT_STYLE Notification that represent latest location.
     */
    private fun generateNotification(): Notification {
        d("generateNotification()")
        val mainNotificationText = getString(R.string.warning_dengue)
        val titleText = getString(R.string.app_name)
        // 2. Build the BIG_TEXT_STYLE.
        val bigTextStyle = NotificationCompat.BigTextStyle()
            .bigText(mainNotificationText)
            .setBigContentTitle(titleText)
        val launchActivityIntent = Intent(this, MainActivity::class.java)
        val activityPendingIntent = PendingIntent.getActivity(
            this, 0, launchActivityIntent, PendingIntent.FLAG_IMMUTABLE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationCompatBuilder =
                NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            return notificationCompatBuilder
                .setStyle(bigTextStyle)
                .setContentTitle(titleText)
                .setContentText(mainNotificationText)
                .setSmallIcon(R.drawable.ic_launcher)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(activityPendingIntent)
                .build()
        } else {
            val bigTextStyle = Notification.BigTextStyle()
                .bigText(mainNotificationText)
                .setBigContentTitle(titleText)
            return Notification.Builder(this)
                .setStyle(bigTextStyle)
                .setContentTitle(titleText)
                .setContentText(mainNotificationText)
                .setSmallIcon(R.drawable.ic_launcher)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)
                //.setVisibility(Notification.VISIBILITY_PUBLIC)
                .setContentIntent(activityPendingIntent)
                .build()
        }


    }

    fun generateNotificationDengue(isDengue: Boolean) {
        var pendingIntent: PendingIntent = Intent(
            applicationContext,
            MainActivity::class.java
        ).let { intentNotification ->
            PendingIntent.getActivity(applicationContext, 0, intentNotification, FLAG_IMMUTABLE)
        }
        val sdf = SimpleDateFormat("hh:mm:ss")
        val currentDate = sdf.format(Date())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            var notification = NotificationCompat.Builder(
                applicationContext,
                NOTIFICATION_CHANNEL_ID
            )
                .setContentTitle("Dengue Alert - ${getString(R.string.at)} $currentDate")
                .setContentText(if (isDengue) getString(R.string.has_dengue) else getString(R.string.no_dengue))
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.VISIBILITY_PUBLIC)
                .setDefaults(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
            with(NotificationManagerCompat.from(applicationContext)) {
                notify(NOTIFICATION_ID, notification.build())
            }
        }else{
            val notificationPopup = Notification.Builder(this)
                .setContentTitle("Dengue Alert - ${getString(R.string.at)} $currentDate")
                .setContentText(if (isDengue) getString(R.string.has_dengue) else getString(R.string.no_dengue))
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)
                .setCategory(Notification.CATEGORY_CALL)
                .build()
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(0, notificationPopup)
        }
        if(isDengue){
            postCountNotification()
        }

    }

    private val key = "1234567890123456"
    fun decrypt(toDecrypt: ByteArray): String? {
        return try {
            val iv = IvParameterSpec(key.toByteArray(Charsets.UTF_8))
            val skeySpec = SecretKeySpec(key.toByteArray(Charsets.UTF_8), "AES")
            val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv)
            val cipherText = cipher.doFinal(Base64.decode(toDecrypt, Base64.DEFAULT))
            String(cipherText)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        const val NOTIFICATION_ID = 12345678
        private const val NOTIFICATION_ID_FOREGROUND = 1234567
    }
    fun postCountNotification()
    {
        val api =RemoteDataSource().buildApi().postCountNotification()
        api.enqueue(object :Callback<okhttp3.Response>{
            override fun onResponse(
                call: Call<okhttp3.Response>,
                response: Response<okhttp3.Response>
            ) {
                print(response.body())
            }

            override fun onFailure(call: Call<okhttp3.Response>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }
}
