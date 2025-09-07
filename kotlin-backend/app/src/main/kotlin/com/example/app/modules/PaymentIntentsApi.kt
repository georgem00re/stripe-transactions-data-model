package com.example.app.modules

import com.example.app.BadRequestError
import com.example.app.database.OrderRepository
import com.example.app.database.PaymentIntentRepository
import com.example.app.database.PostgresPaymentIntentRepository
import com.example.app.generated.apis.PaymentIntentsApiModule
import com.example.app.generated.models.CreatePaymentIntentRequestBody
import com.example.app.generated.models.CreatePaymentIntentSuccessResponse
import com.example.app.infrastructure.koin.KtorKoinComponent
import io.ktor.server.application.ApplicationCall
import org.koin.core.component.get
import org.koin.dsl.module

val paymentIntentsApiModule =
    module {
        single<PaymentIntentsApiModule> { PaymentIntentsApi() }
        factory<PaymentIntentRepository> { PostgresPaymentIntentRepository() }
    }

class OrderCostDiscrepancy : BadRequestError("The payment amount does not match the order total")

class PaymentIntentsApi : PaymentIntentsApiModule, KtorKoinComponent() {
    override suspend fun createPaymentIntent(
        call: ApplicationCall,
        data: CreatePaymentIntentRequestBody,
    ): CreatePaymentIntentSuccessResponse {
        val orderId = data.orderId
        val amountGpx = data.amountGbx

        val orderCost = get<OrderRepository>().use { orderRepository ->
            orderRepository.getOrderCost(orderId)
        }
        if (orderCost != amountGpx) throw OrderCostDiscrepancy()

        val paymentIntentId = get<PaymentIntentRepository>().use { paymentIntentRepository ->
            paymentIntentRepository.createPaymentIntent(
                orderId = orderId,
                amountGbx = data.amountGbx,
            )
        }
        return CreatePaymentIntentSuccessResponse(paymentIntentId)
    }
}
