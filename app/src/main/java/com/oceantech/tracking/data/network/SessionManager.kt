package com.oceantech.tracking.data.network

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.oceantech.tracking.R


/**
 * Session manager to save and fetch data from SharedPreferences
 */
class SessionManager(context: Context) {

    private var prefs: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val TOKEN_REFRESH = "refresh_token"
        const val LANGUAGE = "language"
        const val ROLE_ADMIN = "ROLE_ADMIN"
    }

    /**
     * Function to save auth token refresh
     */

    fun saveRoleAdmin(admin: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean(ROLE_ADMIN, admin)
        editor.apply()
    }

    fun fetchRole(): Boolean {
        Log.e("ROLE", "fetchRole: " + prefs.getBoolean(ROLE_ADMIN, false))
        return prefs.getBoolean(ROLE_ADMIN, false)
    }

    fun clearRole() {
        val editor = prefs.edit()
        editor.remove(ROLE_ADMIN)
        editor.apply()
    }

    fun saveAuthTokenRefresh(token: String) {
        val editor = prefs.edit()
        editor.putString(TOKEN_REFRESH, token)
        editor.apply()
    }

    fun fetchAuthTokenRefresh(): String? {
        return prefs.getString(TOKEN_REFRESH, null)
    }

    fun fetchAuthToken(): String? {
        Log.e("TOKEN", "fetchAuthToken: " + prefs.getString(USER_TOKEN, null))
        return prefs.getString(USER_TOKEN, null)
    }

    fun saveLanguage(language: String) {
        val editor = prefs.edit()
        editor.putString(LANGUAGE, language)
        editor.apply()
    }

    fun fetchLanguage(): String? {
        return prefs.getString(LANGUAGE, "en")
    }

    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    fun clearAuthToken() {
        val editor = prefs.edit()
        editor.remove(USER_TOKEN)
        editor.remove(TOKEN_REFRESH)
        editor.apply()
    }


}
