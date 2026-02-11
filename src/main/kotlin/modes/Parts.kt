package modes

import PartsConfig
import armatureScript.VTessPartsParser
import java.io.File


fun generateParts(args: PartsConfig) {
    VTessPartsParser(args.inDir).createFile(args.outDirSvg + "\\GeneratedArmature.kt")
//    copyPngFiles(args.inDir, args.outDirImg)
}

fun copyPngFiles(folderIn: String, folderOut: String) {
    File(folderIn)
        .walk()
        .filter { it.name.endsWith("png", true) }
        .forEach { it.copyTo(File(folderOut, it.name), true) }
}