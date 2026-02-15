package org.home.model

import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.home.json.CatchAllJsonSerializer
import org.home.json.OtherJsonNodesCollection

@Serializable(with = CompleteSave.Serializer::class)
@KeepGeneratedSerializer
data class CompleteSave(
    @SerialName("SslValue") val sslValue: SslValue = SslValue.EMPTY,
    private val _otherNodes: OtherJsonNodesCollection = OtherJsonNodesCollection.EMPTY
) {

    companion object {
        val EMPTY = CompleteSave()
    }

    object Serializer : CatchAllJsonSerializer<CompleteSave>(generatedSerializer())

}
