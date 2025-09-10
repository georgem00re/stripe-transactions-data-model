package com.example.app.database

import com.example.app.generated.models.ListPaymentIntentsSuccessResponsePaymentIntentsInner
import java.io.Closeable
import java.sql.Connection
import java.util.UUID

interface PaymentIntentRepository : Closeable {
    fun createPaymentIntent(orderId: UUID, amountGbx: Long, stripeId: String): UUID
    fun listPaymentIntents(): List<ListPaymentIntentsSuccessResponsePaymentIntentsInner>
}

class PostgresPaymentIntentRepository(
    private val connection: Connection = PooledDataSource.dataSource.connection,
) : PaymentIntentRepository {
    override fun close() {
        connection.close()
    }

    override fun createPaymentIntent(orderId: UUID, amountGbx: Long, stripeId: String): UUID {
        val result = connection.prepareStatement(
            """
                INSERT INTO payment_intent(id, order_id, amount_gbx, stripe_id)
                VALUES (?::uuid, ?::uuid, ?, ?)
                RETURNING id
            """.trimIndent()
        ).apply {
            setUUID(1, UUID.randomUUID())
            setUUID(2, orderId)
            setLong(3, amountGbx)
            setString(4, stripeId)
        }.executeQuery()

        check(result.next())
        return result.getUUID("id")
    }

    override fun listPaymentIntents(): List<ListPaymentIntentsSuccessResponsePaymentIntentsInner> {
        val result = connection.prepareStatement(
            """
                SELECT * FROM payment_intent
            """.trimIndent()
        ).executeQuery()

        return buildList {
            while (result.next()) {
                add(
                    ListPaymentIntentsSuccessResponsePaymentIntentsInner(
                        paymentIntentId = result.getUUID("id"),
                        orderId = result.getUUID("order_id"),
                        amountGbx = result.getLong("amount_gbx"),
                        stripeId = result.getString("stripe_id")
                    )
                )
            }
        }
    }
}
