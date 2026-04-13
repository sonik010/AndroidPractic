package com.example.practica.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_books")
data class FavoriteBookEntity(
    @PrimaryKey
    val bookId: Int,
    val title: String,
    val author: String,
    val coverUrl: String?,
    val rating: Float,
    val year: Int
)