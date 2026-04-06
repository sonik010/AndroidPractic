// file: src/main/java/com/example/practica/ui/viewmodel/BookDetailViewModelFactory.kt
package com.example.practica.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.practica.data.database.AppDatabase
import com.example.practica.data.repository.BookRepository
import com.example.practica.data.repository.FavoritesRepository

class BookDetailViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Создаем базу данных Room
        val database = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "favorites_db"
        ).build()

        // Создаем репозитории
        val bookRepository = BookRepository()
        val favoritesRepository = FavoritesRepository(database)

        // Возвращаем ViewModel
        return BookDetailViewModel(bookRepository, favoritesRepository) as T
    }
}