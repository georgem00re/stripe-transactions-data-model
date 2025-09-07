package com.example.app.modules

import com.example.app.database.PostgresProductRepository
import com.example.app.database.ProductRepository
import com.example.app.generated.apis.ProductsApiModule
import com.example.app.generated.models.CreateProductRequestBody
import com.example.app.generated.models.CreateProductSuccessResponse
import com.example.app.infrastructure.koin.KtorKoinComponent
import io.ktor.server.application.ApplicationCall
import org.koin.core.component.get
import org.koin.dsl.module

val productsApiModule =
    module {
        single<ProductsApiModule> { ProductsApi() }
        factory<ProductRepository> { PostgresProductRepository() }
    }

class ProductsApi : ProductsApiModule, KtorKoinComponent() {
    override suspend fun createProduct(
        call: ApplicationCall,
        data: CreateProductRequestBody,
    ): CreateProductSuccessResponse {
        val productId = get<ProductRepository>().use { productRepository ->
            productRepository.createProduct(
                priceGbx = data.priceGbx
            )
        }

        return CreateProductSuccessResponse(productId)
    }
}
