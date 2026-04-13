package com.example.practica.data.repository

import com.example.practica.data.datastore.FilterSettings
import com.example.practica.data.remote.RetrofitClient
import com.example.practica.data.remote.toBook
import com.example.practica.domain.model.Book
import com.example.practica.domain.repository.IBookRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.UnknownHostException

class BookRepository : IBookRepository {

    override suspend fun searchBooks(query: String, filters: FilterSettings): Result<List<Book>> = withContext(Dispatchers.IO) {
        try {
            if (query.isBlank()) return@withContext Result.success(emptyList())

            val response = RetrofitClient.api.searchBooks(query = query)
            val books = response.books?.mapNotNull { bookDoc ->
                try {
                    bookDoc.toBook()
                } catch (e: Exception) { null }
            }?.filter { book ->
                // Применяем фильтры
                (filters.genre.isBlank() || book.genre.contains(filters.genre, ignoreCase = true)) &&
                        (filters.minRating == 0 || book.rating >= filters.minRating) &&
                        (filters.year == 0 || book.year >= filters.year)
            } ?: emptyList()

            Result.success(books)
        } catch (e: Exception) {
            Result.failure(Exception("Ошибка: ${e.message}"))
        }
    }
}