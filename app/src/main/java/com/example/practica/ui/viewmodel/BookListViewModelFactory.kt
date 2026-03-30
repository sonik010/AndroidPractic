package com.example.practica.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.practica.data.repository.BookRepository
import com.example.practica.domain.usecase.SearchBooksUseCase

object BookListViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository = BookRepository()
        val useCase = SearchBooksUseCase(repository)
        return BookListViewModel(useCase) as T
    }
}