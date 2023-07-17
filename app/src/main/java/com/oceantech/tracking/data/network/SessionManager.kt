package com.oceantech.tracking.data.network

import android.content.Context
import android.content.SharedPreferences
import com.oceantech.tracking.R
import java.util.Locale


/**
 * Session manager to save and fetch data from SharedPreferences
 */
class SessionManager(context: Context) {

    private var prefs: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val TOKEN_REFRESH = "refresh_token"
        const val DARK_MODE = "dark_mode"
        const val LANGUAGE = "language"
    }

    /**
     * Function to save auth token refresh
     */
    fun saveAuthTokenRefresh(token: String) {
        val editor = prefs.edit()
        editor.putString(TOKEN_REFRESH, token)
        editor.apply()
    }

    fun fetchAuthTokenRefresh(): String? {
        return prefs.getString(TOKEN_REFRESH, null)
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    fun deleteAuthToken() {
        val editor = prefs.edit()
        editor.remove(USER_TOKEN)
        editor.apply()
    }

    fun saveDarkMode(isDarkMode: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean(DARK_MODE, isDarkMode)
        editor.apply()
    }

    fun getDarkMode(): Boolean = prefs.getBoolean(DARK_MODE, false)

    fun saveLanguage(language: String) {
        val editor = prefs.edit()
        editor.putString(LANGUAGE, language)
        editor.apply()
    }

    fun getLanguage() : String? = prefs.getString(LANGUAGE, Locale.getDefault().language)
}
