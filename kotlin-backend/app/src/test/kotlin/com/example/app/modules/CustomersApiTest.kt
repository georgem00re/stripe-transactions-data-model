package com.example.app.modules

import com.example.app.database.TestDatabase
import com.example.app.infrastructure.koin.KoinContext
import com.example.app.utils.createCustomer
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
class CustomersApiTest : KoinTest {
    private val testDatabase = TestDatabase()

    @BeforeEach
    fun beforeEach() {
        testDatabase.emptyDatabase()
        startKoin {
            modules(
                module {
                    single { CustomersApi() }
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
    fun `Customers can be created`() {
        val customerId = get<CustomersApi>().createCustomer()
        assertNotNull(customerId)
    }
}
