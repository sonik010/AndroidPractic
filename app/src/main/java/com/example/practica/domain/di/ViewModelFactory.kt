package com.example.practica.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.practica.data.repository.BookRepository
import com.example.practica.domain.usecase.SearchBooksUseCase
import com.example.practica.ui.viewmodel.BookDetailViewModel
import com.example.practica.ui.viewmodel.BookListViewModel

class ViewModelFactory(
    private val searchBooksUseCase: SearchBooksUseCase,
    private val bookRepository: BookRepository  // ← добавляем репозиторий
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(BookListViewModel::class.java) -> {
                BookListViewModel(searchBooksUseCase) as T
            }
            modelClass.isAssignableFrom(BookDetailViewModel::class.java) -> {
                BookDetailViewModel(bookRepository) as T  // ← передаём репозиторий
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}