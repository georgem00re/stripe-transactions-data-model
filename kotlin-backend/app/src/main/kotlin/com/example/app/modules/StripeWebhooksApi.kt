package com.example.app.modules

import com.example.app.generated.apis.StripeWebhooksApiModule
import com.example.app.generated.models.SuccessResponse
import com.example.app.infrastructure.koin.KtorKoinComponent
import com.example.app.integrations.stripe.StripeConfig
import com.example.app.integrations.stripe.getStripeConfig
import io.ktor.server.application.ApplicationCall
import org.koin.dsl.module

val stripeWebhooksApiModule = module {
    single<StripeWebhooksApiModule> { StripeWebhooksApi() }
    single<StripeConfig> { getStripeConfig() }
}

class StripeWebhooksApi : StripeWebhooksApiModule, KtorKoinComponent() {
    override suspend fun receiveStripeEvent(
        call: ApplicationCall,
        data: String,
    ): SuccessResponse {
        return SuccessResponse(true)
    }
}
