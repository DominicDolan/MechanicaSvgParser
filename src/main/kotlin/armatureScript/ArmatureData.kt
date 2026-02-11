package armatureScript

object ArmatureData {

    private val _allLists = ArrayList<PointIdGroup>()
    val allGroups: List<PointIdGroup> get() = _allLists

    val allPointIds by lazy {
        allGroups.flatMap { it.ids.toList() }
    }

    val headPointsIds = PointIdGroup("Head", arrayOf(
        "h1", "h2"
    ))

    val torsoPoints = PointIdGroup("Torso", arrayOf(
        "rtTorso",
        "ltTorso",
        "luaTorso",
        "ruaTorso",
        "headTorso"
    ))

    val hairPoints = PointIdGroup("Hair", arrayOf(
        "hair1",
        "hair2"
    ))

    val rightUpperArm = PointIdGroup("RightUpperArm", arrayOf(
        "rua1",
        "rua2"
    ))

    val rightLowerArm = PointIdGroup("RightLowerArm", arrayOf(
        "rla1",
        "rla2"
    ))

    val leftUpperArm = PointIdGroup("LeftUpperArm", arrayOf(
        "lua1",
        "lua2"
    ))

    val leftLowerArm = PointIdGroup("LeftLowerArm", arrayOf(
        "lla1",
        "lla2"
    ))

    val rightThigh = PointIdGroup("RightThigh", arrayOf(
        "rt1",
        "rt2"
    ))

    val rightCrus = PointIdGroup("RightCrus", arrayOf(
        "rc1",
        "rc2"
    ))

    val leftThigh = PointIdGroup("LeftThigh", arrayOf(
        "lt1",
        "lt2"
    ))

    val leftCrus = PointIdGroup("LeftCrus", arrayOf(
        "lc1",
        "lc2"
    ))

    fun allToIdGroup(name: String): PointIdGroup {
        return PointIdGroup(name, allPointIds.toTypedArray())
    }

    fun findGroupContaining(id: String): PointIdGroup {
        return _allLists.find { it.ids.contains(id) } ?: throw IllegalArgumentException("Argument Id with value $id is not part of the Armature Point Ids")
    }

    class PointIdGroup(val name: String, val ids: Array<String>) {
        val variableName: String get() = name.replaceFirstChar(Char::lowercaseChar)
        val className: String get() = "Generated${name}Part"
        init {
            _allLists.add(this)
        }
    }
}
