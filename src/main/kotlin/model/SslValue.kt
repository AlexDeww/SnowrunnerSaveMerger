package org.home.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.home.model.ISslValue.LevelGarageStatuses
import org.home.model.ISslValue.ObjectiveStates
import org.home.model.ISslValue.UpgradesGiverData
import org.home.model.ISslValue.WatchPointsData

@Serializable(with = SslValueSerializer::class)
data class SslValue(
    override val real: Real,
    override val others: Map<String, JsonElement>,
) : ISslValue by real, Extensible<SslValue.Real> {

    @Serializable
    data class Real(
        @SerialName("finishedObjs") override val finishedObjs: Set<String>,
        @SerialName("objectiveStates") override val objectiveStates: ObjectiveStates,
        @SerialName("upgradesGiverData") override val upgradesGiverData: UpgradesGiverData,
        @SerialName("levelGarageStatuses") override val levelGarageStatuses: LevelGarageStatuses,
        @SerialName("discoveredObjectives") override val discoveredObjectives: Set<String>,
        @SerialName("viewedUnactivatedObjectives") override val viewedUnactivatedObjectives: Set<String>,
        @SerialName("watchPointsData") override val watchPointsData: WatchPointsData,
        @SerialName("visitedLevels") override val visitedLevels: Set<String>,
        @SerialName("persistentProfileData") override val persistentProfileData: PersistentProfileData,
    ) : ISslValue

}

interface ISslValue {
    val finishedObjs: Set<String>
    val objectiveStates: ObjectiveStates
    val upgradesGiverData: UpgradesGiverData
    val levelGarageStatuses: LevelGarageStatuses
    val discoveredObjectives: Set<String>
    val viewedUnactivatedObjectives: Set<String>
    val watchPointsData: WatchPointsData
    val visitedLevels: Set<String>
    val persistentProfileData: PersistentProfileData

    @Serializable
    @JvmInline
    value class ObjectiveStates(val state: Map<String, JsonObject>)

    @Serializable
    @JvmInline
    value class UpgradesGiverData(val data: Map<String, Map<String, Int>>)

    @Serializable
    @JvmInline
    value class LevelGarageStatuses(val statues: Map<String, Int>)

    @Serializable
    data class WatchPointsData(
        @SerialName("data") val data: Map<String, Map<String, Boolean>>
    )

}

object SslValueSerializer : KSerializer<SslValue> by ExtensibleJsonSerializer(
    realSerializer = SslValue.Real.serializer(),
    factory = ::SslValue
)
