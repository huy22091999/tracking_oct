package com.oceantech.tracking.utils

import android.annotation.TargetApi
import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.preference.PreferenceManager
import java.util.*

private const val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"

class LocalHelper {
    fun onAttach(context: Context): Context? {
        val lang = getPersistedData(context, Locale.getDefault().language)
        return setLocale(context, lang)
    }

    fun onAttach(context: Context, defaultLanguage: String): Context? {
        val lang = getPersistedData(context, defaultLanguage)
        return setLocale(context, lang)
    }

    fun getLanguage(context: Context): String? {
        return getPersistedData(context, Locale.getDefault().language)
    }

    fun setLanguage(context: Context, language: String?): Context? {
        return setLocale(context, language)
    }

    private fun setLocale(context: Context, language: String?): Context? {
        persist(context, language)
        return updateResources(context, language)
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            updateResources(context, language)
//        } else updateResourcesLegacy(context, language)
    }

    //Take the default language of device
    private fun getPersistedData(context: Context, defaultLanguage: String): String? {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(SELECTED_LANGUAGE, defaultLanguage)
    }

    private fun persist(context: Context, language: String?) {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString(SELECTED_LANGUAGE, language)
        editor.apply()
    }

    //Use this method to change language with the devices having android version >=N
    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResources(context: Context, language: String?): Context? {
//        val locale = language?.let { Locale(language) }
//        locale?.let { Locale.setDefault(locale) }
//        val configuration: Configuration = context.resources.configuration
//        configuration.setLocale(locale)
//        configuration.setLayoutDirection(locale)
//        return context.createConfigurationContext(configuration)
        return language?.let { TrackingContextWrapper.wrap(context, it) }
    }

}

class TrackingContextWrapper(context: Context): ContextWrapper(context){
    companion object{
        @RequiresApi(Build.VERSION_CODES.N)
        fun wrap(context: Context, language: String): ContextWrapper{
            val configuration: Configuration = context.resources.configuration
            val sysLocale = configuration.locales.get(0)
            if (language != "" && sysLocale.language != language){
                val locale =  Locale(language)
                Locale.setDefault(locale)
                configuration.setLocale(locale)
                context.createConfigurationContext(configuration)
            }
            return TrackingContextWrapper(context)
        }
    }
}