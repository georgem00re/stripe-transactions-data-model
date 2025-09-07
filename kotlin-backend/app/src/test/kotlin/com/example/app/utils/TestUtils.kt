package com.example.app.utils

import com.example.app.generated.models.CreateOrderRequestBody
import com.example.app.generated.models.CreateOrderRequestBodyProductsInner
import com.example.app.generated.models.CreateProductRequestBody
import com.example.app.modules.CustomersApi
import com.example.app.modules.OrdersApi
import com.example.app.modules.ProductsApi
import kotlinx.coroutines.runBlocking
import java.util.UUID

fun CustomersApi.createCustomer() =
    runBlocking {
        createCustomer(
            call = createMockApplicationCall(),
        ).customerId
    }

fun CustomersApi.listCustomers() =
    runBlocking {
        listCustomers(
            call = createMockApplicationCall(),
        ).customers
    }

fun ProductsApi.createProduct(priceGbx: Int) =
    runBlocking {
        createProduct(
            call = createMockApplicationCall(),
            data = CreateProductRequestBody(
                priceGbx = priceGbx,
            )
        ).productId
    }

fun ProductsApi.listProducts() =
    runBlocking {
        listProducts(
            call = createMockApplicationCall(),
        ).products
    }

fun OrdersApi.createOrder(customerId: UUID, productIds: List<UUID>) = runBlocking {
    createOrder(
        call = createMockApplicationCall(),
        data = CreateOrderRequestBody(
            customerId = customerId,
            products = productIds.map {
                CreateOrderRequestBodyProductsInner(it)
            }
        )
    ).orderId
}
