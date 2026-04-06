// file: src/main/java/com/example/practica/data/datastore/FilterPreferences.kt
package com.example.practica.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FilterPreferences(private val dataStore: DataStore<Preferences>) {
    companion object {
        val GENRE_KEY = stringPreferencesKey("filter_genre")
        val MIN_RATING_KEY = intPreferencesKey("filter_min_rating")
        val YEAR_KEY = intPreferencesKey("filter_year")
    }

    val filterFlow: Flow<FilterSettings> = dataStore.data.map { preferences ->
        FilterSettings(
            genre = preferences[GENRE_KEY] ?: "",
            minRating = preferences[MIN_RATING_KEY] ?: 0,
            year = preferences[YEAR_KEY] ?: 0
        )
    }

    suspend fun saveFilter(genre: String, minRating: Int, year: Int) {
        dataStore.edit { preferences ->
            preferences[GENRE_KEY] = genre
            preferences[MIN_RATING_KEY] = minRating
            preferences[YEAR_KEY] = year
        }
    }
}