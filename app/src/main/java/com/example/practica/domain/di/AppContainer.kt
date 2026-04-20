package com.example.practica.di

import com.example.practica.data.repository.BookRepository
import com.example.practica.domain.repository.IBookRepository
import com.example.practica.domain.usecase.SearchBooksUseCase

class AppContainer {
    val bookRepository: IBookRepository = BookRepository()
    val searchBooksUseCase: SearchBooksUseCase = SearchBooksUseCase(bookRepository as BookRepository)
    val viewModelFactory: ViewModelFactory by lazy {
        ViewModelFactory(searchBooksUseCase, bookRepository as BookRepository)
    }
}