package com.example.app.database

import setUUID
import java.io.Closeable
import java.sql.Connection
import java.util.UUID

interface CustomerRepository : Closeable {
    fun createCustomer(): UUID
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
}
