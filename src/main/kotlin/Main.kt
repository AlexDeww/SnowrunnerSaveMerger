package org.home

import kotlinx.serialization.json.*
import org.home.model.SaveData
import org.home.model.SslValue
import org.home.utils.MergeObjectHelper
import org.home.utils.invoke
import org.home.utils.merge
import java.io.File
import java.io.FileInputStream
import java.io.FilterInputStream

private val Json = Json { ignoreUnknownKeys = true }

fun main(args: Array<String>) {
    val arguments = Arguments.parse(args)

    if (arguments.targetFilePath.isEmpty()) println("target file path is empty").also { return }
    if (arguments.beginFilePath.isEmpty()) println("begin file path is empty").also { return }
    if (arguments.endFilePath.isEmpty()) println("end file path is empty").also { return }

    val configFileTarget = File(arguments.targetFilePath)
    val configFileBegin = File(arguments.beginFilePath)
    val configFileEnd = File(arguments.endFilePath)

    mergeSaves(
        targetFile = configFileTarget,
        beginFile = configFileBegin,
        endFile = configFileEnd
    )
}

private fun mergeSaves(targetFile: File, beginFile: File, endFile: File) {
    val targetSaveData = targetFile.readSaveData()
    val beginSaveData = beginFile.readSaveData()
    val endSaveData = endFile.readSaveData()

    val resultSaveData = mergeSaveData(
        targetSaveData = targetSaveData,
        beginSaveData = beginSaveData,
        endSaveData = endSaveData,
    )

    targetFile.writeSaveData(resultSaveData)
}

private fun mergeSaveData(
    targetSaveData: SaveData,
    beginSaveData: SaveData,
    endSaveData: SaveData
): SaveData {
    val sslValueMergeHelper = MergeObjectHelper.create(
        targetObj = targetSaveData.completeSave.sslValue,
        beginObj = beginSaveData.completeSave.sslValue,
        endObj = endSaveData.completeSave.sslValue,
    )

    val newSslValue = sslValueMergeHelper {
        targetObject.copy(
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

    return targetSaveData.run { copy(completeSave = completeSave.copy(sslValue = newSslValue)) }
}

private fun File.readSaveData(): SaveData = SaveConfigInputStream(inputStream()).use {
    Json.decodeFromStream<SaveData>(it)
}

private fun File.writeSaveData(saveData: SaveData) = outputStream().use {
    Json.encodeToStream(saveData, it)
    it.write(0)
}

private class SaveConfigInputStream(fileStream: FileInputStream) : FilterInputStream(fileStream) {
    override fun read(): Int = super.read().let { if (it == 0) -1 else it }
    override fun read(b: ByteArray, off: Int, len: Int): Int =
        super.read(b, off, len).let { if (it != -1 && b[off + it - 1] == 0.toByte()) it - 1 else it }
}
