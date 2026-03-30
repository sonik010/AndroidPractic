package com.example.practica.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practica.domain.model.Book
import com.example.practica.domain.repository.IBookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookDetailViewModel(
    private val repository: IBookRepository
) : ViewModel() {

    companion object {
        private const val TAG = "BookDetailViewModel"
    }

    private val _uiState = MutableStateFlow<BookDetailUiState>(BookDetailUiState.Loading)
    val uiState: StateFlow<BookDetailUiState> = _uiState.asStateFlow()

    private var cachedBooks = mutableMapOf<Int, Book>()

    fun loadBook(bookId: Int) {
        Log.d(TAG, "loadBook called with ID: $bookId")

        // Проверяем кэш
        cachedBooks[bookId]?.let {
            Log.d(TAG, "Book found in cache: ${it.title}")
            _uiState.value = BookDetailUiState.Success(it)
            return
        }

        _uiState.value = BookDetailUiState.Loading

        viewModelScope.launch {
            // Нужно найти книгу по ID. Так как API не поддерживает поиск по ID,
            // мы будем искать по названию, но для демо используем прямой поиск
            // Временно: ищем книгу по ID через поиск всех книг по популярным запросам
            val popularQueries = listOf("the", "harry potter", "lord of the rings", "1984")

            for (query in popularQueries) {
                Log.d(TAG, "Searching with query: $query")
                val result = repository.searchBooks(query)
                if (result.isSuccess) {
                    val books = result.getOrNull() ?: emptyList()
                    val book = books.find { it.id == bookId }
                    if (book != null) {
                        Log.d(TAG, "Book found: ${book.title}")
                        cachedBooks[bookId] = book
                        _uiState.value = BookDetailUiState.Success(book)
                        return@launch
                    }
                }
            }

            // Если не нашли
            Log.d(TAG, "Book not found with ID: $bookId")
            _uiState.value = BookDetailUiState.Error("Книга не найдена. Попробуйте найти её через поиск.")
        }
    }
}

sealed class BookDetailUiState {
    object Loading : BookDetailUiState()
    data class Success(val book: Book) : BookDetailUiState()
    data class Error(val message: String) : BookDetailUiState()
}