package com.example.library.presentation.product  // ← ИЗМЕНИТЬ

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.library.R
import com.example.library.domain.model.Product
import com.example.library.presentation.ui.UiState

@Composable
fun ProductDetailScreen(  // ← ИЗМЕНИТЬ
    productId: Long,  // ← ИЗМЕНИТЬ
    onBackClick: () -> Unit,
    onNavigateToAuth: () -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel()  // ← ИЗМЕНИТЬ
) {
    val productState by viewModel.productState.collectAsState()  // ← ИЗМЕНИТЬ
    val isInCart by viewModel.isInCart.collectAsState()  // ← ИЗМЕНИТЬ
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)  // ← ИЗМЕНИТЬ
    }

    when (productState) {
        is UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is UiState.Error -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = (productState as UiState.Error).message)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.loadProduct(productId) }) {
                    Text("Попробовать снова")
                }
            }
        }

        is UiState.Success<*> -> {
            val product = (productState as UiState.Success<Product>).data  // ← ИЗМЕНИТЬ
            ProductDetailContent(  // ← ИЗМЕНИТЬ
                product = product,
                isInCart = isInCart,  // ← ИЗМЕНИТЬ
                isLoading = isLoading,
                onToggleCart = { viewModel.toggleCart() },  // ← ИЗМЕНИТЬ
                onBackClick = onBackClick,
                onNavigateToAuth = onNavigateToAuth
            )
        }

        else -> {
            Box(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
fun ProductDetailContent(  // ← ИЗМЕНИТЬ
    product: Product,
    isInCart: Boolean,  // ← ИЗМЕНИТЬ
    isLoading: Boolean,
    onToggleCart: () -> Unit,  // ← ИЗМЕНИТЬ
    onBackClick: () -> Unit,
    onNavigateToAuth: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        TextButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.Start)
        ) {
            Text("← Назад в каталог")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(MaterialTheme.shapes.medium)
        ) {
            if (product.imageUrl!!.isNotEmpty()) {  // ← ИЗМЕНИТЬ
                Image(
                    painter = rememberAsyncImagePainter(product.imageUrl),  // ← ИЗМЕНИТЬ
                    contentDescription = "Изображение дивана",  // ← ИЗМЕНИТЬ
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.book_placeholder),
                    contentDescription = "Нет изображения",  // ← ИЗМЕНИТЬ
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = product.name,  // ← ИЗМЕНИТЬ
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Материал: ${product.material}",  // ← ИЗМЕНИТЬ
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )

        Row(
            modifier = Modifier.padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "★ ${product.rating}",  // ← ИЗМЕНИТЬ
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "${product.price} руб.",  // ← ИЗМЕНИТЬ
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = product.dimensions,  // ← ИЗМЕНИТЬ
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Text(
            text = "Описание:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 24.dp)
        )

        Text(
            text = product.description,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onToggleCart,  // ← ИЗМЕНИТЬ
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Обработка...")
            } else {
                Text(
                    text = if (isInCart) "Убрать из корзины" else "Добавить в корзину",  // ← ИЗМЕНИТЬ
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        if (isInCart) {  // ← ИЗМЕНИТЬ
            Text(
                text = "В корзине",  // ← ИЗМЕНИТЬ
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}