package com.example.app.database

import com.example.app.generated.models.ListCustomersSuccessResponseCustomersInner
import setUUID
import java.io.Closeable
import java.sql.Connection
import java.util.UUID

interface CustomerRepository : Closeable {
    fun createCustomer(): UUID
    fun getCustomers(): List<ListCustomersSuccessResponseCustomersInner>
}

class PostgresCustomerRepository(
    private val connection: Connection = PooledDataSource.dataSource.connection,
) : CustomerRepository {
    override fun close() {
        connection.close()
    }

    override fun createCustomer(): UUID {
        val result = connection.prepareStatement(
            """
            INSERT INTO customer(id)
            VALUES (?::uuid)
            RETURNING id
            """.trimIndent(),
        ).apply {
            setUUID(1, UUID.randomUUID())
        }.executeQuery()

        check(result.next())
        return result.getUUID("id")
    }

    override fun getCustomers(): List<ListCustomersSuccessResponseCustomersInner> {
        val result = connection.prepareStatement(
            """
                SELECT * FROM customer
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
