package com.example.app.modules

import com.example.app.database.CustomerRepository
import com.example.app.database.OrderRepository
import com.example.app.database.PaymentIntentRepository
import com.example.app.database.PostgresCustomerRepository
import com.example.app.database.PostgresOrderRepository
import com.example.app.database.PostgresPaymentIntentRepository
import com.example.app.database.PostgresProductRepository
import com.example.app.database.ProductRepository
import com.example.app.database.TestDatabase
import com.example.app.infrastructure.koin.KoinContext
import com.example.app.integrations.stripe.StripePayments
import com.example.app.utils.createCustomer
import com.example.app.utils.createMockStripePayments
import com.example.app.utils.createOrder
import com.example.app.utils.createPaymentIntent
import com.example.app.utils.createProduct
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.assertNotNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PaymentIntentsApiTest : KoinTest {
    private val testDatabase = TestDatabase()

    @BeforeEach
    fun beforeEach() {
        testDatabase.emptyDatabase()
        startKoin {
            modules(
                module {
                    single { OrdersApi() }
                    single { CustomersApi() }
                    single { ProductsApi() }
                    single { PaymentIntentsApi() }
                    factory<StripePayments> { createMockStripePayments() }
                    factory<OrderRepository> { PostgresOrderRepository() }
                    factory<CustomerRepository> { PostgresCustomerRepository() }
                    factory<ProductRepository> { PostgresProductRepository() }
                    factory<PaymentIntentRepository> { PostgresPaymentIntentRepository() }
                },
            )
        }.let {
            KoinContext.setContext(it)
        }
    }

    @AfterEach
    fun afterEach() {
        stopKoin()
    }

    @Test
    fun `Payment intents can be created`() {
        val customerId = get<CustomersApi>().createCustomer()
        val productAmountGbx = 10L
        val productId = get<ProductsApi>().createProduct(productAmountGbx)

        val orderId = get<OrdersApi>().createOrder(
            customerId = customerId,
            productIds = listOf(productId),
        )
        val paymentIntentId = get<PaymentIntentsApi>().createPaymentIntent(
            orderId = orderId,
            amountGbx = productAmountGbx
        )
        assertNotNull(paymentIntentId)
    }
}
