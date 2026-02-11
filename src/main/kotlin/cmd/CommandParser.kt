package cmd

enum class RunMode {
    FRAMES, PARTS
}

interface CommandArguments {
    val mode: RunMode

    val watch: Boolean

    val outDir: String
    val outDirSvg: String
    val outDirImg: String
    val inDir: String

    val frameList: List<String>
}

class CommandParser(args: Array<String>) : CommandArguments {
    override val mode = parseRunMode(args)

    override val watch = args.any { it == "--watch" }

    override val outDir = parsePath("--out", args)
    override val outDirSvg = parsePath("--out-svg", args, default = outDir)
    override val outDirImg = parsePath("--img-out", args, default = outDir)
    override val inDir = parsePath("--in", args)
    override val frameList: List<String>
        get() = TODO("Not yet implemented")

    private fun parsePath(term: String, args: Array<String>, default: String = "."): String {
        val index = args.indexOfFirst { it == term }

        if (index + 1 <= args.lastIndex) {
            return args[index + 1].replace("\"", "")
        }

        return default
    }

    private fun parseRunMode(args: Array<String>): RunMode {
        require(args.isNotEmpty()) { "No argument passed. Either 'frame' or 'parts' should be supplied as the run mode" }

        return if (args[0] == "frame") {
            RunMode.FRAMES
        } else if (args[0] == "parts") {
            RunMode.PARTS
        } else {
            throw IllegalArgumentException("The first argument passed to the program should be 'frame' or 'parts'")
        }
    }
}