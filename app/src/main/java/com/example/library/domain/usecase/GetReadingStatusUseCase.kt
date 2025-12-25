package com.example.library.domain.usecase

import com.example.library.data.repository.ProductRepository
import javax.inject.Inject

class GetCartStatusUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(userId: Long, productId: Long): Result<Boolean> {
        return repository.isInCart(userId, productId)
    }
}