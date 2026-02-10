package org.home.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable(with = CompleteSaveSerializer::class)
data class CompleteSave(
    override val real: Real,
    override val others: Map<String, JsonElement>
) : ICompleteSave by real, Extensible<CompleteSave.Real> {

    @Serializable
    data class Real(
        @SerialName("SslValue") override val sslValue: SslValue
    ) : ICompleteSave

}

interface ICompleteSave {
    val sslValue: SslValue
}

object CompleteSaveSerializer : KSerializer<CompleteSave> by ExtensibleJsonSerializer(
    realSerializer = CompleteSave.Real.serializer(),
    factory = ::CompleteSave
)
