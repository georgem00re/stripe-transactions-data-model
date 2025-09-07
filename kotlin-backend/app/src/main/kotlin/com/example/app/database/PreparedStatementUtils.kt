import com.example.app.database.PaymentStatus
import java.sql.PreparedStatement
import java.util.UUID

fun PreparedStatement.setUUID(
    parameterIndex: Int,
    uuid: UUID,
) = setString(parameterIndex, uuid.toString())

fun PreparedStatement.setPaymentStatus(parameterIndex: Int, paymentStatus: PaymentStatus) {
    this.setString(parameterIndex, paymentStatus.value)
}
