package com.example.app.database

import com.example.app.generated.models.ListCustomersSuccessResponseCustomersInner
import java.io.Closeable
import java.sql.Connection
import java.util.UUID

interface ProductRepository : Closeable {
    fun createProduct(priceGbx: Int): UUID
    fun listProducts(): List<ListCustomersSuccessResponseCustomersInner>
}

class PostgresProductRepository(
    private val connection: Connection = PooledDataSource.dataSource.connection,
) : ProductRepository {
    override fun close() {
        connection.close()
    }
    override fun createProduct(priceGbx: Int): UUID {
        val result = connection.prepareStatement(
            """
            INSERT INTO product(id, price_gbx)
            VALUES (?::uuid, ?)
            RETURNING id
            """.trimIndent(),
        ).apply {
            setUUID(1, UUID.randomUUID())
            setInt(2, priceGbx)
        }.executeQuery()

        check(result.next())
        return result.getUUID("id")
    }

    override fun listProducts(): List<ListCustomersSuccessResponseCustomersInner> {
        val result = connection.prepareStatement(
            """
                SELECT * FROM product
            """.trimIndent()
        ).executeQuery()

        return buildList {
            while (result.next()) {
                add(
                    ListCustomersSuccessResponseCustomersInner(
                        id = result.getUUID("id")
                    )
                )
            }
        }
    }
}
