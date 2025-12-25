package com.example.library.data.repository


import com.example.library.data.api.ProductApi
import com.example.library.data.api.UserApi
import com.example.library.data.dto.AuthRequestDto
import com.example.library.data.dto.mapper.toProduct  // ← ДОБАВИТЬ
import com.example.library.data.dto.mapper.toUser     // ← ДОБАВИТЬ
import com.example.library.domain.model.Product
import com.example.library.domain.model.User
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productApi: ProductApi,
    private val userApi: UserApi
) : ProductRepository {

    private var cachedProducts: List<Product>? = null

    override suspend fun getProducts(): Result<List<Product>> {
        return try {
            val response = productApi.getProducts()
            if (response.isSuccessful) {
                val products = response.body()?.map { it.toProduct() } ?: emptyList()
                cachedProducts = products
                Result.success(products)
            } else {
                Result.failure(Exception("Ошибка загрузки товаров: ${response.code()}"))
            }
        } catch (e: Exception) {
            cachedProducts?.let {
                return Result.success(it)
            }
            Result.failure(e)
        }
    }

    override suspend fun getProductById(id: Long): Result<Product> {
        return try {
            val response = productApi.getProductById(id)
            if (response.isSuccessful) {
                val product = response.body()?.toProduct()
                if (product != null) {
                    Result.success(product)
                } else {
                    Result.failure(Exception("Товар не найден"))
                }
            } else {
                Result.failure(Exception("Ошибка загрузки товара: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProductsByCategory(category: String): Result<List<Product>> {
        return try {
            val response = productApi.getProductsByCategory(category)
            if (response.isSuccessful) {
                val products = response.body()?.map { it.toProduct() } ?: emptyList()
                Result.success(products)
            } else {
                Result.failure(Exception("Ошибка загрузки категории: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUser(id: Long): Result<User> {
        return try {
            val response = userApi.getUserById(id)
            if (response.isSuccessful) {
                val user = response.body()?.toUser()
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("Пользователь не найден"))
                }
            } else {
                Result.failure(Exception("Ошибка загрузки пользователя: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.success(
                User(
                    id = id,
                    username = "user",
                    name = "Пользователь",
                    email = "user@example.com",
                    phone = "+7 (999) 000-00-00",
                    cartItems = emptyList()
                )
            )
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        username: String,
        phone: String,
        name: String
    ): Result<User> {
        return try {
            val request = AuthRequestDto(
                email = email,
                password = password,
                username = username,
                phone = phone,
                name = name
            )

            val response = userApi.register(request)
            if (response.isSuccessful) {
                val authResponse = response.body()
                val user = authResponse?.user?.toUser()
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("Ошибка регистрации"))
                }
            } else {
                Result.failure(Exception("Ошибка регистрации: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val request = AuthRequestDto(
                email = email,
                password = password
            )

            val response = userApi.login(request)
            if (response.isSuccessful) {
                val authResponse = response.body()
                val user = authResponse?.user?.toUser()
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("Ошибка входа"))
                }
            } else {
                Result.failure(Exception("Ошибка входа: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addToCart(userId: Long, productId: Long): Result<User> {
        return try {
            val response = userApi.toggleCartStatus(userId, productId, true)
            if (response.isSuccessful) {
                val userDto = response.body()
                val user = userDto?.toUser()
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("Ошибка парсинга ответа"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                if (response.code() == 400 && errorBody?.contains("уже") == true) {
                    Result.success(
                        User(
                            id = userId,
                            username = "temp",
                            name = "Temp",
                            email = "temp@example.com",
                            phone = "",
                            cartItems = emptyList()
                        )
                    )
                } else {
                    Result.failure(Exception("Ошибка: ${response.code()} - $errorBody"))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun removeFromCart(userId: Long, productId: Long): Result<User> {
        return try {
            val response = userApi.toggleCartStatus(userId, productId, false)
            if (response.isSuccessful) {
                val user = response.body()?.toUser()
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("Ошибка удаления из корзины"))
                }
            } else {
                Result.failure(Exception("Ошибка удаления из корзины: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCartItems(userId: Long): Result<List<Product>> {
        return try {
            val response = userApi.getCartItems(userId)
            if (response.isSuccessful) {
                val products = response.body()?.map { it.toProduct() } ?: emptyList()
                Result.success(products)
            } else {
                Result.failure(Exception("Ошибка загрузки корзины: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isInCart(userId: Long, productId: Long): Result<Boolean> {
        return try {
            val response = userApi.getCartStatus(userId, productId)
            if (response.isSuccessful) {
                val body = response.body()
                val isInCart = body?.get("isInCart") as? Boolean ?: false
                Result.success(isInCart)
            } else {
                Result.failure(Exception("Ошибка получения статуса: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCartItemsForProfile(userId: Long): Result<List<Product>> {
        return try {
            val response = userApi.getCartItemsForProfile(userId)
            if (response.isSuccessful) {
                val products = response.body()?.map { it.toProduct() } ?: emptyList()
                Result.success(products)
            } else {
                Result.failure(Exception("Ошибка загрузки корзины: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}