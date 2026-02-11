package svg

import generator.ScriptBuilderContext
import java.io.File

abstract class SvgFileParser(fileIn: String) {
    protected val file: File = File(fileIn)

    protected abstract fun ScriptBuilderContext.buildScript()

    fun createFile(fileOut: String) {
        val script = generator.buildScript {
            buildScript()
        }

        File(fileOut).writeText(script)
    }
}