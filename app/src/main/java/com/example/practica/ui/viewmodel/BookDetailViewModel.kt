package com.example.practica.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practica.domain.model.Book
import com.example.practica.domain.repository.IBookRepository
import com.example.practica.data.repository.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookDetailViewModel(
    private val repository: IBookRepository,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BookDetailUiState>(BookDetailUiState.Loading)
    val uiState: StateFlow<BookDetailUiState> = _uiState.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private var cachedBooks = mutableMapOf<Int, Book>()

    fun loadBook(bookId: Int) {
        cachedBooks[bookId]?.let {
            _uiState.value = BookDetailUiState.Success(it)
            return
        }

        _uiState.value = BookDetailUiState.Loading

        viewModelScope.launch {
            val queries = listOf("harry potter", "lord of the rings", "1984", "the hobbit")

            for (query in queries) {
                val result = repository.searchBooks(query)
                if (result.isSuccess) {
                    val books = result.getOrNull() ?: emptyList()
                    val book = books.find { it.id == bookId }
                    if (book != null) {
                        cachedBooks[bookId] = book
                        _uiState.value = BookDetailUiState.Success(book)
                        return@launch
                    }
                }
            }

            _uiState.value = BookDetailUiState.Error("Книга не найдена")
        }
    }

    fun checkFavoriteStatus(bookId: Int) {
        viewModelScope.launch {
            _isFavorite.value = favoritesRepository.isFavorite(bookId)
        }
    }

    fun toggleFavorite(book: Book) {
        viewModelScope.launch {
            if (_isFavorite.value) {
                favoritesRepository.removeFromFavorites(book.id)
                _isFavorite.value = false
            } else {
                favoritesRepository.addToFavorites(book)
                _isFavorite.value = true
            }
        }
    }
}

sealed class BookDetailUiState {
    object Loading : BookDetailUiState()
    data class Success(val book: Book) : BookDetailUiState()
    data class Error(val message: String) : BookDetailUiState()
}