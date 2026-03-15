package com.example.practica.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.practica.ui.components.BottomNavigationBar
import com.example.practica.ui.navigation.AppNavigation

@Composable
fun BookListApp() {
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            AppNavigation(
                navController = navController,
                modifier = Modifier.weight(1f)
            )
            BottomNavigationBar(navController = navController)
        }
    }
}