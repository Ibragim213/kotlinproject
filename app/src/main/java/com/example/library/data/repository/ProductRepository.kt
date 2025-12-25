package com.example.library.data.repository

import com.example.library.domain.model.Product
import com.example.library.domain.model.User

interface ProductRepository {
    suspend fun getProducts(): Result<List<Product>>
    suspend fun getProductById(id: Long): Result<Product>
    suspend fun getProductsByCategory(category: String): Result<List<Product>>
    suspend fun getUser(id: Long): Result<User>
    suspend fun register(email: String, password: String, username: String, phone: String, name: String): Result<User>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun addToCart(userId: Long, productId: Long): Result<User>
    suspend fun removeFromCart(userId: Long, productId: Long): Result<User>
    suspend fun getCartItems(userId: Long): Result<List<Product>>
    suspend fun isInCart(userId: Long, productId: Long): Result<Boolean>
    suspend fun getCartItemsForProfile(userId: Long): Result<List<Product>>
}