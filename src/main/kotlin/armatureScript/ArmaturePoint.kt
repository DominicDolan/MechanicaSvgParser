package armatureScript

class ArmaturePoint(val id: String, val x: Double, val y: Double) {
    override fun toString(): String {
        return "($x, $y)"
    }
}