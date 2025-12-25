package com.example.library.data.api

import com.example.library.data.dto.ProductDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductApi {
    @GET("api/products")
    suspend fun getProducts(): Response<List<ProductDto>>

    @GET("api/products/{id}")
    suspend fun getProductById(@Path("id") id: Long): Response<ProductDto>

    @GET("api/products/category/{category}")
    suspend fun getProductsByCategory(@Path("category") category: String): Response<List<ProductDto>>
}