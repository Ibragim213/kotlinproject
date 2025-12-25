package com.example.library.domain.usecase

import com.example.library.data.repository.ProductRepository
import com.example.library.domain.model.Product
import javax.inject.Inject

class GetProductUseCase @Inject constructor(
    private val repository: ProductRepository  // ДОЛЖЕН быть ProductRepository, не BookRepository!
) {
    suspend operator fun invoke(id: Long): Result<Product> {
        return repository.getProductById(id)
    }
}