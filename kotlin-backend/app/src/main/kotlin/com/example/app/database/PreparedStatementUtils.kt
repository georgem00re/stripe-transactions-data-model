import java.sql.PreparedStatement
import java.util.UUID

fun PreparedStatement.setUUID(
    parameterIndex: Int,
    uuid: UUID,
) = setString(parameterIndex, uuid.toString())
