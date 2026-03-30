package com.example.practica.domain.usecase

import com.example.practica.domain.model.Book
import com.example.practica.domain.repository.IBookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchBooksUseCase(
    private val repository: IBookRepository
) {
    operator fun invoke(query: String): Flow<SearchBooksState> = flow {
        emit(SearchBooksState.Loading)

        val result = repository.searchBooks(query)

        when {
            result.isSuccess -> {
                val books = result.getOrNull() ?: emptyList()
                if (books.isEmpty()) {
                    emit(SearchBooksState.Empty)
                } else {
                    emit(SearchBooksState.Success(books))
                }
            }
            result.isFailure -> {
                emit(SearchBooksState.Error(result.exceptionOrNull()?.message ?: "Неизвестная ошибка"))
            }
        }
    }
}

sealed class SearchBooksState {
    object Loading : SearchBooksState()
    object Empty : SearchBooksState()
    data class Success(val books: List<Book>) : SearchBooksState()
    data class Error(val message: String) : SearchBooksState()
}