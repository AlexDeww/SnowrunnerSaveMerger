package org.home

import kotlinx.io.files.Path
import kotlinx.io.readString
import kotlinx.serialization.json.*
import kotlinx.serialization.json.io.encodeToSink
import org.home.model.SaveData
import org.home.model.SslValue
import org.home.utils.*

private val Json = Json { ignoreUnknownKeys = true }

fun main(args: Array<String>) {
    val arguments = Arguments.parse(args)

    if (!arguments.baseFile.exists()) println("Base file [${arguments.baseFile}] does not exist").also { return }
    if (!arguments.sourceFile.exists()) println("Source file [${arguments.sourceFile}] does not exist").also { return }
    if (arguments.originFile?.exists() == false) println("Origin file [${arguments.originFile}] does not exist").also { return }

    mergeSaves(
        baseFile = arguments.baseFile,
        originFile = arguments.originFile,
        sourceFile = arguments.sourceFile,
        makeBackup = arguments.makeBackup == true
    )

    println("Successfully merged progress into [${arguments.baseFile}]")
}

private fun mergeSaves(baseFile: Path, originFile: Path?, sourceFile: Path, makeBackup: Boolean) {
    val baseSaveData = baseFile.readSaveData()
    val originSaveData = originFile?.readSaveData() ?: SaveData.EMPTY
    val sourceSaveData = sourceFile.readSaveData()

    val resultSaveData = mergeSaveData(
        baseSaveData = baseSaveData,
        originSaveData = originSaveData,
        sourceSaveData = sourceSaveData,
    )

    if (makeBackup) baseFile.copyTo(Path("$baseFile.bak"))
    baseFile.writeSaveData(resultSaveData)
}

private fun mergeSaveData(
    baseSaveData: SaveData,
    originSaveData: SaveData,
    sourceSaveData: SaveData
): SaveData {
    val sslValueMergeHelper = MergeObjectHelper.create(
        baseObj = baseSaveData.completeSave.sslValue,
        originObj = originSaveData.completeSave.sslValue,
        sourceObj = sourceSaveData.completeSave.sslValue,
    )

    val newSslValue = sslValueMergeHelper {
        baseObj.copy(
            finishedObjs = SslValue::finishedObjs.merge(asMonotonic = true),
            objectiveStates = SslValue::objectiveStates.merge(),
            upgradesGiverData = SslValue::upgradesGiverData.merge(),
            levelGarageStatuses = SslValue::levelGarageStatuses.merge(),
            discoveredObjectives = SslValue::discoveredObjectives.merge(asMonotonic = true),
            viewedUnactivatedObjectives = SslValue::viewedUnactivatedObjectives.merge(),
            watchPointsData = SslValue::watchPointsData.merge(),
            visitedLevels = SslValue::visitedLevels.merge(asMonotonic = true),
            persistentProfileData = SslValue::persistentProfileData.merge()
        )
    }

    return baseSaveData.run { copy(completeSave = completeSave.copy(sslValue = newSslValue)) }
}

private fun Path.readSaveData(): SaveData = inputSource().use { source ->
    val rawData = source.readString().trimEnd { it == '\u0000' }
    Json.decodeFromString<SaveData>(rawData)
}

private fun Path.writeSaveData(saveData: SaveData) = outputSink().use {
    Json.encodeToSink(saveData, it)
    it.writeByte(0)
}
