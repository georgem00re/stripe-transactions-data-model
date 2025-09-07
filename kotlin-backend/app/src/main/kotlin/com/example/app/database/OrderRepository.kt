package com.example.app.database

import setPaymentStatus
import setUUID
import java.io.Closeable
import java.sql.Connection
import java.util.UUID

interface OrderRepository : Closeable {
    fun createOrderWithOrderLines(customerId: UUID, productIds: List<UUID>): UUID
}

enum class PaymentStatus(val value: String) {
    Paid("Paid"),
    Unpaid("Unpaid"),
}

class PostgresOrderRepository(
    private val connection: Connection = PooledDataSource.dataSource.connection,
) : OrderRepository {
    override fun close() {
        connection.close()
    }

    override fun createOrderWithOrderLines(customerId: UUID, productIds: List<UUID>): UUID {
        return connection.withTransaction {
            val orderId = createOrder(customerId = customerId)
            productIds.forEach { productId ->
                createOrderLine(
                    orderId = orderId,
                    productId = productId
                )
            }
            orderId
        }
    }

    private fun createOrderLine(orderId: UUID, productId: UUID): UUID {
        val result = connection.prepareStatement(
            """
                INSERT INTO order_line (id, order_id, product_id)
                VALUES (?::uuid, ?::uuid, ?::uuid)
                RETURNING id
            """.trimIndent()
        ).apply {
            setUUID(1, UUID.randomUUID())
            setUUID(2, orderId)
            setUUID(3, productId)
        }.executeQuery()

        check(result.next())
        return result.getUUID("id")
    }

    private fun createOrder(customerId: UUID): UUID {
        val result = connection.prepareStatement(
            """
            INSERT INTO "order"(id, customer_id, payment_status)
            VALUES (?::uuid, ?::uuid, ?::payment_status)
            RETURNING id
            """.trimIndent(),
        ).apply {
            setUUID(1, UUID.randomUUID())
            setUUID(2, customerId)
            setPaymentStatus(3, PaymentStatus.Unpaid)
        }.executeQuery()

        check(result.next())
        return result.getUUID("id")
    }
}
