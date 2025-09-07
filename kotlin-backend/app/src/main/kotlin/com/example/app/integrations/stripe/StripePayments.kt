package com.example.app.integrations.stripe

import com.stripe.Stripe
import com.stripe.model.PaymentIntent
import com.stripe.param.PaymentIntentCreateParams

const val CURRENCY = "gbp"

interface StripePayments {
    fun createPaymentIntent(amountGbx: Long): String
}

class StripePaymentsImpl(stripeSecretKey: String) : StripePayments {
    init {
        Stripe.apiKey = stripeSecretKey
    }
    override fun createPaymentIntent(amountGbx: Long): String {
        return PaymentIntent.create(
            PaymentIntentCreateParams.Builder()
                .setAmount(amountGbx)
                .setCurrency(CURRENCY)
                .build()
        ).id
    }
}
