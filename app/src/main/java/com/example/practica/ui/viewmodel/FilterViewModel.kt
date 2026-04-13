// file: src/main/java/com/example/practica/ui/viewmodel/FilterViewModel.kt
package com.example.practica.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practica.data.datastore.FilterPreferences
import com.example.practica.data.datastore.FilterSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FilterViewModel(
    private val filterPreferences: FilterPreferences
) : ViewModel() {

    private val _settings = MutableStateFlow(FilterSettings("", 0, 0))
    val settings: StateFlow<FilterSettings> = _settings.asStateFlow()

    init {
        viewModelScope.launch {
            filterPreferences.filterFlow.collect { settings ->
                _settings.value = settings
            }
        }
    }

    fun saveFilter(genre: String, minRating: Int, year: Int) {
        viewModelScope.launch {
            filterPreferences.saveFilter(genre, minRating, year)
        }
    }
}