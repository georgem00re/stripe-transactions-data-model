package com.example.app.database

import com.example.app.generated.models.ListOrdersSuccessResponseOrdersInnerProductsInner
import java.sql.ResultSet
import java.util.UUID

fun ResultSet.getUUID(columnLabel: String): UUID {
    val result = this.getString(columnLabel)
    return UUID.fromString(result)
}

fun ResultSet.getPaymentStatus(columnLabel: String): ListOrdersSuccessResponseOrdersInnerProductsInner.PaymentStatus {
    return ListOrdersSuccessResponseOrdersInnerProductsInner.PaymentStatus.valueOf(getString(columnLabel))
}
