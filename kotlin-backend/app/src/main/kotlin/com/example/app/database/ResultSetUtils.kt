package com.example.app.database

import java.sql.ResultSet
import java.util.UUID

fun ResultSet.getUUID(columnLabel: String): UUID {
    val result = this.getString(columnLabel)
    return UUID.fromString(result)
}
