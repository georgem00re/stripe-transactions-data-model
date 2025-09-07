package com.example.app.database

import java.sql.Connection

fun <T> Connection.withTransaction(block: () -> T): T {
    try {
        setAutoCommit(false)
        val result = block()
        commit()
        return result
    } catch (exception: Exception) {
        rollback()
        throw exception
    } finally {
        setAutoCommit(true)
    }
}
