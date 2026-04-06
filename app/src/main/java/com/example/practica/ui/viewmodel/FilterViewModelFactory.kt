// file: src/main/java/com/example/practica/ui/viewmodel/FilterViewModelFactory.kt
package com.example.practica.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.practica.data.datastore.DataStoreSingleton
import com.example.practica.data.datastore.FilterPreferences

class FilterViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val dataStore = DataStoreSingleton.getInstance(context)
        val filterPreferences = FilterPreferences(dataStore)
        return FilterViewModel(filterPreferences) as T
    }
}