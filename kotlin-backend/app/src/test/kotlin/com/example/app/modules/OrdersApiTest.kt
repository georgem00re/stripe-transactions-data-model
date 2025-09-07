package com.example.app.modules

import com.example.app.database.CustomerRepository
import com.example.app.database.OrderRepository
import com.example.app.database.PostgresCustomerRepository
import com.example.app.database.PostgresOrderRepository
import com.example.app.database.PostgresProductRepository
import com.example.app.database.ProductRepository
import com.example.app.database.TestDatabase
import com.example.app.generated.models.ListOrdersSuccessResponseOrdersInnerProductsInner
import com.example.app.infrastructure.koin.KoinContext
import com.example.app.utils.createCustomer
import com.example.app.utils.createOrder
import com.example.app.utils.createProduct
import com.example.app.utils.listOrders
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrdersApiTest : KoinTest {
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
                    factory<OrderRepository> { PostgresOrderRepository() }
                    factory<CustomerRepository> { PostgresCustomerRepository() }
                    factory<ProductRepository> { PostgresProductRepository() }
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
    fun `Orders can be created and listed`() {
        val customerId = get<CustomersApi>().createCustomer()
        val productId = get<ProductsApi>().createProduct(10)

        val orderId = get<OrdersApi>().createOrder(
            customerId = customerId,
            productIds = listOf(productId),
        )

        val orders = get<OrdersApi>().listOrders()
        assertEquals(1, orders.size)

        val order = orders.first()
        assertEquals(customerId, order.customerId)
        assertEquals(orderId, order.orderId)

        val orderProducts = order.products
        assertEquals(1, orderProducts.size)

        val orderProduct = orderProducts.first()
        assertEquals(productId, orderProduct.productId)
        assertEquals(ListOrdersSuccessResponseOrdersInnerProductsInner.PaymentStatus.Unpaid, orderProduct.paymentStatus)
    }
}
