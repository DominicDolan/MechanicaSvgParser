package armatureScript

import generator.ClassBuilder
import generator.ScriptBuilderContext
import org.intellij.lang.annotations.Language
import svg.SvgFileParser
import utils.camelToSnakeCase
import utils.kebabToCamelCase
import java.nio.file.Paths

class VTessFrameParser(inputLocation: String) : SvgFileParser(inputLocation) {
    private val points = getArmaturePointsFromFile()
    private val fileName = inputLocation.substringAfterLast('/').substringAfterLast('\\')
    private val frameName = fileName.removeSuffix(".svg").kebabToCamelCase()

    override fun ScriptBuilderContext.buildScript() {
        addRawText("""
            package com.vtess.game.level.player.armature.frames
            
            import com.cave.library.vector.vec2.Vector2
            import com.vtess.game.level.player.armature.frames.FrameDefinition
            
        """.trimIndent())

        val obj = ClassBuilder.createAnonymousObject().extends("FrameDefinition").body {
            for (point in points) {
                val x = point.x; val y = point.y
                addVal(point.id).override().value("Vector2.create($x, $y)")
            }
        }
        addVal(frameName).value(obj)
    }

    private fun getArmaturePointsFromFile(): List<ArmaturePoint> {
        val svg = file.readText()
        return com.microsoft.playwright.Playwright.create().use { playwright ->
            val browser = playwright.chromium().launch()
            val page = browser.newPage()
            
            page.setContent(svg)
            
            ArmatureData.allPointIds.map { pointId ->
                @Language("JavaScript") val result = page.evaluate("""
                    (id) => {
                        const circle = document.getElementById(id);
                        if (!circle) return null;
                        const rect = circle.getBoundingClientRect();
                        return {
                            x: rect.x + rect.width / 2,
                            y: rect.y + rect.height / 2
                        };
                    }
                """, pointId)
                
                if (result == null) {
                    throw IllegalStateException("Cannot find id: $pointId")
                }
                
                @Suppress("UNCHECKED_CAST")
                val coords = result as Map<String, Any>
                val x = (coords["x"] as Number).toDouble()
                val y = (coords["y"] as Number).toDouble()
                
                ArmaturePoint(pointId, x, y)
            }.also {
                browser.close()
            }
        }
    }

    fun createFileWithFrameName(outDir: String) {
        val outPath = Paths.get(outDir).resolve("$frameName.kt")
        createFile(outPath.toString())
    }

}