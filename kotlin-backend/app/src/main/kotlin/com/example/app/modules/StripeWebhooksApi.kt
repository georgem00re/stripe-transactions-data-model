package com.example.app.modules

import com.example.app.BadRequestError
import com.example.app.database.PooledDataSource
import com.example.app.database.PostgresOrderRepository
import com.example.app.database.PostgresPaymentIntentRepository
import com.example.app.database.withTransaction
import com.example.app.generated.apis.StripeWebhooksApiModule
import com.example.app.generated.models.SuccessResponse
import com.example.app.infrastructure.koin.KtorKoinComponent
import com.example.app.integrations.stripe.StripeConfig
import com.example.app.integrations.stripe.getStripeConfig
import com.stripe.model.PaymentIntent
import com.stripe.net.Webhook
import io.ktor.server.application.ApplicationCall
import org.koin.core.component.get
import org.koin.dsl.module
import org.slf4j.LoggerFactory

val stripeWebhooksApiModule = module {
    single<StripeWebhooksApiModule> { StripeWebhooksApi() }
    single<StripeConfig> { getStripeConfig() }
}

class MissingStripeSignature : BadRequestError("Missing 'Stripe-Signature' header")
class PaymentIntentNotFound(stripeId: String) : Exception("Could not find a payment intent with Stripe ID $stripeId")

class StripeWebhooksApi : StripeWebhooksApiModule, KtorKoinComponent() {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun receiveStripeEvent(
        call: ApplicationCall,
        data: String,
    ): SuccessResponse {
        val signatureHeader = call.request.headers["Stripe-Signature"] ?: throw MissingStripeSignature()
        val stripeEvent = Webhook.constructEvent(data, signatureHeader, get<StripeConfig>().signingSecret)
        logger.info("Received an event from Stripe of type ${stripeEvent.type}")
        logger.debug("stripeEvent.apiVersion: {}", stripeEvent.apiVersion)

        val eventDeserialiser = stripeEvent.dataObjectDeserializer
        val stripeObject = eventDeserialiser.`object`.orElse(null)

        when (stripeEvent.type) {
            "payment_intent.succeeded" -> {
                val paymentIntent = stripeObject as PaymentIntent
                val paymentIntentStripeId = paymentIntent.id
                logger.debug("paymentIntentStripeId, {}", paymentIntentStripeId)

                PooledDataSource.dataSource.connection.use { connection ->
                    connection.withTransaction {
                        val orderId = PostgresPaymentIntentRepository(connection)
                            .getPaymentIntentByStripeId(paymentIntentStripeId)?.orderId
                                ?: throw PaymentIntentNotFound(paymentIntentStripeId)

                        PostgresOrderRepository(connection).markOrderAsPaid(orderId)
                    }
                }
            }
        }
        return SuccessResponse(true)
    }
}
