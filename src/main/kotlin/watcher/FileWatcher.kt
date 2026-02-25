package watcher

import armatureScript.VTessFrameParser
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.streams.toList

class FileWatcher(private val filePatterns: List<String>) {

    private var byteCounts: Map<String, Int> = checkFiles()

    private fun getMatchingFiles(): List<File> {
        return filePatterns.flatMap { pattern ->
            if (pattern.contains('*') || pattern.contains('?') || pattern.contains('[')) {
                // Glob pattern - expand it
                val matcher = FileSystems.getDefault().getPathMatcher("glob:$pattern")
                val baseDir = findBaseDirectory(pattern)

                Files.walk(baseDir)
                    .filter { Files.isRegularFile(it) }
                    .filter { matcher.matches(it) }
                    .map { it.toFile() }
                    .toList()
            } else {
                // Single file
                val file = File(pattern)
                if (file.exists() && file.isFile) listOf(file) else emptyList()
            }
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

    private fun checkFiles(): Map<String, Int> {
        val files = getMatchingFiles()
        return files.associateBy({ it.absolutePath }, { file -> file.readBytes().sumOf { it.toInt() } })
    }

    fun watch(onChange: (file: File) -> Unit) {
        println("Watching ${filePatterns.size} file pattern(s)")
        filePatterns.forEach { println("  - $it") }

        while (true) {
            Thread.sleep(500)

            val oldByteCounts = byteCounts
            byteCounts = checkFiles()

            val changedFiles = getMatchingFiles().filter { byteCounts[it.absolutePath] != oldByteCounts[it.absolutePath] }

            if (changedFiles.isNotEmpty()) {
                println("${changedFiles.map { it.name }.toTypedArray().contentToString()} updated")
                for (file in changedFiles) {
                    onChange(file)
                }
            }
        }
    }
}