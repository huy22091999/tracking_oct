package com.oceantech.tracking.data.network

import android.content.Context
import android.content.SharedPreferences
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
        const val USER_NAME = "user_name"
        const val PASSWORD ="password"
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
    fun saveUserName(userName: String) {
        val editor = prefs.edit()
        editor.putString(USER_NAME, userName)
        editor.apply()
    }
    fun fetchUserName(): String? {
        return prefs.getString(USER_NAME, null)
    }
    fun savePassWord(token: String) {
        val editor = prefs.edit()
        editor.putString(PASSWORD, token)
        editor.apply()
    }
    fun fetchPassword(): String? {
        return prefs.getString(PASSWORD, null)
    }
    fun clearAuthToken(){
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}
