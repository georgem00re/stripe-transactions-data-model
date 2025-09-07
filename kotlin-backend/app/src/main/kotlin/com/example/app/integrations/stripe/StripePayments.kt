package com.example.app.integrations.stripe

import com.stripe.Stripe
import com.stripe.model.PaymentIntent
import com.stripe.param.PaymentIntentCreateParams

object StripePayments {
    init {
        Stripe.apiKey = System.getenv("STRIPE_SECRET_KEY") ?: throw Exception("Missing variable STRIPE_SECRET_KEY")
    }
    fun createPaymentIntent(amountGbx: Long): PaymentIntent {
        return PaymentIntent.create(
            PaymentIntentCreateParams.Builder()
                .setAmount(amountGbx)
                .setCurrency("gbp")
                .build()
        )
    }
}
