package com.example.app.database

import com.example.app.generated.models.ListOrdersSuccessResponseOrdersInnerProductsInner
import java.sql.PreparedStatement
import java.util.UUID

fun PreparedStatement.setUUID(
    parameterIndex: Int,
    uuid: UUID,
) = setString(parameterIndex, uuid.toString())

fun PreparedStatement.setPaymentStatus(
    parameterIndex: Int,
    paymentStatus: ListOrdersSuccessResponseOrdersInnerProductsInner.PaymentStatus
) {
    this.setString(parameterIndex, paymentStatus.value)
}
