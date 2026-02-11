package armatureScript

import org.jsoup.nodes.Element

class ArmaturePoint(circleElement: Element) {
    val id: String = circleElement.id()
    val x = circleElement.attr("cx").toDouble()
    val y = circleElement.attr("cy").toDouble()

    override fun toString(): String {
        return "($x, $y)"
    }
}