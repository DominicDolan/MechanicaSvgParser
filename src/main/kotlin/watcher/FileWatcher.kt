package watcher

import armatureScript.VTessFrameParser
import java.io.File

class FileWatcher(private val path: String) {

    private var byteCounts: Map<String, Int> = checkPath()

    private fun checkPath(): Map<String, Int> {
        val files = File(path)
            .listFiles()
            .filter { it.isFile }
            .filter { it.name.contains(".svg") }

        return files
            .associateBy({ it.name }, { file -> file.readBytes().sumOf { it.toInt() } })
    }

    fun watch(onChange: (file: File) -> Unit) {
        println("Watching files in $path")
        while (true) {
            Thread.sleep(500)

            val files = File(path)
                .listFiles()
                .filter { it.isFile }
                .filter { it.name.contains(".svg") }

            val oldByteCounts = byteCounts
            byteCounts = checkPath()

            val changedFiles = files.filter { byteCounts[it.name] != oldByteCounts[it.name] }

            if (changedFiles.isNotEmpty()) {
                println("${changedFiles.map { it.name }.toTypedArray().contentToString()} updated")
                for (file in changedFiles) {
                    onChange(file)
                }

            }
        }
    }
}