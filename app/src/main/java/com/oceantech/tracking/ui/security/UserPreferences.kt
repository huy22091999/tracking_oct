package com.oceantech.tracking.ui.security

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(UserPreferences.APP_PREFERENCES)

class UserPreferences @Inject constructor(context: Context) {

    private val mContext = context

    val accessToken: Flow<String?>
        get() = mContext.dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN]
        }

    val refreshToken: Flow<String?>
        get() = mContext.dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN]
        }

    val userId: Flow<Long?>
        get() = mContext.dataStore.data.map { preferences ->
            preferences[USER_ID]
        }

    val username: Flow<String?>
        get() = mContext.dataStore.data.map { preferences ->
            preferences[USERNAME]
        }

    val userFullname: Flow<String?>
        get() = mContext.dataStore.data.map { preferences ->
            preferences[USER_FULLNAME]
        }

    suspend fun saveUserFullname(fullname: String) {
        mContext.dataStore.edit { preferences ->
            preferences[USER_FULLNAME] = fullname
        }
    }


    val userEmail: Flow<String?>
        get() = mContext.dataStore.data.map { preferences ->
            preferences[USER_EMAIL]
        }
    val userRole: Flow<String?>
        get() = mContext.dataStore.data.map { preferences ->
            preferences[USER_ROLE]
        }

    suspend fun saveUserRole(authority: String) {
        mContext.dataStore.edit { preferences ->
            preferences[USER_ROLE] = authority
        }
    }

    suspend fun saveAccessTokens(accessToken: String, refreshToken: String) {
        mContext.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
            preferences[REFRESH_TOKEN] = refreshToken
        }
    }

//    suspend fun saveUserData(user: User) {
//        mContext.dataStore.edit { preferences ->
//            preferences[USER_ID] = user.id ?: 0
//            preferences[USER_ROLE] = user.roles[0].name ?: ""
//            preferences[USERNAME] = user.username ?: ""
//            preferences[USER_FULLNAME] = user.fullname ?: "[Unknown]"
//            preferences[USER_EMAIL] = user.email ?: "email@gmail.com"
//        }
//    }

    suspend fun clear() {
        mContext.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("key_access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("key_refresh_token")
        private val USER_ID = longPreferencesKey("key_user_id")
        private val USER_ROLE = stringPreferencesKey("user_role")
        private val USERNAME = stringPreferencesKey("key_user_name")
        private val USER_FULLNAME = stringPreferencesKey("key_user_fullname")
        private val USER_EMAIL = stringPreferencesKey("key_user_email")
        const val APP_PREFERENCES = "nimpe_data_store"
    }

}