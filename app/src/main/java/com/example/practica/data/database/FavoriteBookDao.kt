// file: src/main/java/com/example/practica/data/database/FavoriteBookDao.kt
package com.example.practica.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteBookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorites(book: FavoriteBookEntity)

    @Query("DELETE FROM favorite_books WHERE bookId = :bookId")
    suspend fun removeFromFavorites(bookId: Int)

    @Query("SELECT * FROM favorite_books")
    suspend fun getAllFavorites(): List<FavoriteBookEntity>

    @Query("SELECT * FROM favorite_books")
    fun getAllFavoritesFlow(): Flow<List<FavoriteBookEntity>>

    @Query("SELECT COUNT(*) > 0 FROM favorite_books WHERE bookId = :bookId")
    suspend fun isFavorite(bookId: Int): Boolean
}