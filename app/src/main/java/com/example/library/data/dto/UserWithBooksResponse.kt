package com.example.library.data.dto

data class UserWithProductsResponse(
    val user: UserDto,
    val cartItems: List<ProductDto>
)