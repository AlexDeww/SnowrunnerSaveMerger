package org.home.json

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.MetaSerializable
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.jsonObject

@OptIn(ExperimentalSerializationApi::class)
@MetaSerializable
@Target(AnnotationTarget.CLASS)
private annotation class OtherJsonNodes

@Serializable
@OtherJsonNodes
@JvmInline
value class OtherJsonNodesCollection(val nodes: Map<String, JsonElement>) {
    companion object {
        val EMPTY = OtherJsonNodesCollection(emptyMap())
    }
}

open class CatchAllJsonSerializer<T : Any>(
    serializer: KSerializer<T>
) : JsonTransformingSerializer<T>(serializer) {

    private val otherNodesPropName by lazy {
        (0 until descriptor.elementsCount)
            .firstOrNull {
                descriptor.getElementDescriptor(it)
                    .annotations
                    .filterIsInstance<OtherJsonNodes>()
                    .isNotEmpty()
            }
            ?.let { descriptor.getElementName(it) }
            .orEmpty()
    }

    override fun transformDeserialize(element: JsonElement): JsonElement {
        if (element !is JsonObject || otherNodesPropName.isEmpty()) return element

        val modelKeys = descriptor.elementNames.filterTo(mutableSetOf()) { it != otherNodesPropName }
        val modelNodes = mutableMapOf<String, JsonElement>()
        val otherNodes = mutableMapOf<String, JsonElement>()

        element.forEach {
            if (it.key in modelKeys) modelNodes.put(it.key, it.value)
            else otherNodes.put(it.key, it.value)
        }

        return JsonObject(modelNodes + (otherNodesPropName to JsonObject(otherNodes)))
    }

    override fun transformSerialize(element: JsonElement): JsonElement {
        if (element !is JsonObject || otherNodesPropName.isEmpty() || otherNodesPropName !in element) return element

        val allNodes = element.toMutableMap()
        val others = requireNotNull(allNodes.remove(otherNodesPropName)).jsonObject

        return JsonObject(allNodes + others)
    }

}
