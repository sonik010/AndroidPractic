package com.example.practica.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practica.data.datastore.FilterPreferences
import com.example.practica.data.datastore.FilterSettings
import com.example.practica.domain.model.Book
import com.example.practica.domain.usecase.SearchBooksState
import com.example.practica.domain.usecase.SearchBooksUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class BookListViewModel(
    private val searchBooksUseCase: SearchBooksUseCase,
    private val filterPreferences: FilterPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow<BookListUiState>(BookListUiState.Initial)
    val uiState: StateFlow<BookListUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filters = MutableStateFlow(FilterSettings())
    val filters: StateFlow<FilterSettings> = _filters.asStateFlow()

    init {
        filterPreferences.filterFlow.onEach { filters ->
            _filters.value = filters
            searchBooks()
        }.launchIn(viewModelScope)
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun searchBooks() {
        val query = _searchQuery.value
        if (query.isBlank()) {
            _uiState.value = BookListUiState.Empty
            return
        }

        viewModelScope.launch {
            searchBooksUseCase(query, _filters.value).collect { state ->
                when (state) {
                    is SearchBooksState.Loading -> {
                        _uiState.value = BookListUiState.Loading
                    }
                    is SearchBooksState.Success -> {
                        _uiState.value = BookListUiState.Success(state.books)
                    }
                    is SearchBooksState.Empty -> {
                        _uiState.value = BookListUiState.Empty
                    }
                    is SearchBooksState.Error -> {
                        _uiState.value = BookListUiState.Error(state.message)
                    }
                }
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