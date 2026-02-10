@file:OptIn(ExperimentalSerializationApi::class)

package org.home

import kotlinx.serialization.ExperimentalSerializationApi
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

fun main() {
    val configFileBegin = File("C:\\Users\\deadi\\Desktop\\remote_s\\CompleteSave.cfg")
    val configFileEnd = File("C:\\Users\\deadi\\Desktop\\remote_e\\CompleteSave.cfg")
    val configFileTarget = File("J:\\Programs\\Steam\\userdata\\42206345\\1465360\\remote\\CompleteSave.cfg")

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

private fun File.writeSaveData(saveData: SaveData) {
    outputStream().use {
        Json.encodeToStream(saveData, it)
        it.write(0)
    }
}

private class SaveConfigInputStream(fileStream: FileInputStream) : FilterInputStream(fileStream) {
    override fun read(): Int = super.read().let { if (it == 0) -1 else it }
    override fun read(b: ByteArray, off: Int, len: Int): Int =
        super.read(b, off, len).let { if (it != -1 && b[off + it - 1] == 0.toByte()) it - 1 else it }
}
