package com.example.library.data.dto.mapper

import com.example.library.data.dto.UserDto
import com.example.library.domain.model.User

fun UserDto.toUser(): User {
    return User(
        id = id,
        username = username,
        name = name,
        email = email,
        phone = phone,
        cartItems = cartItems ?: emptyList()
    )
}