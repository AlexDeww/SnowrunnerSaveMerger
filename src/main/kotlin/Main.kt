@file:OptIn(ExperimentalSerializationApi::class)

package org.home

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.*
import org.home.model.SaveData
import org.home.model.SslValue
import org.home.utils.*
import java.io.File
import java.io.FileInputStream
import java.io.FilterInputStream

private typealias JsonDiffResult = List<JsonDiffOperation>
private typealias JsonDiffOperation = DiffOperation<JsonElement>

private fun mergeSaves(targetFile: File, beginFile: File, endFile: File) {
    val targetSaveData = targetFile.readSaveData()
    val beginSaveData = beginFile.readSaveData()
    val endSaveData = endFile.readSaveData()

    val sslValueMergeHelper = MergeObjectHelper.create(
        targetObj = targetSaveData.completeSave.sslValue,
        beginObj = beginSaveData.completeSave.sslValue,
        endObj = endSaveData.completeSave.sslValue,
    )

    val newSslValue = sslValueMergeHelper {
        val newReal = targetObject.real.copy(
            finishedObjs = SslValue::finishedObjs.merge(),
            levelGarageStatuses = SslValue::levelGarageStatuses.merge(),
            discoveredObjectives = SslValue::discoveredObjectives.merge(),
            viewedUnactivatedObjectives = SslValue::viewedUnactivatedObjectives.merge(),
            visitedLevels = SslValue::visitedLevels.merge(),
            objectiveStates = SslValue::objectiveStates.merge(),
            upgradesGiverData = SslValue::upgradesGiverData.merge(),
            watchPointsData = SslValue::watchPointsData.merge(),
            persistentProfileData = SslValue::persistentProfileData.merge()
        )
        targetObject.copy(real = newReal)
    }

    val newTargetSaveData = targetSaveData.copy(
        real = targetSaveData.real.copy(
            completeSave = targetSaveData.completeSave.copy(
                real = targetSaveData.completeSave.real.copy(sslValue = newSslValue)
            )
        )
    )

    targetFile.writeSaveData(newTargetSaveData)
}

private val Json = Json { ignoreUnknownKeys = true }

