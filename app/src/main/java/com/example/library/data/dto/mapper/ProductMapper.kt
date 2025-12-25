package com.example.library.data.dto.mapper

import com.example.library.data.dto.ProductDto
import com.example.library.domain.model.Product

fun ProductDto.toProduct(): Product {
    return Product(
        id = id,
        name = name,
        material = "Ткань/Кожа", // дефолтное значение
        description = description,
        imageUrl = imageUrl,
        price = price,
        dimensions = "Глубина: 85см × Ширина: 80см × Высота: 100см",
        rating = 4.5, // дефолтный рейтинг
        inStock = isAvailable ?: true,
        category = category,
        color = "Различные цвета"
    )
}