package com.example.practica.domain.repository

import com.example.practica.domain.model.Book

interface IBookRepository {
    suspend fun searchBooks(query: String): Result<List<Book>>
}