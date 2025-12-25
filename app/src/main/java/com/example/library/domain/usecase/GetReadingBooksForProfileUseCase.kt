package com.example.library.domain.usecase

import com.example.library.data.repository.ProductRepository
import com.example.library.domain.model.Product
import javax.inject.Inject

class GetCartItemsForProfileUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(userId: Long): Result<List<Product>> {
        return repository.getCartItemsForProfile(userId)
    }
}