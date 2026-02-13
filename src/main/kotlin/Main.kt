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

private fun mergeSaves(baseFile: File, originFile: File?, sourceFile: File, makeBackup: Boolean) {
    val baseSaveData = baseFile.readSaveData()
    val originSaveData = originFile?.readSaveData() ?: SaveData.EMPTY
    val sourceSaveData = sourceFile.readSaveData()

    val resultSaveData = mergeSaveData(
        baseSaveData = baseSaveData,
        originSaveData = originSaveData,
        sourceSaveData = sourceSaveData,
    )

    if (makeBackup) baseFile.copyTo(File(baseFile.path + ".bak"), overwrite = true)
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
            finishedObjs = SslValue::finishedObjs.merge(isMonotonic = true),
            objectiveStates = SslValue::objectiveStates.merge(),
            upgradesGiverData = SslValue::upgradesGiverData.merge(),
            levelGarageStatuses = SslValue::levelGarageStatuses.merge(),
            discoveredObjectives = SslValue::discoveredObjectives.merge(isMonotonic = true),
            viewedUnactivatedObjectives = SslValue::viewedUnactivatedObjectives.merge(),
            watchPointsData = SslValue::watchPointsData.merge(),
            visitedLevels = SslValue::visitedLevels.merge(isMonotonic = true),
            persistentProfileData = SslValue::persistentProfileData.merge()
        )
    }

    return baseSaveData.run { copy(completeSave = completeSave.copy(sslValue = newSslValue)) }
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
