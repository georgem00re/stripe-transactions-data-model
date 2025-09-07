package com.example.app.database

import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

object PooledDataSource {
    private val config = ConfigFactory.load()

    // We use Hikari to pool database connections. Creating a new database connection is expensive (time and resources).
    // A connection pool reuses existing connections instead of opening new ones every time. This improves performance
    // by reducing latency and prevents overload by limiting how many connections can be open at once.
    private val hikariConfig =
        HikariConfig().apply {
            jdbcUrl = config.getString("db.jdbcUrl")
            maximumPoolSize = config.getInt("db.maximumPoolSize")
        }

    val dataSource: DataSource = HikariDataSource(hikariConfig)
}
