package com.example.bikeputernew.datastore.datastore

import android.content.Context
import android.util.Log
import androidx.compose.ui.res.stringResource
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import com.example.bikeputernew.R
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import com.example.bikeputernew.datastore.database.BikeViewModel
import com.example.bikeputernew.values.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val TAG = "StoreBikeputerData"

class StoreBikeputerData(private val context: Context) {

    companion object {
        private val Context.datastore : DataStore<Preferences> by preferencesDataStore(Constants.PREFERENCES)
        val DEVICE_NAME = stringPreferencesKey("bikeputer_name")
    }

    val getDeviceName: Flow<String?> = context.datastore.data.map { preferences ->
        Log.i(TAG, "getting device name from preferences")
        preferences[DEVICE_NAME] ?: Constants.DEVICE_DEFAULT_NAME
    }

    suspend fun saveDeviceName(name: String) {
        Log.i(TAG, "saving $name to preferences as device name")
        context.datastore.edit { preferences ->
            preferences[DEVICE_NAME] = name
        }
    }
}