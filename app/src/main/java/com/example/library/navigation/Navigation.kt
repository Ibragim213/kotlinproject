package com.example.library.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.library.presentation.auth.AuthScreen
import com.example.library.presentation.catalog.CatalogScreen
import com.example.library.presentation.home.HomeScreen  // ← ИМПОРТ НОВОГО ЭКРАНА
import com.example.library.presentation.product.ProductDetailScreen
import com.example.library.presentation.profile.ProfileScreen
import androidx.compose.material3.Scaffold

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            when (currentRoute) {
                Screen.Home.route,
                Screen.Catalog.route,
                Screen.Profile.route -> BottomNavigationBar(navController = navController)
                else -> {}
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,  // ← ИЗМЕНИТЬ НА ГЛАВНУЮ
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = Screen.Home.route) {
                HomeScreen(
                    navController = navController
                )
            }
            composable(route = Screen.Catalog.route) {
                CatalogScreen(
                    onProductClick = { productId ->
                        navController.navigate(Screen.ProductDetail.createRoute(productId))
                    },
                    onNavigateToProfile = {
                        navController.navigate(Screen.Profile.route)
                    },
                    onNavigateToAuth = {
                        navController.navigate(Screen.Auth.route)
                    }
                )
            }
            composable(
                route = Screen.ProductDetail.routeWithArgs,
                arguments = listOf(
                    navArgument(Screen.ProductDetail.argProductId) {
                        type = NavType.LongType
                    }
                )
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getLong(Screen.ProductDetail.argProductId) ?: 0L
                ProductDetailScreen(
                    productId = productId,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToAuth = {
                        navController.navigate(Screen.Auth.route)
                    }
                )
            }
            composable(route = Screen.Profile.route) {
                ProfileScreen(
                    onNavigateToAuth = {
                        navController.navigate(Screen.Auth.route)
                    }
                )
            }
            composable(route = Screen.Auth.route) {
                AuthScreen(
                    onLoginSuccess = {
                        navController.popBackStack()
                        navController.navigate(Screen.Profile.route) {
                            popUpTo(Screen.Home.route) {  // ← ИЗМЕНИТЬ НА HOME
                                inclusive = false
                            }
                        }
                    }
                )
            }
        }
    }
}