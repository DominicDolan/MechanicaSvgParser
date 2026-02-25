package modes

import FramesConfig
import armatureScript.VTessFrameParser

fun generateFrames(args: FramesConfig, inputLocation: String) {
    VTessFrameParser(inputLocation).createFileWithFrameName(args.outDir)
}