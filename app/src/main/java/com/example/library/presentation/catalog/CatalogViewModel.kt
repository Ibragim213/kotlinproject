package com.example.library.presentation.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.library.domain.model.Product
import com.example.library.domain.usecase.GetProductsUseCase  // ← ИЗМЕНИТЬ импорт
import com.example.library.presentation.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase  // ← ИЗМЕНИТЬ имя и тип
) : ViewModel() {

    private val _productsState = MutableStateFlow<UiState<List<Product>>>(UiState.Loading)
    val productsState: StateFlow<UiState<List<Product>>> = _productsState.asStateFlow()

    init {
        loadProducts()  // ← ИЗМЕНИТЬ имя метода
    }

    fun loadProducts() {  // ← ИЗМЕНИТЬ имя метода
        viewModelScope.launch {
            _productsState.value = UiState.Loading
            try {
                val result = getProductsUseCase()  // ← Вызов правильного Use Case
                _productsState.value = if (result.isSuccess) {
                    UiState.Success(result.getOrThrow())
                } else {
                    UiState.Error("Ошибка загрузки товаров")
                }
            } catch (e: Exception) {
                _productsState.value = UiState.Error("Ошибка сети")
            }
        }
    }

    fun refreshProducts() {  // ← ИЗМЕНИТЬ имя метода
        loadProducts()
    }
}