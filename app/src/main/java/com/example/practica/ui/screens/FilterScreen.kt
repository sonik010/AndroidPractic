package com.example.practica.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.practica.ui.viewmodel.FilterViewModel
import com.example.practica.ui.viewmodel.FilterViewModelFactory



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel: FilterViewModel = viewModel(
        factory = FilterViewModelFactory(context))
    val settings by viewModel.settings.collectAsState()
    var genre by remember { mutableStateOf(settings.genre) }
    var minRating by remember { mutableStateOf(settings.minRating) }
    var year by remember { mutableStateOf(settings.year) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Фильтры") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        genre = ""
                        minRating = 0
                        year = 0
                    }) { Text("Сбросить") }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = genre,
                onValueChange = { genre = it },
                label = { Text("Жанр") },
                placeholder = { Text("Например: fantasy, fiction") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = if (minRating == 0) "" else minRating.toString(),
                onValueChange = { minRating = it.toIntOrNull() ?: 0 },
                label = { Text("Мин. рейтинг (0-5)") },
                placeholder = { Text("0") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = if (year == 0) "" else year.toString(),
                onValueChange = { year = it.toIntOrNull() ?: 0 },
                label = { Text("Год издания (от)") },
                placeholder = { Text("0") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.saveFilter(genre, minRating, year)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Применить")
            }
        }
    }
}