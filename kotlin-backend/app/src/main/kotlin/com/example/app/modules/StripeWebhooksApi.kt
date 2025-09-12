package com.example.app.modules

import com.example.app.BadRequestError
import com.example.app.generated.apis.StripeWebhooksApiModule
import com.example.app.generated.models.SuccessResponse
import com.example.app.infrastructure.koin.KtorKoinComponent
import com.example.app.integrations.stripe.StripeConfig
import com.example.app.integrations.stripe.getStripeConfig
import com.stripe.net.Webhook
import io.ktor.server.application.ApplicationCall
import org.koin.dsl.module
import org.koin.core.component.get

val stripeWebhooksApiModule = module {
    single<StripeWebhooksApiModule> { StripeWebhooksApi() }
    single<StripeConfig> { getStripeConfig() }
}

class MissingStripeSignature : BadRequestError("Missing 'Stripe-Signature' header")

class StripeWebhooksApi : StripeWebhooksApiModule, KtorKoinComponent() {
    override suspend fun receiveStripeEvent(
        call: ApplicationCall,
        data: String,
    ): SuccessResponse {
        val signatureHeader = call.request.headers["Stripe-Signature"] ?: throw MissingStripeSignature()
        val stripeEvent = Webhook.constructEvent(data, signatureHeader, get<StripeConfig>().signingSecret)

        return SuccessResponse(true)
    }
}
