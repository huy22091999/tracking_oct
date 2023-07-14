package com.oceantech.tracking.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(ThemePreferences.THEME_PREFERENCES)

class ThemePreferences @Inject constructor(context: Context) {
    private val mContext = context

//    val appTheme: Flow<String?>
//        get() = mContext.dataStore.data.map { preferences ->
//            preferences[ThemePreferences.THEME_PREFERENCES]
//        }
//
//    suspend fun saveAppTheme(theme:String) {
//        mContext.dataStore.edit { preferences ->
//            preferences[ThemePreferences.THEME_PREFERENCES] = theme
//        }
//    }
//
//    suspend fun clear() {
//        mContext.dataStore.edit { preferences ->
//            preferences.clear()
//        }
//    }
//
    companion object {
        const val THEME_PREFERENCES = "theme_data_store"
    }
}