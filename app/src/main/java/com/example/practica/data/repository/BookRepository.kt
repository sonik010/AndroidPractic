package com.example.practica.data.repository

import com.example.practica.data.remote.RetrofitClient
import com.example.practica.data.remote.toBook
import com.example.practica.data.utils.safeApiCall
import com.example.practica.domain.model.Book
import com.example.practica.domain.repository.IBookRepository

class BookRepository : IBookRepository {

    override suspend fun searchBooks(query: String): Result<List<Book>> = safeApiCall {
        if (query.isBlank()) {
            return@safeApiCall emptyList()
        }

        val response = RetrofitClient.api.searchBooks(query = query)
        response.books?.mapNotNull { bookDoc ->
            try {
                bookDoc.toBook()
            } catch (e: Exception) {
                null
            }
        } ?: emptyList()
    }
}