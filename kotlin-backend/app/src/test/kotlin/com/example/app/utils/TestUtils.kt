package com.example.app.utils

import com.example.app.generated.models.CreateProductRequestBody
import com.example.app.modules.CustomersApi
import com.example.app.modules.ProductsApi
import kotlinx.coroutines.runBlocking

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
