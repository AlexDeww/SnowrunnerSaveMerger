package org.home.utils

import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

fun Path.exists(): Boolean = SystemFileSystem.exists(this)

fun Path.copyTo(newPath: Path) {
    SystemFileSystem.source(this).buffered().use { source ->
        SystemFileSystem.sink(newPath).buffered().use { sink ->
            source.transferTo(sink)
        }
    }
}

fun Path.inputSource() = SystemFileSystem.source(this).buffered()

fun Path.outputSink() = SystemFileSystem.sink(this).buffered()
