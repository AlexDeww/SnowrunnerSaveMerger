package org.home.model

import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.home.json.CatchAllJsonSerializer
import org.home.json.OtherJsonNodesCollection
import kotlin.jvm.JvmInline

@Serializable(with = PersistentProfileData.Serializer::class)
@KeepGeneratedSerializer
data class PersistentProfileData(
    @SerialName("discoveredTrucks") val discoveredTrucks: DiscoveredTrucks = DiscoveredTrucks.EMPTY,
    @SerialName("unlockedItemNames") val unlockedItemNames: UnlockedItemNames = UnlockedItemNames.EMPTY,
    private val _otherNodes: OtherJsonNodesCollection = OtherJsonNodesCollection.EMPTY
) {

    companion object {
        val EMPTY = PersistentProfileData()
    }

    object Serializer : CatchAllJsonSerializer<PersistentProfileData>(generatedSerializer())

    @Serializable
    @JvmInline
    value class DiscoveredTrucks(val trucks: Map<String, DiscoveredTrucksItem>) {
        companion object {
            val EMPTY = DiscoveredTrucks(emptyMap())
        }
    }

    @Serializable
    @JvmInline
    value class UnlockedItemNames(val names: Map<String, Boolean>) {
        companion object {
            val EMPTY = UnlockedItemNames(emptyMap())
        }
    }

    @Serializable
    data class DiscoveredTrucksItem(
        @SerialName("current") val current: Int,
        @SerialName("all") val all: Int,
    )

}
