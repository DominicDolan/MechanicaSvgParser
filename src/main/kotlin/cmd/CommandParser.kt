package cmd

import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Paths

enum class RunMode {
    FRAMES, PARTS, PATHS
}

interface CommandArguments {
    val mode: RunMode

    val watch: Boolean

    val outDir: String
    val outDirSvg: String
    val outDirImg: String
    val inDir: String

    val fileList: List<String>
}

class CommandParser(args: Array<String>) : CommandArguments {
    override val mode = parseRunMode(args)

    override val watch = args.any { it == "--watch" }

    override val outDir = parsePath("--out", args)
    override val outDirSvg = parsePath("--out-svg", args, default = outDir)
    override val outDirImg = parsePath("--img-out", args, default = outDir)
    override val inDir = parsePath("--in", args)
    override val fileList: List<String>

    init {
        println("inDir: $inDir")
        fileList = expandGlobPattern(inDir)
    }

    private fun expandGlobPattern(pattern: String): List<String> {
        val path = Paths.get(pattern)

        // If the pattern contains glob characters, use glob matching
        if (pattern.contains('*') || pattern.contains('?') || pattern.contains('[')) {
            val matcher = FileSystems.getDefault().getPathMatcher("glob:$pattern")

            // Find the base directory to start searching from
            val baseDir = findBaseDirectory(pattern)

            return Files.walk(baseDir)
                .filter { Files.isRegularFile(it) }
                .filter { matcher.matches(it) }
                .map { it.toString() }
                .toList()
        }

        // If no glob characters, treat as a single file
        return if (Files.exists(path) && Files.isRegularFile(path)) {
            listOf(pattern)
        } else {
            emptyList()
        }
    }

    private fun findBaseDirectory(pattern: String): java.nio.file.Path {
        val parts = pattern.split('/')
        val basePathParts = parts.takeWhile { !it.contains('*') && !it.contains('?') && !it.contains('[') }
        return if (basePathParts.isEmpty()) {
            Paths.get(".")
        } else {
            Paths.get(basePathParts.joinToString("/"))
        }
    }

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
        } else if (args[0] == "paths") {
            RunMode.PATHS
        } else {
            throw IllegalArgumentException("The first argument passed to the program should be 'frame' or 'parts'")
        }
    }
}