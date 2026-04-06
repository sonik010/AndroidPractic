package com.example.practica.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.practica.domain.model.Book
import com.example.practica.ui.viewmodel.BookDetailUiState
import com.example.practica.ui.viewmodel.BookDetailViewModel
import androidx.compose.ui.platform.LocalContext
import com.example.practica.ui.viewmodel.BookDetailViewModelFactory


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    navController: NavController,
    bookId: Int
) {

    val context = LocalContext.current
    val viewModel: BookDetailViewModel = viewModel(
        factory = BookDetailViewModelFactory(context)
    )

    val uiState by viewModel.uiState.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()

    LaunchedEffect(bookId) {
        viewModel.loadBook(bookId)
        viewModel.checkFavoriteStatus(bookId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    when (uiState) {
                        is BookDetailUiState.Success -> Text((uiState as BookDetailUiState.Success).book.title.take(20))
                        else -> Text("Детали книги")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    when (uiState) {
                        is BookDetailUiState.Success -> {
                            val book = (uiState as BookDetailUiState.Success).book
                            IconButton(onClick = { viewModel.toggleFavorite(book) }) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = if (isFavorite) "Удалить из избранного" else "Добавить в избранное",
                                    tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        else -> {}
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        when (uiState) {
            is BookDetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is BookDetailUiState.Success -> {
                val book = (uiState as BookDetailUiState.Success).book
                BookDetailContent(
                    book = book,
                    modifier = Modifier.padding(paddingValues),
                    onFavoriteClick = { viewModel.toggleFavorite(book) },
                    isFavorite = isFavorite
                )
            }
            is BookDetailUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = (uiState as BookDetailUiState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        IconButton(onClick = { viewModel.loadBook(bookId) }) {
                            Text("Повторить")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookDetailContent(
    book: Book,
    modifier: Modifier = Modifier,
    onFavoriteClick: () -> Unit,
    isFavorite: Boolean
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Обложка книги
                if (book.coverUrl != null) {
                    AsyncImage(
                        model = book.coverUrl,
                        contentDescription = "Обложка ${book.title}",
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📖", fontSize = 48.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = book.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = book.author,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Рейтинг",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format("%.1f / 5.0", book.rating),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = book.year.toString(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Год", fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = book.pages.toString(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Страниц", fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = book.genre,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text("Жанр", fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Описание",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = book.description,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        item {
            Button(
                onClick = onFavoriteClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFavorite) Color.Red else MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isFavorite) "Удалить из избранного" else "Добавить в избранное")
            }
        }
    }
}