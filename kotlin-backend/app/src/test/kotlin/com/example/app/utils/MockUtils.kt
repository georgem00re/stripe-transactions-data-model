package com.example.app.utils

import com.example.app.integrations.stripe.StripePayments
import io.ktor.http.ParametersBuilder
import io.ktor.server.application.ApplicationCall
import io.mockk.every
import io.mockk.mockk

fun createMockApplicationCall(): ApplicationCall {
    val mockCall = mockk<ApplicationCall>()
    every { mockCall.request.queryParameters } returns ParametersBuilder().build()
    return mockCall
}

fun createMockStripePayments(
    stripePaymentIntentId: String = exampleStripePaymentIntentId
): StripePayments {
    val mockStripePayments = mockk<StripePayments>()

    every {
        mockStripePayments.createPaymentIntent(any())
    } returns stripePaymentIntentId

    return mockStripePayments
}
