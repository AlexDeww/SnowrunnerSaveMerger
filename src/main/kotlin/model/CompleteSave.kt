package org.home.model

import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.home.json.CatchAllJsonSerializer
import org.home.json.OtherJsonNodesCollection

@Serializable(with = CompleteSave.Serializer::class)
@KeepGeneratedSerializer
data class CompleteSave(
    @SerialName("SslValue") val sslValue: SslValue,
    private val _otherNodes: OtherJsonNodesCollection
) {

    object Serializer : CatchAllJsonSerializer<CompleteSave>(generatedSerializer())

}