fun main() {
    val configFileA = File("C:\\Users\\deadi\\Desktop\\remote_s\\CompleteSave.cfg")
    val configFileB = File("C:\\Users\\deadi\\Desktop\\remote_e\\CompleteSave.cfg")
    val configFileT = File("J:\\Programs\\Steam\\userdata\\42206345\\1465360\\remote\\CompleteSave.cfg")

    val saveDataA = configFileA.readSaveData()
    val saveDataB = configFileB.readSaveData()
    val saveDataT = configFileT.readSaveData()

    val sslValueMergeHelper = MergeObjectHelper.create(
        targetObj = saveDataT.completeSave.sslValue,
        beginObj = saveDataA.completeSave.sslValue,
        endObj = saveDataB.completeSave.sslValue,
    )

    val sslValueR = sslValueMergeHelper.run {
        targetObject.real.copy(
            finishedObjs = SslValue::finishedObjs.merge(),
            levelGarageStatuses = SslValue::levelGarageStatuses.merge(),
            discoveredObjectives = SslValue::discoveredObjectives.merge(),
            viewedUnactivatedObjectives = SslValue::viewedUnactivatedObjectives.merge(),
            visitedLevels = SslValue::visitedLevels.merge(),
            objectiveStates = SslValue::objectiveStates.merge(),
            upgradesGiverData = SslValue::upgradesGiverData.merge(),
            watchPointsData = SslValue::watchPointsData.merge(),
            persistentProfileData = SslValue::persistentProfileData.merge()
        )
    }

    val saveDataR = saveDataT.copy(
        real = saveDataT.real.copy(
            completeSave = saveDataT.completeSave.copy(
                real = saveDataT.completeSave.real.copy(
                    sslValue = saveDataT.completeSave.sslValue.copy(real = sslValueR)
                )
            )
        )
    )

    val saveConfigDataA = SaveConfigData.createFromFile(configFileA)
    val saveConfigDataB = SaveConfigData.createFromFile(configFileB)
    val saveConfigDataT = SaveConfigData.createFromFile(configFileT)

    val finishedObjsSet = saveConfigDataT.finishedObjs.mapTo(mutableSetOf()) { it.jsonPrimitive.content }
    val objectiveStates = saveConfigDataA.objectiveStates
        .diff(saveConfigDataB.objectiveStates)
        .filter { it.key !in finishedObjsSet }
        .applyDiffOn(saveConfigDataT.objectiveStates)

    val finishedObjs = saveConfigDataA.finishedObjs
        .diff(saveConfigDataB.finishedObjs)
        .applyDiffOn(saveConfigDataT.finishedObjs)

    val upgradesGiverData = saveConfigDataA.upgradesGiverData
        .diff(saveConfigDataB.upgradesGiverData)
        .applyDiffOn(saveConfigDataT.upgradesGiverData) { old, new ->
            old?.jsonObject
                ?.diff(new.jsonObject, areValueChanged = ::areStatusIntValueChanged)
                ?.applyDiffOn(old.jsonObject)
                ?: new
        }

    val levelGarageStatuses = saveConfigDataT.levelGarageStatuses
        .diff(saveConfigDataB.levelGarageStatuses, areValueChanged = ::areStatusIntValueChanged)
        .applyDiffOn(saveConfigDataT.levelGarageStatuses)

    val discoveredObjectives = saveConfigDataA.discoveredObjectives
        .diff(saveConfigDataB.discoveredObjectives)
        .applyDiffOn(saveConfigDataT.discoveredObjectives)

    val viewedUnactivatedObjectives = saveConfigDataA.viewedUnactivatedObjectives
        .diff(saveConfigDataB.viewedUnactivatedObjectives)
        .applyDiffOn(saveConfigDataT.viewedUnactivatedObjectives)

    val watchPointsData = saveConfigDataA.watchPointsData
        .diff(saveConfigDataB.watchPointsData)
        .applyDiffOn(saveConfigDataT.watchPointsData) { old, new ->
            old?.jsonObject
                ?.diff(new.jsonObject, areValueChanged = ::areStatusBooleanValueChanged)
                ?.applyDiffOn(old.jsonObject)
                ?: new
        }

    val visitedLevels = saveConfigDataA.visitedLevels
        .diff(saveConfigDataB.visitedLevels)
        .applyDiffOn(saveConfigDataT.visitedLevels)

    val discoveredTrucks = saveConfigDataA.discoveredTrucks
        .diff(saveConfigDataB.discoveredTrucks)
        .applyDiffOn(saveConfigDataT.discoveredTrucks) { old, new ->
            old?.jsonObject
                ?.diff(new.jsonObject, areValueChanged = ::areStatusIntValueChanged)
                ?.applyDiffOn(old.jsonObject)
                ?: new
        }

    val ownedTrucks = saveConfigDataA.ownedTrucks
        .diff(saveConfigDataB.ownedTrucks)
        .applyDiffOn(saveConfigDataT.ownedTrucks)

    val outJson = buildJsonObject {
        saveConfigDataT.root.forEach { put(it.key, it.value) }

        putJsonObject("CompleteSave") {
            saveConfigDataT.root
                .getValue("CompleteSave").jsonObject
                .forEach { put(it.key, it.value) }

            putJsonObject("SslValue") {
                saveConfigDataT.root
                    .getValue("CompleteSave").jsonObject
                    .getValue("SslValue").jsonObject
                    .forEach { put(it.key, it.value) }

                put("objectiveStates", objectiveStates)
                put("finishedObjs", finishedObjs)
                put("upgradesGiverData", upgradesGiverData)
                put("levelGarageStatuses", levelGarageStatuses)
                put("discoveredObjectives", discoveredObjectives)
                put("viewedUnactivatedObjectives", viewedUnactivatedObjectives)
                putJsonObject("watchPointsData") { put("data", watchPointsData) }
                put("visitedLevels", visitedLevels)

                putJsonObject("persistentProfileData") {
                    saveConfigDataT.root
                        .getValue("CompleteSave").jsonObject
                        .getValue("SslValue").jsonObject
                        .getValue("persistentProfileData").jsonObject
                        .forEach { put(it.key, it.value) }

                    put("discoveredTrucks", discoveredTrucks)
                    put("ownedTrucks", ownedTrucks)
                }
            }
        }
    }

    val outRaw = Json.encodeToString(outJson)
    val outRaw2 = Json.encodeToString(saveDataR)

    val a = Json.decodeFromString<JsonObject>(outRaw)
    val b = Json.decodeFromString<JsonObject>(outRaw2)

    print(a == b)
//    configFileT.outputStream().writer().append(outRaw).append(ZERO.toInt().toChar()).flush()
}

