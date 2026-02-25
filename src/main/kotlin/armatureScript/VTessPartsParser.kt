package armatureScript

import generator.ClassBuilder
import generator.ScriptBuilderContext
import org.jsoup.Jsoup
import svg.SvgFileParser
import utils.camelToSnakeCase
import java.io.File


class VTessPartsParser(fileIn: String) : SvgFileParser(fileIn) {
    private val armatureParts = getArmaturePartsInFolder()

    override fun ScriptBuilderContext.buildScript() {
        addRawText("""
                package com.vtess.game.level.player.armature.generated

                import com.cave.library.vector.vec2.Vector2
                import com.vtess.game.level.player.armature.generic.GenericArmature
                
            """.trimIndent())

        addClass("GeneratedArmature").extends("GenericArmature.Defaults<GeneratedPart>(), AutoGenerateArmatureDefinition").body {

            for (part in armatureParts) {
                val pngFile = "\"${part.fileName.removeSuffix(".svg")}.png\""
                val partScript = ClassBuilder
                    .createAnonymousObject()
                    .extends(part.interfaceName)
                    .body {
                        addVal("imageFileName").override().value(pngFile)
                        addVal("imageWidth").override().value(part.viewBox.width.toString())
                        addVal("imageHeight").override().value(part.viewBox.width.toString())
                        for (point in part.points) {
                            val x = point.x; val y = point.y
                            addVal(point.id).override().value("Vector2.create($x, $y)")
                        }
                    }

                addVal(part.variableName).override().value(partScript)
            }
        }
    }


    private fun getArmaturePartsInFolder(): List<ArmaturePart> {
        return file
            .walk()
            .filter { it.name.endsWith("svg", true) }
            .map { PartsParser(it) }
            .flatMap { parser -> ArmatureData.allGroups.map { parser.searchIds(it) } }
            .filterNotNull()
            .toList()
    }
}


class PartsParser(file: File) {
    private val doc = Jsoup.parse(file.readText())
    private val fileName = file.name

    fun searchIds(ids: ArmatureData.PointIdGroup): ArmaturePart? {
        val list = ArrayList<ArmaturePoint>()
        for (id in ids.ids) {
            val el = doc.getElementById(id) ?: return null
            list.add(ArmaturePoint(el.id(), el.attr("cx").toDouble(), el.attr("cy").toDouble()))
        }
        val viewBox = findViewBox()
        return ArmaturePart(fileName, ids.variableName, ids.className, list, viewBox)
    }

    private fun findViewBox(): SvgViewBox {
        val svgElements = doc.getElementsByTag("svg")
        require(svgElements.isNotEmpty())

        val svgElement = svgElements.first()
        requireNotNull(svgElement)

        val (x, y, width, height) = svgElement.attr("viewBox").split(" ").map { it.toDouble() }

        return SvgViewBox(x, y, width, height)
    }
}

data class ArmaturePart(
    val fileName: String,
    val variableName: String,
    val interfaceName: String,
    val points: List<ArmaturePoint>,
    val viewBox: SvgViewBox
)

data class SvgViewBox(val x: Double, val y: Double, val width: Double, val height: Double)
