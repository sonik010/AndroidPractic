package com.example.practica.data.repository

import com.example.practica.data.remote.RetrofitClient
import com.example.practica.data.remote.toBook
import com.example.practica.domain.model.Book
import com.example.practica.domain.repository.IBookRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.UnknownHostException

class BookRepository : IBookRepository {

    override suspend fun searchBooks(query: String): Result<List<Book>> = withContext(Dispatchers.IO) {
        try {
            if (query.isBlank()) {
                return@withContext Result.success(emptyList())
            }

            val response = RetrofitClient.api.searchBooks(query = query)
            val books = response.books?.mapNotNull { bookDoc ->
                try {
                    bookDoc.toBook()
                } catch (e: Exception) {
                    null
                }
            } ?: emptyList()

            Result.success(books)
        } catch (e: UnknownHostException) {
            Result.failure(Exception("Нет подключения к интернету. Проверьте соединение."))
        } catch (e: IOException) {
            Result.failure(Exception("Ошибка сети: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Ошибка загрузки данных: ${e.message}"))
        }
    }
}