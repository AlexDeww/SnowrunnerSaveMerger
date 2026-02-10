package org.home.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import org.home.model.IPersistentProfileData.DiscoveredTrucks
import org.home.model.IPersistentProfileData.OwnedTrucks

@Serializable(with = PersistentProfileDataSerializer::class)
data class PersistentProfileData(
    override val real: Real,
    override val others: Map<String, JsonElement>
) : IPersistentProfileData by real, Extensible<PersistentProfileData.Real> {

    @Serializable
    data class Real(
        @SerialName("discoveredTrucks") override val discoveredTrucks: DiscoveredTrucks,
        @SerialName("ownedTrucks") override val ownedTrucks: OwnedTrucks,
    ) : IPersistentProfileData

}

interface IPersistentProfileData {
    val discoveredTrucks: DiscoveredTrucks
    val ownedTrucks: OwnedTrucks

    @Serializable
    @JvmInline
    value class DiscoveredTrucks(val trucks: Map<String, DiscoveredTrucksItem>)

    @Serializable
    @JvmInline
    value class OwnedTrucks(val trucks: Map<String, Int>)

    @Serializable
    data class DiscoveredTrucksItem(
        @SerialName("current") val current: Int,
        @SerialName("all") val all: Int,
    )
}

object PersistentProfileDataSerializer : KSerializer<PersistentProfileData> by ExtensibleJsonSerializer(
    realSerializer = PersistentProfileData.Real.serializer(),
    factory = ::PersistentProfileData
)
