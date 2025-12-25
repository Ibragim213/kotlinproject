package com.example.library.domain.usecase

import com.example.library.data.repository.ProductRepository
import javax.inject.Inject

class ToggleCartUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend fun toggleCartStatus(userId: Long, productId: Long, isInCart: Boolean): Result<Boolean> {
        return try {
            val result = if (isInCart) {
                repository.removeFromCart(userId, productId)
            } else {
                repository.addToCart(userId, productId)
            }

            if (result.isSuccess) {
                Result.success(!isInCart)
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Ошибка изменения корзины"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}