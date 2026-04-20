package com.example.practica.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface OpenLibraryApi {
    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("limit") limit: Int = 20,
        @Query("fields") fields: String = "key,title,author_name,first_publish_year,number_of_pages_median,subject,ratings_average,cover_i,first_sentence"
    ): SearchResponse
}