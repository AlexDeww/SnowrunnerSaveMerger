package org.home.model

import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import org.home.json.CatchAllJsonSerializer
import org.home.json.OtherJsonNodesCollection

@Serializable(with = SslValue.Serializer::class)
@KeepGeneratedSerializer
data class SslValue(
    @SerialName("finishedObjs") val finishedObjs: Set<String>,
    @SerialName("objectiveStates") val objectiveStates: ObjectiveStates,
    @SerialName("upgradesGiverData") val upgradesGiverData: UpgradesGiverData,
    @SerialName("levelGarageStatuses") val levelGarageStatuses: LevelGarageStatuses,
    @SerialName("discoveredObjectives") val discoveredObjectives: Set<String>,
    @SerialName("viewedUnactivatedObjectives") val viewedUnactivatedObjectives: Set<String>,
    @SerialName("watchPointsData") val watchPointsData: WatchPointsData,
    @SerialName("visitedLevels") val visitedLevels: Set<String>,
    @SerialName("persistentProfileData") val persistentProfileData: PersistentProfileData,
    private val _otherNodes: OtherJsonNodesCollection
) {

    object Serializer : CatchAllJsonSerializer<SslValue>(generatedSerializer())

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
