package org.home.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable(with = SaveDataSerializer::class)
data class SaveData(
    override val real: Real,
    override val others: Map<String, JsonElement>
) : ISaveData by real, Extensible<SaveData.Real> {

    @Serializable
    data class Real(
        @SerialName("CompleteSave") override val completeSave: CompleteSave
    ) : ISaveData

}

interface ISaveData {
    val completeSave: CompleteSave
}

object SaveDataSerializer : KSerializer<SaveData> by ExtensibleJsonSerializer(
    realSerializer = SaveData.Real.serializer(),
    factory = ::SaveData
)
