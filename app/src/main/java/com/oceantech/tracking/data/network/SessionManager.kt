package com.oceantech.tracking.data.network

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.oceantech.tracking.R
import com.oceantech.tracking.data.model.User

/**
 * Session manager to save and fetch data from SharedPreferences
 */
class SessionManager(private val context: Context) {

    private var prefs: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val TOKEN_REFRESH="refresh_token"
        const val LANGUAGE = "app_language"
        const val THEME = "app_theme"
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
    fun saveAppTheme(theme: String) {
        val editor = prefs.edit()
        editor.putString(THEME, theme)
        editor.apply()
    }
    fun fetchAppTheme(): String? {
        return prefs.getString(THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM.toString())
    }

    fun saveAppLanguage(language:String){
        val editor = prefs.edit()
        editor.putString(LANGUAGE, language)
        editor.apply()
    }

    fun fetchAppLanguage(): String? {
        return prefs.getString(LANGUAGE, "vi")
    }

    fun clearAuthToken(){
        val editor = prefs.edit()
        //editor.clear()
        editor.putString(TOKEN_REFRESH, null)
        editor.putString(USER_TOKEN, null)
        editor.apply()
    }
}
