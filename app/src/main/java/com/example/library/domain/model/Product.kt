package com.example.library.domain.model

data class Product(
    val id: Long,
    val name: String,
    val material: String,
    val description: String,
    val imageUrl: String?,
    val price: Double,
    val dimensions: String,
    val rating: Double,
    val inStock: Boolean,
    val category: String,
    val color: String,
    val isInCart: Boolean = false
)