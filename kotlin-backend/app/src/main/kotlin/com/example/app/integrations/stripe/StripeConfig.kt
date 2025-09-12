package com.example.app.integrations.stripe

data class StripeConfig(
    val secretKey: String,
    val signingSecret: String
)

private const val SECRET_KEY = "STRIPE_SECRET_KEY"
private const val SIGNING_SECRET = "STRIPE_WEBHOOK_SIGNING_SECRET"

class MissingStripeEnvironmentVariable(key: String) : Exception("Missing Stripe environment variable: $key")

fun getStripeConfig(): StripeConfig {
    return StripeConfig(
        secretKey = System.getenv(SECRET_KEY) ?: throw MissingStripeEnvironmentVariable(SECRET_KEY),
        signingSecret = System.getenv(SIGNING_SECRET) ?: throw MissingStripeEnvironmentVariable(SIGNING_SECRET)
    )
}
