package com.example.practica.data.remote

import com.google.gson.annotations.SerializedName
import com.example.practica.domain.model.Book as DomainBook

data class SearchResponse(
    @SerializedName("docs")
    val books: List<BookDoc>? = emptyList(),
    @SerializedName("num_found")
    val totalCount: Int = 0
)

data class BookDoc(
    @SerializedName("key")
    val key: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("author_name")
    val authorNames: List<String>?,
    @SerializedName("first_publish_year")
    val firstPublishYear: Int?,
    @SerializedName("number_of_pages_median")
    val pages: Int?,
    @SerializedName("subject")
    val genres: List<String>?,
    @SerializedName("ratings_average")
    val rating: Double?,
    @SerializedName("cover_i")
    val coverId: Int?,
    @SerializedName("first_sentence")
    val firstSentence: List<String>?
)

fun BookDoc.toBook(): DomainBook {
    return DomainBook(
        id = key?.hashCode() ?: System.currentTimeMillis().hashCode(),
        openLibraryId = key ?: "",
        title = title,
        author = authorNames?.joinToString(", "),
        year = firstPublishYear,
        pages = pages,
        genre = genres?.firstOrNull(),
        description = firstSentence?.firstOrNull(),
        rating = rating?.toFloat(),
        coverUrl = coverId?.takeIf { it != 0 }?.let {
            "https://covers.openlibrary.org/b/id/$it-M.jpg"
        }
    )
}