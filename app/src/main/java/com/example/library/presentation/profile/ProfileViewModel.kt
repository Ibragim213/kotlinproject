package com.example.library.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.library.data.auth.AuthManager
import com.example.library.domain.model.Product
import com.example.library.domain.model.User
import com.example.library.domain.usecase.GetCartItemsForProfileUseCase
import com.example.library.domain.usecase.GetUserUseCase
import com.example.library.presentation.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val getCartItemsForProfileUseCase: GetCartItemsForProfileUseCase,  // ← ИСПРАВЛЕНО
    private val authManager: AuthManager
) : ViewModel() {

    private val _userState = MutableStateFlow<UiState<User>>(UiState.Loading)
    val userState: StateFlow<UiState<User>> = _userState.asStateFlow()

    private val _cartItemsState = MutableStateFlow<UiState<List<Product>>>(UiState.Loading)  // ← ИЗМЕНИТЬ имя
    val cartItemsState: StateFlow<UiState<List<Product>>> = _cartItemsState.asStateFlow()    // ← ИЗМЕНИТЬ имя

    private val _isLoggedIn = MutableStateFlow(authManager.isLoggedIn)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        if (authManager.isLoggedIn) {
            loadUser()
            loadCartItems()  // ← ИЗМЕНИТЬ имя метода
        }
    }

    fun loadUser() {
        viewModelScope.launch {
            if (!authManager.isLoggedIn) {
                _isLoggedIn.value = false
                return@launch
            }

            _userState.value = UiState.Loading
            try {
                val currentUser = authManager.currentUser
                if (currentUser != null) {
                    val result = getUserUseCase(currentUser.id)
                    _userState.value = if (result.isSuccess) {
                        UiState.Success(result.getOrThrow())
                    } else {
                        UiState.Success(currentUser)
                    }
                } else {
                    _userState.value = UiState.Error("Пользователь не найден")
                    _isLoggedIn.value = false
                }
            } catch (e: Exception) {
                _userState.value = UiState.Error("Ошибка загрузки профиля")
            }
        }
    }

    fun loadCartItems() {  // ← ИЗМЕНИТЬ имя метода
        viewModelScope.launch {
            val userId = authManager.getCurrentUserId()
            if (userId == 0L) return@launch

            _cartItemsState.value = UiState.Loading  // ← ИСПРАВИТЬ имя
            try {
                val result = getCartItemsForProfileUseCase(userId)  // ← ИСПРАВЛЕНО имя Use Case
                _cartItemsState.value = if (result.isSuccess) {     // ← ИСПРАВИТЬ имя
                    UiState.Success(result.getOrThrow())
                } else {
                    UiState.Error("Ошибка загрузки корзины")  // ← ИЗМЕНИТЬ сообщение
                }
            } catch (e: Exception) {
                _cartItemsState.value = UiState.Error("Ошибка: ${e.message}")
            }
        }
    }

    fun logout() {
        authManager.logout()
        _isLoggedIn.value = false
        _userState.value = UiState.Loading
        _cartItemsState.value = UiState.Loading  // ← ИСПРАВИТЬ имя
    }

    fun updateLoginStatus() {
        _isLoggedIn.value = authManager.isLoggedIn
        if (authManager.isLoggedIn) {
            loadUser()
            loadCartItems()  // ← ИЗМЕНИТЬ имя метода
        }
    }
}