private fun areStatusIntValueChanged(old: JsonElement, new: JsonElement): Boolean =
    new.jsonPrimitive.int > old.jsonPrimitive.int

private fun areStatusBooleanValueChanged(old: JsonElement, new: JsonElement): Boolean =
    new.jsonPrimitive.boolean > old.jsonPrimitive.boolean

private class SaveConfigData(
    val root: JsonObject,
    val objectiveStates: JsonObject,
    val finishedObjs: JsonArray,
    val upgradesGiverData: JsonObject,
    val levelGarageStatuses: JsonObject,
    val discoveredObjectives: JsonArray,
    val viewedUnactivatedObjectives: JsonArray,
    val watchPointsData: JsonObject,
    val visitedLevels: JsonArray,
    val discoveredTrucks: JsonObject,
    val ownedTrucks: JsonObject,
) {

    companion object {
        fun createFromFile(saveConfigFile: File): SaveConfigData {
            val jsonRawData = SaveConfigInputStream(saveConfigFile.inputStream())
            val jsonData = Json.decodeFromStream<JsonObject>(jsonRawData)

            val completeSave = jsonData.getValue("CompleteSave").jsonObject
            val sslValue = completeSave.getValue("SslValue").jsonObject
            val persistentProfileData = sslValue.getValue("persistentProfileData").jsonObject

            return SaveConfigData(
                root = jsonData,
                objectiveStates = sslValue.getValue("objectiveStates").jsonObject,
                finishedObjs = sslValue.getValue("finishedObjs").jsonArray,
                upgradesGiverData = sslValue.getValue("upgradesGiverData").jsonObject,
                levelGarageStatuses = sslValue.getValue("levelGarageStatuses").jsonObject,
                discoveredObjectives = sslValue.getValue("discoveredObjectives").jsonArray,
                viewedUnactivatedObjectives = sslValue.getValue("viewedUnactivatedObjectives").jsonArray,
                watchPointsData = sslValue.getValue("watchPointsData").jsonObject.getValue("data").jsonObject,
                visitedLevels = sslValue.getValue("visitedLevels").jsonArray,
                discoveredTrucks = persistentProfileData.getValue("discoveredTrucks").jsonObject,
                ownedTrucks = persistentProfileData.getValue("ownedTrucks").jsonObject
            )
        }
    }

}

private fun JsonDiffResult.applyDiffOn(
    jsonObject: JsonObject,
    resolveValue: (old: JsonElement?, new: JsonElement) -> JsonElement = { _, new -> new }
): JsonObject = jsonObject
    .toMutableMap()
    .applyDiff(diffResult = this, resolveValue = resolveValue)
    .let { JsonObject(it) }

private fun JsonDiffResult.applyDiffOn(jsonArray: JsonArray): JsonArray = jsonArray
    .toMutableList()
    .applyDiff(this)
    .let { JsonArray(it) }

private fun File.readSaveData(): SaveData {
    return saveConfigInputStream().use { Json.decodeFromStream<SaveData>(it) }
}

private fun File.writeSaveData(saveData: SaveData) {
    outputStream().use {
        Json.encodeToStream(saveData, it)
        it.write(0)
    }
}

private fun File.saveConfigInputStream(): SaveConfigInputStream = SaveConfigInputStream(inputStream())

private class SaveConfigInputStream(fileStream: FileInputStream) : FilterInputStream(fileStream) {
    override fun read(): Int = super.read().let { if (it == 0) -1 else it }
    override fun read(b: ByteArray, off: Int, len: Int): Int =
        super.read(b, off, len).let { if (it != -1 && b[off + it - 1] == 0.toByte()) it - 1 else it }
}
