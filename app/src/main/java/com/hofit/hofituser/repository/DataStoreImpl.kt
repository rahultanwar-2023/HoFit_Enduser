package com.hofit.hofituser.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreImpl(context: Context) {

    private val dataStore = context.createDataStore(name = "user_prefs")

    companion object {
        val USER_LOCATION = preferencesKey<String>("user_location")
    }

    suspend fun storeLocation(location: String) {
        dataStore.edit {
            it[USER_LOCATION] = location
        }
    }

    val locationFlow: Flow<String> = dataStore.data.map {
        it[USER_LOCATION] ?: ""
    }

}