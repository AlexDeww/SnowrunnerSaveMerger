package org.home

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
import java.io.File
import kotlinx.cli.ArgType.Boolean as ArgBoolean

class Arguments private constructor() {

    companion object {
        fun parse(args: Array<String>): Arguments = Arguments().apply { parser.parse(args) }
    }

    private val parser = ArgParser("SnowrunnerMergeSave")

    val baseFile by parser.option(ArgFile, "base", "b", "Your save file").required()
    val sourceFile by parser.option(ArgFile, "source", "s", "Source save file").required()
    val originFile by parser.option(ArgFile, "origin", "o", "Original state of source (optional)")
    val makeBackup by parser.option(ArgBoolean, "backup", "m", "Make backup")

}

private object ArgFile : ArgType<File>(true) {
    override val description: kotlin.String = ""
    override fun convert(value: kotlin.String, name: kotlin.String): File = File(value)
}
