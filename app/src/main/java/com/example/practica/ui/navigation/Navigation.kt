package com.example.practica.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.practica.data.Book
import com.example.practica.ui.screens.BookDetailScreen
import com.example.practica.ui.screens.BookListScreen
import com.example.practica.ui.screens.FavoritesScreen
import com.example.practica.ui.screens.ProfileScreen
import com.google.gson.Gson

sealed class Screen(val route: String) {
    object BookList : Screen("book_list")
    object Favorites : Screen("favorites")
    object Profile : Screen("profile")
    object BookDetail : Screen("book_detail/{bookJson}") {
        fun passBook(book: Book): String {
            val bookJson = Gson().toJson(book)
            return "book_detail/${bookJson}"
        }
    }
}

@Composable
fun AppNavigation(modifier: Modifier = Modifier, navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavHost(
        navController = navController,
        startDestination = Screen.BookList.route,
        modifier = modifier
    ) {
        composable(Screen.BookList.route) {
            BookListScreen(
                navController = navController,
                onBookClick = { book ->
                    navController.navigate(Screen.BookDetail.passBook(book))
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
            arguments = listOf(navArgument("bookJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val bookJson = backStackEntry.arguments?.getString("bookJson") ?: return@composable
            val book = Gson().fromJson(bookJson, Book::class.java)
            BookDetailScreen(
                navController = navController,
                book = book
            )
        }
    }
}