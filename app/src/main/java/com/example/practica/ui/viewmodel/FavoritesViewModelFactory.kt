package com.example.practica.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.practica.data.repository.FavoritesRepository

class FavoritesViewModelFactory(
    private val repository: FavoritesRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavoritesViewModel(repository) as T
    }
}