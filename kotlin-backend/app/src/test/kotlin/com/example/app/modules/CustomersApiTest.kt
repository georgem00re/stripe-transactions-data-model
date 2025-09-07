package com.example.app.modules

import com.example.app.database.CustomerRepository
import com.example.app.database.PostgresCustomerRepository
import com.example.app.database.TestDatabase
import com.example.app.infrastructure.koin.KoinContext
import com.example.app.utils.createCustomer
import com.example.app.utils.listCustomers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomersApiTest : KoinTest {
    private val testDatabase = TestDatabase()

    @BeforeEach
    fun beforeEach() {
        testDatabase.emptyDatabase()
        startKoin {
            modules(
                module {
                    single { CustomersApi() }
                    factory<CustomerRepository> { PostgresCustomerRepository() }
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
    fun `Customers can be created and retrieved`() {
        val customerId = get<CustomersApi>().createCustomer()

        val customers = get<CustomersApi>().listCustomers()
        assertEquals(1, customers.size)

        val customer = customers.first()
        assertEquals(customerId, customer.id)
    }
}
