package com.example.app.modules

import com.example.app.database.PostgresProductRepository
import com.example.app.database.ProductRepository
import com.example.app.database.TestDatabase
import com.example.app.infrastructure.koin.KoinContext
import com.example.app.utils.createProduct
import com.example.app.utils.listProducts
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
class ProductsApiTest : KoinTest {
    private val testDatabase = TestDatabase()

    @BeforeEach
    fun beforeEach() {
        testDatabase.emptyDatabase()
        startKoin {
            modules(
                module {
                    single { ProductsApi() }
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
    fun `Products can be created and listed`() {
        val productId = get<ProductsApi>().createProduct(999)

        val products = get<ProductsApi>().listProducts()
        assertEquals(1, products.size)

        val product = products.first()
        assertEquals(productId, product.id)
    }
}
