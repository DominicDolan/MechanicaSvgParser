interface Vector2 {
    val x: Double
    val y: Double

    companion object {
        fun create(x: Double, y: Double): Vector2 {
            return object : Vector2 {
                override val x: Double = x
                override val y: Double = y
            }
        }
    }
}

