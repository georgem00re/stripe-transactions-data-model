package com.example.app.modules

import CustomerRepository
import PostgresCustomerRepository
import com.example.app.generated.apis.CustomersApiModule
import com.example.app.generated.models.CreateCustomerSuccessResponse
import com.example.app.infrastructure.koin.KtorKoinComponent
import io.ktor.server.application.ApplicationCall
import org.koin.dsl.module
import org.koin.core.component.get

val customersApiModule =
    module {
        single<CustomersApiModule> { CustomersApi() }
        factory<CustomerRepository> { PostgresCustomerRepository() }
    }

class CustomersApi : CustomersApiModule, KtorKoinComponent() {
    override suspend fun createCustomer(call: ApplicationCall): CreateCustomerSuccessResponse {
        val customerId = get<CustomerRepository>().use { customerRepository ->
            customerRepository.createCustomer()
        }
        return CreateCustomerSuccessResponse(customerId)
    }
}
