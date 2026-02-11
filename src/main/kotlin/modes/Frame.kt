package modes

import FramesConfig
import armatureScript.VTessFrameParser

fun generateFrames(args: FramesConfig, fileName: String) {
    VTessFrameParser(args.inDir, fileName).createFileWithFrameName(args.outDir)
}