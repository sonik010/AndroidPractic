package com.example.practica.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.practica.R
import com.example.practica.ui.navigation.Screen
import androidx.compose.runtime.getValue

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("Книги", Screen.BookList.route, R.drawable.ic_book),
        BottomNavItem("Избранное", Screen.Favorites.route, R.drawable.ic_favorite),
        BottomNavItem("Профиль", Screen.Profile.route, R.drawable.ic_profile)
    )

    NavigationBar(
        tonalElevation = 8.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                        navController.navigate(item.route) {
                            // Избегаем множественных копий стека
                            popUpTo(Screen.BookList.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                }
            )
        }
    }
}

data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: Int
)