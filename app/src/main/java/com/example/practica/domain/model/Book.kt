package com.example.practica.domain.model

data class Book(
    val id: Int,
    val openLibraryId: String,
    val title: String,
    val author: String,
    val year: Int,
    val pages: Int,
    val genre: String,
    val description: String,
    val rating: Float,
    val coverUrl: String?
)