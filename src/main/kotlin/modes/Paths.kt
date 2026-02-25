package modes

import PathsConfig
import armatureScript.VTessPathParser

fun generatePaths(config: PathsConfig, file: String) {
    val parser = VTessPathParser(file)
    parser.createFileWithPathName(config.outDir)
}