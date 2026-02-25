package armatureScript

import generator.ClassBuilder
import generator.ScriptBuilderContext
import org.intellij.lang.annotations.Language
import svg.SvgFileParser
import utils.kebabToCamelCase
import utils.kebabToPascalCase
import java.nio.file.Paths

class VTessPathParser(inputLocation: String) : SvgFileParser(inputLocation) {
    private val pathData = getPathsFromFile()
    private val fileName = inputLocation.substringAfterLast('/').substringAfterLast('\\')
    private val pathName = fileName.removeSuffix(".svg").kebabToCamelCase()

    override fun ScriptBuilderContext.buildScript() {
        addRawText("""
            package com.vtess.game.level.player.sprite
            
            import com.cave.library.vector.vec2.Vector2
            
        """.trimIndent())

        addObject(pathName.kebabToPascalCase()).body {
            for ((id, vertices) in pathData) {
                val verticesString = vertices.joinToString(",\n                ") { (x, y) ->
                    "Vector2.create($x, $y)"
                }
                addVal(id).value("""arrayOf(
                $verticesString
            )""")
            }
        }
    }

    private fun getPathsFromFile(): Map<String, List<Pair<Double, Double>>> {
        val svg = file.readText()
        return com.microsoft.playwright.Playwright.create().use { playwright ->
            val browser = playwright.chromium().launch()
            val page = browser.newPage()
            
            page.setContent(svg)
            
            @Language("JavaScript") val result = page.evaluate("""
                () => {
                    const paths = document.querySelectorAll('path');
                    const result = {};
                    
                    paths.forEach(path => {
                        const id = path.id;
                        if (!id) return;
                        
                        const d = path.getAttribute('d');
                        if (!d) return;
                        
                        // Parse the path data to get vertices
                        const vertices = [];
                        const commands = d.match(/[MLHVCSQTAZmlhvcsqtaz][^MLHVCSQTAZmlhvcsqtaz]*/g);
                        
                        let currentX = 0, currentY = 0;
                        
                        commands?.forEach(cmd => {
                            const type = cmd[0];
                            const coords = cmd.slice(1).trim().split(/[\s,]+/).filter(x => x).map((v) => parseFloat(v));
                            
                            if (type === 'M' || type === 'm') {
                                // Move command
                                for (let i = 0; i < coords.length; i += 2) {
                                    if (type === 'M') {
                                        currentX = coords[i];
                                        currentY = coords[i + 1];
                                    } else {
                                        currentX += coords[i];
                                        currentY += coords[i + 1];
                                    }
                                    vertices.push({ x: currentX, y: currentY });
                                }
                            } else if (type === 'L' || type === 'l') {
                                // Line command
                                for (let i = 0; i < coords.length; i += 2) {
                                    if (type === 'L') {
                                        currentX = coords[i];
                                        currentY = coords[i + 1];
                                    } else {
                                        currentX += coords[i];
                                        currentY += coords[i + 1];
                                    }
                                    vertices.push({ x: currentX, y: currentY });
                                }
                            } else if (type === 'Z' || type === 'z') {
                                // Close path - no new vertex needed
                            }
                            // Add more command types as needed
                        });
                        
                        result[id] = vertices;
                    });
                    
                    return result;
                }
            """)
            
            @Suppress("UNCHECKED_CAST")
            val pathsMap = result as Map<String, List<Map<String, Any>>>
            
            pathsMap.mapValues { (_, vertices) ->
                val formattedVertices = vertices.map { coords ->
                    val x = (coords["x"] as Number).toDouble()
                    val y = (coords["y"] as Number).toDouble()
                    x to y
                }

                formattedVertices.map { (x, y) -> (x - formattedVertices.first().first) to (-y + formattedVertices.first().second) }
            }.also {
                browser.close()
            }
        }
    }

    fun createFileWithPathName(outDir: String) {
        val outPath = Paths.get(outDir).resolve("${pathName.kebabToPascalCase()}.kt")
        createFile(outPath.toString())
    }

}
