package armatureScript

import generator.ClassBuilder
import generator.ScriptBuilderContext
import org.jsoup.Jsoup
import svg.SvgFileParser
import utils.camelToSnakeCase
import utils.kebabToCamelCase

class VTessFrameParser(location: String, fileName: String) : SvgFileParser("$location\\$fileName") {
    private val points = getArmaturePointsFromFile()
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

        val doc = Jsoup.parse(svg)

        return ArmatureData.allPointIds.map {
            val el = doc.getElementById(it) ?: throw IllegalStateException("Cannot find id: $it")
            ArmaturePoint(el)
        }
    }

    fun createFileWithFrameName(outDir: String) {
        createFile("${outDir}\\${frameName}.kt")
    }

}