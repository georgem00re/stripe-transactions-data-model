package com.example.app.database

import com.example.app.generated.models.ListOrdersSuccessResponseOrdersInner
import com.example.app.generated.models.ListOrdersSuccessResponseOrdersInnerProductsInner
import java.io.Closeable
import java.sql.Connection
import java.util.UUID

interface OrderRepository : Closeable {
    fun createOrderWithOrderLines(customerId: UUID, productIds: List<UUID>): UUID
    fun getOrders(): List<ListOrdersSuccessResponseOrdersInner>
    fun getOrderCost(orderId: UUID): Long
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

    data class OrderLineRow(
        val customerId: UUID,
        val orderId: UUID,
        val productId: UUID,
        val paymentStatus: ListOrdersSuccessResponseOrdersInnerProductsInner.PaymentStatus,
    )

    data class OrderRow(
        val customerId: UUID,
        val orderId: UUID,
    )

    override fun getOrders(): List<ListOrdersSuccessResponseOrdersInner> {
        val result = connection.prepareStatement(
            """
                SELECT
                    "order".customer_id as customer_id,
                    "order".payment_status as payment_status,
                    "order".id as order_id,
                    order_line.product_id as product_id
                FROM
                "order" INNER JOIN order_line
                ON "order".id = order_line.order_id
            """.trimIndent()
        ).executeQuery()

        val flatList = buildList {
            while (result.next()) {
                add(
                    OrderLineRow(
                        customerId = result.getUUID("customer_id"),
                        orderId = result.getUUID("order_id"),
                        productId = result.getUUID("product_id"),
                        paymentStatus = result.getPaymentStatus("payment_status")
                    )
                )
            }
        }
        return flatList.groupBy {
            OrderRow(
                customerId = it.customerId,
                orderId = it.orderId,
            )
        }.map { (orderData, orderRows) ->
            ListOrdersSuccessResponseOrdersInner(
                orderId = orderData.orderId,
                customerId = orderData.customerId,
                products = orderRows.map {
                    ListOrdersSuccessResponseOrdersInnerProductsInner(
                        productId = it.productId,
                        paymentStatus = it.paymentStatus
                    )
                }
            )
        }
    }

    override fun getOrderCost(orderId: UUID): Long {
        val result = connection.prepareStatement(
            """
                SELECT
                    SUM(product.price_gbx) as total_cost
                FROM order_line 
                INNER JOIN product 
                ON order_line.product_id = product.id
                WHERE order_line.order_id = ?::uuid
            """.trimIndent()
        ).apply {
            setUUID(1, orderId)
        }.executeQuery()

        check(result.next())
        return result.getLong("total_cost")
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
            setPaymentStatus(3, ListOrdersSuccessResponseOrdersInnerProductsInner.PaymentStatus.Unpaid)
        }.executeQuery()

        check(result.next())
        return result.getUUID("id")
    }
}
