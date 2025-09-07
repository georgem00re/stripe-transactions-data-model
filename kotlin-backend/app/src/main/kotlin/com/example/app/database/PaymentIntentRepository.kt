package com.example.app.database

import java.io.Closeable
import java.sql.Connection
import java.util.UUID

interface PaymentIntentRepository : Closeable {
    fun createPaymentIntent(orderId: UUID, amountGbx: Int): UUID
}

class PostgresPaymentIntentRepository(
    private val connection: Connection = PooledDataSource.dataSource.connection,
) : PaymentIntentRepository {
    override fun close() {
        connection.close()
    }

    override fun createPaymentIntent(orderId: UUID, amountGbx: Int): UUID {
        val result = connection.prepareStatement(
            """
                INSERT INTO payment_intent(id, order_id, amount_gbx)
                VALUES (?::uuid, ?::uuid, ?)
                RETURNING id
            """.trimIndent()
        ).apply {
            setUUID(1, UUID.randomUUID())
            setUUID(2, orderId)
            setInt(3, amountGbx)
        }.executeQuery()

        check(result.next())
        return result.getUUID("id")
    }
}
