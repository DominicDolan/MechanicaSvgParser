import cmd.CommandArguments
import cmd.CommandParser
import cmd.RunMode
import modes.generateFrames
import modes.generateParts
import modes.generatePaths
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


class PathsConfig{
    val inDir: String = "./resources/paths"
    val outDir: String = "/home/doghouse/Source/Mechanica/v-tess/src/main/kotlin/com/vtess/game/level/player/sprite"
}

fun runCommand(arguments: CommandArguments, command: (file: String) -> Unit) {
    if (!arguments.watch) {
        for (file in arguments.fileList) {
            command(file)
        }
    } else {
        // Watch the original glob pattern from inDir or the expanded file list
        val watchPatterns = if (arguments.inDir.contains('*') || arguments.inDir.contains('?') || arguments.inDir.contains('[')) {
            listOf(arguments.inDir)
        } else {
            arguments.fileList
        }

        val watcher = FileWatcher(watchPatterns)

        watcher.watch {
            println("File changed: ${it.absolutePath}")
            command(it.absolutePath)
        }
    }
}

fun main(args: Array<String>) {
    val arguments: CommandArguments = CommandParser(args)
    if (arguments.mode == RunMode.PARTS) {
        generateParts(PartsConfig())
    } else if (arguments.mode == RunMode.FRAMES) {
        runCommand(arguments) { generateFrames(FramesConfig(), it) }
    } else if (arguments.mode == RunMode.PATHS) {
        runCommand(arguments) { generatePaths(PathsConfig(), it) }
    }

}
