package org.home

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required

class Arguments private constructor() {

    companion object {
        fun parse(args: Array<String>): Arguments = Arguments().apply { parser.parse(args) }
    }

    private val parser = ArgParser("SnowrunnerMergeSave", prefixStyle = ArgParser.OptionPrefixStyle.JVM)

    val targetFilePath by parser.option(ArgType.String, "t", description = "Target save file").required()
    val beginFilePath by parser.option(ArgType.String, "b", description = "Begin save file").required()
    val endFilePath by parser.option(ArgType.String, "e", description = "End save file").required()

}
