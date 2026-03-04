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
    @SerialName("discoveredUpgrades") val discoveredUpgrades: DiscoveredUpgrades = DiscoveredUpgrades.EMPTY,
    @SerialName("unlockedItemNames") val unlockedItemNames: UnlockedItemNames = UnlockedItemNames.EMPTY,
    @SerialName("addons") val addons: Addons = Addons.EMPTY,
    private val _otherNodes: OtherJsonNodesCollection = OtherJsonNodesCollection.EMPTY
) {

    companion object {
        val EMPTY = PersistentProfileData()
    }

    object Serializer : CatchAllJsonSerializer<PersistentProfileData>(generatedSerializer())

    @Serializable
    @JvmInline
    value class DiscoveredTrucks(val trucks: Map<String, DiscoveredItem>) {
        companion object {
            val EMPTY = DiscoveredTrucks(emptyMap())
        }
    }

    @Serializable
    @JvmInline
    value class DiscoveredUpgrades(val upgrades: Map<String, DiscoveredItem>) {
        companion object {
            val EMPTY = DiscoveredUpgrades(emptyMap())
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
    @JvmInline
    value class Addons(val addons: Map<String, Int>) {
        companion object {
            val EMPTY = Addons(emptyMap())
        }
    }

    @Serializable
    data class DiscoveredItem(
        @SerialName("current") val current: Int,
        @SerialName("all") val all: Int,
    )

}
