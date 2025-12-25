package com.example.library.presentation.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.library.data.auth.AuthManager
import com.example.library.domain.model.Product
import com.example.library.domain.usecase.GetCartStatusUseCase
import com.example.library.domain.usecase.GetProductUseCase
import com.example.library.domain.usecase.ToggleCartUseCase
import com.example.library.presentation.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getProductUseCase: GetProductUseCase,
    private val toggleCartUseCase: ToggleCartUseCase,
    private val getCartStatusUseCase: GetCartStatusUseCase,
    private val authManager: AuthManager
) : ViewModel() {

    private val _productState = MutableStateFlow<UiState<Product>>(UiState.Loading)
    val productState: StateFlow<UiState<Product>> = _productState.asStateFlow()

    private val _isInCart = MutableStateFlow(false)
    val isInCart: StateFlow<Boolean> = _isInCart.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var currentProductId: Long = 0L

    fun loadProduct(productId: Long) {
        currentProductId = productId
        viewModelScope.launch {
            _productState.value = UiState.Loading
            try {
                val productResult = getProductUseCase(productId)
                if (productResult.isSuccess) {
                    val product = productResult.getOrThrow()
                    _productState.value = UiState.Success(product)
                    loadCartStatus()
                } else {
                    _productState.value = UiState.Error("Товар не найден")
                }
            } catch (e: Exception) {
                _productState.value = UiState.Error("Ошибка: ${e.message}")
            }
        }
    }

    fun toggleCart() {
        val userId = authManager.getCurrentUserId()
        if (userId == 0L) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentStatus = _isInCart.value
                val result = toggleCartUseCase.toggleCartStatus(userId, currentProductId, currentStatus)

                if (result.isSuccess) {
                    val newStatus = result.getOrThrow()
                    _isInCart.value = newStatus
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadCartStatus() {
        val userId = authManager.getCurrentUserId()
        if (userId == 0L) return

        try {
            val result = getCartStatusUseCase(userId, currentProductId)
            if (result.isSuccess) {
                _isInCart.value = result.getOrThrow()
            }
        } catch (e: Exception) {
            // Игнорируем ошибку
        }
    }
}