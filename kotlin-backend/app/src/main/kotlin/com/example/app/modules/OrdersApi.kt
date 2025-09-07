package com.example.app.modules

import com.example.app.database.OrderRepository
import com.example.app.database.PostgresOrderRepository
import com.example.app.generated.apis.OrdersApiModule
import com.example.app.generated.models.CreateOrderRequestBody
import com.example.app.generated.models.CreateOrderSuccessResponse
import com.example.app.infrastructure.koin.KtorKoinComponent
import io.ktor.server.application.ApplicationCall
import org.koin.core.component.get
import org.koin.dsl.module

val ordersApiModule =
    module {
        single<OrdersApiModule> { OrdersApi() }
        factory<OrderRepository> { PostgresOrderRepository() }
    }

class OrdersApi : OrdersApiModule, KtorKoinComponent() {
    override suspend fun createOrder(
        call: ApplicationCall,
        data: CreateOrderRequestBody,
    ): CreateOrderSuccessResponse {
        val orderId = get<OrderRepository>().use { orderRepository ->
            orderRepository.createOrderWithOrderLines(
                customerId = data.customerId,
                productIds = data.products.map { it.productId }
            )
        }
        return CreateOrderSuccessResponse(orderId)
    }
}
