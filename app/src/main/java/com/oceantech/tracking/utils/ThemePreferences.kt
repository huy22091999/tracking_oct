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

    companion object {
        const val THEME_PREFERENCES = "theme_data_store"
    }
}