package com.example.library.domain.usecase

import com.example.library.data.repository.ProductRepository
import com.example.library.domain.model.User
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        username: String,
        phone: String,
        name: String
    ): Result<User> {
        return repository.register(email, password, username, phone, name)
    }
}