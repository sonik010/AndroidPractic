package com.example.practica.domain.repository

import com.example.practica.data.datastore.FilterSettings
import com.example.practica.domain.model.Book

interface IBookRepository {
    suspend fun searchBooks(query: String, filters: FilterSettings = FilterSettings("", 0, 0)): Result<List<Book>>
}