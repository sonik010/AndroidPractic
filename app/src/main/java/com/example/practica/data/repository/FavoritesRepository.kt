package com.example.practica.data.repository

import com.example.practica.data.database.AppDatabase
import com.example.practica.data.database.FavoriteBookEntity
import com.example.practica.domain.model.Book
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FavoritesRepository(
    private val database: AppDatabase
) {

    private val dao = database.favoriteBookDao()

    suspend fun addToFavorites(book: Book) = withContext(Dispatchers.IO) {
        val entity = FavoriteBookEntity(
            bookId = book.id,
            title = book.title,
            author = book.author,
            coverUrl = book.coverUrl,
            rating = book.rating,
            year = book.year
        )
        dao.addToFavorites(entity)
    }

    suspend fun removeFromFavorites(bookId: Int) = withContext(Dispatchers.IO) {
        dao.removeFromFavorites(bookId)
    }

    suspend fun getAllFavorites(): List<Book> = withContext(Dispatchers.IO) {
        dao.getAllFavorites().map { entity ->
            Book(
                id = entity.bookId,
                openLibraryId = "",
                title = entity.title,
                author = entity.author,
                year = entity.year,
                pages = 0,
                genre = "",
                description = "",
                rating = entity.rating,
                coverUrl = entity.coverUrl
            )
        }
    }

    suspend fun isFavorite(bookId: Int): Boolean = withContext(Dispatchers.IO) {
        dao.isFavorite(bookId)
    }
}