package com.example.practica.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.practica.ui.screens.BookDetailScreen
import com.example.practica.ui.screens.BookListScreen
import com.example.practica.ui.screens.FavoritesScreen
import com.example.practica.ui.screens.ProfileScreen

sealed class Screen(val route: String) {
    object BookList : Screen("book_list")
    object Favorites : Screen("favorites")
    object Profile : Screen("profile")
    object BookDetail : Screen("book_detail/{bookId}") {
        fun passBook(bookId: Int): String {
            return "book_detail/$bookId"
        }
    }
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.BookList.route,
        modifier = modifier
    ) {
        composable(Screen.BookList.route) {
            BookListScreen(
                navController = navController,
                onBookClick = { book ->
                    navController.navigate(Screen.BookDetail.passBook(book.id))
                }
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen()
        }

        composable(Screen.Profile.route) {
            ProfileScreen()
        }

        composable(
            route = Screen.BookDetail.route,
            arguments = listOf(navArgument("bookId") { type = NavType.IntType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getInt("bookId") ?: return@composable
            BookDetailScreen(
                navController = navController,
                bookId = bookId
            )
        }
    }
}