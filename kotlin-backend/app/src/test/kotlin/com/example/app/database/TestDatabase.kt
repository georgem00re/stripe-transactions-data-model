package com.example.app.database

import com.example.app.database.PooledDataSource.dataSource

class TestDatabase() {
    fun emptyDatabase() {
        deleteAllCustomers()
        deleteAllProducts()
        deleteAllOrders()
        deleteAllOrderLines()
    }

    private fun deleteAllCustomers() {
        dataSource.connection.use { connection ->
            connection.prepareStatement(
                """
                TRUNCATE TABLE customer CASCADE
                """.trimIndent(),
            ).executeUpdate()
        }
    }

    private fun deleteAllProducts() {
        dataSource.connection.use { connection ->
            connection.prepareStatement(
                """
                TRUNCATE TABLE product CASCADE
                """.trimIndent(),
            ).executeUpdate()
        }
    }

    private fun deleteAllOrders() {
        dataSource.connection.use { connection ->
            connection.prepareStatement(
                """
                TRUNCATE TABLE "order" CASCADE
                """.trimIndent(),
            ).executeUpdate()
        }
    }

    private fun deleteAllOrderLines() {
        dataSource.connection.use { connection ->
            connection.prepareStatement(
                """
                TRUNCATE TABLE order_line CASCADE
                """.trimIndent(),
            ).executeUpdate()
        }
    }
}
