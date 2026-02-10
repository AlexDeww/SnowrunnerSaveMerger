package org.home.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

interface Extensible<R> {
    val real: R
    val others: Map<String, JsonElement>
}

class ExtensibleJsonSerializer<R : Any, T : Extensible<R>>(
    private val realSerializer: KSerializer<R>,
    private val factory: (R, Map<String, JsonElement>) -> T
) : KSerializer<T> {

    override val descriptor: SerialDescriptor = realSerializer.descriptor

    override fun deserialize(decoder: Decoder): T {
        val jsonDecoder = decoder as JsonDecoder
        val jsonObject = jsonDecoder.decodeJsonElement().jsonObject

        val real = jsonDecoder.json.decodeFromJsonElement(realSerializer, jsonObject)

        val knownKeys = descriptor.elementNames.toSet()
        val others = jsonObject.filterKeys { it !in knownKeys }

        return factory(real, others)
    }

    override fun serialize(encoder: Encoder, value: T) {
        val jsonEncoder = encoder as JsonEncoder
        val realJsonObject = jsonEncoder.json.encodeToJsonElement(realSerializer, value.real).jsonObject

        val result = JsonObject(realJsonObject + value.others)
        jsonEncoder.encodeJsonElement(result)
    }

}
