package org.home.utils

import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

fun Path.exists(): Boolean = SystemFileSystem.exists(this)

fun Path.inputSource() = SystemFileSystem.source(this).buffered()

fun Path.outputSink() = SystemFileSystem.sink(this).buffered()

fun Path.copyTo(newPath: Path) {
    inputSource().use { source -> newPath.outputSink().use { sink -> source.transferTo(sink) } }
}
