package com.example.library.domain.usecase

import com.example.library.data.repository.ProductRepository
import com.example.library.domain.model.User
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(id: Long): Result<User> {
        return repository.getUser(id)
    }
}