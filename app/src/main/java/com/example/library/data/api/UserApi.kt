package com.example.library.data.api

import com.example.library.data.dto.AuthRequestDto
import com.example.library.data.dto.AuthResponseDto
import com.example.library.data.dto.ProductDto
import com.example.library.data.dto.UserDto
import retrofit2.Response
import retrofit2.http.*

interface UserApi {
    @POST("api/users/register")
    suspend fun register(@Body request: AuthRequestDto): Response<AuthResponseDto>

    @POST("api/users/login")
    suspend fun login(@Body request: AuthRequestDto): Response<AuthResponseDto>

    @GET("api/users/{id}")
    suspend fun getUserById(@Path("id") id: Long): Response<UserDto>

    @POST("api/users/{userId}/cart/{productId}")
    suspend fun toggleCartStatus(
        @Path("userId") userId: Long,
        @Path("productId") productId: Long,
        @Query("addToCart") addToCart: Boolean = true
    ): Response<UserDto>

    @GET("api/users/{userId}/cart")
    suspend fun getCartItems(@Path("userId") userId: Long): Response<List<ProductDto>>

    @GET("api/users/{userId}/cart/{productId}/status")
    suspend fun getCartStatus(
        @Path("userId") userId: Long,
        @Path("productId") productId: Long
    ): Response<Map<String, Any>>

    @GET("api/users/{userId}/cart-items")
    suspend fun getCartItemsForProfile(@Path("userId") userId: Long): Response<List<ProductDto>>
}