package com.example.app.utils

import com.example.app.modules.CustomersApi
import kotlinx.coroutines.runBlocking

fun CustomersApi.createCustomer() =
    runBlocking {
        createCustomer(
            call = createMockApplicationCall(),
        )
    }
