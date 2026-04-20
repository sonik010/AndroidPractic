package com.example.practica.domain.usecase

import com.example.practica.domain.model.Book
import com.example.practica.domain.repository.IBookRepository

class SearchBooksUseCase(
    private val repository: IBookRepository
) {
    suspend operator fun invoke(query: String): Result<List<Book>> {
        return repository.searchBooks(query)
    }
}