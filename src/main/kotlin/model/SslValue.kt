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
    @SerialName("finishedObjs") val finishedObjs: Set<String> = emptySet(),
    @SerialName("objectiveStates") val objectiveStates: ObjectiveStates = ObjectiveStates.EMPTY,
    @SerialName("upgradesGiverData") val upgradesGiverData: UpgradesGiverData = UpgradesGiverData.EMPTY,
    @SerialName("levelGarageStatuses") val levelGarageStatuses: LevelGarageStatuses = LevelGarageStatuses.EMPTY,
    @SerialName("discoveredObjectives") val discoveredObjectives: Set<String> = emptySet(),
    @SerialName("viewedUnactivatedObjectives") val viewedUnactivatedObjectives: Set<String> = emptySet(),
    @SerialName("watchPointsData") val watchPointsData: WatchPointsData = WatchPointsData.EMPTY,
    @SerialName("visitedLevels") val visitedLevels: Set<String> = emptySet(),
    @SerialName("persistentProfileData") val persistentProfileData: PersistentProfileData = PersistentProfileData.EMPTY,
    private val _otherNodes: OtherJsonNodesCollection = OtherJsonNodesCollection.EMPTY
) {

    companion object {
        val EMPTY = SslValue()
    }

    object Serializer : CatchAllJsonSerializer<SslValue>(generatedSerializer())

    @Serializable
    @JvmInline
    value class ObjectiveStates(val states: Map<String, JsonObject>) {
        companion object {
            val EMPTY = ObjectiveStates(emptyMap())
        }
    }

    @Serializable
    @JvmInline
    value class UpgradesGiverData(val data: Map<String, Map<String, Int>> = emptyMap()) {
        companion object {
            val EMPTY = UpgradesGiverData(emptyMap())
        }
    }

    @Serializable
    @JvmInline
    value class LevelGarageStatuses(val statuses: Map<String, Int>) {
        companion object {
            val EMPTY = LevelGarageStatuses(emptyMap())
        }
    }

    @Serializable
    data class WatchPointsData(
        @SerialName("data") val data: Map<String, Map<String, Boolean>>
    ) {
        companion object {
            val EMPTY = WatchPointsData(emptyMap())
        }
    }

}
