// file: src/main/java/com/example/practica/ui/viewmodel/FavoritesViewModel.kt
package com.example.practica.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practica.data.repository.FavoritesRepository
import com.example.practica.domain.model.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val _favorites = MutableStateFlow<List<Book>>(emptyList())
    val favorites: StateFlow<List<Book>> = _favorites.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadFavorites() {
        viewModelScope.launch {
            _isLoading.value = true
            _favorites.value = favoritesRepository.getAllFavorites()
            _isLoading.value = false
        }
    }

    fun removeFromFavorites(bookId: Int) {
        viewModelScope.launch {
            favoritesRepository.removeFromFavorites(bookId)
            loadFavorites() // Обновляем список
        }
    }
}