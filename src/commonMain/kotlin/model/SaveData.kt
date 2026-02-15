package org.home.model

import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.home.json.CatchAllJsonSerializer
import org.home.json.OtherJsonNodesCollection

@Serializable(with = SaveData.Serializer::class)
@KeepGeneratedSerializer
data class SaveData(
    @SerialName("CompleteSave") val completeSave: CompleteSave = CompleteSave.EMPTY,
    private val _otherNodes: OtherJsonNodesCollection = OtherJsonNodesCollection.EMPTY
) {

    companion object {
        val EMPTY = SaveData()
    }

    object Serializer : CatchAllJsonSerializer<SaveData>(generatedSerializer())

}
