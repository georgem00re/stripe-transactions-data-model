package com.example.app.database

import com.example.app.database.PooledDataSource.dataSource

class TestDatabase() {
    fun emptyDatabase() {
        deleteAllCustomers()
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
}
