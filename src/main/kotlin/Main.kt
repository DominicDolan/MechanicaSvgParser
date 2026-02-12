import cmd.CommandArguments
import cmd.CommandParser
import cmd.RunMode
import modes.generateFrames
import modes.generateParts
import watcher.FileWatcher

class FramesConfig{
    val inDir: String = "./resources/frames"
    val outDir: String = "/home/doghouse/Source/Mechanica/v-tess/src/main/kotlin/com/vtess/game/level/player/armature/frames"
}

class PartsConfig {
    val inDir: String = "./resources/parts"
    val outDirSvg: String = "C:\\Users\\Doghouse\\FSrc\\Mechanica\\primagame\\src\\main\\kotlin\\com\\vtess\\game\\level\\player\\armature\\generated"
    val outDirImg: String = "F:\\Src\\Mechanica\\primagame\\src\\main\\resources\\res\\images\\new"
}

fun main(args: Array<String>) {
    val arguments: CommandArguments = CommandParser(args)
    if (arguments.mode == RunMode.PARTS) {
        generateParts(PartsConfig())
    } else if (arguments.mode == RunMode.FRAMES) {
        val config = FramesConfig()
        if (!arguments.watch) {
            for (file in arguments.frameList) {
                generateFrames(config, file)
            }
        } else {
            val watcher = FileWatcher(config.inDir)

            watcher.watch {
                generateFrames(config, it.name)
            }
        }
    }

}
