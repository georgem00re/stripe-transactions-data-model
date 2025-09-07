package com.example.app.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.OffsetDateTime

class OffsetDateTimeSerializer : KSerializer<OffsetDateTime> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("OffsetDateTime", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): OffsetDateTime {
        val string = decoder.decodeString()
        // Do we need to worry if string .isEmpty() ?
        return OffsetDateTime.parse(string)
    }

    override fun serialize(
        encoder: Encoder,
        value: OffsetDateTime,
    ) {
        encoder.encodeString(value.toString())
    }
}
