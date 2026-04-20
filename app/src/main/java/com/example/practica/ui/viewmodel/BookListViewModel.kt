// BookListViewModel.kt
package com.example.practica.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practica.domain.model.Book
import com.example.practica.domain.usecase.SearchBooksUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookListViewModel(
    private val searchBooksUseCase: SearchBooksUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow<BookListUiState>(BookListUiState.Initial)
    val uiState: StateFlow<BookListUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun searchBooks() {
        val query = _searchQuery.value
        if (query.isBlank()) {
            _uiState.value = BookListUiState.Initial
            return
        }

        // Отменяем предыдущий поиск (debounce)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500) // Debounce

            _uiState.value = BookListUiState.Loading

            val result = searchBooksUseCase(query)

            _uiState.value = when {
                result.isSuccess -> {
                    val books = result.getOrNull() ?: emptyList()
                    if (books.isEmpty()) BookListUiState.Empty
                    else BookListUiState.Success(books)
                }
                result.isFailure -> {
                    val error = result.exceptionOrNull()
                    BookListUiState.Error(error?.message ?: "Неизвестная ошибка")
                }
                else -> BookListUiState.Error("Неизвестная ошибка")
            }
        }
    }
}

sealed class BookListUiState {
    object Initial : BookListUiState()
    object Loading : BookListUiState()
    object Empty : BookListUiState()
    data class Success(val books: List<Book>) : BookListUiState()
    data class Error(val message: String) : BookListUiState()
}