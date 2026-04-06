// file: src/main/java/com/example/practica/ui/screens/FavoritesScreen.kt
package com.example.practica.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.room.Room
import androidx.compose.ui.platform.LocalContext
import com.example.practica.data.database.AppDatabase
import com.example.practica.data.repository.FavoritesRepository
import com.example.practica.ui.viewmodel.FavoritesViewModel
import com.example.practica.ui.viewmodel.FavoritesViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    navController: NavController? = null
) {
    val context = LocalContext.current

    // Создаем Room database синглтон
    val database = remember {
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "favorites_db"
        ).build()
    }

    val repository = remember { FavoritesRepository(database) }

    // Используем фабрику для создания ViewModel
    val viewModel: FavoritesViewModel = viewModel(
        factory = FavoritesViewModelFactory(repository)
    )

    val favorites by viewModel.favorites.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Загружаем избранное при первом запуске
    LaunchedEffect(Unit) {
        viewModel.loadFavorites()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Избранное") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            favorites.isEmpty() -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Нет избранных книг")
            }
            else -> LazyColumn(
                modifier = Modifier.padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(favorites) { book ->
                    BookListItem(
                        book = book,
                        onClick = { navController?.navigate("book_detail/${book.id}") }
                    )
                }
            }
        }
    }
}