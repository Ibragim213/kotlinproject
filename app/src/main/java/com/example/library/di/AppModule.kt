package com.example.library.di

import android.content.Context
import com.example.library.data.api.ProductApi
import com.example.library.data.api.UserApi
import com.example.library.data.auth.AuthManager
import com.example.library.data.repository.ProductRepository
import com.example.library.data.repository.ProductRepositoryImpl
import com.example.library.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // 1. HTTP и Retrofit
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8089/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 2. API интерфейсы
    @Provides
    @Singleton
    fun provideProductApi(retrofit: Retrofit): ProductApi {
        return retrofit.create(ProductApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    // 3. Auth Manager
    @Provides
    @Singleton
    fun provideAuthManager(@ApplicationContext context: Context): AuthManager {
        return AuthManager(context)
    }

    // 4. РЕПОЗИТОРИЙ - самое важное!
    @Provides
    @Singleton
    fun provideProductRepository(
        productApi: ProductApi,
        userApi: UserApi
    ): ProductRepository {
        return ProductRepositoryImpl(productApi, userApi)
    }

    // 5. USE CASES - все должны быть здесь!
    @Provides
    @Singleton
    fun provideGetProductsUseCase(repository: ProductRepository): GetProductsUseCase {
        return GetProductsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetProductUseCase(repository: ProductRepository): GetProductUseCase {
        return GetProductUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetUserUseCase(repository: ProductRepository): GetUserUseCase {
        return GetUserUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideLoginUseCase(repository: ProductRepository): LoginUseCase {
        return LoginUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideRegisterUseCase(repository: ProductRepository): RegisterUseCase {
        return RegisterUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideToggleCartUseCase(repository: ProductRepository): ToggleCartUseCase {
        return ToggleCartUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetCartStatusUseCase(repository: ProductRepository): GetCartStatusUseCase {
        return GetCartStatusUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetCartItemsForProfileUseCase(repository: ProductRepository): GetCartItemsForProfileUseCase {
        return GetCartItemsForProfileUseCase(repository)
    }
}