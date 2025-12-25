package com.example.library.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")  // ← НОВЫЙ ЭКРАН
    object Auth : Screen("auth")
    object Catalog : Screen("catalog")
    object Profile : Screen("profile")
    object ProductDetail : Screen("product_detail/{productId}") {
        const val routeWithArgs = "product_detail/{productId}"
        const val argProductId = "productId"
        fun createRoute(productId: Long) = "product_detail/$productId"
    }
}