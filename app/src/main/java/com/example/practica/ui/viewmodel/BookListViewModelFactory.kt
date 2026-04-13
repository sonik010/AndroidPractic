package com.example.practica.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.practica.data.datastore.DataStoreSingleton
import com.example.practica.data.datastore.FilterPreferences
import com.example.practica.data.repository.BookRepository
import com.example.practica.domain.usecase.SearchBooksUseCase

class BookListViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository = BookRepository()
        val useCase = SearchBooksUseCase(repository)
        val dataStore = DataStoreSingleton.getInstance(context)
        val filterPreferences = FilterPreferences(dataStore)
        return BookListViewModel(useCase, filterPreferences) as T
    }
}