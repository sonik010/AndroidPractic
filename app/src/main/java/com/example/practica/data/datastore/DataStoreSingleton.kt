// file: src/main/java/com/example/practica/data/datastore/DataStoreSingleton.kt
package com.example.practica.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private val Context.dataStore by preferencesDataStore("filter_prefs")

object DataStoreSingleton {
    private var instance: DataStore<Preferences>? = null

    fun getInstance(context: Context): DataStore<Preferences> {
        return instance ?: synchronized(this) {
            instance ?: context.applicationContext.dataStore.also {
                instance = it
            }
        }
    }
}